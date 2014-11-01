/*
 * $Id$
 *
 * Copyright 2013, 2014 Allen D. Ball.  All rights reserved.
 */
package ball.annotation.processing;

import ball.annotation.ServiceProviderFor;
import ball.io.IOUtil;
import ball.util.ant.taskdefs.BootstrapProcessorTask;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.FileObject;

import static ball.util.ClassUtil.isAbstract;
import static ball.util.StringUtil.NIL;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.tools.Diagnostic.Kind.ERROR;
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
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Processor.class })
@For({ ServiceProviderFor.class })
public class ServiceProviderForProcessor extends AbstractAnnotationProcessor
                                         implements BootstrapProcessorTask.Processor {
    private static final String PATH = META_INF + "/services/%s";

    private MapImpl map = new MapImpl();

    /**
     * Sole constructor.
     */
    public ServiceProviderForProcessor() { super(); }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        boolean result = true;

        try {
            if (! roundEnv.errorRaised()) {
                result &= super.process(annotations, roundEnv);

                if (roundEnv.processingOver()) {
                    for (Map.Entry<String,Set<String>> entry :
                             map.entrySet()) {
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
            }
        } catch (Exception exception) {
            print(ERROR, null, exception);
        }

        return result;
    }

    @Override
    protected void process(RoundEnvironment env,
                           TypeElement annotation,
                           Element element) throws Exception {
        List<? extends TypeElement> value =
            getAnnotationValue(element, annotation);

        if (! value.isEmpty()) {
            switch (element.getKind()) {
            case CLASS:
                if (! element.getModifiers().contains(ABSTRACT)) {
                    if (hasPublicNoArgumentConstructor(element)) {
                        for (TypeElement service : value) {
                            if (isAssignable(element.asType(), service.asType())) {
                                map.add(service, (TypeElement) element);
                            } else {
                                print(ERROR,
                                      element,
                                      element.getKind() + " annotated with "
                                      + AT + annotation.getSimpleName()
                                      + " and specifies "
                                      + service.getQualifiedName()
                                      + " but is not an implementing class");
                            }
                        }
                    } else {
                        print(ERROR,
                              element,
                              element.getKind() + " annotated with "
                              + AT + annotation.getSimpleName()
                              + " but does not have a " + PUBLIC
                              + " no-argument constructor");
                    }
                } else {
                    print(ERROR,
                          element,
                          element.getKind() + " annotated with "
                          + AT + annotation.getSimpleName()
                          + " but is " + ABSTRACT);
                }
                break;

            default:
                break;
            }
        } else {
            print(ERROR,
                  element,
                  element.getKind() + " annotated with "
                  + AT + annotation.getSimpleName()
                  + " but no services specified");
        }
    }

    private List<? extends TypeElement> getAnnotationValue(Element element,
                                                           TypeElement annotation) {
        AnnotationValue value =
            getAnnotationValue(element, annotation, "value()");
        ArrayList<TypeElement> list = new ArrayList<>();

        for (Object object : (List<?>) value.getValue()) {
            TypeMirror mirror =
                (TypeMirror) ((AnnotationValue) object).getValue();

            list.add((TypeElement) types.asElement(mirror));
        }

        return list;
    }

    @Override
    public void process(Set<Class<?>> set, File destdir) throws IOException {
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

    private class MapImpl extends TreeMap<String,Set<String>> {
        private static final long serialVersionUID = -5826890336322674613L;

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
            return add(elements.getBinaryName(service).toString(),
                       elements.getBinaryName(provider).toString());
        }
    }
}
