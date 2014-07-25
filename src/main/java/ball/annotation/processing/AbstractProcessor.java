/*
 * $Id$
 *
 * Copyright 2012 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.annotation.processing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Arrays;
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
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.FileObject;

import static java.util.Arrays.asList;
import static java.util.Collections.disjoint;
import static javax.lang.model.element.ElementKind.METHOD;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.lang.model.util.ElementFilter.constructorsIn;
import static javax.lang.model.util.ElementFilter.methodsIn;

/**
 * Extends {@link javax.annotation.processing.AbstractProcessor} by
 * providing a {@link #getSupportedSourceVersion()} implementation, methods
 * to report {@link javax.tools.Diagnostic.Kind#ERROR}s and
 * {@link javax.tools.Diagnostic.Kind#WARNING}s, and access to
 * {@link ProcessingEnvironment#getFiler()},
 * {@link ProcessingEnvironment#getElementUtils()}, and
 * {@link ProcessingEnvironment#getTypeUtils()}.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
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

    /** UTF-8 */
    protected static final Charset CHARSET = Charset.forName("UTF-8");

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
     * Method to print an diagnositc message.
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
     * Method to get a {@link PackageElement} for a {@link TypeElement}.
     *
     * @param   type            The {@link TypeElement}.
     *
     * @return  The {@link PackageElement} for the {@link TypeElement}.
     */
    protected PackageElement getPackageElementFor(TypeElement type) {
        PackageElement pkg = null;

        if (type != null) {
            Element container = type.getEnclosingElement();

            while (container != null) {
                if (container instanceof PackageElement) {
                    pkg = (PackageElement) container;
                    break;
                }

                container = container.getEnclosingElement();
            }
        }

        return pkg;
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
     * Method to get a {@link List} of {@link TypeElement}s for an array
     * of {@link Class}es.
     *
     * @param   types           The {@link Class}es.
     *
     * @return  The {@link List} of {@link TypeElement}s for the
     *          {@link Class}es.
     */
    protected List<TypeElement> getTypeElementsFor(Class<?>... types) {
        TypeElement[] array = new TypeElement[types.length];

        for (int i = 0; i < array.length; i += 1) {
            array[i] = getTypeElementFor(types[i]);
        }

        return Arrays.asList(array);
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
        AnnotationMirror annotation = null;

        for (AnnotationMirror mirror : element.getAnnotationMirrors()) {
            if (mirror.getAnnotationType().toString().equals(name)) {
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
        return getAnnotationValue(getAnnotationMirror(element, type), name);
    }

    /**
     * Method to get an {@link Element}'s {@link AnnotationValue}.
     *
     * @param   element         The annotated {@link Element}.
     * @param   type            The {@link Annotation} type
     *                          ({@link TypeElement}).
     * @param   name            The {@link Annotation} element name.
     *
     * @return  The {@link AnnotationValue} if the {@link Element} is
     *          annotated with the argument annotation; {@code null}
     *          otherwise.
     *
     * @see Elements#getElementValuesWithDefaults(AnnotationMirror)
     */
    protected AnnotationValue getAnnotationValue(Element element,
                                                 TypeElement type,
                                                 String name) {
        return getAnnotationValue(getAnnotationMirror(element, type), name);
    }

    private AnnotationValue getAnnotationValue(AnnotationMirror annotation,
                                               String name) {
        AnnotationValue value = null;

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

    @Override
    public String toString() { return super.toString(); }

    /**
     * {@link PrintWriter} implementation suitable for creating Java file
     * artifacts such as service provider files.
     */
    protected class PrintWriterImpl extends PrintWriter {

        /**
         * Construct a {@link PrintWriter} for a {@link File}.
         */
        public PrintWriterImpl(File file) throws IOException {
            this(new FileOutputStream(file));
        }

        /**
         * Construct a {@link PrintWriter} for a {@link FileObject}.
         */
        public PrintWriterImpl(FileObject file) throws IOException {
            this(file.openOutputStream());
        }

        private PrintWriterImpl(OutputStream out) throws IOException {
            super(new OutputStreamWriter(out, CHARSET));
        }

        /**
         * Method to write a complete file.
         *
         * @param       comment         The first line {@link String}.
         * @param       iterable        The {@link Iterable} of line
         *                              {@link String}s.
         */
        public void write(String comment, Iterable<String> iterable) {
            println("# " + comment);

            for (String line : iterable) {
                println(line);
            }
        }
    }
}