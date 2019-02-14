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
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.io.IOUtils;

import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Inline {@link Taglet} to include a static {@link Class} {@link Field} or
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
        this.configuration = writer.configuration();

        FluentNode node = null;

        try {
            ClassDoc doc = getContainingClassDoc(tag.holder());
            String[] text = tag.text().trim().split(Pattern.quote("#"), 2);

            if (text.length > 1) {
                Class<?> type =
                    getClassFor((! isEmpty(text[0]))
                                    ? getClassDoc(doc, text[0])
                                    : doc);

                node = node(doc, type.getField(text[1]));
            } else {
                Class<?> type = getClassFor(doc);

                node =
                    node(doc,
                         ((type != null) ? type : getClass())
                         .getResourceAsStream(text[0]));
            }
        } catch (Exception exception) {
            throw new IllegalArgumentException(tag.position().toString(),
                                               exception);
        }

        return content(writer, node);
    }

    private FluentNode node(ClassDoc doc, Field field) throws Exception {
        Object object = field.get(null);
        FluentNode node = null;

        if (object instanceof Collection) {
            node =
                table(tr(th("Element")))
                .add(((Collection<?>) object)
                     .stream()
                     .map(t -> tr(td(node(doc, t))))
                     .collect(Collectors.toList()));
        } else if (object instanceof Map) {
            node =
                table(tr(th("Key"), th("Value")))
                .add(((Map<?,?>) object).entrySet()
                     .stream()
                     .map(t -> tr(td(node(doc, t.getKey())),
                                  td(node(doc, t.getValue()))))
                     .collect(Collectors.toList()));
        } else {
            node = pre(String.valueOf(object));
        }

        return node;
    }

    private FluentNode node(ClassDoc doc, InputStream in) throws Exception {
        ReaderWriterDataSource ds =
            new ReaderWriterDataSource(null, null);

        try (OutputStream out = ds.getOutputStream()) {
            IOUtils.copy(in, out);
        }

        return pre(ds.toString());
    }
}
