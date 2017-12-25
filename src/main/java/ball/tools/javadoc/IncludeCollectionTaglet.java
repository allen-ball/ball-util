/*
 * $Id$
 *
 * Copyright 2017 Allen D. Ball.  All rights reserved.
 */
package ball.tools.javadoc;

import ball.annotation.ServiceProviderFor;
import ball.xml.HTML;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.taglets.Taglet;
import com.sun.tools.doclets.internal.toolkit.taglets.TagletWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import static ball.util.StringUtil.NIL;

/**
 * Inline {@link Taglet} to include a {@link Collection} value as a table.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Taglet.class })
@TagletName("include.collection")
public class IncludeCollectionTaglet extends AbstractInlineTaglet {
    public static void register(Map<String,Taglet> map) {
        register(IncludeCollectionTaglet.class, map);
    }

    /**
     * Sole constructor.
     */
    public IncludeCollectionTaglet() { super(); }

    @Override
    public Content getTagletOutput(Tag tag,
                                   TagletWriter writer) throws IllegalArgumentException {
        Element element = null;

        try {
            ClassDoc doc = getContainingClassDoc(tag.holder());
            String[] text = tag.text().trim().split(Pattern.quote("#"), 2);
            Class<?> type = getClassFor(getClassDoc(doc, text[0]));
            Collection<?> collection =
                (Collection<?>) type.getField(text[1]).get(null);

            Element table = HTML.table(document);

            HTML.tr(table,
                    HTML.b(table.getOwnerDocument(), "Element"));

            for (Object object : collection) {
                Element tr = HTML.tr(table, NIL);

                renderTo(doc, object, tr.getFirstChild());
            }

            element = table;
        } catch (Exception exception) {
            throw new IllegalArgumentException(tag.position().toString(),
                                               exception);
        }

        return content(writer, element);
    }

    protected void renderTo(ClassDoc doc, Object object, Node node) {
        if (object instanceof byte[]) {
            append(node, toString((byte[]) object));
        } else if (object instanceof boolean[]) {
            append(node, Arrays.toString((boolean[]) object));
        } else if (object instanceof double[]) {
            append(node, Arrays.toString((double[]) object));
        } else if (object instanceof float[]) {
            append(node, Arrays.toString((float[]) object));
        } else if (object instanceof int[]) {
            append(node, Arrays.toString((int[]) object));
        } else if (object instanceof long[]) {
            append(node, Arrays.toString((long[]) object));
        } else if (object instanceof Object[]) {
            append(node, "[");

            Object[] array = (Object[]) object;

            for (int i = 0; i < array.length; i += 1) {
                if (i > 0) {
                    append(node, ", ");
                }

                renderTo(doc, array[i], node);
            }

            append(node, "]");
        } else if (object instanceof Class<?>) {
            Object link = getClassDocLink(doc, (Class<?>) object);

            if (link instanceof Node) {
                node.appendChild((Node) link);
            } else {
                append(node, String.valueOf(link));
            }
        } else if (object instanceof Enum<?>) {
            Object link = getEnumDocLink(doc, (Enum<?>) object);

            if (link instanceof Node) {
                node.appendChild((Node) link);
            } else {
                append(node, String.valueOf(link));
            }
        } else if (object instanceof Collection<?>) {
            renderTo(doc, ((Collection<?>) object).toArray(new Object[] { }),
                     node);
        } else {
            append(node, String.valueOf(object));
        }
    }

    private void append(Node node, String string) {
        node.appendChild(node.getOwnerDocument().createTextNode(string));
    }

    private String toString(byte[] bytes) {
        StringBuilder buffer = new StringBuilder();

        buffer.append("[");

        for (int i = 0; i < bytes.length; i += 1) {
            if (i > 0) {
                buffer.append(", ");
            }

            buffer.append(String.format("0x%02X", bytes[i]));
        }

        buffer.append("]");

        return buffer.toString();
    }
}
