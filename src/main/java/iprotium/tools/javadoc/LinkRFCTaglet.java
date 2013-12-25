/*
 * $Id$
 *
 * Copyright 2012, 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.tools.javadoc;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.internal.toolkit.taglets.Taglet;
import com.sun.tools.doclets.internal.toolkit.taglets.TagletOutput;
import com.sun.tools.doclets.internal.toolkit.taglets.TagletWriter;
import iprotium.annotation.ServiceProviderFor;
import iprotium.xml.HTML;
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
public class LinkRFCTaglet extends AbstractInlineTaglet {
    public static void register(Map<String,Taglet> map) {
        register(LinkRFCTaglet.class, map);
    }

    private static final String TEXT = "RFC%d";
    private static final String PROTOCOL = "http";
    private static final String HOST = "www.ietf.org";
    private static final String PATH = "/rfc/rfc%d.txt";

    /**
     * Sole constructor.
     */
    public LinkRFCTaglet() { super(); }

    @Override
    public TagletOutput getTagletOutput(Tag tag,
                                        TagletWriter writer) throws IllegalArgumentException {
        Element element = null;

        try {
            int rfc = Integer.valueOf(tag.text().trim());

            element =
                HTML.a(document,
                       new URI(PROTOCOL, HOST, format(PATH, rfc), null),
                       format(TEXT, rfc));
        } catch (Exception exception) {
            throw new IllegalArgumentException(tag.position().toString(),
                                               exception);
        }

        return output(writer, element);
    }
}
