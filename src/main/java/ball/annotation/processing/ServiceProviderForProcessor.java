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
import ball.annotation.ServiceProviderFor;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Stream;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static java.lang.reflect.Modifier.isAbstract;
import static java.util.stream.Collectors.toList;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.StandardLocation.CLASS_OUTPUT;
import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * {@link Processor} implementation to check {@link Class}es annotated with
 * {@link ServiceProviderFor} to verify the annotated {@link Class}:
 * <ol>
 *   <li value="1">Is concrete</li>
 *   <li value="2">Has a public no-argument constructor</li>
 *   <li value="3">
 *     Implements the {@link Class}es specified by
 *     {@link ServiceProviderFor#value()}
 *   </li>
 * </ol>
 *
 * Note: Google offers a similar
 * {@link.uri https://github.com/google/auto/tree/master/service target=newtab AutoService}
 * library.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Processor.class })
@For({ ServiceProviderFor.class })
@NoArgsConstructor @ToString
public class ServiceProviderForProcessor extends AnnotatedProcessor
                                         implements ClassFileProcessor {
    private static final String PATH = "META-INF/services/%s";

    @Override
    protected void process(RoundEnvironment roundEnv,
                           TypeElement annotation, Element element) {
        super.process(roundEnv, annotation, element);

        AnnotationMirror mirror = getAnnotationMirror(element, annotation);
        AnnotationValue value = getAnnotationValue(mirror, "value");

        if (! isEmptyArray(value)) {
            if (withoutModifiers(ABSTRACT).test(element)) {
                String provider =
                    elements.getBinaryName((TypeElement) element).toString();
                List<TypeElement> services =
                    Stream.of(value)
                    .filter(Objects::nonNull)
                    .map(t -> (List<?>) t.getValue())
                    .flatMap(List::stream)
                    .map(t -> (AnnotationValue) t)
                    .map(t -> (TypeMirror) t.getValue())
                    .map(t -> (TypeElement) types.asElement(t))
                    .collect(toList());

                for (TypeElement service : services) {
                    if (types.isAssignable(element.asType(),
                                           service.asType())) {
                    } else {
                        print(ERROR, element,
                              "%s: %s does not implement %s",
                              annotation.getSimpleName(),
                              element.getKind(), service.getQualifiedName());
                    }
                }
            } else {
                print(ERROR, element,
                      "%s: %s is %s",
                      annotation.getSimpleName(), element.getKind(), ABSTRACT);
            }
        } else {
            print(ERROR, element, mirror, value, "value() is empty");
        }
    }

    @Override
    public void process(Set<Class<?>> set,
                        JavaFileManager fm) throws Throwable {
        Map<String,Set<String>> map = new TreeMap<>();

        for (Class<?> provider : set) {
            if (! isAbstract(provider.getModifiers())) {
                ServiceProviderFor annotation =
                    provider.getAnnotation(ServiceProviderFor.class);

                if (annotation != null) {
                    for (Class<?> service : annotation.value()) {
                        if (service.isAssignableFrom(provider)) {
                            map.computeIfAbsent(service.getName(),
                                                k -> new TreeSet<>())
                                .add(provider.getName());
                        }
                    }
                }
            }
        }

        for (Map.Entry<String,Set<String>> entry : map.entrySet()) {
            String service = entry.getKey();
            FileObject file =
                fm.getFileForOutput(CLASS_OUTPUT,
                                    EMPTY, String.format(PATH, service), null);

            try (PrintWriter writer = new PrintWriter(file.openWriter())) {
                writer.println("# " + service);

                entry.getValue().stream().forEach(t -> writer.println(t));
            }
        }
    }
}
