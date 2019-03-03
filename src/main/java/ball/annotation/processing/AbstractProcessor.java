/*
 * $Id$
 *
 * Copyright 2012 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.annotation.processing;

import ball.activation.ThrowableDataSource;
import ball.beans.PropertyMethodEnum;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import lombok.NoArgsConstructor;

import static ball.lang.Keyword.THROWS;
import static ball.lang.Punctuation.COMMA;
import static ball.lang.Punctuation.LP;
import static ball.lang.Punctuation.RP;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.disjoint;
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
@NoArgsConstructor(access = PROTECTED)
public abstract class AbstractProcessor
                      extends javax.annotation.processing.AbstractProcessor {
    /** {@link #META_INF} = {@value #META_INF} */
    protected static final String META_INF = "META-INF";

    /** {@link #AT} = {@value #AT} */
    protected static final String AT = "@";
    /** {@link #COLON} = {@value #COLON} */
    protected static final String COLON = ":";
    /** {@link #DOT} = {@value #DOT} */
    protected static final String DOT = ".";
    /** {@link #SLASH} = {@value #SLASH} */
    protected static final String SLASH = "/";
    /** {@link #SPACE} = {@value #SPACE} */
    protected static final String SPACE = " ";

    /** UTF-8 */
    protected static final Charset CHARSET = UTF_8;

    /** See {@link ProcessingEnvironment#getFiler()}. */
    protected Filer filer = null;
    /** See {@link ProcessingEnvironment#getElementUtils()}. */
    protected Elements elements = null;
    /** See {@link ProcessingEnvironment#getTypeUtils()}. */
    protected Types types = null;

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
     * @param   message         The message {@link CharSequence}.
     */
    protected void print(Diagnostic.Kind kind,
                         Element element, CharSequence message) {
        processingEnv.getMessager().printMessage(kind, message, element);
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
        Optional<ExecutableElement> optional =
            constructorsIn(element.getEnclosedElements())
            .stream()
            .filter(t -> t.getModifiers().contains(PUBLIC))
            .filter(t -> t.getParameters().isEmpty())
            .findFirst();

        return optional.isPresent();
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
        Optional<ExecutableElement> optional =
            types.directSupertypes(type.asType())
            .stream()
            .map(t -> overrides(overrider, types.asElement(t)))
            .filter(Objects::nonNull)
            .findFirst();

        return optional.orElse(null);
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
        Optional<ExecutableElement> optional =
            methodsIn(type.getEnclosedElements())
            .stream()
            .filter(t -> disjoint(t.getModifiers(),
                                  Arrays.asList(PRIVATE, STATIC)))
            .filter(t -> elements.overrides(overrider, t, type))
            .findFirst();

        if (! optional.isPresent()) {
            optional =
                Optional.ofNullable(overrides(overrider,
                                              types.asElement(type.getSuperclass())));
        }

        return optional.orElse(null);
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
        Optional<ExecutableElement> optional = Optional.empty();

        if (type != null) {
            optional =
                methodsIn(type.getEnclosedElements())
                .stream()
                .filter(t -> overrides(t, overridden))
                .findFirst();

            if (! optional.isPresent()) {
                optional =
                    Optional.ofNullable(type.getSuperclass())
                    .map(t -> (TypeElement) types.asElement(t))
                    .filter(Objects::nonNull)
                    .map(t -> implementationOf(overridden, t));
            }
        }

        return optional.orElse(null);
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
    protected boolean isAssignable(TypeMirror from, TypeMirror to) {
        return types.isAssignable(from, to);
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
            isAssignable &= isAssignable(from, getTypeMirrorFor(to));
        }

        return isAssignable;
    }

    /**
     * See {@link Types#isAssignable(TypeMirror,TypeMirror)}.
     *
     * @param   from            The parameter list (the left-hand side of
     *                          the assignment).
     * @param   to              The argument list (the right-hand side of
     *                          the assignment).
     *
     * @return  {@code true} if {@code from} can be assigned to {@code to};
     *          {@code false} otherwise.
     */
    protected boolean isAssignable(List<? extends Element> from,
                                   Class<?>[] to) {
        boolean isAssignable = (from.size() == to.length);

        if (isAssignable) {
            for (int i = 0; i < to.length; i += 1) {
                isAssignable &= isAssignable(from.get(i).asType(), to[i]);

                if (! isAssignable) {
                    break;
                }
            }
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
    protected boolean isSameType(TypeMirror from, TypeMirror to) {
        return types.isSameType(from, to);
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
            isSameType &= isSameType(from, getTypeMirrorFor(to));
        }

        return isSameType;
    }

    /**
     * See {@link Types#isSameType(TypeMirror,TypeMirror)}.
     *
     * @param   from            The parameter list (the left-hand side of
     *                          the type test).
     * @param   to              The argument list (the right-hand side of
     *                          the type test).
     *
     * @return  {@code true} if {@code from} represents the same types as
     *                          {@code to}; {@code false} otherwise.
     */
    protected boolean isSameType(List<? extends Element> from, Class<?>[] to) {
        boolean isSameType = (from.size() == to.length);

        if (isSameType) {
            for (int i = 0; i < to.length; i += 1) {
                isSameType &= isSameType(from.get(i).asType(), to[i]);

                if (! isSameType) {
                    break;
                }
            }
        }

        return isSameType;
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
     * Method to get an {@link ExecutableElement} for a {@link Class}
     * {@link Method}.
     *
     * @param   type            The {@link Class}.
     * @param   name            The {@link Method} name.
     * @param   parameters      The {@link Method} parameter types.
     *
     * @return  The {@link ExecutableElement} for the {@link Method}.
     *
     * @throws  NoSuchMethodException
     *                          If the named {@link Method} does not exist.
     */
    protected ExecutableElement getExecutableElementFor(Class<?> type,
                                                        String name,
                                                        Class<?>... parameters)
                                        throws NoSuchMethodException {
        return getExecutableElementFor(type.getDeclaredMethod(name,
                                                              parameters));
    }

    /**
     * Method to get an {@link ExecutableElement} for a {@link Method}.
     *
     * @param   method          The {@link Method}.
     *
     * @return  The {@link ExecutableElement} for the {@link Method}.
     */
    protected ExecutableElement getExecutableElementFor(Method method) {
        TypeElement type = getTypeElementFor(method.getDeclaringClass());

        return getExecutableElementFor(type, method);
    }

    /**
     * Method to get an {@link ExecutableElement} for a {@link Method} if an
     * equivalent is declared in the argument {@link TypeElement}.
     *
     * @param   type            The {@link TypeElement}.
     * @param   method          The {@link Method} prototype.
     *
     * @return  The {@link ExecutableElement} for the {@link Method}.
     */
    protected ExecutableElement getExecutableElementFor(TypeElement type,
                                                        Method method) {
        Optional<ExecutableElement> optional = Optional.empty();

        if (type != null) {
            optional =
                methodsIn(type.getEnclosedElements())
                .stream()
                .filter(t -> t.getSimpleName().contentEquals(method.getName()))
                .filter(t -> t.isVarArgs() == method.isVarArgs())
                .filter(t -> isSameType(t.getReturnType(),
                                        method.getReturnType()))
                .filter(t -> isSameType(t.getParameters(),
                                        method.getParameterTypes()))
                .findFirst();
        }

        return optional.orElse(null);
    }

    /**
     * Method to get a {@link PackageElement} for a {@link TypeElement}.
     *
     * @param   type            The {@link TypeElement}.
     *
     * @return  The {@link PackageElement} for the {@link TypeElement}.
     */
    protected PackageElement getPackageElementFor(TypeElement type) {
        return (type != null) ? elements.getPackageOf(type) : null;
    }

    /**
     * Method to get a {@link TypeElement} for a {@link Class}.
     *
     * @param   type            The {@link Class}.
     *
     * @return  The {@link TypeElement} for the {@link Class}.
     */
    protected TypeElement getTypeElementFor(Class<?> type) {
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
     * Method to get a {@link TypeMirror} for a {@link Class}.
     *
     * @param   type            The {@link Class}.
     *
     * @return  The {@link TypeMirror} for the {@link Class}.
     */
    protected TypeMirror getTypeMirrorFor(Class<?> type) {
        TypeMirror mirror = null;

        try {
            if (type.isArray()) {
                TypeMirror component =
                    getTypeMirrorFor(type.getComponentType());

                mirror = types.getArrayType(component);
            } else if (type.isPrimitive()) {
                TypeKind kind = TypeKind.valueOf(type.getName().toUpperCase());

                mirror =
                    kind.isPrimitive()
                        ? types.getPrimitiveType(kind)
                        : types.getNoType(kind);
            } else {
                mirror = getTypeElementFor(type).asType();
            }
        } catch (Exception exception) {
            throw new IllegalArgumentException("type=" + String.valueOf(type),
                                               exception);
        }

        return mirror;
    }

    /**
     * Method to get a {@link List} of {@link TypeMirror}s for an array
     * of {@link Class}es.
     *
     * @param   types           The {@link Class}es.
     *
     * @return  The {@link List} of {@link TypeMirror}s for the
     *          {@link Class}es.
     */
    protected List<TypeMirror> getTypeMirrorsFor(Class<?>... types) {
        return (Arrays.stream(types)
                .map(t -> getTypeMirrorFor(t))
                .collect(Collectors.toList()));
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
        Optional<String> optional =
            Arrays.stream(PropertyMethodEnum.values())
            .filter(t -> t.getPropertyName(element.getSimpleName().toString()) != null)
            .filter(t -> isAssignable(element.getReturnType(),
                                      t.getReturnType()))
            .filter(t -> isAssignable(element.getParameters(),
                                      t.getParameterTypes()))
            .map(t -> t.getPropertyName(element.getSimpleName().toString()))
            .findFirst();

        return optional.orElse(null);
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
            .filter(t -> isAssignable(element.getParameters(),
                                      t.getParameterTypes()))
            .findFirst();

        return optional.isPresent();
    }

    /**
     * Method to translate {@link Class} {@link java.lang.reflect.Modifier}
     * bits to an {@link EnumSet} of {@link Modifier}s.
     *
     * @param   modifiers       The {@code int} representing the modifiers.
     *
     * @return  The {@link EnumSet} of {@link Modifier}s.
     */
    protected EnumSet<Modifier> asModifierSet(int modifiers) {
        TreeMap<String,Modifier> map = new TreeMap<>();

        if (modifiers != 0) {
            for (Modifier modifier : Modifier.values()) {
                map.put(modifier.toString(), modifier);
            }

            String string = java.lang.reflect.Modifier.toString(modifiers);

            map.keySet()
                .retainAll(Arrays.asList(string.split("[\\p{Space}]+")));
        }

        return EnumSet.copyOf(map.values());
    }

    /**
     * See {@link #asModifierSet(int)}.
     *
     * @param   type            The {@link Class}.
     *
     * @return  The {@link EnumSet} of {@link Modifier}s.
     */
    protected EnumSet<Modifier> getModifierSetFor(Class<?> type) {
        return asModifierSet(type.getModifiers());
    }

    /**
     * See {@link #asModifierSet(int)}.
     *
     * @param   member          The {@link Member}.
     *
     * @return  The {@link EnumSet} of {@link Modifier}s.
     */
    protected EnumSet<Modifier> getModifierSetFor(Member member) {
        return asModifierSet(member.getModifiers());
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
        Optional<AnnotationMirror> optional =
            element.getAnnotationMirrors()
            .stream()
            .filter(t -> t.getAnnotationType().toString().equals(name))
            .map(t -> (AnnotationMirror) t)
            .findFirst();

        return optional.orElse(null);
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
            if (element.getModifiers().contains(PUBLIC)) {
                Arrays.stream(PropertyMethodEnum.values())
                    .filter(t -> t.getPropertyName(element.getSimpleName().toString()) != null)
                    .filter(t -> isAssignable(element.getReturnType(),
                                              t.getReturnType()))
                    .filter(t -> isAssignable(element.getParameters(),
                                              t.getParameterTypes()))
                    .map(t -> t.getPropertyName(element.getSimpleName().toString()))
                    .findFirst()
                    .ifPresent(t -> set.add(t));
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
     * (including the trailing {@value SLASH}).
     *
     * @param   element         The {@link PackageElement}.
     *
     * @return  The {@link PackageElement} name as a path.
     */
    protected String asPath(PackageElement element) {
        return asPath(element.getQualifiedName().toString()) + SLASH;
    }

    /**
     * Method to get {@link String} representation of a {@link Method} for
     * use in diagnostic messages.
     *
     * @param   method          The {@link Method}.
     *
     * @return  The {@link String} representation.
     */
    protected String toString(Method method) {
        StringBuilder buffer = new StringBuilder();
        int modifiers = method.getModifiers();

        if (modifiers != 0) {
            buffer
                .append(java.lang.reflect.Modifier.toString(modifiers))
                .append(SPACE);
        }

        buffer
            .append(method.getReturnType().getSimpleName())
            .append(SPACE)
            .append(method.getName())
            .append(LP.lexeme());

        Class<?>[] types = method.getParameterTypes();

        for (int i = 0; i < types.length; i += 1) {
            if (i > 0) {
                buffer.append(COMMA.lexeme());
            }

            buffer.append(types[i].getSimpleName());
        }

        buffer.append(RP.lexeme());

        Class<?>[] exceptions = method.getExceptionTypes();

        for (int i = 0; i < exceptions.length; i += 1) {
            if (i == 0) {
                buffer.append(THROWS.lexeme());
            } else {
                buffer.append(COMMA.lexeme());
            }

            buffer.append(SPACE).append(exceptions[i].getSimpleName());
        }

        return buffer.toString().trim();
    }

    /**
     * Static method to get the argument {@link Package} name as a path
     * (including the trailing {@value SLASH}).
     *
     * @param   pkg             The {@link Package}.
     *
     * @return  The {@link Package} name as a path.
     */
    protected static String asPath(Package pkg) {
        return asPath(pkg.getName()) + SLASH;
    }

    /**
     * Static method to get the argument name as a path.
     *
     * @param   name            The name {@link String}.
     *
     * @return  The argument {@link String} as a path.
     */
    protected static String asPath(String name) {
        return name.replaceAll(Pattern.quote(DOT), SLASH);
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
     * {@link TypeElement} {@link List} from {@link AnnotationValue}.
     */
    protected class TypeElementList extends ArrayList<TypeElement> {
        private static final long serialVersionUID = -6613114921805902827L;

        /**
         * Sole constructor.
         *
         * @param       value   The {link AnnotationValue}.
         */
        public TypeElementList(AnnotationValue value) {
            super();

            if (value != null) {
                for (Object object : (List<?>) value.getValue()) {
                    TypeMirror mirror =
                        (TypeMirror) ((AnnotationValue) object).getValue();

                    add((TypeElement) types.asElement(mirror));
                }
            }
        }
    }
}
