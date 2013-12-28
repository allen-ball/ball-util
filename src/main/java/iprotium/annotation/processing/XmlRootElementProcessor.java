/*
 * $Id$
 *
 * Copyright 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.annotation.processing;

import iprotium.annotation.ServiceProviderFor;
import iprotium.io.IOUtil;
import iprotium.util.ant.taskdefs.BootstrapProcessorTask;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.xml.bind.annotation.XmlRootElement;

import static iprotium.util.ClassUtil.isAbstract;
import static iprotium.util.StringUtil.NIL;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.StandardLocation.CLASS_OUTPUT;

/**
 * {@link Processor} implementation to generate {@code jaxb.index} files
 * from {@link Class}es annotated with {@link XmlRootElement}.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Processor.class })
public class XmlRootElementProcessor extends AbstractAnnotationProcessor
                                     implements BootstrapProcessorTask.Processor {
    private static final String JAXB_INDEX = "jaxb.index";

    private MapImpl map = new MapImpl();

    /**
     * Sole constructor.
     */
    public XmlRootElementProcessor() { super(XmlRootElement.class); }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        boolean result = super.process(annotations, roundEnv);

        try {
            if (roundEnv.processingOver()) {
                for (Map.Entry<String,Set<String>> entry : map.entrySet()) {
                    FileObject file =
                        filer.createResource(CLASS_OUTPUT,
                                             entry.getKey(), JAXB_INDEX);
                    PrintWriterImpl writer = null;

                    try {
                        writer = new PrintWriterImpl(file);
                        writer.write(JAXB_INDEX, entry.getValue());
                    } finally {
                        IOUtil.close(writer);
                    }
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
        switch (element.getKind()) {
        case CLASS:
            map.add((TypeElement) element);
            break;

        default:
            throw new IllegalStateException();
            /* break; */
        }
    }

    @Override
    public void process(Set<Class<?>> set, File destdir) throws IOException {
        for (Class<?> type : set) {
            if (! isAbstract(type)) {
                XmlRootElement annotation =
                    type.getAnnotation(XmlRootElement.class);

                if (annotation != null) {
                    map.add(type);
                }
            }
        }

        for (Map.Entry<String,Set<String>> entry : map.entrySet()) {
            String pkg = entry.getKey();
            File file =
                new File(destdir,
                         pkg.replaceAll(Pattern.quote("."), File.separator)
                         + File.separator + JAXB_INDEX);

            IOUtil.mkdirs(file.getParentFile());

            PrintWriterImpl writer = null;

            try {
                writer = new PrintWriterImpl(file);
                writer.write(JAXB_INDEX, entry.getValue());
            } finally {
                IOUtil.close(writer);
            }
        }
    }

    private class MapImpl extends TreeMap<String,Set<String>> {
        private static final long serialVersionUID = 447668382517967323L;

        public MapImpl() { super(); }

        public boolean add(Class<?> type) {
            return get(type.getPackage().getName()).add(type.getSimpleName());
        }

        public boolean add(TypeElement type) {
            PackageElement pkg = getPackageElementFor(type);
            String name =
                (pkg != null) ? pkg.getQualifiedName().toString() : NIL;

            return get(name).add(type.getSimpleName().toString());
        }

        @Override
        public Set<String> get(Object key) {
            if (! super.containsKey(key)) {
                super.put((String) key, new TreeSet<String>());
            }

            return super.get(key);
        }
    }
}
