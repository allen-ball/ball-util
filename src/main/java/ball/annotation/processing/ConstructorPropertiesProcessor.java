/*
 * $Id$
 *
 * Copyright 2012 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.annotation.processing;

import ball.annotation.ServiceProviderFor;
import java.beans.ConstructorProperties;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.WARNING;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * {@link Processor} implementation to verify {@link ConstructorProperties}
 * annotation are actual bean properties of the {@link
 * java.lang.reflect.Constructor}'s {@link Class}.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Processor.class })
@For({ ConstructorProperties.class })
public class ConstructorPropertiesProcessor
             extends AbstractAnnotationProcessor {

    /**
     * Sole constructor.
     */
    public ConstructorPropertiesProcessor() { super(); }

    @Override
    public void process(RoundEnvironment roundEnv,
                        TypeElement annotation,
                        Element element) throws Exception {
        switch (element.getKind()) {
        case CONSTRUCTOR:
            String[] value =
                element.getAnnotation(ConstructorProperties.class).value();
            List<? extends VariableElement> parameters =
                ((ExecutableElement) element).getParameters();

            if (value.length != parameters.size()) {
                print(WARNING,
                      element,
                      Arrays.asList(value) + " does not match "
                      + element.getKind() + " parameters");
            }

            TypeElement type = (TypeElement) element.getEnclosingElement();
            Set<String> properties = getPropertyNames(type);

            Arrays.stream(value)
                .filter(t -> (! isEmpty(t)))
                .filter(t -> (! properties.contains(t)))
                .forEach(t -> print(WARNING,
                                    element,
                                    "bean property `" + t + "' not defined"));
            break;

        default:
            print(ERROR,
                  element,
                  element.getKind() + " annotated with "
                  + AT + annotation.getSimpleName());
            break;
        }
    }
}
