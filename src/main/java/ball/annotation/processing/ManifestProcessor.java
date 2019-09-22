/*
 * $Id$
 *
 * Copyright 2013 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.annotation.processing;

import ball.annotation.Manifest.Attribute;
import ball.annotation.Manifest.DependsOn;
import ball.annotation.Manifest.DesignTimeOnly;
import ball.annotation.Manifest.JavaBean;
import ball.annotation.Manifest.MainClass;
import ball.annotation.Manifest.Section;
import ball.annotation.ServiceProviderFor;
import ball.util.ant.taskdefs.BootstrapProcessorTask;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.Arrays;
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
import lombok.NoArgsConstructor;
import lombok.ToString;

import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.WARNING;
import static javax.tools.StandardLocation.CLASS_OUTPUT;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.join;

/**
 * {@link Processor} implementation to scan {@link ball.annotation.Manifest}
 * {@link java.lang.annotation.Annotation}s and generate a
 * {@link java.util.jar.Manifest META-INF/MANIFEST.MF}.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Processor.class })
@For({
        Attribute.class, MainClass.class, Section.class,
            JavaBean.class, DependsOn.class, DesignTimeOnly.class
     })
@NoArgsConstructor @ToString
public class ManifestProcessor extends AbstractAnnotationProcessor
                               implements ClassFileProcessor {
    private static final String MANIFEST_MF = "MANIFEST.MF";

    private static final String _CLASS = ".class";

    private static abstract class PROTOTYPE {
        public static void main(String[] argv) { }
    }

    private static final Method METHOD =
        PROTOTYPE.class.getDeclaredMethods()[0];

    private ManifestImpl manifest = null;
    private HashSet<Element> processed = new HashSet<>();

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        boolean result = true;

        try {
            String path = META_INF + SLASH + MANIFEST_MF;

            if (manifest == null) {
                manifest = new ManifestImpl();

                FileObject file = filer.getResource(CLASS_OUTPUT, EMPTY, path);

                if (file.getLastModified() != 0) {
                    try (InputStream in = file.openInputStream()) {
                        manifest.read(in);
                    } catch (Exception exception) {
                    }
                }

                manifest.init();
            }

            if (! roundEnv.errorRaised()) {
                result &= super.process(annotations, roundEnv);

                if (roundEnv.processingOver()) {
                    FileObject file =
                        filer.createResource(CLASS_OUTPUT, EMPTY, path);

                    try (OutputStream out = file.openOutputStream()) {
                        manifest.write(out);
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
                        getExecutableElementFor(type, METHOD);

                    if (method != null
                        && (method.getModifiers()
                            .containsAll(getModifierSetFor(METHOD)))) {
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
                              + toString(METHOD) + "'");
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
                try (FileInputStream in = new FileInputStream(file)) {
                    manifest.read(in);
                }
            }

            manifest.init();
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

        Files.createDirectories(file.toPath().getParent());

        try (OutputStream out = new FileOutputStream(file)) {
            manifest.write(out);
        }
    }

    @Override
    protected String asPath(TypeElement element) {
        return super.asPath(element) + _CLASS;
    }

    @Override
    protected String asPath(Class<?> type) {
        return super.asPath(type) + _CLASS;
    }

    private class ManifestImpl extends Manifest {
        private static final String MANIFEST_VERSION = "Manifest-Version";

        public ManifestImpl() { super(); }

        protected void init() {
            if (getMainAttributes().getValue(MANIFEST_VERSION) == null) {
                getMainAttributes().putValue(MANIFEST_VERSION, "1.0");
            }
        }

        public Attributes putAttributes(String name, Attributes attributes) {
            return getEntries().put(name, attributes);
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
                                    join(depends.value(), SPACE));
            }

            if (design != null) {
                attributes.putValue(getAttributeName(design.getClass()),
                                    join(design.value(), SPACE));
            }
        }

        private String getAttributeName(Class<? extends Annotation> type) {
            return type.getAnnotation(Attribute.class).value();
        }

        @Override
        public String toString() { return super.toString(); }
    }
}
