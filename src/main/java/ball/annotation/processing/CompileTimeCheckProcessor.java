package ball.annotation.processing;
/*-
 * ##########################################################################
 * Utilities
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2008 - 2020 Allen D. Ball
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
import ball.annotation.CompileTimeCheck;
import ball.annotation.ServiceProviderFor;
import ball.tools.javac.AbstractTaskListener;
import com.sun.source.util.TaskEvent;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static java.util.stream.Collectors.toSet;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.WARNING;
import static javax.tools.StandardLocation.CLASS_OUTPUT;
import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * {@link CompileTimeCheck} {@link Processor}.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Processor.class })
@For({ CompileTimeCheck.class })
@NoArgsConstructor @ToString
public class CompileTimeCheckProcessor extends AnnotatedProcessor {
    private static final EnumSet<Modifier> FIELD_MODIFIERS =
        EnumSet.of(STATIC, FINAL);

    private final Set<Element> set = new HashSet<>();
    private Path base = null;
    private ClassLoader loader = null;

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        try {
            //javac.addTaskListener(new OnAnnotationProcessingFinished());
            //javac.addTaskListener(new TaskListenerImpl());

            base =
                Paths.get(filer.getResource(CLASS_OUTPUT, EMPTY, "UNUSED")
                          .toUri().resolve(EMPTY));
            loader = new ClassLoaderImpl();
        } catch (Exception exception) {
            print(ERROR, exception);
        }
    }

    @Override
    protected void process(RoundEnvironment roundEnv,
                           TypeElement annotation, Element element) {
        super.process(roundEnv, annotation, element);

        switch (element.getKind()) {
        case FIELD:
            if (! set.contains(element)) {
                if (element.getModifiers().containsAll(FIELD_MODIFIERS)) {
                    set.add(element);
                } else {
                    print(ERROR, element,
                          "%s must be %s", element.getKind(), FIELD_MODIFIERS);
                }
            }
            break;

        default:
            throw new IllegalStateException(element.getKind().name());
            /* break; */
        }
    }

    private void check(Element element) {
        TypeElement type = (TypeElement) element.getEnclosingElement();
        String name = type.getQualifiedName().toString();

        try {
            Class.forName(name, false, loader);
        } catch (Throwable throwable) {
            if (throwable instanceof ExceptionInInitializerError) {
                throwable = throwable.getCause();
            }

            while (throwable instanceof InvocationTargetException) {
                throwable = throwable.getCause();
            }

            print(ERROR, element, "%s", throwable.getMessage());
        }
    }

    @NoArgsConstructor @ToString
    private class TaskListenerImpl extends AbstractTaskListener {
        @Override
        public void started(TaskEvent event) {
            print(WARNING, "STARTED %s", event);
        }

        @Override
        public void finished(TaskEvent event) {
            print(WARNING, "FINISHED %s", event);
        }
    }

    @NoArgsConstructor @ToString
    private class OnAnnotationProcessingFinished extends AbstractTaskListener {
        @Override
        public void finished(TaskEvent event) {
            if (event.getKind() == TaskEvent.Kind.ANNOTATION_PROCESSING) {
                javac.removeTaskListener(this);
                javac.addTaskListener(new OnGenerationFinished());
            }
        }
    }

    @NoArgsConstructor @ToString
    private class OnGenerationFinished extends AbstractTaskListener {
        private final Set<? extends Element> required =
            set.stream()
            .map(Element::getEnclosingElement)
            .collect(toSet());

        @Override
        public void finished(TaskEvent event) {
            if (event.getKind() == TaskEvent.Kind.GENERATE) {
                TypeElement type = event.getTypeElement();

                required.remove(type);

                if (required.isEmpty()) {
                    javac.removeTaskListener(this);
                }
            }
        }
    }

    @ToString
    private class ClassLoaderImpl extends ClassLoader {
        public ClassLoaderImpl() { super(null); }

        @Override
        public Class<?> findClass(String name) throws ClassNotFoundException {
            Class<?> type = null;

            try {
                Path path = getClassFilePath(name);
                byte[] bytes = Files.readAllBytes(path);
            } catch (Exception exception) {
                type = super.findClass(name);
            }

            return type;
        }

        private Path getClassFilePath(String name) {
            Path path = Paths.get(base.toString(), name.split("[.]"));

            return path.resolveSibling(path.getFileName() + ".class");
        }
    }
}
