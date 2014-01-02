/*
 * $Id$
 *
 * Copyright 2014 Allen D. Ball.  All rights reserved.
 */
package iprotium.annotation.processing;

import iprotium.annotation.ResourceFile;
import iprotium.annotation.ServiceProviderFor;
import iprotium.io.IOUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;

import static iprotium.util.StringUtil.NIL;
import static iprotium.util.StringUtil.isNil;
import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.StandardLocation.CLASS_OUTPUT;

/**
 * {@link Processor} implementation to check and assemble
 * {@link ResourceFile} {@link java.lang.annotation.Annotation}s.
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
        boolean result = super.process(annotations, roundEnv);

        try {
            if (roundEnv.processingOver()) {
                for (Map.Entry<String,List<String>> entry : map.entrySet()) {
                    String path = entry.getKey();
                    FileObject file =
                        filer.createResource(CLASS_OUTPUT, NIL, path);
                    PrintWriterImpl writer = null;

                    try {
                        writer = new PrintWriterImpl(file);
                        writer.write(path, entry.getValue());
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
                           TypeElement annotation,
                           Element element) throws Exception {
        String path = element.getAnnotation(ResourceFile.class).path();
        String[] lines = element.getAnnotation(ResourceFile.class).lines();

        if (! isNil(path)) {
            if (lines != null) {
                TypeElement type = (TypeElement) element;
                PackageElement pkg = getPackageElementFor(type);
                ArrayList<String> list =
                    new ArrayList<String>(Arrays.asList(lines));

                for (int i = 0, n = list.size(); i < n; i += 1) {
                    String line = list.get(i);

                    line =
                        line.replaceAll(Pattern.quote(ResourceFile.CLASS),
                                        type.getQualifiedName().toString());
                    line =
                        line.replaceAll(Pattern.quote(ResourceFile.PACKAGE),
                                        (pkg != null) ?
                                            pkg.getQualifiedName().toString()
                                            : NIL);

                    list.set(i, line);
                }

                map.add(path, list);
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
        private static final long serialVersionUID = 3629640658706499264L;

        public MapImpl() { super(); }

        public boolean add(String path, Collection<String> collection) {
            if (! containsKey(path)) {
                put(path, new ArrayList<String>());
            }

            return get(path).addAll(collection);
        }
    }
}
