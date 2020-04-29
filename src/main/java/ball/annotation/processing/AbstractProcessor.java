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
import ball.activation.ThrowableDataSource;
import ball.beans.PropertyMethodEnum;
import ball.lang.reflect.JavaLangReflectMethods;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.disjoint;
import static java.util.stream.Collectors.toList;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.lang.model.util.ElementFilter.constructorsIn;
import static javax.lang.model.util.ElementFilter.methodsIn;
import static javax.tools.Diagnostic.Kind.ERROR;
import static lombok.AccessLevel.PROTECTED;

/**
 * Extends {@link javax.annotation.processing.AbstractProcessor} by
 * providing a {@link #getSupportedSourceVersion()} implementation, methods
 * to report {@link javax.tools.Diagnostic.Kind#ERROR}s and
 * {@link javax.tools.Diagnostic.Kind#WARNING}s, and access to
 * {@link ProcessingEnvironment#getFiler()},
 * {@link ProcessingEnvironment#getElementUtils()}, and
 * {@link ProcessingEnvironment#getTypeUtils()}.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@NoArgsConstructor(access = PROTECTED) @ToString
public abstract class AbstractProcessor
                      extends javax.annotation.processing.AbstractProcessor
                      implements JavaLangReflectMethods {
    /** UTF-8 */
    protected static final Charset CHARSET = UTF_8;

    /** See {@link ProcessingEnvironment#getFiler()}. */
    protected Filer filer = null;
    /** See {@link ProcessingEnvironment#getElementUtils()}. */
    protected Elements elements = null;
    /** See {@link ProcessingEnvironment#getTypeUtils()}. */
    protected Types types = null;

    /**
     * Method to get an {@link Annotation} on {@link.this} instance
     * {@link Class}.
     *
     * @param   annotation      The {@link Annotation} {@link Class}.
     * @param   <A>             The {@link Annotation} subtype.
     *
     * @return  The {@link Annotation} or {@code null}.
     */
    protected <A extends Annotation> A getAnnotation(Class<A> annotation) {
        return getClass().getAnnotation(annotation);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /**
     * See {@link #elements} and {@link #types}.
     */
    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        try {
            filer = processingEnv.getFiler();
            elements = processingEnv.getElementUtils();
            types = processingEnv.getTypeUtils();
        } catch (Exception exception) {
            print(ERROR, null, exception);
        }
    }

    @Override
    public abstract boolean process(Set<? extends TypeElement> annotations,
                                    RoundEnvironment roundEnv);

    /**
     * Method to print a diagnositc message.
     *
     * @param   kind            The {@link javax.tools.Diagnostic.Kind}.
     * @param   element         The offending {@link Element}.
     * @param   format          The message format {@link String}.
     * @param   argv            Optional arguments to the  message format
     *                          {@link String}.
     */
    protected void print(Diagnostic.Kind kind,
                         Element element, String format, Object... argv) {
        processingEnv.getMessager()
            .printMessage(kind, String.format(format, argv), element);
    }

    /**
     * Method to print a {@link Throwable}.
     *
     * @param   kind            The {@link javax.tools.Diagnostic.Kind}.
     * @param   element         The offending {@link Element}.
     * @param   throwable       The {@link Throwable}.
     */
    protected void print(Diagnostic.Kind kind,
                         Element element, Throwable throwable) {
        print(kind, element, new ThrowableDataSource(throwable).toString());
    }

    /**
     * Method to get a {@link TypeElement} for a {@link Class}.
     *
     * @param   type            The {@link Class}.
     *
     * @return  The {@link TypeElement} for the {@link Class}.
     */
    protected TypeElement asTypeElement(Class<?> type) {
        TypeElement element = null;

        try {
            element = elements.getTypeElement(type.getCanonicalName());
        } catch (Exception exception) {
            throw new IllegalArgumentException("type=" + String.valueOf(type),
                                               exception);
        }

        return element;
    }

    /**
     * Method to get an {@link ExecutableElement} for a {@link Class}
     * {@link Method}.
     *
     * @param   type            The {@link Class}.
     * @param   name            The {@link Method} name.
     * @param   parameters      The {@link Method} parameter types.
     *
     * @return  The {@link ExecutableElement} for the {@link Method}.
     */
    protected ExecutableElement asExecutableElement(Class<?> type,
                                                    String name,
                                                    Class<?>... parameters) {
        return asExecutableElement(asTypeElement(type), name, parameters);
    }

    /**
     * Method to get an {@link ExecutableElement} for a {@link Class}
     * {@link Method}.
     *
     * @param   type            The {@link TypeElement}.
     * @param   name            The {@link Method} name.
     * @param   parameters      The {@link Method} parameter types.
     *
     * @return  The {@link ExecutableElement} for the {@link Method}.
     */
    protected ExecutableElement asExecutableElement(TypeElement type,
                                                    String name,
                                                    Class<?>... parameters) {
        ExecutableElement element =
            methodsIn(type.getEnclosedElements())
            .stream()
            .filter(t -> t.getSimpleName().contentEquals(name))
            .filter(t -> areAssignable(t.getParameters(), parameters))
            .findFirst().orElse(null);

        return element;
    }

    private boolean areAssignable(List<? extends Element> from,
                                  Class<?>[] to) {
        boolean areAssignable = (from.size() == to.length);

        if (areAssignable) {
            for (int i = 0; i < to.length; i += 1) {
                areAssignable &=
                    isAssignable(types.erasure(from.get(i).asType()), to[i]);

                if (! areAssignable) {
                    break;
                }
            }
        }

        return areAssignable;
    }

    /**
     * Method to get a {@link TypeMirror} for a {@link Class}.
     *
     * @param   type            The {@link Class}.
     *
     * @return  The {@link TypeMirror} for the {@link Class}.
     */
    protected TypeMirror asTypeMirror(Class<?> type) {
        TypeMirror mirror = null;

        try {
            if (type.isArray()) {
                TypeMirror component = asTypeMirror(type.getComponentType());

                mirror = types.getArrayType(component);
            } else if (type.isPrimitive()) {
                TypeKind kind = TypeKind.valueOf(type.getName().toUpperCase());

                mirror =
                    kind.isPrimitive()
                        ? types.getPrimitiveType(kind)
                        : types.getNoType(kind);
            } else {
                mirror = asTypeElement(type).asType();
            }
        } catch (Exception exception) {
            throw new IllegalArgumentException("type=" + String.valueOf(type),
                                               exception);
        }

        return mirror;
    }

    /**
     * See {@link Types#isAssignable(TypeMirror,TypeMirror)}.
     *
     * @param   from            The left-hand side of the assignment.
     * @param   to              The right-hand side of the assignment.
     *
     * @return  {@code true} if {@code from} can be assigned to {@code to};
     *          {@code false} otherwise.
     */
    protected boolean isAssignable(TypeMirror from, Class<?> to) {
        boolean isAssignable = true;

        if (from instanceof ArrayType && to.isArray()) {
            isAssignable &=
                isAssignable(((ArrayType) from).getComponentType(),
                             to.getComponentType());
        } else if (from instanceof PrimitiveType && to.isPrimitive()) {
            isAssignable &= from.toString().equals(to.getName());
        } else {
            isAssignable &=
                types.isAssignable(from, asTypeMirror(to));
        }

        return isAssignable;
    }

    /**
     * See {@link Types#isSameType(TypeMirror,TypeMirror)}.
     *
     * @param   from            The left-hand side of the type test.
     * @param   to              The right-hand side of the type test.
     *
     * @return  {@code true} if {@code from} represents the same type as
     *                          {@code to}; {@code false} otherwise.
     */
    protected boolean isSameType(TypeMirror from, Class<?> to) {
        boolean isSameType = true;

        if (from instanceof ArrayType && to.isArray()) {
            isSameType &=
                isSameType(((ArrayType) from).getComponentType(),
                           to.getComponentType());
        } else if (from instanceof PrimitiveType && to.isPrimitive()) {
            isSameType &= from.toString().equals(to.getName());
        } else {
            isSameType &= types.isSameType(from, asTypeMirror(to));
        }

        return isSameType;
    }

    /**
     * Method to get an {@link Annotation}'s {@link Target}
     * {@link ElementType}s.
     *
     * @param   annotation      The {@link Annotation}.
     *
     * @return  The array of {@link Target} {@link ElementType}s or
     *          {@code null} if the argument is {@code null} or if none
     *          specified.
     */
    protected ElementType[] getTargetElementTypesFor(Annotation annotation) {
        return ((annotation != null)
                    ? getTargetElementTypesFor(annotation.annotationType())
                    : null);
    }

    /**
     * Method to get an {@link Annotation}'s {@link Target}
     * {@link ElementType}s.
     *
     * @param   type            The {@link Annotation#annotationType()}.
     *
     * @return  The array of {@link Target} {@link ElementType}s or
     *          {@code null} if the argument is {@code null} or if none
     *          specified.
     */
    protected ElementType[] getTargetElementTypesFor(Class<? extends Annotation> type) {
        Target target =
            (type != null) ? type.getAnnotation(Target.class) : null;

        return (target != null) ? target.value() : null;
    }

    /**
     * Method to determine if an {@link Element} is a {@link TypeElement}
     * that has a public no-argument constructor.
     *
     * @param   element         The {@link Element}.
     *
     * @return  {@code true} if the {@link Element} has a public no-argument
     *          constructor; {@code false} otherwise.
     */
    protected boolean hasPublicNoArgumentConstructor(Element element) {
        return constructorsIn(element.getEnclosedElements())
               .stream()
               .anyMatch(t -> (t.getModifiers().contains(PUBLIC)
                               && t.getParameters().isEmpty()));
    }

    /**
     * Method to return the {@link ExecutableElement} ({@link Method}) the
     * argument {@link ExecutableElement} overrides (if any).
     *
     * @param   overrider       The {@link ExecutableElement}.
     *
     * @return  The overridden {@link ExecutableElement} if any;
     *          {@code null} otherwise.
     *
     * @see Elements#overrides(ExecutableElement,ExecutableElement,TypeElement)
     */
    protected ExecutableElement overrides(ExecutableElement overrider) {
        TypeElement type = (TypeElement) overrider.getEnclosingElement();
        ExecutableElement element =
            types.directSupertypes(type.asType())
            .stream()
            .map(t -> overrides(overrider, types.asElement(t)))
            .filter(Objects::nonNull)
            .findFirst().orElse(null);

        return element;
    }

    private ExecutableElement overrides(ExecutableElement overrider,
                                        Element type) {
        ExecutableElement overridden = null;

        if (type != null) {
            switch (type.getKind()) {
            case CLASS:
            case INTERFACE:
                overridden = overridden(overrider, (TypeElement) type);
                break;

            default:
                break;
            }
        }

        return overridden;
    }

    private ExecutableElement overridden(ExecutableElement overrider,
                                         TypeElement type) {
        ExecutableElement element =
            methodsIn(type.getEnclosedElements())
            .stream()
            .filter(t -> disjoint(t.getModifiers(),
                                  Arrays.asList(PRIVATE, STATIC)))
            .filter(t -> elements.overrides(overrider, t, type))
            .findFirst().orElse(null);

        if (element == null) {
            element =
                overrides(overrider, types.asElement(type.getSuperclass()));
        }

        return element;
    }

    /**
     * Method to determine if a {@link ExecutableElement} ({@link Method})
     * overrides another {@link ExecutableElement}.
     *
     * @param   overrider       The (possibly) overriding
     *                          {@link ExecutableElement}.
     * @param   overridden      The overridden {@link ExecutableElement}.
     *
     * @return  {@code true} if {@code overrider} overrides
     *          {@code overridden}; {@code false} otherwise.
     *
     * @see Elements#overrides(ExecutableElement,ExecutableElement,TypeElement)
     */
    protected boolean overrides(ExecutableElement overrider,
                                ExecutableElement overridden) {
        TypeElement type = (TypeElement) overridden.getEnclosingElement();

        return elements.overrides(overrider, overridden, type);
    }

    /**
     * Method to return the {@link ExecutableElement} ({@link Method}) the
     * argument {@link ExecutableElement} is overriden by (if any).
     *
     * @param   overridden      The {@link ExecutableElement}.
     * @param   type            The {@link TypeElement}.
     *
     * @return  The overriding {@link ExecutableElement} if any;
     *          {@code null} otherwise.
     *
     * @see #overrides(ExecutableElement)
     */
    protected ExecutableElement implementationOf(ExecutableElement overridden,
                                                 TypeElement type) {
        ExecutableElement element = null;

        if (type != null) {
            element =
                methodsIn(type.getEnclosedElements())
                .stream()
                .filter(t -> overrides(t, overridden))
                .findFirst().orElse(null);

            if (element == null) {
                element =
                    Optional.ofNullable(type.getSuperclass())
                    .map(t -> (TypeElement) types.asElement(t))
                    .filter(Objects::nonNull)
                    .map(t -> implementationOf(overridden, t))
                    .orElse(null);
            }
        }

        return element;
    }

    /**
     * Method to return the {@link ExecutableElement} ({@link Method}) the
     * argument {@link ExecutableElement} is specified by (if any).
     *
     * @param   method          The {@link ExecutableElement}.
     *
     * @return  The specification {@link ExecutableElement} if any;
     *          {@code null} otherwise.
     *
     * @see #overrides(ExecutableElement)
     */
    protected ExecutableElement specifiedBy(ExecutableElement method) {
        ExecutableElement specification = overrides(method);

        if (specification != null) {
            for (;;) {
                ExecutableElement overridden = overrides(specification);

                if (overridden != null) {
                    specification = overridden;
                } else {
                    break;
                }
            }
        }

        return specification;
    }

    /**
     * Method to get an {@link Element}'s {@link AnnotationMirror}.
     *
     * @param   element         The annotated {@link Element}.
     * @param   type            The {@link Annotation} type ({@link Class}).
     *
     * @return  The {@link AnnotationMirror} if the {@link Element} is
     *          annotated with the argument annotation; {@code null}
     *          otherwise.
     *
     * @see Element#getAnnotationMirrors()
     */
    protected AnnotationMirror getAnnotationMirror(Element element,
                                                   Class<? extends Annotation> type) {
        return getAnnotationMirror(element, type.getName());
    }

    /**
     * Method to get an {@link Element}'s {@link AnnotationMirror}.
     *
     * @param   element         The annotated {@link Element}.
     * @param   type            The {@link Annotation} type
     *                          ({@link TypeElement}).
     *
     * @return  The {@link AnnotationMirror} if the {@link Element} is
     *          annotated with the argument annotation; {@code null}
     *          otherwise.
     *
     * @see Element#getAnnotationMirrors()
     */
    protected AnnotationMirror getAnnotationMirror(Element element,
                                                   TypeElement type) {
        return getAnnotationMirror(element,
                                   type.getQualifiedName().toString());
    }

    private AnnotationMirror getAnnotationMirror(Element element,
                                                 String name) {
        AnnotationMirror mirror =
            element.getAnnotationMirrors()
            .stream()
            .filter(t -> t.getAnnotationType().toString().equals(name))
            .map(t -> (AnnotationMirror) t)
            .findFirst().orElse(null);

        return mirror;
    }

    /**
     * Method to get bean property name from an {@link ExecutableElement}.
     *
     * @param   element         The {@link ExecutableElement}.
     *
     * @return  the name {@link String} if the {@link ExecutableElement}
     *          is a getter or setter method; {@code null} otherwise.
     */
    protected String getPropertyName(ExecutableElement element) {
        String string =
            Arrays.stream(PropertyMethodEnum.values())
            .filter(t -> t.getPropertyName(element.getSimpleName().toString()) != null)
            .filter(t -> isAssignable(element.getReturnType(),
                                      t.getReturnType()))
            .filter(t -> areAssignable(element.getParameters(),
                                       t.getParameterTypes()))
            .map(t -> t.getPropertyName(element.getSimpleName().toString()))
            .findFirst().orElse(null);

        return string;
    }

    /**
     * Method to determine if an {@link ExecutableElement} is a bean getter.
     *
     * @param   element         The {@link ExecutableElement}.
     *
     * @return  {@code true} if the {@link Element} has a non-private getter
     *          method; {@code false} otherwise.
     */
    protected boolean isGetterMethod(ExecutableElement element) {
        Optional <PropertyMethodEnum> optional =
            Arrays.asList(PropertyMethodEnum.GET, PropertyMethodEnum.IS)
            .stream()
            .filter(t -> (! element.getModifiers().contains(PRIVATE)))
            .filter(t -> t.getPropertyName(element.getSimpleName().toString()) != null)
            .filter(t -> isAssignable(element.getReturnType(),
                                      t.getReturnType()))
            .filter(t -> areAssignable(element.getParameters(),
                                       t.getParameterTypes()))
            .findFirst();

        return optional.isPresent();
    }

    /**
     * Method to get the {@link Set} of bean property names for the
     * specified {@link TypeElement}.
     *
     * @param   type            The {@link TypeElement} to analyze.
     *
     * @return  The {@link Set} of bean property names.
     */
    protected Set<String> getPropertyNames(TypeElement type) {
        return getPropertyNames(new TreeSet<>(), type);
    }

    private Set<String> getPropertyNames(Set<String> set, TypeElement type) {
        for (ExecutableElement element :
                 methodsIn(type.getEnclosedElements())) {
            if (! element.getModifiers().contains(PRIVATE)) {
                Arrays.stream(PropertyMethodEnum.values())
                    .filter(t -> t.getPropertyName(element.getSimpleName().toString()) != null)
                    .filter(t -> isAssignable(element.getReturnType(),
                                              t.getReturnType()))
                    .filter(t -> areAssignable(element.getParameters(),
                                               t.getParameterTypes()))
                    .map(t -> t.getPropertyName(element.getSimpleName().toString()))
                    .forEach(t -> set.add(t));
            }
        }

        Element superclass = types.asElement(type.getSuperclass());

        if (superclass != null)
            switch (superclass.getKind()) {
            case CLASS:
                getPropertyNames(set, (TypeElement) superclass);
                break;

            default:
                break;
        }

        return set;
    }

    /**
     * Method to get the argument {@link TypeElement} name as a path.
     *
     * @param   element         The {@link TypeElement}.
     *
     * @return  The {@link TypeElement} name as a path.
     *
     * @see Elements#getBinaryName(TypeElement)
     */
    protected String asPath(TypeElement element) {
        return asPath(elements.getBinaryName(element).toString());
    }

    /**
     * Method to get the argument {@link Class} name as a path.
     *
     * @param   type            The {@link Class}.
     *
     * @return  The {@link Class} name as a path.
     */
    protected String asPath(Class<?> type) { return asPath(type.getName()); }

    /**
     * Method to get the argument {@link PackageElement} name as a path
     * (including the trailing {@code /}).
     *
     * @param   element         The {@link PackageElement}.
     *
     * @return  The {@link PackageElement} name as a path.
     */
    protected String asPath(PackageElement element) {
        return asPath(element.getQualifiedName().toString()) + "/";
    }

    /**
     * Static method to get the argument {@link Package} name as a path
     * (including the trailing {@code /}).
     *
     * @param   pkg             The {@link Package}.
     *
     * @return  The {@link Package} name as a path.
     */
    protected static String asPath(Package pkg) {
        return asPath(pkg.getName()) + "/";
    }

    /**
     * Static method to get the argument name as a path.
     *
     * @param   name            The name {@link String}.
     *
     * @return  The argument {@link String} as a path.
     */
    protected static String asPath(String name) {
        return name.replaceAll(Pattern.quote("."), "/");
    }

    /**
     * Method to translate a {@link FileObject} to a {@link Path}.
     *
     * @param   file            The {@link FileObject}.
     *
     * @return  The corresponding {@link Path}.
     */
    protected static Path toPath(FileObject file) {
        return Paths.get(file.toUri());
    }

    /**
     * Method to get the {@link List} of {@link TypeElement}s from an
     * {@link AnnotationValue}.
     *
     * @param   value           The {@link AnnotationValue}.
     *
     * @return  The {@link List}.
     */
    protected List<TypeElement> getTypeElementListFrom(AnnotationValue value) {
        List<TypeElement> list =
            Stream.of(value)
            .filter(Objects::nonNull)
            .map(t -> (List<?>) t.getValue())
            .flatMap(List::stream)
            .map(t -> (AnnotationValue) t)
            .map(t -> (TypeMirror) t.getValue())
            .map(t -> (TypeElement) types.asElement(t))
            .collect(toList());

        return list;
    }
}
