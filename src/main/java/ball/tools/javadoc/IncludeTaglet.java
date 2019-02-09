/*
 * $Id$
 *
 * Copyright 2014 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.tools.javadoc;

import ball.activation.ReaderWriterDataSource;
import ball.annotation.ServiceProviderFor;
import ball.xml.HTML;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.taglets.TagletWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Inline {@link Taglet} to include a static {@link Class} member or
 * resource in the Javadoc output.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Taglet.class })
@TagletName("include")
@NoArgsConstructor @ToString
public class IncludeTaglet extends AbstractInlineTaglet
                           implements SunToolsInternalToolkitTaglet {
    private static final IncludeTaglet INSTANCE = new IncludeTaglet();

    public static void register(Map<String,Taglet> map) {
        map.putIfAbsent(INSTANCE.getName(), INSTANCE);
    }

    @Override
    public Content getTagletOutput(Tag tag,
                                   TagletWriter writer) throws IllegalArgumentException {
        /* this.configuration = writer.configuration(); */

        Element element = null;
        Object resource = null;

        try {
            String[] text = tag.text().trim().split(Pattern.quote("#"), 2);
            ClassDoc doc = getContainingClassDoc(tag.holder());

            if (text.length > 1) {
                Class<?> type =
                    getClassFor((! isEmpty(text[0]))
                                    ? getClassDoc(doc, text[0])
                                    : doc);

                resource = type.getField(text[1]).get(null);
            } else {
                Class<?> type = getClassFor(doc);

                resource =
                    ((type != null) ? type : getClass())
                    .getResourceAsStream(text[0]);
            }

            if (resource == null) {
                throw new IllegalArgumentException(tag.text());
            }

            if (resource instanceof Collection) {
                Collection<?> collection = (Collection<?>) resource;

                element = HTML.table(DOCUMENT);

                HTML.tr(element,
                        HTML.b(element.getOwnerDocument(), "Element"));

                for (Object object : collection) {
                    Element tr = HTML.tr(element, EMPTY);

                    renderTo(doc, object, tr.getFirstChild());
                }
            } else if (resource instanceof Map) {
                Map<?,?> map = (Map<?,?>) resource;

                element = HTML.table(DOCUMENT);

                HTML.tr(element,
                        HTML.b(element.getOwnerDocument(), "Key"),
                        HTML.b(element.getOwnerDocument(), "Value"));

                for (Map.Entry<?,?> entry : map.entrySet()) {
                    Element tr = HTML.tr(element, EMPTY, EMPTY);

                    renderTo(doc, entry.getKey(), tr.getFirstChild());
                    renderTo(doc, entry.getValue(), tr.getLastChild());
                }
            } else if (resource instanceof InputStream) {
                ReaderWriterDataSource ds =
                    new ReaderWriterDataSource(null, null);

                try (OutputStream out = ds.getOutputStream()) {
                    IOUtils.copy((InputStream) resource, out);
                }

                element = HTML.pre(DOCUMENT, ds.toString());
            } else {
                element = HTML.pre(DOCUMENT, String.valueOf(resource));
            }
        } catch (Exception exception) {
            throw new IllegalArgumentException(tag.position().toString(),
                                               exception);
        } finally {
            if (resource instanceof Closeable) {
                try {
                    ((Closeable) resource).close();
                } catch (IOException exception) {
                }
            }
        }

        return content(writer, element);
    }

    private void renderTo(ClassDoc doc, Object object, Node node) {
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
            Class<?> type = (Class<?>) object;
            Object link = getClassDocLink(doc, type);

            if (link instanceof Node) {
                node.appendChild((Node) link);
            } else {
                append(node, type.getCanonicalName());
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
