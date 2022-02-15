package ball.annotation.processing;
/*-
 * ##########################################################################
 * Utilities
 * %%
 * Copyright (C) 2008 - 2022 Allen D. Ball
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

import ball.annotation.ServiceProviderFor;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.annotation.processing.Processor;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static java.lang.reflect.Modifier.isAbstract;
import static javax.tools.StandardLocation.CLASS_OUTPUT;

/**
 * {@link Processor} implementation to generate {@code jaxb.index} files
 * from {@link Class}es annotated with JAXB annotations.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 */
@ServiceProviderFor({ Processor.class })
@For({ XmlRootElement.class, XmlType.class })
@NoArgsConstructor @ToString
public class JAXBIndexProcessor extends AnnotatedProcessor implements ClassFileProcessor {
    private static final String JAXB_INDEX = "jaxb.index";
    private static final String DOT = ".";

    private final Set<String> set = new TreeSet<>();

    @Override
    public void process(Set<Class<?>> set, JavaFileManager fm) throws Exception {
        Map<String,Set<String>> map = new TreeMap<>();

        for (Class<?> type : set) {
            if (! isAbstract(type.getModifiers())) {
                for (Class<? extends Annotation> annotation : getSupportedAnnotationTypeList()) {
                    if (type.isAnnotationPresent(annotation)) {
                        String key = type.getPackage().getName();
                        String value = type.getCanonicalName().substring(key.length());

                        if (value.startsWith(DOT)) {
                            value = value.substring(DOT.length());
                        }

                        map.computeIfAbsent(key, k -> new TreeSet<>())
                            .add(value);
                    }
                }
            }
        }

        for (Map.Entry<String,Set<String>> entry : map.entrySet()) {
            String path = JAXB_INDEX;
            FileObject file = fm.getFileForOutput(CLASS_OUTPUT, entry.getKey(), JAXB_INDEX, null);

            try (PrintWriter writer = new PrintWriter(file.openWriter())) {
                writer.println("# " + JAXB_INDEX);

                entry.getValue().stream().forEach(t -> writer.println(t));
            }
        }
    }
}
