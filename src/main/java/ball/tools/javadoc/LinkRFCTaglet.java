/*
 * $Id$
 *
 * Copyright 2012 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.tools.javadoc;

import ball.annotation.ServiceProviderFor;
import ball.xml.FluentNode;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;
import java.net.URI;
import java.util.Map;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Inline {@link Taglet} providing links to external RFCs.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Taglet.class })
@TagletName("link.rfc")
@NoArgsConstructor @ToString
public class LinkRFCTaglet extends AbstractInlineTaglet
                           implements SunToolsInternalToolkitTaglet {
    private static final LinkRFCTaglet INSTANCE = new LinkRFCTaglet();

    public static void register(Map<Object,Object> map) {
        map.putIfAbsent(INSTANCE.getName(), INSTANCE);
    }

    private static final String TEXT = "RFC%d";
    private static final String PROTOCOL = "https";
    private static final String HOST = "www.rfc-editor.org";
    private static final String PATH = "/rfc/rfc%d.txt";

    @Override
    public FluentNode toNode(Tag tag) throws Throwable {
        FluentNode node = fragment();
        int rfc = Integer.valueOf(tag.text().trim());

        node =
            a(new URI(PROTOCOL, HOST, String.format(PATH, rfc), null),
              String.format(TEXT, rfc))
            .add(attr("target", "newtab"));

        return node;
    }
}
