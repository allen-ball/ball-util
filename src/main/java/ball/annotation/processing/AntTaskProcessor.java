/*
 * $Id$
 *
 * Copyright 2013 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.annotation.processing;

import ball.annotation.ServiceProviderFor;
import ball.util.PropertiesImpl;
import ball.util.ant.taskdefs.AntLib;
import ball.util.ant.taskdefs.AntTask;
import ball.xml.FluentDocument;
import ball.xml.FluentDocumentBuilderFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.util.LinkedHashSet;
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
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.tools.ant.Task;

import static java.lang.reflect.Modifier.isAbstract;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.StandardLocation.CLASS_OUTPUT;
import static javax.xml.transform.OutputKeys.INDENT;
import static javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

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
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Processor.class })
@For({ AntLib.class, AntTask.class })
@NoArgsConstructor @ToString
public class AntTaskProcessor extends AbstractAnnotationProcessor
                              implements ClassFileProcessor {
    private static final String ANTLIB_XML = "antlib.xml";

    private static final String NO = "no";
    private static final String YES = "yes";

    private static final String INDENT_AMOUNT =
        "{http://xml.apache.org/xslt}indent-amount";

    private static final Transformer TRANSFORMER;

    static {
        try {
            TRANSFORMER = TransformerFactory.newInstance().newTransformer();
            TRANSFORMER.setOutputProperty(OMIT_XML_DECLARATION, NO);
            TRANSFORMER.setOutputProperty(INDENT, YES);
            TRANSFORMER.setOutputProperty(INDENT_AMOUNT, String.valueOf(2));
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private ResourceMap map = new ResourceMap();
    private LinkedHashSet<String> packages = new LinkedHashSet<>();

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
                                                 EMPTY, entry.getKey());

                        try (OutputStream out = file.openOutputStream()) {
                            entry.getValue().store(out, entry.getKey());
                        }
                    }

                    for (String pkg : packages) {
                        AntLibXML antlib = new AntLibXML(pkg, map);
                        FileObject file =
                            filer.createResource(CLASS_OUTPUT,
                                                 EMPTY, antlib.getPath());

                        try (OutputStream out = file.openOutputStream()) {
                            antlib.writeTo(out);
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

            if (isNotEmpty(name)) {
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

            Files.createDirectories(file.toPath().getParent());

            try (OutputStream out = new FileOutputStream(file)) {
                entry.getValue().store(out, entry.getKey());
            }
        }

        for (String pkg : packages) {
            AntLibXML xml = new AntLibXML(pkg, map);
            File file = new File(destdir, xml.getPath());

            Files.createDirectories(file.toPath().getParent());

            try (OutputStream out = new FileOutputStream(file)) {
                xml.writeTo(out);
            }
        }
    }

    private class ResourceMap extends TreeMap<String,PropertiesImpl> {
        private static final long serialVersionUID = 6091007173199004788L;

        private  ResourceMap() { super(); }

        public boolean put(String resource, String name, String task) {
            PropertiesImpl value =
                computeIfAbsent(resource, k -> new PropertiesImpl());

            return (value.put(name, task) != task);
        }

        public boolean put(String resource, String name, Class<?> task) {
            return put(resource, name, task.getName());
        }

        public boolean put(String resource, String name, TypeElement task) {
            return put(resource,
                       name, elements.getBinaryName(task).toString());
        }
    }

    private static class AntLibXML extends TreeMap<String,String> {
        private static final long serialVersionUID = -4362352118276244430L;

        /** @serial */ private final String path;

        private AntLibXML(String pkg, ResourceMap map) {
            super();

            this.path = asPath(pkg) + SLASH + ANTLIB_XML;

            map.values()
                .stream()
                .flatMap(t -> t.entrySet().stream())
                .filter(t -> t.getValue().toString().startsWith(pkg + DOT))
                .forEach(t -> put(t.getKey().toString(),
                                  t.getValue().toString()));
        }

        public String getPath() { return path; }

        public FluentDocument asDocument() throws Exception {
            FluentDocument d =
                FluentDocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .newDocument();

            d.add(d.element("antlib",
                            entrySet().stream()
                            .map(t -> d.element("taskdef",
                                                d.attr("name", t.getKey()),
                                                d.attr("classname", t.getValue())))));

            return d;
        }

        public void writeTo(OutputStream out) throws Exception {
            TRANSFORMER.transform(new DOMSource(asDocument()),
                                  new StreamResult(out));
        }
    }
}
