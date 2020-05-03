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
import ball.annotation.Regex;
import ball.annotation.ServiceProviderFor;
import java.util.regex.Pattern;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static javax.tools.Diagnostic.Kind.ERROR;

/**
 * {@link Processor} implementation to verify {@link String} initializers
 * marked by the {@link Regex} annotation are valid {@link Pattern}s.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Processor.class })
@For({ Regex.class })
@NoArgsConstructor @ToString
public class RegexProcessor extends AnnotatedProcessor {
    @Override
    public void process(RoundEnvironment roundEnv,
                        TypeElement annotation, Element element) {
        Object regex = ((VariableElement) element).getConstantValue();

        if (regex != null) {
            if (regex instanceof String) {
                String string = (String) regex;

                try {
                    Pattern.compile(string);
                } catch (Exception exception) {
                    print(ERROR, element,
                          "%s annotated with @%s but cannot convert '%s' to %s",
                          element.getKind(), annotation.getSimpleName(),
                          string, Pattern.class.getName());
                    print(ERROR, element, exception);
                }
            } else {
                print(ERROR, element,
                      "Constant value is not %s",
                      String.class.getSimpleName());
            }
        }
    }
}
