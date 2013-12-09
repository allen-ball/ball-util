/*
 * $Id$
 *
 * Copyright 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.tools.javadoc;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;
import iprotium.annotation.ServiceProviderFor;
import java.net.URI;
import java.util.Map;

/**
 * Inline {@link Taglet} to provide external links.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Taglet.class })
@TagletName("link.uri")
public class LinkURITaglet extends AbstractTaglet {
    public static void register(Map<String,Taglet> map) {
        register(LinkURITaglet.class, map);
    }

    /**
     * Sole constructor.
     */
    public LinkURITaglet() { super(true, true, true, true, true, true, true); }

    @Override
    public String toString(Tag tag) {
        String in = tag.text().trim();
        String out = in;

        try {
            String[] argv = in.split("[\\p{Space}]+", 2);
            URI href = new URI(argv[0]);
            String text = (argv.length > 1) ? argv[1] : null;

            out = toString(a(text, href));
        } catch (Exception exception) {
        }

        return out;
    }
}
