/*
 * $Id$
 *
 * Copyright 2019 Allen D. Ball.  All rights reserved.
 */
package ball.tools.javadoc;

import ball.annotation.ServiceProviderFor;
import ball.xml.FluentNode;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static org.apache.commons.lang3.StringUtils.isEmpty;

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
        "javax.inject.Inject",
        "org.springframework.beans.factory.annotation.Autowired",
        "org.springframework.beans.factory.annotation.Value"
    };

    public static final Set<Class<? extends Annotation>> ANNOTATIONS;

    static {
        HashSet<Class<? extends Annotation>> set = new HashSet<>();

        for (String name : NAMES) {
            try {
                set.add(Class.forName(name).asSubclass(Annotation.class));
            } catch (Exception exception) {
            }
        }

        ANNOTATIONS = Collections.unmodifiableSet(set);
    }

    @Override
    public FluentNode toNode(Tag tag) throws Throwable {
        if (ANNOTATIONS.isEmpty()) {
            throw new Error("No annotations to map");
        }

        ClassDoc doc = null;
        String[] argv = tag.text().trim().split("[\\p{Space}]+", 2);

        if (! isEmpty(argv[0])) {
            doc = getClassDocFor(tag, argv[0]);
        } else {
            doc = getContainingClassDocFor(tag);
        }

        return table(tag, getClassFor(doc));
    }

    private FluentNode table(Tag tag, Class<?> type) {
        return table(tr(th("Annotation(s)"), th("Field")),
                     fragment(Arrays.stream(type.getDeclaredFields())
                              .filter(t -> (Arrays.stream(t.getAnnotations())
                                            .filter(a -> ANNOTATIONS.contains(a.annotationType()))
                                            .findFirst().isPresent()))
                              .map(t -> tr(tag, t))));
    }

    private FluentNode tr(Tag tag, Field field) {
        return tr(td(fragment(Arrays.stream(field.getAnnotations())
                              .filter(t -> ANNOTATIONS.contains(t.annotationType()))
                              .map(t -> a(tag, t)))),
                  td(declaration(tag, field)));
    }
}
