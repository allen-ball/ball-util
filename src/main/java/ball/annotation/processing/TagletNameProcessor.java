/*
 * $Id$
 *
 * Copyright 2013 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.annotation.processing;

import ball.annotation.ServiceProviderFor;
import ball.tools.javadoc.TagletName;
import com.sun.tools.doclets.internal.toolkit.taglets.Taglet;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.tools.Diagnostic.Kind.ERROR;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * {@link Processor} implementation to check {@link Class}es annotated with
 * {@link TagletName}:
 * <ol>
 *   <li value="1">
 *     Are an instance of the legacy {@link com.sun.tools.doclets.Taglet} or
 *     the Sun internal {@link Taglet},
 *   </li>
 *   <li value="2">Concrete, and</li>
 *   <li value="3">Have a public no-argument constructor</li>
 * </ol>
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Processor.class })
@For({ TagletName.class })
public class TagletNameProcessor extends AbstractAnnotationProcessor {

    /**
     * Sole constructor.
     */
    public TagletNameProcessor() { super(); }

    @Override
    protected void process(RoundEnvironment env,
                           TypeElement annotation,
                           Element element) throws Exception {
        String name = element.getAnnotation(TagletName.class).value();

        if (! isEmpty(name)) {
            if (isAssignable(element.asType(), Taglet.class)) {
                if (! element.getModifiers().contains(ABSTRACT)) {
                    if (! hasPublicNoArgumentConstructor(element)) {
                        print(ERROR,
                              element,
                              element.getKind() + " annotated with "
                              + AT + annotation.getSimpleName()
                              + " but does not have a " + PUBLIC
                              + " no-argument constructor");
                    }
                } else {
                    print(ERROR,
                          element,
                          element.getKind() + " annotated with "
                          + AT + annotation.getSimpleName()
                          + " but is " + ABSTRACT);
                }
            } else {
                print(ERROR,
                      element,
                      element.getKind() + " annotated with "
                      + AT + annotation.getSimpleName()
                      + " but does not implement" + Taglet.class.getName());
            }
        } else {
            print(ERROR,
                  element,
                  element.getKind() + " annotated with "
                  + AT + annotation.getSimpleName()
                  + " but does not specify value()");
        }
    }
}
