package ball.annotation.processing;
/*-
 * ##########################################################################
 * Utilities
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2008 - 2021 Allen D. Ball
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
import ball.beans.PropertyMethodEnum;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileManager;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static java.util.Collections.disjoint;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static javax.lang.model.element.ElementKind.CONSTRUCTOR;
import static javax.lang.model.element.ElementKind.METHOD;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.lang.model.util.ElementFilter.constructorsIn;
import static javax.lang.model.util.ElementFilter.fieldsIn;
import static javax.lang.model.util.ElementFilter.methodsIn;
import static javax.tools.StandardLocation.CLASS_PATH;
import static lombok.AccessLevel.PROTECTED;

/**
 * Utility methods for {@link javax.annotation.processing.Processor} and
 * {@code Taglet} implementations.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@NoArgsConstructor(access = PROTECTED) @ToString
public abstract class JavaxLangModelUtilities {
    /** See {@link javax.annotation.processing.ProcessingEnvironment#getElementUtils()}. */
    protected Elements elements = null;
    /** See {@link javax.annotation.processing.ProcessingEnvironment#getTypeUtils()}. */
    protected Types types = null;
    /** {@link com.sun.source.util.JavacTask} {@link JavaFileManager} instance. */
    protected JavaFileManager fm = null;
    private transient ClassLoader loader = null;

    /**
     * Method to get the {@link ClassLoader} for loading dependencies.
     *
     * @return  The {@link ClassLoader}.
     */
    protected ClassLoader getClassLoader() {
        if (loader == null) {
            loader = getClassPathClassLoader(fm, getClass().getClassLoader());
        }

        return loader;
    }

    /**
     * Method to get the {@link Class} corresponding to a
     * {@link TypeElement}.
     *
     * @param   element         The {@link TypeElement}.
     *
     * @return  The {@link Class} for the {@link TypeElement}.
     */
    protected Class<?> asClass(TypeElement element) {
        Class<?> type = null;
        Name name = elements.getBinaryName(element);

        if (name == null) {
            name = element.getQualifiedName();
        }

        try {
            type = getClassLoader().loadClass(name.toString());
        } catch (Exception exception) {
            throw new IllegalArgumentException("type=" + name, exception);
        }

        return type;
    }

    /**
     * Method to get the {@code package-info.class} ({@link Class})
     * corresponding to a {@link PackageElement}.
     *
     * @param   element         The {@link PackageElement}.
     *
     * @return  The {@link Class} for the {@link PackageElement}
     *          {@code package-info.class}.
     */
    protected Class<?> asPackageInfoClass(PackageElement element) {
        Class<?> type = null;
        String name =
            element.getQualifiedName().toString() + ".package-info";

        try {
            type = getClassLoader().loadClass(name);
        } catch (Exception exception) {
            throw new IllegalArgumentException("type=" + name, exception);
        }

        return type;
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
            throw new IllegalArgumentException("type=" + type, exception);
        }

        return element;
    }

    /**
     * Method to get a {@link ExecutableElement} for a {@link Constructor}.
     *
     * @param   constructor      The {@link Constructor}.
     *
     * @return  The {@link ExecutableElement} for the {@link Constructor}.
     */
    protected ExecutableElement asExecutableElement(Constructor<?> constructor) {
        TypeElement type = asTypeElement(constructor.getDeclaringClass());
        Element element =
            constructorsIn(type.getEnclosedElements()).stream()
            .filter(hasSameSignatureAs(constructor))
            .findFirst().orElse(null);

        return (ExecutableElement) element;
    }

    /**
     * Method to get a {@link ExecutableElement} for a {@link Method}.
     *
     * @param   method          The {@link Method}.
     *
     * @return  The {@link ExecutableElement} for the {@link Method}.
     */
    protected ExecutableElement asExecutableElement(Method method) {
        return getMethod(asTypeElement(method.getDeclaringClass()), method);
    }

    @Deprecated
    protected ExecutableElement getMethod(Method method) {
        return asExecutableElement(method);
    }

    /**
     * Method to get a {@link VariableElement} for a {@link Field}.
     *
     * @param   field           The {@link Field}.
     *
     * @return  The {@link VariableElement} for the {@link Field}.
     */
    protected VariableElement asVariableElement(Field field) {
        TypeElement type = asTypeElement(field.getDeclaringClass());
        Element element =
            fieldsIn(type.getEnclosedElements())
            .stream()
            .filter(t -> t.getSimpleName().contentEquals(field.getName()))
            .findFirst().orElse(null);

        return (VariableElement) element;
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

        if (type.isArray()) {
            mirror = types.getArrayType(asTypeMirror(type.getComponentType()));
        } else if (type.isPrimitive()) {
            mirror = asTypeMirror(TypeKind.valueOf(type.getName().toUpperCase()));
        } else {
            mirror = asTypeElement(type).asType();
        }

        return mirror;
    }

    private TypeMirror asTypeMirror(TypeKind type) {
        return type.isPrimitive() ? types.getPrimitiveType(type) : types.getNoType(type);
    }

    /**
     * Method to get a {@link List} of {@link TypeMirror}s for an array of
     * {@link Class}es.
     *
     * @param   types           The array of {@link Class}es.
     *
     * @return  The {@link List} of {@link TypeMirror}s.
     */
    protected List<TypeMirror> asTypeMirrorList(Class<?>... types) {
        return Stream.of(types).map(t -> asTypeMirror(t)).collect(toList());
    }

    /**
     * Method to get the enclosing {@link TypeElement} for an
     * {@link Element}.
     *
     * @param   element         The {@link Element}.
     *
     * @return  The enclosing {@link TypeElement}.
     */
    protected TypeElement getEnclosingTypeElement(Element element) {
        while (element != null) {
            if (element instanceof TypeElement) {
                break;
            }

            element = element.getEnclosingElement();
        }

        return (TypeElement) element;
    }

    /**
     * Method to get the {@link TypeElement} for a context {@link Element}.
     *
     * @param   context         The context {@link Element}.
     * @param   name            The name of the {@link Element}
     *                          ({@link Class}).
     *
     * @return  The context's {@link TypeElement}.
     */
    protected TypeElement getTypeElementFor(Element context, String name) {
        if (! name.contains(".")) {
            name =
                elements.getPackageOf(context).getQualifiedName() + "." + name;
        }

        return elements.getTypeElement(name);
    }

    /**
     * Constructor to get an {@link ExecutableElement} for a {@link Class}
     * {@link Constructor} by parameter list.
     *
     * @param   type            The {@link TypeElement}.
     * @param   parameters      The constructor parameter types.
     *
     * @return  The {@link ExecutableElement} for the constructor.
     */
    protected ExecutableElement getConstructor(TypeElement type,
                                               List<TypeMirror> parameters) {
        Element element =
            constructorsIn(type.getEnclosedElements())
            .stream()
            .filter(hasSameSignatureAs(parameters))
            .findFirst().orElse(null);

        return (ExecutableElement) element;
    }

    /**
     * Method to get an {@link ExecutableElement} for a {@link Method}
     * prototype.
     *
     * @param   type            The {@link TypeElement}.
     * @param   method          The prototype {@link Method}.
     *
     * @return  The {@link ExecutableElement} for the method.
     */
    protected ExecutableElement getMethod(TypeElement type, Method method) {
        Element element =
            methodsIn(type.getEnclosedElements())
            .stream()
            .filter(hasSameSignatureAs(method))
            .findFirst().orElse(null);

        return (ExecutableElement) element;
    }

    /**
     * Method to return the {@link ExecutableElement}
     * ({@link java.lang.reflect.Method}) the argument
     * {@link ExecutableElement} overrides (if any).
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
            .filter(withoutModifiers(PRIVATE, STATIC))
            .filter(t -> elements.overrides(overrider, t, type))
            .findFirst().orElse(null);

        if (element == null) {
            element =
                overrides(overrider, types.asElement(type.getSuperclass()));
        }

        return element;
    }

    /**
     * Method to determine if a {@link ExecutableElement}
     * ({@link java.lang.reflect.Method}) overrides another
     * {@link ExecutableElement}.
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
     * Method to return the {@link ExecutableElement}
     * ({@link java.lang.reflect.Method}) the argument
     * {@link ExecutableElement} is overriden by (if any).
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
     * Method to return the {@link ExecutableElement}
     * ({@link java.lang.reflect.Method}) the argument
     * {@link ExecutableElement} is specified by (if any).
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
     * Method to generate the application signature of an
     * {@link Executable}.
     *
     * @param   executable      The {@link Executable}.
     *
     * @return  The signature {@link String}.
     */
    protected String signature(Executable executable) {
        String signature =
            Stream.of(executable.getParameterTypes())
            .map(Class::getCanonicalName)
            .collect(joining(",", "(", ")"));

        return signature;
    }

    /**
     * Method to generate the application signature of an
     * {@link ExecutableElement}.
     *
     * @param   element         The {@link ExecutableElement}.
     *
     * @return  The signature {@link String}.
     */
    protected String signature(ExecutableElement element) {
        String signature =
            element.getParameters().stream()
            .map(VariableElement::asType)
            .map(Object::toString)
            .collect(joining(",", "(", ")"));

        return signature;
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
     * Method to get an {@link AnnotationMirror} element's
     * {@link AnnotationValue}.
     *
     * @param   annotation      The {@link AnnotationMirror}.
     * @param   name            The simple name of the element.
     *
     * @return  The {@link AnnotationValue} if it is defined; {@code null}
     *          otherwise.
     *
     * @see Elements#getElementValuesWithDefaults(AnnotationMirror)
     */
    protected AnnotationValue getAnnotationValue(AnnotationMirror annotation, String name) {
        AnnotationValue value =
            elements.getElementValuesWithDefaults(annotation).entrySet()
            .stream()
            .filter(t -> named(name).test(t.getKey()))
            .map(t -> t.getValue())
            .findFirst().orElse(null);

        return value;
    }

    /**
     * Method to determine if an {@link AnnotationValue} is "empty":
     * {@code null} or an empty array.
     *
     * @param   value           The {@link AnnotationValue}.
     *
     * @return  {@code true} if empty; {code false} otherwise.
     */
    protected boolean isEmptyArray(AnnotationValue value) {
        List<?> list = (List<?>) ((value != null) ? value.getValue() : null);

        return (list == null || list.isEmpty());
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
            Stream.of(PropertyMethodEnum.values())
            .filter(t -> t.getPropertyName(element.getSimpleName().toString()) != null)
            .filter(t -> isAssignableTo(t.getReturnType(),
                                        e -> ((ExecutableElement) e).getReturnType()).test(element))
            .filter(t -> withParameters(t.getParameterTypes()).test(element))
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
            Stream.of(PropertyMethodEnum.GET, PropertyMethodEnum.IS)
            .filter(t -> t.getPropertyName(element.getSimpleName().toString()) != null)
            .filter(t -> withoutModifiers(PRIVATE).test(element))
            .filter(t -> isAssignableTo(t.getReturnType(),
                                        e -> ((ExecutableElement) e).getReturnType()).test(element))
            .filter(t -> withParameters(t.getParameterTypes()).test(element))
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
            if (withoutModifiers(PRIVATE).test(element)) {
                Stream.of(PropertyMethodEnum.values())
                    .filter(t -> t.getPropertyName(element.getSimpleName().toString()) != null)
                    .filter(t -> isAssignableTo(t.getReturnType(),
                                                e -> ((ExecutableElement) e).getReturnType()).test(element))
                    .filter(t -> withParameters(t.getParameterTypes()).test(element))
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
    /*
     * Element Predicate Calculus
     */
    protected <E extends Enum<E>> EnumSet<E> toEnumSet(E[] array) {
        return EnumSet.copyOf(Arrays.asList(array));
    }

    private <E extends Enum<E>> Predicate<Element> is(E e, Function<? super Element,E> extractor) {
        return t -> e.equals(extractor.apply(t));
    }

    protected Predicate<Element> hasSameSignatureAs(List<TypeMirror> parameters) {
        return is(CONSTRUCTOR, Element::getKind).and(withParameters(parameters));
    }

    protected Predicate<Element> hasSameSignatureAs(Executable executable) {
        return hasSameSignatureAs(executable.getName(),
                                  executable.getParameterTypes());
    }

    protected Predicate<Element> hasSameSignatureAs(CharSequence name,
                                                    Class<?>[] parameters) {
        return is(METHOD, Element::getKind).and(named(name).and(withParameters(parameters)));
    }

    protected Predicate<Element> isAssignableTo(Class<?> type) {
        return isAssignableTo(type, t -> t.asType());
    }

    protected Predicate<Element> isAssignableTo(TypeMirror type) {
        return isAssignableTo(type, t -> t.asType());
    }

    protected Predicate<Element> isAssignableTo(Class<?> type,
                                                Function<? super Element,TypeMirror> extractor) {
        return isAssignableTo(asTypeMirror(type), extractor);
    }

    protected Predicate<Element> isAssignableTo(TypeMirror type,
                                                Function<? super Element,TypeMirror> extractor) {
        return t -> types.isAssignable(extractor.apply(t), type);
    }

    protected Predicate<Element> named(CharSequence name) {
        return t -> t.getSimpleName().contentEquals(name);
    }

    protected Predicate<Element> withParameters(Class<?>[] parameters) {
        return withParameters(asTypeMirrorList(parameters));
    }

    protected Predicate<Element> withParameters(List<TypeMirror> parameters) {
        return new Predicate<Element>() {
            @Override
            public boolean test(Element element) {
                boolean match =
                    parameters.size() == ((ExecutableElement) element).getParameters().size();

                if (match) {
                    match &=
                        IntStream.range(0, parameters.size())
                        .allMatch(i -> isAssignableTo(parameters.get(i),
                                                      t -> types.erasure(((ExecutableElement) t)
                                                                         .getParameters().get(i).asType()))
                                       .test(element));
                }

                return match;
            }
        };
    }

    protected Predicate<Element> withModifiers(Modifier... modifiers) {
        return withModifiers(toEnumSet(modifiers));
    }

    protected Predicate<Element> withModifiers(Set<Modifier> modifiers) {
        return with(modifiers, t -> t.getModifiers());
    }

    protected Predicate<Element> withoutModifiers(Modifier... modifiers) {
        return withoutModifiers(toEnumSet(modifiers));
    }

    protected Predicate<Element> withoutModifiers(Set<Modifier> modifiers) {
        return without(modifiers, t -> t.getModifiers());
    }

    protected <E> Predicate<Element> with(Set<E> set,
                                          Function<Element,Collection<E>> extractor) {
        return t -> extractor.apply(t).containsAll(set);
    }

    protected <E> Predicate<Element> without(Set<E> set,
                                             Function<Element,Collection<E>> extractor) {
        return t -> disjoint(set, extractor.apply(t));
    }

    /**
     * See {@link JavaFileManager#getClassLoader(javax.tools.JavaFileManager.Location) JavaFileManager.getClassLoader(CLASS_PATH)}.
     *
     * @param   fm              The {@link JavaFileManager}.
     * @param   parent          The parent {@link ClassLoader}.
     *
     * @return  The {@link ClassLoader}.
     */
    protected ClassLoader getClassPathClassLoader(JavaFileManager fm,
                                                  ClassLoader parent) {
        ClassLoader loader = parent;

        if (fm != null) {
            loader = fm.getClassLoader(CLASS_PATH);

            if (loader instanceof URLClassLoader) {
                loader =
                    URLClassLoader
                    .newInstance(((URLClassLoader) loader).getURLs(), parent);
            }
        }

        return loader;
    }

    /**
     * See {@link JavaFileManager#getClassLoader(javax.tools.JavaFileManager.Location) JavaFileManager.getClassLoader(CLASS_PATH)}.
     *
     * @param   fm              The {@link JavaFileManager}.
     *
     * @return  The {@link ClassLoader}.
     */
    protected ClassLoader getClassPathClassLoader(JavaFileManager fm) {
        return getClassPathClassLoader(fm, getClass().getClassLoader());
    }
}
