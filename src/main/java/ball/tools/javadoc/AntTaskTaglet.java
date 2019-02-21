/*
 * $Id$
 *
 * Copyright 2019 Allen D. Ball.  All rights reserved.
 */
package ball.tools.javadoc;

import ball.annotation.ServiceProviderFor;
import ball.util.ant.taskdefs.AntTask;
import ball.xml.FluentNode;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.tools.ant.IntrospectionHelper;
import org.apache.tools.ant.Task;
import org.w3c.dom.Node;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Inline {@link Taglet} to document {@link.uri http://ant.apache.org/ Ant}
 * {@link Task}s.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Taglet.class })
@TagletName("ant.task")
@NoArgsConstructor @ToString
public class AntTaskTaglet extends AbstractInlineTaglet
                            implements SunToolsInternalToolkitTaglet {
    private static final AntTaskTaglet INSTANCE = new AntTaskTaglet();

    public static void register(Map<Object,Object> map) {
        map.putIfAbsent(INSTANCE.getName(), INSTANCE);
    }

    private static final String ____ = "____";
    private static final char COLON = ':';

    private static final Object[][] CODEC = new Object[][] {
        { COLON, "-COLON-" },
        { '$', "-DOLLAR-" }
    };

    @Override
    public FluentNode toNode(Tag tag) throws Throwable {
        ClassDoc doc = null;
        String name = tag.text().trim();

        if (! isEmpty(name)) {
            doc = getClassDocFor(tag, name);
        } else {
            doc = getContainingClassDocFor(tag);
        }

        Class<?> type = getClassFor(doc);

        if (! Task.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException(type.getCanonicalName()
                                               + " is not a subclass of "
                                               + Task.class.getCanonicalName());
        }

        FluentNode node = div(attr("class", "block"));
        String[] tokens = render(template(tag, type)).split(____);

        for (int i = 0; i < tokens.length; i += 1) {
            if ((i % 2) == 0) {
                node.add(code(tokens[i]));
            } else {
                String[] subtokens = decode(tokens[i]);

                node.add(a(tag, subtokens[0], subtokens[1]));
            }
        }

        return node;
    }

    private FluentNode template(Tag tag, Class<?> type) {
        AntTask annotation = type.getAnnotation(AntTask.class);
        String name =
            (annotation != null) ? annotation.value() : type.getSimpleName();

        return type(0, new HashSet<>(), tag, new SimpleEntry<>(name, type));
    }

    private Node a(Tag tag, String name, String content) {
        return a(tag, name, isNotEmpty(content) ? code(content) : null);
    }

    private FluentNode type(int depth, Set<Map.Entry<?,?>> set,
                            Tag tag, Map.Entry<String,Class<?>> entry) {
        IntrospectionHelper helper =
            IntrospectionHelper.getHelper(entry.getValue());
        FluentNode node =
            element(encode(entry.getValue(), entry.getKey()))
            .add(attributes(tag, helper))
            .add(content(depth + 1, set, tag, helper));

        return node;
    }

    private Node[] attributes(Tag tag, IntrospectionHelper helper) {
        List<Node> list =
            helper.getAttributeMap().entrySet()
            .stream()
            .map(t -> attr(t.getKey(), encode(t.getValue(), null)))
            .collect(Collectors.toList());

        return list.toArray(new Node[] { });
    }

    private FluentNode content(int depth, Set<Map.Entry<?,?>> set,
                               Tag tag, IntrospectionHelper helper) {
        FluentNode node =
            fragment(helper.getNestedElementMap().entrySet()
                     .stream()
                     .map(t -> ((set.add(t) && depth < 3)
                                ? type(depth, set, tag, t)
                                : element(encode(t.getValue(),
                                                 t.getKey())).value("...")))
                     .collect(Collectors.toList()));

        if (helper.supportsCharacters()) {
            node.add(text("text"));
        }

        return node;
    }

    private String encode(Class<?> type, String name) {
        String string = type.getCanonicalName();

        if (isNotEmpty(name)) {
            string += String.valueOf(COLON) + name;
        }

        for (Object[] codec : CODEC) {
            string =
                string.replaceAll(Pattern.quote(codec[0].toString()),
                                  codec[1].toString());
        }

        return ____ + string + ____;
    }

    private String[] decode(String string) {
        for (Object[] codec : CODEC) {
            string =
                string.replaceAll(Pattern.quote(codec[1].toString()),
                                  codec[0].toString());
        }

        String[] tokens = string.split(String.valueOf(COLON), 2);

        return (tokens.length > 1) ? tokens : new String[] { tokens[0], null };
    }
}
