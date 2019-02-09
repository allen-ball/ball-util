/*
 * $Id$
 *
 * Copyright 2013 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.tools.javadoc;

import ball.annotation.ServiceProviderFor;
import ball.xml.HTML;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.taglets.TagletWriter;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.w3c.dom.Element;

/**
 * Inline {@link Taglet} to provide external links.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Taglet.class })
@TagletName("link.uri")
@NoArgsConstructor @ToString
public class LinkURITaglet extends AbstractInlineTaglet
                           implements SunToolsInternalToolkitTaglet {
    private static final LinkURITaglet INSTANCE = new LinkURITaglet();

    public static void register(Map<String,Taglet> map) {
        map.putIfAbsent(INSTANCE.getName(), INSTANCE);
    }

    private static final String SPACES = "[\\p{Space}]+";

    @Override
    public Content getTagletOutput(Tag tag,
                                   TagletWriter writer) throws IllegalArgumentException {
        this.configuration = writer.configuration();

        Element element = null;

        try {
            String text = tag.text().trim();
            String[] argv = text.split(SPACES, 2);
            URI href = new URI(argv[0]);

            text = (argv.length > 1) ? argv[1] : null;

            LinkedHashMap<String,String> map = new LinkedHashMap<>();

            for (;;) {
                argv = text.split(SPACES, 2);

                String[] nvp = argv[0].split("=", 2);

                if (argv.length > 1 && nvp.length > 1) {
                    map.put(nvp[0], nvp[1]);
                    text = argv[1];
                } else {
                    break;
                }
            }

            element = HTML.a(DOCUMENT, href, text);

            for (Map.Entry<String,String> entry : map.entrySet()) {
                element.setAttribute(entry.getKey(), entry.getValue());
            }
        } catch (Exception exception) {
            throw new IllegalArgumentException(tag.position().toString(),
                                               exception);
        }

        return content(writer, element);
    }
}
