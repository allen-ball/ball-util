/*
 * $Id$
 *
 * Copyright 2013, 2014 Allen D. Ball.  All rights reserved.
 */
package ball.annotation.processing;

import ball.annotation.Manifest.Attribute;
import ball.annotation.Manifest.DependsOn;
import ball.annotation.Manifest.DesignTimeOnly;
import ball.annotation.Manifest.JavaBean;
import ball.annotation.Manifest.MainClass;
import ball.annotation.Manifest.Section;
import ball.annotation.ServiceProviderFor;
import ball.io.IOUtil;
import ball.util.ant.taskdefs.BootstrapProcessorTask;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;

import static ball.lang.Keyword.THROWS;
import static ball.lang.Punctuation.COMMA;
import static ball.lang.Punctuation.LP;
import static ball.lang.Punctuation.RP;
import static ball.util.StringUtil.NIL;
import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.WARNING;
import static javax.tools.StandardLocation.CLASS_OUTPUT;

/**
 * {@link Processor} implementation to scan {@link ball.annotation.Manifest}
 * {@link java.lang.annotation.Annotation}s and generate a
 * {@link java.util.jar.Manifest META-INF/MANIFEST.MF}.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Processor.class })
@For({
        Attribute.class, MainClass.class, Section.class,
            JavaBean.class, DependsOn.class, DesignTimeOnly.class
     })
public class ManifestProcessor extends AbstractAnnotationProcessor
                               implements BootstrapProcessorTask.Processor {
    private static final String MANIFEST_MF = "MANIFEST.MF";

    private static final String DOT_CLASS = ".class";

    private static abstract class MAIN {
        public static void main(String[] argv) { }
    }

    private static final Method MAIN = MAIN.class.getDeclaredMethods()[0];

    private ManifestImpl manifest = null;
    private HashSet<Element> processed = new HashSet<Element>();

    /**
     * Sole constructor.
     */
    public ManifestProcessor() { super(); }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        boolean result = true;

        try {
            String path = META_INF + SLASH + MANIFEST_MF;

            if (manifest == null) {
                manifest = new ManifestImpl();

                InputStream in = null;

                try {
                    FileObject file =
                        filer.getResource(CLASS_OUTPUT, NIL, path);

                    if (file.getLastModified() != 0) {
                        in = file.openInputStream();
                        manifest.read(in);
                    }
                } catch (Exception exception) {
                } finally {
                    IOUtil.close(in);
                }
            }

            if (! roundEnv.errorRaised()) {
                result &= super.process(annotations, roundEnv);

                if (roundEnv.processingOver()) {
                    FileObject file =
                        filer.createResource(CLASS_OUTPUT, NIL, path);
                    OutputStream out = null;

                    try {
                        out = file.openOutputStream();
                        manifest.write(out);
                    } finally {
                        IOUtil.close(out);
                    }
                }
            }
        } catch (Exception exception) {
            print(ERROR, null, exception);
        }

        return result;
    }

    @Override
    protected void process(RoundEnvironment env,
                           TypeElement ignore,
                           Element element) throws Exception {
        if (! processed.contains(element)) {
            processed.add(element);

            Attribute attribute = element.getAnnotation(Attribute.class);

            MainClass main = element.getAnnotation(MainClass.class);

            if (main != null) {
                switch (element.getKind()) {
                case CLASS:
                case INTERFACE:
                    TypeElement type = (TypeElement) element;
                    ExecutableElement method =
                        getExecutableElementFor(type, MAIN);

                    if (method != null
                        && (method.getModifiers()
                            .containsAll(getModifierSetFor(MAIN)))) {
                        String name = elements.getBinaryName(type).toString();
                        String old = manifest.put(main, name);

                        if (old != null && (! name.equals(old))) {
                            print(WARNING,
                                  element,
                                  element.getKind() + " annotated with "
                                  + AT + main.annotationType().getSimpleName()
                                  + " overwrites previous definition of "
                                  + old);
                        }
                    } else {
                        print(ERROR,
                              element,
                              element.getKind() + " annotated with "
                              + AT + main.annotationType().getSimpleName()
                              + " but does not implement `"
                              + toString(MAIN) + "'");
                    }
                    break;

                default:
                    break;
                }
            }

            Section section = element.getAnnotation(Section.class);

            if (section != null) {
                switch (element.getKind()) {
                case PACKAGE:
                    manifest.put(asPath((PackageElement) element), section);
                    break;

                default:
                    break;
                }
            }

            JavaBean bean = element.getAnnotation(JavaBean.class);
            DependsOn depends = element.getAnnotation(DependsOn.class);
            DesignTimeOnly design =
                element.getAnnotation(DesignTimeOnly.class);

            if (bean != null || depends != null || design != null) {
                switch (element.getKind()) {
                case CLASS:
                    manifest.put(asPath((TypeElement) element),
                                 bean, depends, design);
                    break;

                default:
                    break;
                }
            }
        }
    }

    @Override
    public void process(Set<Class<?>> set, File destdir) throws IOException {
        File file = new File(new File(destdir, META_INF), MANIFEST_MF);

        if (manifest == null) {
            manifest = new ManifestImpl();

            if (file.exists()) {
                FileInputStream in = null;

                try {
                    in = new FileInputStream(file);
                    manifest.read(in);
                } finally {
                    IOUtil.close(in);
                }
            }
        }

        for (Class<?> type : set) {
            Attribute attribute = type.getAnnotation(Attribute.class);

            MainClass main = type.getAnnotation(MainClass.class);

            if (main != null) {
                manifest.put(main, type.getName());
            }

            Section section = type.getAnnotation(Section.class);

            if (section != null) {
                manifest.put(asPath(type.getPackage()), section);
            }

            JavaBean bean = type.getAnnotation(JavaBean.class);
            DependsOn depends = type.getAnnotation(DependsOn.class);
            DesignTimeOnly design = type.getAnnotation(DesignTimeOnly.class);

            if (bean != null || depends != null || design != null) {
                manifest.put(asPath(type), bean, depends, design);
            }
        }

        IOUtil.mkdirs(file.getParentFile());

        OutputStream out = null;

        try {
            out = new FileOutputStream(file);
            manifest.write(out);
        } finally {
            IOUtil.close(out);
        }
    }

    @Override
    protected String asPath(TypeElement element) {
        return super.asPath(element) + DOT_CLASS;
    }

    @Override
    protected String asPath(Class<?> type) {
        return super.asPath(type) + DOT_CLASS;
    }

    private String toString(Method method) {
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

    private class ManifestImpl extends Manifest {
        public ManifestImpl() {
            super();

            getMainAttributes().putValue("Manifest-Version", "1.0");
            getEntries().put("Build-Information", new BuildInformation());
        }

        public String put(MainClass main, String name) {
            return (getMainAttributes()
                    .putValue(getAttributeName(main.getClass()), name));
        }

        public void put(String path, Section section) {
            Attributes attributes = getEntries().get(path);

            if (attributes == null) {
                attributes = new Attributes();
                getEntries().put(path, attributes);
            }

            attributes.putValue("Sealed", String.valueOf(section.sealed()));
        }

        public void put(String path,
                        JavaBean bean,
                        DependsOn depends, DesignTimeOnly design) {
            Attributes attributes = getEntries().get(path);

            if (attributes == null) {
                attributes = new Attributes();
                getEntries().put(path, attributes);
            }

            attributes.putValue(getAttributeName(JavaBean.class),
                                String.valueOf(true));

            if (depends != null) {
                attributes.putValue(getAttributeName(depends.getClass()),
                                    concatenate(SPACE, depends.value()));
            }

            if (design != null) {
                attributes.putValue(getAttributeName(design.getClass()),
                                    concatenate(SPACE, design.value()));
            }
        }

        private String getAttributeName(Class<? extends Annotation> type) {
            return type.getAnnotation(Attribute.class).value();
        }

        private String concatenate(String separator, String... strings) {
            StringBuilder buffer = null;

            for (String string : strings) {
                if (buffer == null) {
                    buffer = new StringBuilder(string);
                } else {
                    buffer.append(separator).append(string);
                }
            }

            return (buffer != null) ? buffer.toString() : NIL;
        }

        @Override
        public String toString() { return super.toString(); }

        private class BuildInformation extends Attributes {
            public BuildInformation() {
                super();

                putValue("Build-Time",
                         String.valueOf(System.currentTimeMillis()));
                putValue("Java-Vendor", System.getProperty("java.vendor"));
                putValue("Java-Version", System.getProperty("java.version"));
                putValue("Os-Arch", System.getProperty("os.arch"));
                putValue("Os-Name", System.getProperty("os.name"));
                putValue("Os-Version", System.getProperty("os.version"));
            }

            @Override
            public String toString() { return super.toString(); }
        }
    }
}
