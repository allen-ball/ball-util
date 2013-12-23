/*
 * $Id$
 *
 * Copyright 2012, 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.annotation.processing;

import iprotium.annotation.ServiceProviderFor;
import iprotium.util.StringUtil;
import java.beans.ConstructorProperties;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import static java.beans.Introspector.decapitalize;
import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.CONSTRUCTOR;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.type.TypeKind.BOOLEAN;
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
public class ConstructorPropertiesProcessor
             extends AbstractAnnotationProcessor {
    private static final String GET = "get";
    private static final String IS = "is";

    /**
     * Sole constructor.
     */
    public ConstructorPropertiesProcessor() {
        super(ConstructorProperties.class);
    }

    @Override
    public void process(RoundEnvironment roundEnv,
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

    private Set<String> getPropertyNames(TypeElement type) {
        TreeSet<String> set = new TreeSet<String>();

        getPropertyNames(set, type);

        return set;
    }

    private void getPropertyNames(Set<String> set, TypeElement type) {
        for (ExecutableElement element :
                 methodsIn(type.getEnclosedElements())) {
            if ((! element.getModifiers().contains(PRIVATE))
                && element.getParameters().isEmpty()
                && (! types.getNullType().equals(element.getReturnType()))) {
                String name = element.getSimpleName().toString();

                if (name.startsWith(IS)) {
                    if (element.getReturnType().getKind() == BOOLEAN) {
                        set.add(decapitalize(name.substring(IS.length())));
                    }
                }

                if (name.startsWith(GET)) {
                    set.add(decapitalize(name.substring(GET.length())));
                }
            }
        }

        Element superclass = types.asElement(type.getSuperclass());

        if (superclass != null && superclass.getKind() == CLASS) {
            getPropertyNames(set, (TypeElement) superclass);
        }
    }
}
