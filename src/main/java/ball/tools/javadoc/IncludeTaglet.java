/*
 * $Id$
 *
 * Copyright 2014 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.tools.javadoc;

import ball.annotation.ServiceProviderFor;
import ball.xml.FluentNode;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.io.IOUtils;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Inline {@link Taglet} to include a static {@link Class}
 * {@link java.lang.reflect.Field} or resource in the Javadoc output.
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

    public static void register(Map<Object,Object> map) {
        map.putIfAbsent(INSTANCE.getName(), INSTANCE);
    }

    @Override
    public FluentNode toNode(Tag tag) throws Throwable {
        FluentNode node = null;
        String[] text = tag.text().trim().split(Pattern.quote("#"), 2);

        if (text.length > 1) {
            node =
                field(tag,
                      getClassFor(isNotEmpty(text[0])
                                      ? getClassDocFor(tag, text[0])
                                      : getContainingClassDocFor(tag)),
                      text[1]);
        } else {
            node =
                resource(tag,
                         getClassFor(getContainingClassDocFor(tag)),
                         text[0]);
        }

        return node;
    }

    private FluentNode field(Tag tag,
                             Class<?> type, String name) throws Exception {
        Object object = type.getField(name).get(null);
        FluentNode node = null;

        if (object instanceof Collection) {
            node =
                table(tr(th("Element")),
                      fragment(((Collection<?>) object)
                               .stream()
                               .map(t -> tr(td(toHTML(tag, t))))));
        } else if (object instanceof Map) {
            node =
                table(tr(th("Key"), th("Value")),
                      fragment(((Map<?,?>) object).entrySet()
                               .stream()
                               .map(t -> tr(td(toHTML(tag, t.getKey())),
                                            td(toHTML(tag, t.getValue()))))));
        } else {
            node = pre(String.valueOf(object));
        }

        return node;
    }

    private FluentNode resource(Tag tag,
                                Class<?> type, String name) throws Exception {
        String string = null;

        if (type == null) {
            type = getClass();
        }

        try (InputStream in = type.getResourceAsStream(name)) {
            string = IOUtils.toString(in, "UTF-8");
        }

        return pre(string);
    }
}
