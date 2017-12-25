/*
 * $Id$
 *
 * Copyright 2014 - 2017 Allen D. Ball.  All rights reserved.
 */
package ball.tools.javadoc;

import ball.annotation.ServiceProviderFor;
import ball.xml.HTML;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.taglets.Taglet;
import com.sun.tools.doclets.internal.toolkit.taglets.TagletWriter;
import java.util.Map;
import java.util.regex.Pattern;
import org.w3c.dom.Element;

import static ball.util.StringUtil.NIL;

/**
 * Inline {@link Taglet} to include a {@link Map} value as a table.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Taglet.class })
@TagletName("include.map")
public class IncludeMapTaglet extends IncludeCollectionTaglet {
    public static void register(Map<String,Taglet> map) {
        register(IncludeMapTaglet.class, map);
    }

    /**
     * Sole constructor.
     */
    public IncludeMapTaglet() { super(); }

    @Override
    public Content getTagletOutput(Tag tag,
                                   TagletWriter writer) throws IllegalArgumentException {
        Element element = null;

        try {
            ClassDoc doc = getContainingClassDoc(tag.holder());
            String[] text = tag.text().trim().split(Pattern.quote("#"), 2);
            Class<?> type = getClassFor(getClassDoc(doc, text[0]));
            Map<?,?> map = (Map<?,?>) type.getField(text[1]).get(null);

            Element table = HTML.table(document);

            HTML.tr(table,
                    HTML.b(table.getOwnerDocument(), "Key"),
                    HTML.b(table.getOwnerDocument(), "Value"));

            for (Map.Entry<?,?> entry : map.entrySet()) {
                Element tr = HTML.tr(table, NIL, NIL);

                renderTo(doc, entry.getKey(), tr.getFirstChild());
                renderTo(doc, entry.getValue(), tr.getLastChild());
            }

            element = table;
        } catch (Exception exception) {
            throw new IllegalArgumentException(tag.position().toString(),
                                               exception);
        }

        return content(writer, element);
    }
}
