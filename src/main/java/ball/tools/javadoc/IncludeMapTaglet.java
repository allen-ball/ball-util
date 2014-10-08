/*
 * $Id$
 *
 * Copyright 2014 Allen D. Ball.  All rights reserved.
 */
package ball.tools.javadoc;

import ball.annotation.ServiceProviderFor;
import ball.xml.HTML;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.internal.toolkit.taglets.Taglet;
import com.sun.tools.doclets.internal.toolkit.taglets.TagletOutput;
import com.sun.tools.doclets.internal.toolkit.taglets.TagletWriter;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;
import org.w3c.dom.Element;

/**
 * Inline {@link Taglet} to include a {@link Map} value as a table.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Taglet.class })
@TagletName("include.map")
public class IncludeMapTaglet extends AbstractInlineTaglet {
    public static void register(Map<String,Taglet> map) {
        register(IncludeMapTaglet.class, map);
    }

    /**
     * Sole constructor.
     */
    public IncludeMapTaglet() { super(); }

    @Override
    public TagletOutput getTagletOutput(Tag tag,
                                        TagletWriter writer) throws IllegalArgumentException {
        Element element = null;

        try {
            String[] text = tag.text().trim().split(Pattern.quote("#"), 2);
            Class<?> type =
                getClassFor(getClassDoc(getContainingClassDoc(tag.holder()),
                                        text[0]));
            Map<?,?> map = (Map<?,?>) type.getField(text[1]).get(null);

            Element table = HTML.table(document);

            HTML.tr(table,
                    HTML.b(table.getOwnerDocument(), "Key"),
                    HTML.b(table.getOwnerDocument(), "Value"));

            for (Map.Entry<?,?> entry : map.entrySet()) {
                HTML.tr(table,
                        toString(entry.getKey()), toString(entry.getValue()));
            }

            element = table;
        } catch (Exception exception) {
            throw new IllegalArgumentException(tag.position().toString(),
                                               exception);
        }

        return output(writer, element);
    }

    private String toString(Object object) {
        String string = null;

        if (object instanceof byte[]) {
            string = toString((byte[]) object);
        } else if (object instanceof boolean[]) {
            string = Arrays.toString((boolean[]) object);
        } else if (object instanceof double[]) {
            string = Arrays.toString((double[]) object);
        } else if (object instanceof float[]) {
            string = Arrays.toString((float[]) object);
        } else if (object instanceof int[]) {
            string = Arrays.toString((int[]) object);
        } else if (object instanceof long[]) {
            string = Arrays.toString((long[]) object);
        } else if (object instanceof Object[]) {
            string = Arrays.toString((Object[]) object);
        } else {
            string = String.valueOf(object);
        }

        return string;
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
