/*
 * $Id$
 *
 * Copyright 2012 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.annotation.processing;

import ball.annotation.ServiceProviderFor;
import ball.util.StringUtil;
import java.beans.ConstructorProperties;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import static javax.lang.model.element.ElementKind.CONSTRUCTOR;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.util.ElementFilter.methodsIn;
import static javax.tools.Diagnostic.Kind.WARNING;

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
        if (element.getKind() == CONSTRUCTOR) {
            String[] value =
                element.getAnnotation(ConstructorProperties.class).value();
            List<? extends VariableElement> parameters =
                ((ExecutableElement) element).getParameters();

            if (value.length == parameters.size()) {
                TypeElement type = (TypeElement) element.getEnclosingElement();
                Set<String> properties = getPropertyNames(type);

                for (String property : value) {
                    if (! StringUtil.isNil(property)) {
                        if (! properties.contains(property)) {
                            print(WARNING,
                                  element,
                                  "bean property `"
                                  + property + "' not defined");
                        }
                    }
                }
            } else {
                print(WARNING,
                      element,
                      Arrays.asList(value) + " does not match "
                      + element.getKind() + " parameters");
            }
        } else {
            /*
             * compiler.err.annotation.type.not.applicable
             *
             * error(element,
             *       "annotation type not applicable"
             *       + " to this kind of declaration");
             */
        }
    }
}
