/*
 * $Id$
 *
 * Copyright 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.annotation.processing;

import iprotium.annotation.AntTask;
import iprotium.annotation.ServiceProviderFor;
import iprotium.io.IOUtil;
import iprotium.util.PropertiesImpl;
import iprotium.util.StringUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import org.apache.tools.ant.Task;

import static iprotium.util.ClassUtil.isAbstract;
import static iprotium.util.StringUtil.NIL;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.tools.StandardLocation.CLASS_OUTPUT;

/**
 * {@link Processor} implementation to check {@link Class}es annotated with
 * {@link AntTask} are:
 * <ol>
 *   <li value="1">An instance of {@link Task}</li>
 *   <li value="2">Concrete</li>
 *   <li value="3">Has a public no-argument constructor</li>
 * </ol>
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
@ServiceProviderFor({ Processor.class })
public class AntTaskProcessor extends AbstractAnnotationProcessor {
    private MapImpl map = new MapImpl();

    /**
     * Sole constructor.
     */
    public AntTaskProcessor() { super(AntTask.class); }

    /**
     * {@link iprotium.util.ant.taskdefs.BootstrapProcessorTask} bootstrap
     * method.
     */
    public void bootstrap(Set<Class<?>> set, File destdir) throws IOException {
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
                                URI.create(pkg.getName()
                                           .replaceAll(Pattern.quote(DOT),
                                                       SLASH)
                                           + SLASH + resource).normalize()
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

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        boolean result = super.process(annotations, roundEnv);

        try {
            if (roundEnv.processingOver()) {
                for (Map.Entry<String,PropertiesImpl> entry : map.entrySet()) {
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
        } catch (Exception exception) {
            exception.printStackTrace(System.err);
        }

        return result;
    }

    @Override
    protected void process(RoundEnvironment env,
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
                URI.create(((PackageElement) enclosing).getQualifiedName()
                           .toString().replaceAll(Pattern.quote(DOT), SLASH)
                           + SLASH + resource).normalize().toString();
        }

        if (! StringUtil.isNil(name)) {
            if (isAssignable(element, Task.class)) {
                if (! element.getModifiers().contains(ABSTRACT)) {
                    if (hasPublicNoArgumentConstructor(element)) {
                        map.put(resource, name, (TypeElement) element);
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
            } else {
                error(element,
                      element.getKind() + " annotated with "
                      + AT + type.getSimpleName() + " but does not implement"
                      + Task.class.getName());
            }
        } else {
            error(element,
                  element.getKind() + " annotated with "
                  + AT + type.getSimpleName()
                  + " but does not specify value()");
        }
    }

    private class MapImpl extends TreeMap<String,PropertiesImpl> {
        private static final long serialVersionUID = -6937502666191189751L;

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
