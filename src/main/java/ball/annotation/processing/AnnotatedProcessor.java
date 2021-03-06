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
import com.sun.source.util.TaskListener;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static javax.tools.Diagnostic.Kind.ERROR;
import static lombok.AccessLevel.PROTECTED;

/**
 * Abstract {@link javax.annotation.processing.Processor} base class for
 * processing {@link Annotation}s specified by @{@link For}.  Provides
 * built-in support for a number of {@link Annotation} types.
 *
 * {@bean.info}
 *
 * @see AnnotationValueMustConvertTo
 * @see TargetMustBe
 * @see TargetMustExtend
 * @see TargetMustHaveConstructor
 * @see TargetMustHaveModifiers
 * @see TargetMustNotHaveModifiers
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 */
@NoArgsConstructor(access = PROTECTED) @ToString
public abstract class AnnotatedProcessor extends AbstractProcessor {
    private final Set<String> processed = new TreeSet<>();

    /**
     * Method to get the {@link List} of supported {@link Annotation}
     * {@link Class}es.
     *
     * @return  The {@link List} of supported {@link Annotation}
     *          {@link Class}es.
     */
    protected List<Class<? extends Annotation>> getSupportedAnnotationTypeList() {
        return Arrays.asList(getClass().getAnnotation(For.class).value());
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set =
            getSupportedAnnotationTypeList().stream()
            .map(Class::getCanonicalName)
            .collect(toSet());

        return set;
    }

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        try {
            if (this instanceof ClassFileProcessor) {
                TaskListener listener =
                    new COMPILATIONFinishedTaskListener(javac, elements, processed, () -> onCOMPILATIONFinished());

                javac.addTaskListener(listener);
            }
        } catch (Exception exception) {
            print(ERROR, exception);
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        annotations.stream().forEach(t -> process(roundEnv, t));

        return true;
    }

    private void process(RoundEnvironment roundEnv, TypeElement annotation) {
        try {
            roundEnv.getElementsAnnotatedWith(annotation).stream()
                .peek(t -> processed.add(getEnclosingTypeBinaryName(t)))
                .peek(new AnnotationValueMustConvertToCheck(annotation))
                .peek(new TargetMustBeCheck(annotation))
                .peek(new TargetMustHaveModifiersCheck(annotation))
                .peek(new TargetMustNotHaveModifiersCheck(annotation))
                .peek(new TargetMustExtendCheck(annotation))
                .peek(new TargetMustHaveConstructorCheck(annotation))
                .forEach(t -> process(roundEnv, annotation, t));
        } catch (Throwable throwable) {
            print(ERROR, throwable);
        }
    }

    /**
     * Callback method to process an annotated {@link Element}.  Default
     * implementation does nothing.
     *
     * @param   roundEnv        The {@link RoundEnvironment}.
     * @param   annotation      The annotation {@link TypeElement}.
     * @param   element         The annotated {@link Element}.
     */
    protected void process(RoundEnvironment roundEnv, TypeElement annotation, Element element) {
    }

    private String getEnclosingTypeBinaryName(Element element) {
        String name = null;

        switch (element.getKind()) {
        case ANNOTATION_TYPE:
        case CLASS:
        case ENUM:
        case INTERFACE:
            name = elements.getBinaryName((TypeElement) element).toString();
            break;

        case PACKAGE:
            name = ((PackageElement) element).getQualifiedName().toString() + ".package-info";
            break;

        default:
            name = getEnclosingTypeBinaryName(element.getEnclosingElement());
            break;
        }

        return name;
    }

    private void onCOMPILATIONFinished() {
        HashSet<Class<?>> set = new HashSet<>();

        try {
            ClassLoader loader = getClassPathClassLoader(fm);

            for (String name : ClassFileProcessor.list(fm)) {
                try {
                    set.add(Class.forName(name, true, loader));
                } catch (Throwable throwable) {
                }
            }

            ((ClassFileProcessor) this).process(set, fm);
        } catch (Exception exception) {
            print(ERROR, exception);
        }
    }

    @AllArgsConstructor @ToString
    private class AnnotationValueMustConvertToCheck extends Check<Element> {
        private final TypeElement annotation;

        @Override
        public void accept(Element element) {
            AnnotationMirror meta = getAnnotationMirror(annotation, AnnotationValueMustConvertTo.class);

            if (meta != null) {
                AnnotationValue value = getAnnotationValue(meta, "value");
                TypeElement to = (TypeElement) types.asElement((TypeMirror) value.getValue());
                String method = (String) getAnnotationValue(meta, "method").getValue();
                String name = (String) getAnnotationValue(meta, "name").getValue();
                AnnotationMirror mirror = getAnnotationMirror(element, annotation);
                AnnotationValue from = null;

                try {
                    from = getAnnotationValue(mirror, name);

                    Class<?> type = Class.forName(to.getQualifiedName().toString());

                    if (! method.isEmpty()) {
                        type.getMethod(method, from.getValue().getClass())
                            .invoke(null, from.getValue());
                    } else {
                        type.getConstructor(from.getValue().getClass())
                            .newInstance(from.getValue());
                    }
                } catch (Exception exception) {
                    Throwable throwable = exception;

                    while (throwable instanceof InvocationTargetException) {
                        throwable = throwable.getCause();
                    }

                    print(ERROR, element, mirror,
                          "Cannot convert %s to %s\n%s",
                          from, to.getQualifiedName(), throwable.getMessage());
                }
            }
        }
    }

    @AllArgsConstructor @ToString
    private class TargetMustBeCheck extends Check<Element> {
        private final TypeElement annotation;

        @Override
        public void accept(Element element) {
            AnnotationMirror meta = getAnnotationMirror(annotation, TargetMustBe.class);

            if (meta != null) {
                AnnotationValue value = getAnnotationValue(meta, "value");
                String name = ((VariableElement) value.getValue()).getSimpleName().toString();
                ElementKind kind = ElementKind.valueOf(name);

                if (! kind.equals(element.getKind())) {
                    print(ERROR, element,
                          "@%s: %s is not a %s",
                          annotation.getSimpleName(), element.getKind(), kind);
                }
            }
        }
    }

    @AllArgsConstructor @ToString
    private class TargetMustHaveModifiersCheck extends Check<Element> {
        private final TypeElement annotation;

        @Override
        public void accept(Element element) {
            AnnotationMirror meta = getAnnotationMirror(annotation, TargetMustHaveModifiers.class);

            if (meta != null) {
                EnumSet<Modifier> modifiers =
                    Stream.of(getAnnotationValue(meta, "value"))
                    .filter(Objects::nonNull)
                    .map(t -> (List<?>) t.getValue())
                    .flatMap(List::stream)
                    .map(t -> ((AnnotationValue) t).getValue())
                    .map(Objects::toString)
                    .map(Modifier::valueOf)
                    .collect(toCollection(() -> EnumSet.noneOf(Modifier.class)));

                if (! withModifiers(modifiers).test(element)) {
                    print(ERROR, element, "%s must be %s", element.getKind(), modifiers);
                }
            }
        }
    }

    @AllArgsConstructor @ToString
    private class TargetMustNotHaveModifiersCheck extends Check<Element> {
        private final TypeElement annotation;

        @Override
        public void accept(Element element) {
            AnnotationMirror meta = getAnnotationMirror(annotation, TargetMustNotHaveModifiers.class);

            if (meta != null) {
                EnumSet<Modifier> modifiers =
                    Stream.of(getAnnotationValue(meta, "value"))
                    .filter(Objects::nonNull)
                    .map(t -> (List<?>) t.getValue())
                    .flatMap(List::stream)
                    .map(t -> ((AnnotationValue) t).getValue())
                    .map(Objects::toString)
                    .map(Modifier::valueOf)
                    .collect(toCollection(() -> EnumSet.noneOf(Modifier.class)));

                if (! withoutModifiers(modifiers).test(element)) {
                    print(ERROR, element, "%s must not be %s", element.getKind(), modifiers);
                }
            }
        }
    }

    @AllArgsConstructor @ToString
    private class TargetMustExtendCheck extends Check<Element> {
        private final TypeElement annotation;

        @Override
        public void accept(Element element) {
            AnnotationMirror meta = getAnnotationMirror(annotation, TargetMustExtend.class);

            if (meta != null) {
                AnnotationValue value = getAnnotationValue(meta, "value");
                TypeElement type = (TypeElement) types.asElement((TypeMirror) value.getValue());

                if (! types.isAssignable(element.asType(), type.asType())) {
                    print(ERROR, element,
                          "@%s: %s does not extend %s",
                          annotation.getSimpleName(), element, type.getQualifiedName());
                }
            }
        }
    }

    @AllArgsConstructor @ToString
    private class TargetMustHaveConstructorCheck extends Check<Element> {
        private final TypeElement annotation;

        @Override
        public void accept(Element element) {
            AnnotationMirror meta = getAnnotationMirror(annotation, TargetMustHaveConstructor.class);

            if (meta != null) {
                AnnotationValue value = getAnnotationValue(meta, "value");
                String name = ((VariableElement) value.getValue()).getSimpleName().toString();
                Modifier modifier = Modifier.valueOf(name);
                List<TypeMirror> parameters =
                    Stream.of(getAnnotationValue(meta, "parameters"))
                    .filter(Objects::nonNull)
                    .map(t -> (List<?>) t.getValue())
                    .flatMap(List::stream)
                    .map(t -> (AnnotationValue) t)
                    .map(t -> (TypeMirror) t.getValue())
                    .collect(toList());
                ExecutableElement constructor = getConstructor((TypeElement) element, parameters);
                boolean found = (constructor != null && constructor.getModifiers().contains(modifier));

                if (! found) {
                    print(ERROR, element, "@%s: No %s matching constructor", annotation.getSimpleName(), modifier);
                }
            }
        }
    }
}
