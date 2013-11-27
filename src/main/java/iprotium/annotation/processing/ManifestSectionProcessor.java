/*
 * $Id$
 *
 * Copyright 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.annotation.processing;

import iprotium.annotation.ManifestSection;
import iprotium.annotation.ServiceProviderFor;
import iprotium.io.IOUtil;
import iprotium.util.ant.taskdefs.BootstrapProcessorTask;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.regex.Pattern;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;

import static iprotium.util.StringUtil.NIL;
import static javax.tools.StandardLocation.CLASS_OUTPUT;

/**
 * {@link Processor} implementation to scan {@link Package}s annotated with
 * {@link ManifestSection} and generate a {@code META-INF/MANIFEST.MF}.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
@ServiceProviderFor({ Processor.class })
public class ManifestSectionProcessor extends AbstractAnnotationProcessor
                                      implements BootstrapProcessorTask.Processor {
    private static final String PATH = "META-INF/MANIFEST.MF";

    private Manifest manifest =
        new Manifest() {
            { getMainAttributes().putValue("Manifest-Version", "1.0"); }
        };

    /**
     * Sole constructor.
     */
    public ManifestSectionProcessor() { super(ManifestSection.class); }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        boolean result = super.process(annotations, roundEnv);

        try {
            if (roundEnv.processingOver()) {
                FileObject file =
                    filer.createResource(CLASS_OUTPUT, NIL, PATH);
                OutputStream out = null;

                try {
                    out = file.openOutputStream();
                    manifest.write(out);
                } finally {
                    IOUtil.close(out);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace(System.err);
        }

        return result;
    }

    @Override
    protected void process(RoundEnvironment env,
                           Element element) throws Exception {
        String name = ((PackageElement) element).getQualifiedName().toString();

        name = name.replaceAll(Pattern.quote(DOT), SLASH) + SLASH;

        ManifestSection annotation =
            element.getAnnotation(ManifestSection.class);

        manifest.getEntries().put(name, new AttributesImpl(annotation));
    }

    @Override
    public void process(Set<Class<?>> set, File destdir) throws IOException {
        for (Class<?> type : set) {
            ManifestSection annotation =
                type.getAnnotation(ManifestSection.class);

            if (annotation != null) {
                String name = type.getPackage().getName();

                name = name.replaceAll(Pattern.quote(DOT), SLASH) + SLASH;

                manifest.getEntries()
                    .put(name, new AttributesImpl(annotation));
            }
        }

        File file = new File(destdir, PATH);

        IOUtil.mkdirs(file.getParentFile());

        OutputStream out = null;

        try {
            out = new FileOutputStream(file);
            manifest.write(out);
        } finally {
            IOUtil.close(out);
        }
    }

    private class AttributesImpl extends Attributes {
        public AttributesImpl(ManifestSection annotation) {
            super();

            putValue("Sealed", String.valueOf(annotation.sealed()));
        }
    }
}
