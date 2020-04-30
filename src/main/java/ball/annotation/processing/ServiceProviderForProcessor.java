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
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static java.lang.reflect.Modifier.isAbstract;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.PUBLIC;
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
public class ServiceProviderForProcessor extends AbstractAnnotationProcessor
                                         implements ClassFileProcessor {
    private static final String PATH = "META-INF/services/%s";

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
                        String service = entry.getKey();
                        FileObject file =
                            filer.createResource(CLASS_OUTPUT, EMPTY,
                                                 String.format(PATH, service));
                        ArrayList<String> lines = new ArrayList<>();

                        lines.add("# " + service);
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
        AnnotationMirror mirror = getAnnotationMirror(element, annotation);
        AnnotationValue value =
            elements.getElementValuesWithDefaults(mirror).entrySet()
            .stream()
            .filter(t -> t.getKey().toString().equals("value()"))
            .map(t -> t.getValue())
            .findFirst().get();
        List<TypeElement> list = getTypeElementListFrom(value);

        if (! list.isEmpty()) {
            switch (element.getKind()) {
            case CLASS:
                if (! element.getModifiers().contains(ABSTRACT)) {
                    if (hasPublicNoArgumentConstructor(element)) {
                        for (TypeElement service : list) {
                            if (types.isAssignable(element.asType(),
                                                   service.asType())) {
                                map.add(service, (TypeElement) element);
                            } else {
                                print(ERROR, element,
                                      "%s annotated with @%s and specifies %s but is not an implementing class",
                                      element.getKind(),
                                      annotation.getSimpleName(),
                                      service.getQualifiedName());
                            }
                        }
                    } else {
                        print(ERROR, element,
                              "%s annotated with @%s but does not have a %s no-argument constructor",
                              element.getKind(),
                              annotation.getSimpleName(), PUBLIC);
                    }
                } else {
                    print(ERROR, element,
                          "%s annotated with @%s but is %s",
                          element.getKind(),
                          annotation.getSimpleName(), ABSTRACT);
                }
                break;

            default:
                break;
            }
        } else {
            print(ERROR, element,
                  "%s annotated with @%s but no services specified",
                  element.getKind(), annotation.getSimpleName());
        }
    }

    @Override
    public void process(Set<Class<?>> set, File destdir) throws IOException {
        for (Class<?> provider : set) {
            if (! isAbstract(provider.getModifiers())) {
                ServiceProviderFor annotation =
                    provider.getAnnotation(ServiceProviderFor.class);

                if (annotation != null) {
                    for (Class<?> service : annotation.value()) {
                        if (service.isAssignableFrom(provider)) {
                            map.add(service, provider);
                        }
                    }
                }
            }
        }

        for (Map.Entry<String,Set<String>> entry : map.entrySet()) {
            String service = entry.getKey();
            File file = new File(destdir, String.format(PATH, service));

            Files.createDirectories(file.toPath().getParent());

            ArrayList<String> lines = new ArrayList<>();

            lines.add("# " + service);
            lines.addAll(entry.getValue());

            Files.write(file.toPath(), lines, CHARSET);
        }
    }

    @NoArgsConstructor
    private class MapImpl extends TreeMap<String,Set<String>> {
        private static final long serialVersionUID = -5826890336322674613L;

        public boolean add(String service, String provider) {
            if (! containsKey(service)) {
                put(service, new TreeSet<>());
            }

            return get(service).add(provider);
        }

        public boolean add(Class<?> service, Class<?> provider) {
            return add(service.getName(), provider.getName());
        }

        public boolean add(TypeElement service, TypeElement provider) {
            return add(elements.getBinaryName(service).toString(),
                       elements.getBinaryName(provider).toString());
        }
    }
}
