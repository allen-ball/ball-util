package ball.annotation.processing;
/*-
 * ##########################################################################
 * Utilities
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2008 - 2020 Allen D. Ball
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ##########################################################################
 */
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
import lombok.NoArgsConstructor;
import lombok.ToString;

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
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Processor.class })
@For({ ResourceFile.class })
@NoArgsConstructor @ToString
public class ResourceFileProcessor extends AbstractAnnotationProcessor {
    private MapImpl map = new MapImpl();

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
                           TypeElement annotation, Element element) {
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
                print(ERROR, element,
                      "%s annotated with @%s but no lines() specified",
                      element.getKind(), annotation.getSimpleName());
            }
        } else {
            print(ERROR, element,
                  "%s annotated with @%s but no path() specified",
                  element.getKind(), annotation.getSimpleName());
        }
    }

    @NoArgsConstructor
    private class MapImpl extends TreeMap<String,List<String>> {
        private static final long serialVersionUID = 5908228485945805046L;

        public boolean add(String path, Collection<String> collection) {
            return computeIfAbsent(path, k -> new ArrayList<>()).addAll(collection);
        }
    }

    private class Parameters extends TreeMap<String,Object> {
        private static final long serialVersionUID = -1831974140591319719L;

        public Parameters(TypeElement type) {
            super();

            PackageElement pkg = elements.getPackageOf(type);

            put(ResourceFile.CLASS,
                type.getQualifiedName().toString());
            put(ResourceFile.PACKAGE,
                (pkg != null) ? pkg.getQualifiedName().toString() : EMPTY);
        }
    }
}
