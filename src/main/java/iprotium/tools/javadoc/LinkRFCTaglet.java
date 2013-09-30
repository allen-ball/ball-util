/*
 * $Id$
 *
 * Copyright 2012, 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.tools.javadoc;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;
import iprotium.annotation.ServiceProviderFor;
import java.net.URI;
import java.util.Map;

import static java.lang.String.format;

/**
 * Inline {@link Taglet} providing links to external RFCs.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
@ServiceProviderFor({ Taglet.class })
public class LinkRFCTaglet extends AbstractTaglet {
    public static void register(Map<String,Taglet> map) {
        Taglet taglet = new LinkRFCTaglet();
        String key = taglet.getName();

        map.remove(key);
        map.put(key, taglet);
    }

    private static final String TEXT = "RFC%d";
    private static final String PATH = "/rfc/rfc%d.txt";

    /**
     * Sole constructor.
     */
    public LinkRFCTaglet() {
        super("link.rfc", true, true, true, true, true, true, true);
    }

    @Override
    public String toString(Tag tag) {
        String in = tag.text().trim();
        String out = in;

        try {
            int rfc = Integer.valueOf(in);

            out =
                toString(a(format(TEXT, rfc),
                           new URI("http", "www.ietf.org",
                                   format(PATH, rfc), null)));
        } catch (Exception exception) {
        }

        return out;
    }
}
