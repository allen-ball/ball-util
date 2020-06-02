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
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Stream;
import javax.annotation.processing.Processor;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static ball.text.ParameterizedMessageFormat.format;
import static java.util.stream.Collectors.toList;
import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.StandardLocation.CLASS_OUTPUT;
import static org.apache.commons.lang3.StringUtils.EMPTY;

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
public class ResourceFileProcessor extends AnnotatedProcessor
                                   implements ClassFileProcessor {
    @Override
    public void process(Set<Class<?>> set,
                        JavaFileManager fm) throws Throwable {
        Map<String,List<String>> map = new TreeMap<>();

        for (Class<?> type : set) {
            ResourceFile annotation = type.getAnnotation(ResourceFile.class);

            if (annotation != null) {
                Parameters parameters = new Parameters(type);
                String key = format(annotation.path(), parameters);
                List<String> value =
                    Stream.of(annotation.lines())
                    .map(t -> format(t, parameters))
                    .collect(toList());

                map.computeIfAbsent(key, k -> new ArrayList<>())
                    .addAll(value);
            }
        }

        for (Map.Entry<String,List<String>> entry : map.entrySet()) {
            String path = entry.getKey();
            FileObject file =
                fm.getFileForOutput(CLASS_OUTPUT, EMPTY, path, null);

            try (PrintWriter writer = new PrintWriter(file.openWriter())) {
                writer.println("# " + path);

                entry.getValue().stream()
                    .forEach(t -> writer.println(t));
            }
        }
    }

    private class Parameters extends TreeMap<String,Object> {
        private static final long serialVersionUID = 363071267469868108L;

        public Parameters(Class<?> type) {
            super();

            put(ResourceFile.CLASS, type.getCanonicalName());
            put(ResourceFile.PACKAGE, type.getPackage().getName());
        }
    }
}
