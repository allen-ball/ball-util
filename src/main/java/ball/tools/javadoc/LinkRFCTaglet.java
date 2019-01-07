/*
 * $Id$
 *
 * Copyright 2012 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.tools.javadoc;

import ball.annotation.ServiceProviderFor;
import ball.xml.HTML;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.taglets.TagletWriter;
import java.net.URI;
import java.util.Map;
import org.w3c.dom.Element;

import static java.lang.String.format;

/**
 * Inline {@link Taglet} providing links to external RFCs.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Taglet.class })
@TagletName("link.rfc")
public class LinkRFCTaglet extends AbstractInlineTaglet
                           implements SunToolsInternalToolkitTaglet {
    private static final LinkRFCTaglet INSTANCE = new LinkRFCTaglet();

    public static void register(Map<String,Taglet> map) {
        map.putIfAbsent(INSTANCE.getName(), INSTANCE);
    }

    private static final String TEXT = "RFC%d";
    private static final String PROTOCOL = "https";
    private static final String HOST = "www.rfc-editor.org";
    private static final String PATH = "/rfc/rfc%d.txt";

    /**
     * Sole constructor.
     */
    public LinkRFCTaglet() { super(); }

    @Override
    public Content getTagletOutput(Tag tag,
                                   TagletWriter writer) throws IllegalArgumentException {
        this.configuration = writer.configuration();

        Element element = null;

        try {
            int rfc = Integer.valueOf(tag.text().trim());

            element =
                HTML.a(DOCUMENT,
                       new URI(PROTOCOL, HOST, format(PATH, rfc), null),
                       format(TEXT, rfc));
        } catch (Exception exception) {
            throw new IllegalArgumentException(tag.position().toString(),
                                               exception);
        }

        return content(writer, element);
    }
}
