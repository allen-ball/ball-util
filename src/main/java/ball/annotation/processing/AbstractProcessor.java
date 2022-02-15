package ball.annotation.processing;
/*-
 * ##########################################################################
 * Utilities
 * %%
 * Copyright (C) 2008 - 2022 Allen D. Ball
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ##########################################################################
 */
import ball.activation.ThrowableDataSource;
import ball.lang.reflect.JavaLangReflectMethods;
import ball.tools.javac.AbstractTaskListener;
import com.sun.source.util.JavacTask;
import com.sun.source.util.TaskEvent;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.processing.Completion;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import lombok.NoArgsConstructor;
import lombok.Synchronized;
import lombok.ToString;

import static javax.tools.Diagnostic.Kind.ERROR;
import static lombok.AccessLevel.PROTECTED;

/**
 * Provides abstract base class for {@link Processor} by providing a
 * {@link #getSupportedSourceVersion()} implementation, methods to report
 * {@link javax.tools.Diagnostic.Kind#ERROR}s and
 * {@link javax.tools.Diagnostic.Kind#WARNING}s, and access to
 * {@link ProcessingEnvironment#getFiler()},
 * {@link ProcessingEnvironment#getElementUtils()}, and
 * {@link ProcessingEnvironment#getTypeUtils()}.
 *
 * {@bean.info}
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 */
@NoArgsConstructor(access = PROTECTED) @ToString
public abstract class AbstractProcessor extends JavaxLangModelUtilities implements Processor, JavaLangReflectMethods {
    private final Set<String> options = new LinkedHashSet<>();
    private ProcessingEnvironment processingEnv = null;
    /** See {@link JavacTask#instance(ProcessingEnvironment)}. */
    protected JavacTask javac = null;
    /** See {@link ProcessingEnvironment#getFiler()}. */
    protected Filer filer = null;

    {
        SupportedOptions annotation = getClass().getAnnotation(SupportedOptions.class);

        if (annotation != null) {
            Collections.addAll(options, annotation.value());
        }
    }

    @Override
    public Set<String> getSupportedOptions() { return options; }

    @Override
    public abstract Set<String> getSupportedAnnotationTypes();

    @Override
    public SourceVersion getSupportedSourceVersion() {
        SourceVersion[] values = SourceVersion.values();

        return values[values.length - 1];
    }

    @Synchronized
    @Override
    public void init(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;

        try {
            filer = processingEnv.getFiler();
            elements = processingEnv.getElementUtils();
            types = processingEnv.getTypeUtils();

            javac = JavacTask.instance(processingEnv);

            if (javac != null) {
                javac.addTaskListener(new WhenAnnotationProcessingFinished());
            }

            if (fm == null) {
                fm = Shims.getJavaFileManager(processingEnv);
            }
        } catch (Exception exception) {
            print(ERROR, exception);
        }
    }

    @Override
    public abstract boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv);

    @Override
    public Iterable<? extends Completion> getCompletions(Element element, AnnotationMirror annotation, ExecutableElement member, String text) {
        return Collections.emptyList();
    }

    /**
     * Callback method to signal
     * {@link com.sun.source.util.TaskEvent.Kind#ANNOTATION_PROCESSING} is
     * finished.
     */
    protected void whenAnnotationProcessingFinished() { }

    /**
     * Method to print a diagnostic message.
     *
     * @param   kind            The {@link javax.tools.Diagnostic.Kind}.
     * @param   format          The message format {@link String}.
     * @param   argv            Optional arguments to the message format
     *                          {@link String}.
     *
     * @see javax.annotation.processing.Messager#printMessage(Diagnostic.Kind,CharSequence)
     */
    protected void print(Diagnostic.Kind kind, String format, Object... argv) {
        processingEnv.getMessager()
            .printMessage(kind, String.format(format, argv));
    }

    /**
     * Method to print a diagnostic message.
     *
     * @param   kind            The {@link javax.tools.Diagnostic.Kind}.
     * @param   element         The offending {@link Element}.
     * @param   format          The message format {@link String}.
     * @param   argv            Optional arguments to the message format
     *                          {@link String}.
     *
     * @see javax.annotation.processing.Messager#printMessage(Diagnostic.Kind,CharSequence,Element)
     */
    protected void print(Diagnostic.Kind kind, Element element, String format, Object... argv) {
        processingEnv.getMessager()
            .printMessage(kind, String.format(format, argv), element);
    }

    /**
     * Method to print a diagnostic message.
     *
     * @param   kind            The {@link javax.tools.Diagnostic.Kind}.
     * @param   element         The offending {@link Element}.
     * @param   annotation      The offending {@link AnnotationMirror}.
     * @param   format          The message format {@link String}.
     * @param   argv            Optional arguments to the message format
     *                          {@link String}.
     *
     * @see javax.annotation.processing.Messager#printMessage(Diagnostic.Kind,CharSequence,Element,AnnotationMirror)
     */
    protected void print(Diagnostic.Kind kind, Element element, AnnotationMirror annotation, String format, Object... argv) {
        processingEnv.getMessager()
            .printMessage(kind, String.format(format, argv), element, annotation);
    }

    /**
     * Method to print a diagnostic message.
     *
     * @param   kind            The {@link javax.tools.Diagnostic.Kind}.
     * @param   element         The offending {@link Element}.
     * @param   annotation      The offending {@link AnnotationMirror}.
     * @param   value           The offending {@link AnnotationValue}.
     * @param   format          The message format {@link String}.
     * @param   argv            Optional arguments to the message format
     *                          {@link String}.
     *
     * @see javax.annotation.processing.Messager#printMessage(Diagnostic.Kind,CharSequence,Element,AnnotationMirror,AnnotationValue)
     */
    protected void print(Diagnostic.Kind kind, Element element, AnnotationMirror annotation, AnnotationValue value, String format, Object... argv) {
        processingEnv.getMessager()
            .printMessage(kind, String.format(format, argv), element, annotation, value);
    }

    /**
     * Method to print a {@link Throwable}.
     *
     * @param   kind            The {@link javax.tools.Diagnostic.Kind}.
     * @param   throwable       The {@link Throwable}.
     */
    protected void print(Diagnostic.Kind kind, Throwable throwable) {
        print(kind, new ThrowableDataSource(throwable).toString());
    }

    /**
     * Abstract {@link Criterion} base class.
     */
    @NoArgsConstructor(access = PROTECTED) @ToString
    protected abstract class Criterion<T extends Element> implements Predicate<T> { }

    /**
     * Abstract {@link Check} base class.
     */
    @NoArgsConstructor(access = PROTECTED) @ToString
    protected abstract class Check<T extends Element> implements Consumer<T> { }

    @NoArgsConstructor @ToString
    private class WhenAnnotationProcessingFinished extends AbstractTaskListener {
        @Override
        public void finished(TaskEvent event) {
            switch (event.getKind()) {
            case ANNOTATION_PROCESSING:
                javac.removeTaskListener(this);
                whenAnnotationProcessingFinished();
                break;

            default:
                break;
            }
        }
    }
}
