/*
 * $Id$
 *
 * Copyright 2018 Allen D. Ball.  All rights reserved.
 */
package ball.annotation.processing;

import javax.lang.model.element.TypeElement;
import ball.annotation.ServiceProviderFor;
import com.sun.tools.doclets.Taglet;
import java.lang.reflect.Method;
import java.util.Map;
import javax.annotation.processing.Processor;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.tools.Diagnostic.Kind.WARNING;

/**
 * {@link Processor} implementation to verify concrete implementations of
 * {@link Taglet} implement the required static {@code register()} method.
 * See
 * {@link.uri https://docs.oracle.com/javase/8/docs/technotes/guides/javadoc/taglet/overview.html Taglet Overview}
 * and
 * {@link.uri https://docs.oracle.com/javase/8/docs/jdk/api/javadoc/taglet/com/sun/tools/doclets/Taglet.html Taglet}.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Processor.class })
@ForElementKinds({ CLASS })
@ForSubclassesOf(Taglet.class)
public class TagletProcessor extends AbstractNoAnnotationProcessor {
    private static abstract class PROTOTYPE {
        public static void register(Map<String,Taglet> map) { }
    }

    private static final Method METHOD =
        PROTOTYPE.class.getDeclaredMethods()[0];

    /**
     * Sole constructor.
     */
    public TagletProcessor() { super(); }

    @Override
    protected void process(Element element) {
        if (! element.getModifiers().contains(ABSTRACT)) {
            TypeElement type = (TypeElement) element;
            ExecutableElement method =
                getExecutableElementFor(type, METHOD);

            if (! (method != null
                   && (method.getModifiers()
                       .containsAll(getModifierSetFor(METHOD))))) {
                print(WARNING,
                      element,
                      element.getKind()
                      + " implements " + Taglet.class.getName()
                      + " but does not implement `" + toString(METHOD) + "'");
            }
        }
    }
}
