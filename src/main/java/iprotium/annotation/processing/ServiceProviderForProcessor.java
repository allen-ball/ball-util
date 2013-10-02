/*
 * $Id$
 *
 * Copyright 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.annotation.processing;

import iprotium.annotation.ServiceProviderFor;
import iprotium.io.IOUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.FileObject;

import static iprotium.util.ClassUtil.isAbstract;
import static iprotium.util.StringUtil.NIL;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.tools.StandardLocation.CLASS_OUTPUT;

/**
 * {@link Processor} implementation to check {@link Class}es annotated with
 * {@link ServiceProviderFor} to verify the annotated {@link Class}:
 * <ol>
 *   <li value="1">Is concrete</li>
 *   <li value="2">Has a public no-argument constructor</li>
 *   <li value="3">
 *     Implements the {@link Class}es specified by
 *     {@link ServiceProviderFor#value()}
 *   </li>
 * </ol>
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
@ServiceProviderFor({ Processor.class })
public class ServiceProviderForProcessor extends AbstractAnnotationProcessor {
    private static final Charset CHARSET = Charset.forName("UTF-8");

    private static final String PATH = "META-INF/services/%s";

    private MapImpl map = new MapImpl();

    /**
     * Sole constructor.
     */
    public ServiceProviderForProcessor() { super(ServiceProviderFor.class); }

    /**
     * {@link iprotium.util.ant.taskdefs.BootstrapProcessorTask} bootstrap
     * method.
     */
    public void bootstrap(Set<Class<?>> set, File destdir) throws IOException {
        for (Class<?> provider : set) {
            if (! isAbstract(provider)) {
                ServiceProviderFor annotation =
                    provider.getAnnotation(ServiceProviderFor.class);

                if (annotation != null) {
                    for (Class<?> service : annotation.value()) {
                        if (service.isAssignableFrom(provider)) {
                            map.add(service, provider);
                        }
                    }
                }
            }
        }

        for (Map.Entry<String,Set<String>> entry : map.entrySet()) {
            String service = entry.getKey();
            File file = new File(destdir, String.format(PATH, service));

            IOUtil.mkdirs(file.getParentFile());

            PrintWriterImpl writer = null;

            try {
                writer = new PrintWriterImpl(file);
                writer.write(service, entry.getValue());
            } finally {
                IOUtil.close(writer);
            }
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        boolean result = super.process(annotations, roundEnv);

        try {
            if (roundEnv.processingOver()) {
                for (Map.Entry<String,Set<String>> entry : map.entrySet()) {
                    String service = entry.getKey();
                    FileObject file =
                        filer.createResource(CLASS_OUTPUT, NIL,
                                             String.format(PATH, service));
                    PrintWriterImpl writer = null;

                    try {
                        writer = new PrintWriterImpl(file);
                        writer.write(service, entry.getValue());
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
        List<? extends TypeElement> value = getAnnotationValue(element);

        if (! value.isEmpty()) {
            switch (element.getKind()) {
            case CLASS:
                if (! element.getModifiers().contains(ABSTRACT)) {
                    if (hasPublicNoArgumentConstructor(element)) {
                        for (TypeElement service : value) {
                            if (isAssignable(element, service)) {
                                map.add(service, (TypeElement) element);
                            } else {
                                error(element,
                                      element.getKind() + " annotated with "
                                      + AT + type.getSimpleName()
                                      + " and specifies "
                                      + service.getQualifiedName()
                                      + " but is not an implementing class");
                            }
                        }
                    } else {
                        error(element,
                              element.getKind() + " annotated with "
                              + AT + type.getSimpleName()
                              + " but does not have a " + PUBLIC
                              + " no-argument constructor");
                    }
                } else {
                    error(element,
                          element.getKind() + " annotated with "
                          + AT + type.getSimpleName() + " but is " + ABSTRACT);
                }
                break;

            default:
                throw new IllegalStateException();
                /* break; */
            }
        } else {
            error(element,
                  element.getKind() + " annotated with "
                  + AT + type.getSimpleName() + " but no services specified");
        }
    }

    private List<? extends TypeElement> getAnnotationValue(Element element) {
        AnnotationValue value = getAnnotationValue(element, type, "value()");
        ArrayList<TypeElement> list = new ArrayList<TypeElement>();

        for (Object object : (List<?>) value.getValue()) {
            TypeMirror mirror =
                (TypeMirror) ((AnnotationValue) object).getValue();

            list.add((TypeElement) types.asElement(mirror));
        }

        return list;
    }

    private class MapImpl extends TreeMap<String,Set<String>> {
        private static final long serialVersionUID = -7423881318128723418L;

        public MapImpl() { super(); }

        public boolean add(String service, String provider) {
            if (! containsKey(service)) {
                put(service, new TreeSet<String>());
            }

            return get(service).add(provider);
        }

        public boolean add(Class<?> service, Class<?> provider) {
            return add(service.getName(), provider.getName());
        }

        public boolean add(TypeElement service, TypeElement provider) {
            return add(service.getQualifiedName().toString(),
                       provider.getQualifiedName().toString());
        }
    }

    private class PrintWriterImpl extends PrintWriter {
        public PrintWriterImpl(FileObject file) throws IOException {
            super(new OutputStreamWriter(file.openOutputStream(), CHARSET));
        }

        public PrintWriterImpl(File file) throws IOException {
            super(new OutputStreamWriter(new FileOutputStream(file), CHARSET));
        }

        public void write(String service, Collection<String> collection) {
            println("# " + service);

            for (String provider : collection) {
                println(provider);
            }
        }
    }
}
