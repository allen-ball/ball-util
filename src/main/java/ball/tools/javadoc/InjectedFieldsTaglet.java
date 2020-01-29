/*
 * $Id$
 *
 * Copyright 2019, 2020 Allen D. Ball.  All rights reserved.
 */
package ball.tools.javadoc;

import ball.annotation.ServiceProviderFor;
import ball.xml.FluentNode;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Inline {@link Taglet} to provide a report of members whose values are
 * injected.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Taglet.class })
@TagletName("injected.fields")
@NoArgsConstructor @ToString
public class InjectedFieldsTaglet extends AbstractInlineTaglet
                                  implements SunToolsInternalToolkitTaglet {
    private static final InjectedFieldsTaglet INSTANCE =
        new InjectedFieldsTaglet();

    public static void register(Map<Object,Object> map) {
        map.putIfAbsent(INSTANCE.getName(), INSTANCE);
    }

    private static final String[] NAMES = new String[] {
        javax.annotation.Resource.class.getName(),
        javax.annotation.Resources.class.getName(),
        "javax.inject.Inject",
        "javax.inject.Named",
        "org.springframework.beans.factory.annotation.Autowired",
        "org.springframework.beans.factory.annotation.Value"
    };

    @Override
    public FluentNode toNode(Tag tag) throws Throwable {
        ClassDoc doc = null;
        String[] argv = tag.text().trim().split("[\\p{Space}]+", 2);

        if (isNotEmpty(argv[0])) {
            doc = getClassDocFor(tag, argv[0]);
        } else {
            doc = getContainingClassDocFor(tag);
        }

        Set<Class<? extends Annotation>> set = new HashSet<>();

        for (String name : NAMES) {
            Class<?> type = null;

            try {
                type = Class.forName(name);
            } catch (Exception exception) {
            }

            if (type != null) {
                Class<? extends Annotation> annotation =
                    type.asSubclass(Annotation.class);
                Retention retention =
                    annotation.getAnnotation(Retention.class);

                if (retention == null) {
                    throw new IllegalStateException(annotation.getCanonicalName()
                                                    + " does not specify a retention policy");
                }

                switch (retention.value()) {
                case RUNTIME:
                    break;

                case CLASS:
                case SOURCE:
                default:
                    throw new IllegalStateException(annotation.getCanonicalName()
                                                    + " specifies "
                                                    + retention.value()
                                                    + " retention policy");
                    /* break; */
                }

                set.add(annotation);
            }
        }

        if (set.isEmpty()) {
            throw new IllegalStateException("No annotations to map");
        }

        return div(attr("class", "summary"),
                   h3("Injected Field Summary"),
                   table(tag, getClassFor(doc), set));
    }

    private FluentNode table(Tag tag, Class<?> type,
                             Set<Class<? extends Annotation>> set) {
        return table(thead(tr(th("Annotation(s)"), th("Field"))),
                     tbody(Stream.of(type.getDeclaredFields())
                           .filter(t -> (Stream.of(t.getAnnotations())
                                         .filter(a -> set.contains(a.annotationType()))
                                         .findFirst().isPresent()))
                           .map(t -> tr(tag, t, set))));
    }

    private FluentNode tr(Tag tag, Field field,
                          Set<Class<? extends Annotation>> set) {
        return tr(td(fragment(Stream.of(field.getAnnotations())
                              .filter(t -> set.contains(t.annotationType()))
                              .map(t -> annotation(tag, t)))),
                  td(declaration(tag, field)));
    }
}
