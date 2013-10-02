/*
 * $Id$
 *
 * Copyright 2012, 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.annotation.processing;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static java.util.Arrays.asList;
import static java.util.Collections.disjoint;
import static javax.lang.model.element.ElementKind.METHOD;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.lang.model.util.ElementFilter.constructorsIn;
import static javax.lang.model.util.ElementFilter.methodsIn;
import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.WARNING;

/**
 * Extends {@link javax.annotation.processing.AbstractProcessor} by
 * providing a {@link #getSupportedSourceVersion()} implementation, methods
 * to report {@link javax.tools.Diagnostic.Kind#ERROR}s and
 * {@link javax.tools.Diagnostic.Kind#WARNING}s, and access to
 * {@link ProcessingEnvironment#getFiler()},
 * {@link ProcessingEnvironment#getElementUtils()}, and
 * {@link ProcessingEnvironment#getTypeUtils()}.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public abstract class AbstractProcessor
                      extends javax.annotation.processing.AbstractProcessor {
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

    /** See {@link ProcessingEnvironment#getFiler()}. */
    protected Filer filer = null;
    /** See {@link ProcessingEnvironment#getElementUtils()}. */
    protected Elements elements = null;
    /** See {@link ProcessingEnvironment#getTypeUtils()}. */
    protected Types types = null;

    /**
     * Sole constructor.
     */
    protected AbstractProcessor() { super(); }

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

        filer = processingEnv.getFiler();
        elements = processingEnv.getElementUtils();
        types = processingEnv.getTypeUtils();
    }

    @Override
    public abstract boolean process(Set<? extends TypeElement> annotations,
                                    RoundEnvironment roundEnv);

    /**
     * Method to print an error.
     *
     * @param   element         The offending {@link Element}.
     * @param   message         The message {@link CharSequence}.
     */
    protected void error(Element element, CharSequence message) {
        processingEnv.getMessager().printMessage(ERROR, message, element);
    }

    /**
     * Method to print a warning.
     *
     * @param   element         The offending {@link Element}.
     * @param   message         The message {@link CharSequence}.
     */
    protected void warning(Element element, CharSequence message) {
        processingEnv.getMessager().printMessage(WARNING, message, element);
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
        boolean found = false;

        for (ExecutableElement constructor :
                 constructorsIn(element.getEnclosedElements())) {
            found |=
                (constructor.getModifiers().contains(PUBLIC)
                 && constructor.getParameters().isEmpty());

            if (found) {
                break;
            }
        }

        return found;
    }

    /**
     * See {@link Types#isAssignable(TypeMirror,TypeMirror)}.
     */
    protected boolean isAssignable(Element from, Element to) {
        boolean isAssignable = false;

        if (from instanceof TypeElement && to instanceof TypeElement) {
            isAssignable = isAssignable((TypeElement) from, (TypeElement) to);
        }

        return isAssignable;
    }

    private boolean isAssignable(TypeElement from, TypeElement to) {
        boolean isAssignable = types.isAssignable(from.asType(), to.asType());

        if (! isAssignable) {
            for (TypeMirror supertype :
                     types.directSupertypes(from.asType())) {
                isAssignable |= isAssignable(types.asElement(supertype), to);

                if (isAssignable) {
                    break;
                }
            }
        }

        return isAssignable;
    }

    /**
     * See {@link #isAssignable(Element,Element)}.
     */
    protected boolean isAssignable(Class<?> from, Element to) {
        return isAssignable(getTypeElementFor(from), to);
    }

    /**
     * See {@link #isAssignable(Element,Element)}.
     */
    protected boolean isAssignable(Element from, Class<?> to) {
        return isAssignable(from, getTypeElementFor(to));
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
        ExecutableElement overridden = null;
        TypeElement type = (TypeElement) overrider.getEnclosingElement();

        for (TypeMirror supertype : types.directSupertypes(type.asType())) {
            overridden = overrides(overrider, types.asElement(supertype));

            if (overridden != null) {
                break;
            }
        }

        return overridden;
    }

    private ExecutableElement overrides(ExecutableElement overrider,
                                        Element type) {
        ExecutableElement overridden = null;

        if (overridden == null) {
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
        }

        return overridden;
    }

    private ExecutableElement overridden(ExecutableElement overrider,
                                         TypeElement type) {
        ExecutableElement overridden = null;

        if (overridden == null) {
            for (ExecutableElement method :
                     methodsIn(type.getEnclosedElements())) {
                if (disjoint(method.getModifiers(), asList(PRIVATE, STATIC))) {
                    if (elements.overrides(overrider, method, type)) {
                        overridden = method;
                        break;
                    }
                }
            }
        }

        if (overridden == null) {
            overridden =
                overrides(overrider, types.asElement(type.getSuperclass()));
        }

        return overridden;
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
        ExecutableElement overrider = null;

        if (type != null) {
            for (ExecutableElement method :
                     methodsIn(type.getEnclosedElements())) {
                if (overrides(method, overridden)) {
                    overrider = method;
                    break;
                }
            }
        }

        if (overrider == null) {
            if (type != null) {
                TypeMirror mirror = type.getSuperclass();

                if (mirror != null) {
                    TypeElement supertype =
                        (TypeElement) types.asElement(mirror);

                    overrider = implementationOf(overridden, supertype);
                }
            }
        }

        return overrider;
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
     * Method to get an {@link ExecutableElement} for a {@link Class}
     * {@link Method}.
     *
     * @param   type            The {@link Class}.
     * @param   name            The {@link Method} name.
     * @param   parameters      The {@link Method} parameter types.
     *
     * @return  The {@link ExecutableElement} for the {@link Method}.
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
        ExecutableElement executable = null;
        TypeElement type = getTypeElementFor(method.getDeclaringClass());

        if (type != null) {
            for (ExecutableElement element :
                     methodsIn(type.getEnclosedElements())) {
                if (same(element, method)) {
                    executable = element;
                    break;
                }
            }
        }

        return executable;
    }

    private boolean same(ExecutableElement element, Method method) {
        boolean same = (element.getKind() == METHOD);

        if (same) {
            same &= element.getSimpleName().contentEquals(method.getName());
        }

        if (same) {
            same &= (element.isVarArgs() == method.isVarArgs());
        }

        if (same) {
            List<? extends VariableElement> list = element.getParameters();
            Class<?>[] array = method.getParameterTypes();

            if (list.size() == array.length) {
                for (int i = 0, n = list.size(); i < n; i += 1) {
                    same &=
                        types.isSameType(list.get(i).asType(),
                                         getTypeElementFor(array[i]).asType());

                    if (! same) {
                        break;
                    }
                }
            } else {
                same &= false;
            }
        }

        return same;
    }

    /**
     * Method to get a {@link TypeElement} for a {@link Class}.
     *
     * @param   type            The {@link Class}.
     *
     * @return  The {@link TypeElement} for the {@link Class}.
     */
    protected TypeElement getTypeElementFor(Class<?> type) {
        return elements.getTypeElement(type.getCanonicalName());
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
        AnnotationMirror annotation = null;

        for (AnnotationMirror mirror : element.getAnnotationMirrors()) {
            if (mirror.getAnnotationType().toString().equals(type.getName())) {
                annotation = mirror;
                break;
            }
        }

        return annotation;
    }

    /**
     * Method to get an {@link Element}'s {@link AnnotationValue}.
     *
     * @param   element         The annotated {@link Element}.
     * @param   type            The {@link Annotation} type ({@link Class}).
     * @param   name            The {@link Annotation} element name.
     *
     * @return  The {@link AnnotationValue} if the {@link Element} is
     *          annotated with the argument annotation; {@code null}
     *          otherwise.
     *
     * @see Elements#getElementValuesWithDefaults(AnnotationMirror)
     */
    protected AnnotationValue getAnnotationValue(Element element,
                                                 Class<? extends Annotation> type,
                                                 String name) {
        AnnotationValue value = null;
        AnnotationMirror annotation = getAnnotationMirror(element, type);

        if (annotation != null) {
            for (Map.Entry<? extends ExecutableElement,? extends AnnotationValue> entry :
                     elements.getElementValuesWithDefaults(annotation).entrySet()) {
                if (entry.getKey().toString().equals(name)) {
                    value = entry.getValue();
                    break;
                }
            }
        }

        return value;
    }
}
