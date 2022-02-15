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
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.annotation.processing.Processor;
import javax.lang.model.element.ElementKind;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * {@link java.lang.annotation.Annotation} to test an Annotated element is a
 * specific {@link ElementKind kind}.
 *
 * @see AnnotatedProcessor
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 */
@Documented
@Retention(RUNTIME)
@Target(ANNOTATION_TYPE)
public @interface TargetMustBe {
    ElementKind value();

    /**
     * {@link Processor} implementation.
     */
    @ServiceProviderFor({ Processor.class })
    @For({ TargetMustBe.class })
    @NoArgsConstructor @ToString
    public static class ProcessorImpl extends AnnotatedProcessor {
    }
}
