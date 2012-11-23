/*
 * $Id$
 *
 * Copyright 2012 Allen D. Ball.  All rights reserved.
 */
package iprotium.tools.javadoc;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;
import java.net.URI;
import java.util.Map;

/**
 * Inline {@link Taglet} providing links to external RFCs.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class LinkRFCTaglet extends AbstractTaglet {
    public static void register(Map<String,Taglet> map) {
        Taglet taglet = new LinkRFCTaglet();
        String key = taglet.getName();

        map.remove(key);
        map.put(key, taglet);
    }

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
                a("RFC " + String.valueOf(rfc),
                  new URI("http", "www.ietf.org",
                          "/rfc/rfc" + String.valueOf(rfc) + ".txt", null));
        } catch (Exception exception) {
        }

        return out;
    }
}
