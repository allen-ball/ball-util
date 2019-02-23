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
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;
import java.io.StringWriter;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.tools.ant.IntrospectionHelper;
import org.apache.tools.ant.Task;
import org.w3c.dom.Node;

import static javax.xml.transform.OutputKeys.INDENT;
import static javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.repeat;

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

    private static final String NO = "no";
    private static final String YES = "yes";

    private static final String INDENT_AMOUNT =
        "{http://xml.apache.org/xslt}indent-amount";
    private static final String INDENTATION = "  ";

    private final Transformer transformer;

    {
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OMIT_XML_DECLARATION, YES);
            transformer.setOutputProperty(INDENT, YES);
            transformer.setOutputProperty(INDENT_AMOUNT, String.valueOf(INDENTATION.length()));
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static final String DOCUMENTED = "DOCUMENTED";

    @Override
    public String toString(Tag tag) throws IllegalStateException {
        String string = null;

        try {
            StringWriter writer = new StringWriter();

            transformer
                .transform(new DOMSource(toNode(tag)),
                           new StreamResult(writer));

            String template =
                writer.toString()
                .replaceAll(Pattern.quote(DOCUMENTED + "=\"\""), "...");

            string =
                render(div(attr("class", "block"),
                           pre("xml", template)));
        } catch (IllegalStateException exception) {
            throw exception;
        } catch (Throwable throwable) {
            string = render(warning(tag, throwable));
        }

        return string;
    }

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

        return template(tag, type);
    }

    private FluentNode template(Tag tag, Class<?> type) {
        AntTask annotation = type.getAnnotation(AntTask.class);
        String name =
            (annotation != null) ? annotation.value() : type.getSimpleName();

        return type(0, new HashSet<>(), tag, new SimpleEntry<>(name, type));
    }

    private FluentNode type(int depth, Set<Map.Entry<?,?>> set,
                            Tag tag, Map.Entry<String,Class<?>> entry) {
        IntrospectionHelper helper =
            IntrospectionHelper.getHelper(entry.getValue());
        FluentNode node = element(entry.getKey());

        if (set.add(entry)
            && (! entry.getValue().getName()
                  .startsWith(Task.class.getPackage().getName()))) {
            node
                .add(attributes(tag, helper))
                .add(content(depth + 1, set, tag, helper));

            if (helper.supportsCharacters()) {
                String content = "... text ...";

                if (node.hasChildNodes()) {
                    content =
                        "\n" + repeat(INDENTATION, depth + 1) + content + "\n";
                }

                node.add(text(content));
            }
        } else {
            node.add(attr(DOCUMENTED));
        }

        return node;
    }

    private Node[] attributes(Tag tag, IntrospectionHelper helper) {
        List<Node> list =
            helper.getAttributeMap().entrySet()
            .stream()
            .map(t -> attr(t.getKey(), t.getValue().getSimpleName()))
            .collect(Collectors.toList());

        return list.toArray(new Node[] { });
    }

    private FluentNode content(int depth, Set<Map.Entry<?,?>> set,
                               Tag tag, IntrospectionHelper helper) {
        FluentNode node =
            fragment(helper.getNestedElementMap().entrySet()
                     .stream()
                     .map(t -> type(depth, set, tag, t))
                     .collect(Collectors.toList()));

        return node;
    }
}
