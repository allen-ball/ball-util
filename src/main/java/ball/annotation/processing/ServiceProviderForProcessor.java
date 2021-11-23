package ball.annotation.processing;
/*-
 * ##########################################################################
 * Utilities
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2008 - 2021 Allen D. Ball
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
import java.lang.reflect.Method;
import java.util.Collections;
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
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static java.lang.reflect.Modifier.isAbstract;
import static java.util.stream.Collectors.toList;
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
 * or implements Java 9's {@code java.util.ServiceLoader.Provider}
 * {@code public static T provider()} method.
 * <p>
 * Note: Google offers a similar
 * {@link.uri https://github.com/google/auto/tree/master/service target=newtab AutoService}
 * library.
 * </p>
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Processor.class })
@For({ ServiceProviderFor.class })
@NoArgsConstructor @ToString
public class ServiceProviderForProcessor extends AnnotatedProcessor
                                         implements ClassFileProcessor {
    private static abstract class PROTOTYPE {
        public static Object provider() { return null; }
    }

    private static final Method PROTOTYPE =
        PROTOTYPE.class.getDeclaredMethods()[0];

    static { PROTOTYPE.setAccessible(true); }

    private static final String PATH = "META-INF/services/%s";

    @Override
    protected void process(RoundEnvironment roundEnv,
                           TypeElement annotation, Element element) {
        super.process(roundEnv, annotation, element);

        TypeElement type = (TypeElement) element;
        AnnotationMirror mirror = getAnnotationMirror(type, annotation);
        AnnotationValue value = getAnnotationValue(mirror, "value");

        if (! isEmptyArray(value)) {
            ExecutableElement method = getMethod(type, PROTOTYPE);

            if (method != null) {
                if (! method.getModifiers().containsAll(getModifiers(PROTOTYPE))) {
                    print(ERROR, method,
                          "@%s: %s is not %s",
                          annotation.getSimpleName(),
                          method.getKind(), modifiers(PROTOTYPE.getModifiers()));
                }
            } else {
                if (! withoutModifiers(ABSTRACT).test(element)) {
                    print(ERROR, element,
                          "%s: %s must not be %s",
                          annotation.getSimpleName(),
                          element.getKind(), ABSTRACT);
                }

                ExecutableElement constructor =
                    getConstructor((TypeElement) element, Collections.emptyList());
                boolean found =
                    (constructor != null && constructor.getModifiers().contains(PUBLIC));

                if (! found) {
                    print(ERROR, element,
                          "@%s: No %s NO-ARG constructor",
                          annotation.getSimpleName(), PUBLIC);
                }
            }

            String provider = elements.getBinaryName(type).toString();
            List<TypeElement> services =
                Stream.of(value)
                .filter(Objects::nonNull)
                .map(t -> (List<?>) t.getValue())
                .flatMap(List::stream)
                .map(t -> (AnnotationValue) t)
                .map(t -> t.getValue())
                .filter(t -> t instanceof TypeMirror)
                .map(t -> (TypeElement) types.asElement((TypeMirror) t))
                .collect(toList());

            for (TypeElement service : services) {
                if (! isAssignable(type, service)) {
                    print(ERROR, type,
                          "@%s: %s does not implement %s",
                          annotation.getSimpleName(),
                          type.getKind(), service.getQualifiedName());
                }

                if (method != null) {
                    if (! isAssignable(method.getReturnType(), service.asType())) {
                        print(ERROR, method,
                              "@%s: %s does not return %s",
                              annotation.getSimpleName(),
                              method.getKind(), service.getQualifiedName());
                    }
                }
            }
        } else {
            print(ERROR, type, mirror, value, "value() is empty");
        }
    }

    private boolean isAssignable(Element from, Element to) {
        return isAssignable(from.asType(), to.asType());
    }

    private boolean isAssignable(TypeMirror from, TypeMirror to) {
        return types.isAssignable(types.erasure(from), types.erasure(to));
    }

    @Override
    public void process(Set<Class<?>> set, JavaFileManager fm) throws Exception {
        Map<String,Set<String>> map = new TreeMap<>();

        for (Class<?> provider : set) {
            ServiceProviderFor annotation =
                provider.getAnnotation(ServiceProviderFor.class);

            if (annotation != null) {
                for (Class<?> service : annotation.value()) {
                    if (service.isAssignableFrom(provider)) {
                        map.computeIfAbsent(service.getName(), k -> new TreeSet<>())
                            .add(provider.getName());
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
