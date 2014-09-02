/*
 * $Id$
 *
 * Copyright 2013, 2014 Allen D. Ball.  All rights reserved.
 */
package ball.annotation.processing;

import ball.annotation.ServiceProviderFor;
import ball.io.IOUtil;
import ball.util.PropertiesImpl;
import ball.util.StringUtil;
import ball.util.ant.taskdefs.AntTask;
import ball.util.ant.taskdefs.BootstrapProcessorTask;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import org.apache.tools.ant.Task;

import static ball.util.ClassUtil.isAbstract;
import static ball.util.StringUtil.NIL;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.StandardLocation.CLASS_OUTPUT;

/**
 * {@link Processor} implementation to check {@link Class}es annotated with
 * {@link AntTask}:
 * <ol>
 *   <li value="1">Are an instance of {@link Task},</li>
 *   <li value="2">Concrete, and</li>
 *   <li value="3">Have a public no-argument constructor</li>
 * </ol>
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Processor.class })
@For({ AntTask.class })
public class AntTaskProcessor extends AbstractAnnotationProcessor
                              implements BootstrapProcessorTask.Processor {
    private MapImpl map = new MapImpl();

    /**
     * Sole constructor.
     */
    public AntTaskProcessor() { super(); }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        boolean result = true;

        try {
            if (! roundEnv.errorRaised()) {
                result &= super.process(annotations, roundEnv);

                if (roundEnv.processingOver()) {
                    for (Map.Entry<String,PropertiesImpl> entry :
                             map.entrySet()) {
                        FileObject file =
                            filer.createResource(CLASS_OUTPUT,
                                                 NIL, entry.getKey());
                        OutputStream out = null;

                        try {
                            out = file.openOutputStream();
                            entry.getValue().store(out, entry.getKey());
                        } finally {
                            IOUtil.close(out);
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
        String name = element.getAnnotation(AntTask.class).value();
        String resource = element.getAnnotation(AntTask.class).resource();
        Element enclosing = element;

        while (enclosing != null
               && enclosing.getKind() != ElementKind.PACKAGE) {
            enclosing = enclosing.getEnclosingElement();
        }

        if (enclosing != null) {
            resource =
                URI.create(asPath((PackageElement) enclosing) + resource)
                .normalize()
                .toString();
        }

        if (! StringUtil.isNil(name)) {
            if (isAssignable(element.asType(), Task.class)) {
                if (! element.getModifiers().contains(ABSTRACT)) {
                    if (hasPublicNoArgumentConstructor(element)) {
                        map.put(resource, name, (TypeElement) element);
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
            } else {
                print(ERROR,
                      element,
                      element.getKind() + " annotated with "
                      + AT + annotation.getSimpleName()
                      + " but does not implement" + Task.class.getName());
            }
        } else {
            print(ERROR,
                  element,
                  element.getKind() + " annotated with "
                  + AT + annotation.getSimpleName()
                  + " but does not specify value()");
        }
    }

    @Override
    public void process(Set<Class<?>> set, File destdir) throws IOException {
        for (Class<?> type : set) {
            if (Task.class.isAssignableFrom(type)) {
                if (! isAbstract(type)) {
                    AntTask annotation = type.getAnnotation(AntTask.class);

                    if (annotation != null) {
                        String name = annotation.value();
                        String resource = annotation.resource();
                        Package pkg = type.getPackage();

                        if (pkg != null) {
                            resource =
                                URI.create(asPath(pkg) + resource)
                                .normalize()
                                .toString();
                        }

                        map.put(resource, name, type);
                    }
                }
            }
        }

        for (Map.Entry<String,PropertiesImpl> entry : map.entrySet()) {
            File file = new File(destdir, entry.getKey());

            IOUtil.mkdirs(file.getParentFile());

            OutputStream out = null;

            try {
                out = new FileOutputStream(file);
                entry.getValue().store(out, entry.getKey());
            } finally {
                IOUtil.close(out);
            }
        }
    }

    private class MapImpl extends TreeMap<String,PropertiesImpl> {
        private static final long serialVersionUID = 5165986208397332197L;

        public MapImpl() { super(); }

        public boolean put(String resource, String name, String task) {
            if (! containsKey(resource)) {
                put(resource, new PropertiesImpl());
            }

            return (get(resource).put(name, task) != task);
        }

        public boolean put(String resource, String name, Class<?> task) {
            return put(resource, name, task.getName());
        }

        public boolean put(String resource, String name, TypeElement task) {
            return put(resource,
                       name, elements.getBinaryName(task).toString());
        }
    }
}
