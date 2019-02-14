/*
 * $Id$
 *
 * Copyright 2014 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.tools.javadoc;

import ball.activation.ReaderWriterDataSource;
import ball.annotation.ServiceProviderFor;
import ball.xml.FluentNode;
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
import java.util.stream.Collectors;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.io.IOUtils;
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

        FluentNode node = null;
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
                node =
                    table(tr(th("Element")))
                    .add(((Collection<?>) resource)
                         .stream()
                         .map(t -> tr(td(node(doc, t))))
                         .collect(Collectors.toList()));
            } else if (resource instanceof Map) {
                node =
                    table(tr(th("Key"), th("Value")))
                    .add(((Map<?,?>) resource).entrySet()
                         .stream()
                         .map(t -> tr(td(node(doc, t.getKey())),
                                      td(node(doc, t.getValue()))))
                         .collect(Collectors.toList()));
            } else if (resource instanceof InputStream) {
                ReaderWriterDataSource ds =
                    new ReaderWriterDataSource(null, null);

                try (OutputStream out = ds.getOutputStream()) {
                    IOUtils.copy((InputStream) resource, out);
                }

                node = pre(ds.toString());
            } else {
                node = pre(String.valueOf(resource));
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

        return content(writer, node);
    }
}
