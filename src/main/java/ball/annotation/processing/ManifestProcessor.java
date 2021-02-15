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
import ball.annotation.ServiceProviderFor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static ball.annotation.Manifest.Attribute;
import static ball.annotation.Manifest.DependsOn;
import static ball.annotation.Manifest.DesignTimeOnly;
import static ball.annotation.Manifest.JavaBean;
import static ball.annotation.Manifest.MainClass;
import static ball.annotation.Manifest.Section;
import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.JavaFileObject.Kind.CLASS;
import static javax.tools.StandardLocation.CLASS_OUTPUT;
import static javax.tools.StandardLocation.CLASS_PATH;
import static org.apache.commons.lang3.StringUtils.EMPTY;

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
public class ManifestProcessor extends AnnotatedProcessor
                               implements ClassFileProcessor {
    private static final String PATH = "META-INF/MANIFEST.MF";

    private static abstract class PROTOTYPE {
        public static void main(String[] argv) { }
    }

    private static final Method PROTOTYPE =
        PROTOTYPE.class.getDeclaredMethods()[0];

    static { PROTOTYPE.setAccessible(true); }

    @Override
    protected void process(RoundEnvironment roundEnv,
                           TypeElement annotation, Element element) {
        super.process(roundEnv, annotation, element);

        Attribute attribute = element.getAnnotation(Attribute.class);
        MainClass main = element.getAnnotation(MainClass.class);

        if (main != null) {
            switch (element.getKind()) {
            case CLASS:
            case INTERFACE:
                TypeElement type = (TypeElement) element;
                ExecutableElement method = getMethod(type, PROTOTYPE);

                if (method != null
                    && (method.getModifiers()
                        .containsAll(getModifiers(PROTOTYPE)))) {
                } else {
                    print(ERROR, element,
                          "@%s: %s does not implement '%s'",
                          main.annotationType().getSimpleName(),
                          element.getKind(), declaration(PROTOTYPE));
                }
                break;

            default:
                break;
            }
        }

        Section section = element.getAnnotation(Section.class);
        JavaBean bean = element.getAnnotation(JavaBean.class);
        DependsOn depends = element.getAnnotation(DependsOn.class);
        DesignTimeOnly design =
            element.getAnnotation(DesignTimeOnly.class);
    }

    @Override
    public void process(Set<Class<?>> set, JavaFileManager fm) throws Exception {
        ManifestImpl manifest = new ManifestImpl();
        FileObject file = fm.getFileForInput(CLASS_PATH, EMPTY, PATH);

        if (file != null) {
            try (InputStream in = file.openInputStream()) {
                manifest.read(in);
            } catch (IOException exception) {
            }
        } else {
            manifest.init();
        }

        file = fm.getFileForOutput(CLASS_OUTPUT, EMPTY, PATH, null);

        URI root = file.toUri().resolve("..").normalize();

        for (Class<?> type : set) {
            URI uri =
                fm.getJavaFileForInput(CLASS_OUTPUT, type.getName(), CLASS)
                .toUri();

            uri = root.relativize(uri);

            Attribute attribute = type.getAnnotation(Attribute.class);
            MainClass main = type.getAnnotation(MainClass.class);

            if (main != null) {
                manifest.put(main, type.getName());
            }

            Section section = type.getAnnotation(Section.class);

            if (section != null) {
                manifest.put(uri.resolve("").toString(), section);
            }

            JavaBean bean = type.getAnnotation(JavaBean.class);
            DependsOn depends = type.getAnnotation(DependsOn.class);
            DesignTimeOnly design = type.getAnnotation(DesignTimeOnly.class);

            if (bean != null || depends != null || design != null) {
                manifest.put(uri.toString(),
                             bean, depends, design);
            }
        }

        try (OutputStream out = file.openOutputStream()) {
            manifest.write(out);
         }
    }

    @NoArgsConstructor @ToString
    private class ManifestImpl extends Manifest {
        private static final String MANIFEST_VERSION = "Manifest-Version";

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
                                    String.join(" ", depends.value()));
            }

            if (design != null) {
                attributes.putValue(getAttributeName(design.getClass()),
                                    String.join(" ", design.value()));
            }
        }

        private String getAttributeName(Class<? extends Annotation> type) {
            return type.getAnnotation(Attribute.class).value();
        }
    }
}
