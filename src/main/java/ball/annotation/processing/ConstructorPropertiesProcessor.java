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
import java.beans.ConstructorProperties;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import static java.util.stream.Collectors.toList;
import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.WARNING;

/**
 * {@link Processor} implementation to verify {@link ConstructorProperties}
 * annotation are actual bean properties of the
 * {@link java.lang.reflect.Constructor}'s {@link Class}.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 */
@ServiceProviderFor({ Processor.class })
@For({ ConstructorProperties.class })
@NoArgsConstructor @ToString
public class ConstructorPropertiesProcessor extends AnnotatedProcessor {
    @Override
    public void process(RoundEnvironment roundEnv, TypeElement annotation, Element element) {
        super.process(roundEnv, annotation, element);

        AnnotationMirror mirror = getAnnotationMirror(element, annotation);
        AnnotationValue value = getAnnotationValue(mirror, "value");
        List<String> names =
            Stream.of(value)
            .filter(Objects::nonNull)
            .map(t -> (List<?>) t.getValue())
            .flatMap(List::stream)
            .map(t -> (AnnotationValue) t)
            .map(t -> (String) t.getValue())
            .collect(toList());
        List<? extends VariableElement> parameters = ((ExecutableElement) element).getParameters();

        if (names.size() != parameters.size()) {
            print(WARNING, element, mirror, value, "value() does not match %s parameters", element.getKind());
        }

        TypeElement type = (TypeElement) element.getEnclosingElement();
        Set<String> properties = getPropertyNames(type);

        names.stream()
            .filter(StringUtils::isNotEmpty)
            .filter(t -> (! properties.contains(t)))
            .forEach(t -> print(WARNING, element, mirror, "bean property '%s' not defined", t));
    }
}
