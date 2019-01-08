/*
 * $Id$
 *
 * Copyright 2013 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.annotation.processing;

import ball.annotation.ServiceProviderFor;
import ball.util.ant.taskdefs.BootstrapProcessorTask;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.file.Files;
import java.util.ArrayList;
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
import javax.xml.bind.annotation.XmlType;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static java.lang.reflect.Modifier.isAbstract;
import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.StandardLocation.CLASS_OUTPUT;

/**
 * {@link Processor} implementation to generate {@code jaxb.index} files
 * from {@link Class}es annotated with JAXB annotations.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Processor.class })
@For({ XmlRootElement.class, XmlType.class })
@NoArgsConstructor @ToString
public class JAXBIndexProcessor extends AbstractAnnotationProcessor
                                implements BootstrapProcessorTask.Processor {
    private static final String JAXB_INDEX = "jaxb.index";

    private MapImpl map = new MapImpl();

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
                        FileObject file =
                            filer.createResource(CLASS_OUTPUT,
                                                 entry.getKey(), JAXB_INDEX);
                        ArrayList<String> lines = new ArrayList<>();

                        lines.add("# " + JAXB_INDEX);
                        lines.addAll(entry.getValue());

                        Files.createDirectories(toPath(file).getParent());
                        Files.write(toPath(file), lines, CHARSET);
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
            map.add((TypeElement) element);
            break;

        default:
            break;
        }
    }

    @Override
    public void process(Set<Class<?>> set, File destdir) throws IOException {
        for (Class<?> type : set) {
            if (! isAbstract(type.getModifiers())) {
                for (Class<? extends Annotation> annotation :
                         getSupportedAnnotationTypeList()) {
                    if (type.getAnnotation(annotation) != null) {
                        map.add(type);
                    }
                }
            }
        }

        for (Map.Entry<String,Set<String>> entry : map.entrySet()) {
            String pkg = entry.getKey();
            File file = new File(new File(destdir, asPath(pkg)), JAXB_INDEX);

            Files.createDirectories(file.toPath().getParent());

            ArrayList<String> lines = new ArrayList<>();

            lines.add("# " + JAXB_INDEX);
            lines.addAll(entry.getValue());

            Files.write(file.toPath(), lines, CHARSET);
        }
    }

    private class MapImpl extends TreeMap<String,Set<String>> {
        private static final long serialVersionUID = 5693261055822027911L;

        public MapImpl() { super(); }

        public boolean add(Class<?> type) {
            return add(type.getPackage().getName(), type.getCanonicalName());
        }

        public boolean add(TypeElement type) {
            return add(getPackageElementFor(type)
                       .getQualifiedName().toString(),
                       type.getQualifiedName().toString());
        }

        private boolean add(String pkg, String type) {
            if (type.startsWith(pkg)) {
                type = type.substring(pkg.length());
            }

            if (type.startsWith(DOT)) {
                type = type.substring(DOT.length());
            }

            return get(pkg).add(type);
        }

        @Override
        public Set<String> get(Object key) {
            if (! super.containsKey(key)) {
                super.put((String) key, new TreeSet<>());
            }

            return super.get(key);
        }
    }
}
