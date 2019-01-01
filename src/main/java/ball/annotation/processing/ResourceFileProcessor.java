/*
 * $Id$
 *
 * Copyright 2014 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.annotation.processing;

import ball.annotation.ResourceFile;
import ball.annotation.ServiceProviderFor;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;

import static ball.text.ParameterizedMessageFormat.format;
import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.StandardLocation.CLASS_OUTPUT;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * {@link Processor} implementation to check and assemble
 * {@link ResourceFile} {@link java.lang.annotation.Annotation}s.
 *
 * @see ball.text.ParameterizedMessageFormat
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Processor.class })
@For({ ResourceFile.class })
public class ResourceFileProcessor extends AbstractAnnotationProcessor {
    private MapImpl map = new MapImpl();

    /**
     * Sole constructor.
     */
    public ResourceFileProcessor() { super(); }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        boolean result = true;

        try {
            if (! roundEnv.errorRaised()) {
                result &= super.process(annotations, roundEnv);

                if (roundEnv.processingOver()) {
                    for (Map.Entry<String,List<String>> entry :
                             map.entrySet()) {
                        String path = entry.getKey();
                        FileObject file =
                            filer.createResource(CLASS_OUTPUT, EMPTY, path);
                        ArrayList<String> lines = new ArrayList<>();

                        lines.add("# " + path);
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
        String path = element.getAnnotation(ResourceFile.class).path();
        String[] lines = element.getAnnotation(ResourceFile.class).lines();

        if (! isEmpty(path)) {
            if (lines != null) {
                ArrayList<String> list = new ArrayList<>(lines.length);
                Parameters parameters = new Parameters((TypeElement) element);

                for (String line : lines) {
                    list.add(format(line, parameters));
                }

                map.add(format(path, parameters), list);
            } else {
                print(ERROR,
                      element,
                      element.getKind() + " annotated with "
                      + AT + annotation.getSimpleName()
                      + " but no lines() specified");
            }
        } else {
            print(ERROR,
                  element,
                  element.getKind() + " annotated with "
                  + AT + annotation.getSimpleName()
                  + " but no path() specified");
        }
    }

    private class MapImpl extends TreeMap<String,List<String>> {
        private static final long serialVersionUID = 5908228485945805046L;

        public MapImpl() { super(); }

        public boolean add(String path, Collection<String> collection) {
            if (! containsKey(path)) {
                put(path, new ArrayList<>());
            }

            return get(path).addAll(collection);
        }
    }

    private class Parameters extends TreeMap<String,Object> {
        private static final long serialVersionUID = -1831974140591319719L;

        public Parameters(TypeElement type) {
            super();

            PackageElement pkg = getPackageElementFor(type);

            put(ResourceFile.CLASS,
                type.getQualifiedName().toString());
            put(ResourceFile.PACKAGE,
                (pkg != null) ? pkg.getQualifiedName().toString() : EMPTY);
        }
    }
}
