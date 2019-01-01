/*
 * $Id$
 *
 * Copyright 2013 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.annotation.processing;

import ball.activation.JAXBDataSource;
import ball.annotation.ServiceProviderFor;
import ball.io.IOUtil;
import ball.util.PropertiesImpl;
import ball.util.StringUtil;
import ball.util.ant.taskdefs.AntLib;
import ball.util.ant.taskdefs.AntTask;
import ball.util.ant.taskdefs.BootstrapProcessorTask;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.tools.ant.Task;

import static ball.util.StringUtil.NIL;
import static java.lang.reflect.Modifier.isAbstract;
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
 * And {@link Package}s annotated with {@link AntLib}.  Generates the
 * specified resources at the end of annotation processing.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Processor.class })
@For({ AntLib.class, AntTask.class })
public class AntTaskProcessor extends AbstractAnnotationProcessor
                              implements BootstrapProcessorTask.Processor {
    private static final String ANTLIB_XML = "antlib.xml";

    private ResourceMap map = new ResourceMap();
    private LinkedHashSet<String> packages = new LinkedHashSet<>();

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
                        try (OutputStream out = file.openOutputStream()) {
                            entry.getValue().store(out, entry.getKey());
                        }
                    }

                    for (String pkg : packages) {
                        AntLibXML xml = new AntLibXML(pkg, map);
                        FileObject file =
                            filer.createResource(CLASS_OUTPUT,
                                                 NIL, xml.getPath());

                        try (OutputStream out = file.openOutputStream()) {
                            xml.writeTo(out);
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
        switch (element.getKind()) {
        case CLASS:
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
                    /*
                     * See AntTaskMixInProcessor.
                     */
                }
            } else {
                print(ERROR,
                      element,
                      element.getKind() + " annotated with "
                      + AT + annotation.getSimpleName()
                      + " but does not specify value()");
            }
            break;

        case PACKAGE:
            packages.add(((PackageElement) element)
                         .getQualifiedName().toString());
            break;

        default:
            break;
        }
    }

    @Override
    public void process(Set<Class<?>> set, File destdir) throws Exception {
        for (Class<?> type : set) {
            AntTask task = type.getAnnotation(AntTask.class);

            if (task != null) {
                if (Task.class.isAssignableFrom(type)) {
                    if (! isAbstract(type.getModifiers())) {
                        String name = task.value();
                        String resource = task.resource();
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

            AntLib lib = type.getAnnotation(AntLib.class);

            if (lib != null) {
                packages.add(type.getPackage().getName());
            }
        }

        for (Map.Entry<String,PropertiesImpl> entry : map.entrySet()) {
            File file = new File(destdir, entry.getKey());

            IOUtil.mkdirs(file.getParentFile());

            try (OutputStream out = new FileOutputStream(file)) {
                entry.getValue().store(out, entry.getKey());
            }
        }

        for (String pkg : packages) {
            AntLibXML xml = new AntLibXML(pkg, map);
            File file = new File(destdir, xml.getPath());

            IOUtil.mkdirs(file.getParentFile());

            try (OutputStream out = new FileOutputStream(file)) {
                xml.writeTo(out);
            }
        }
    }

    private class ResourceMap extends TreeMap<String,PropertiesImpl> {
        private static final long serialVersionUID = 6091007173199004788L;

        private  ResourceMap() { super(); }

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

    /**
     * {@code <antlib/>}
     */
    @XmlRootElement(name = "antlib")
    @XmlType(propOrder = { "taskdef" })
    public static class AntLibXML extends TreeMap<String,String> {
        private static final long serialVersionUID = 2430157873343229512L;

        /** @serial */ private final String path;

        private AntLibXML(String pkg, ResourceMap map) {
            this(asPath(pkg) + SLASH + ANTLIB_XML);

            for (PropertiesImpl properties : map.values()) {
                for (Map.Entry<?,?> entry : properties.entrySet()) {
                    String task = entry.getKey().toString();
                    String type = entry.getValue().toString();

                    if (type.startsWith(pkg + DOT)) {
                        put(task, type);
                    }
                }
            }
        }

        private AntLibXML(String path) {
            super();

            this.path = path;
        }

        private AntLibXML() { this(null); }

        public String getPath() { return path; }

        @XmlElement(name = "taskdef")
        public Taskdef[] getTaskdef() {
            ArrayList<Taskdef> list = new ArrayList<>(size());

            for (Map.Entry<String,String> entry : entrySet()) {
                list.add(new Taskdef(entry.getKey(), entry.getValue()));
            }

            return list.toArray(new Taskdef[] { });
        }

        public void writeTo(OutputStream out) throws Exception {
            IOUtil.copy(new JAXBDataSource(this).getInputStream(), out);
        }

        /**
         * {@code <taskdef/>}
         */
        @XmlType(propOrder = { "name", "classname" })
        public static class Taskdef {
            private final String name;
            private final String classname;

            private Taskdef(String name, String classname) {
                this.name = name;
                this.classname = classname;
            }

            private Taskdef() { this(null, null); }

            @XmlAttribute
            public String getName() { return name; }

            @XmlAttribute
            public String getClassname() { return classname; }

            @Override
            public String toString() { return super.toString(); }
        }
    }
}
