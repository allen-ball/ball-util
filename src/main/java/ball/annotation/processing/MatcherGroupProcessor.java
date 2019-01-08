/*
 * $Id$
 *
 * Copyright 2019 Allen D. Ball.  All rights reserved.
 */
package ball.annotation.processing;

import ball.annotation.MatcherGroup;
import ball.annotation.ServiceProviderFor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.tools.Diagnostic.Kind.ERROR;

/**
 * {@link Processor} implementation to check {@link MatcherGroup}
 * annotations.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Processor.class })
@For({ MatcherGroup.class })
@NoArgsConstructor @ToString
public class MatcherGroupProcessor extends AbstractAnnotationProcessor {
    @Override
    protected void process(RoundEnvironment env,
                           TypeElement annotation,
                           Element element) throws Exception {
        int group = element.getAnnotation(MatcherGroup.class).value();

        if (group < 0) {
            print(ERROR,
                  element,
                  element.getKind() + " annotated with "
                  + AT + annotation.getSimpleName()
                  + " has invalid value()");
        }

        switch (element.getKind()) {
        case ANNOTATION_TYPE:
        case FIELD:
        default:
            break;

        case METHOD:
            ExecutableElement executable = (ExecutableElement) element;

            if (! element.getModifiers().contains(PRIVATE)) {
                if (executable.isVarArgs() || executable.getParameters().size() != 1) {
                    print(ERROR,
                          element,
                          element.getKind() + " annotated with "
                          + AT + annotation.getSimpleName()
                          + " but does not take exactly one argument");
                }
            } else {
                print(ERROR,
                      element,
                      element.getKind() + " annotated with "
                      + AT + annotation.getSimpleName()
                      + " but is " + PRIVATE);
            }
            break;
        }
    }
}
