/*
 * $Id$
 *
 * Copyright 2013 Allen D. Ball.  All rights reserved.
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
    public TagletOutput getTagletOutput(Tag tag, TagletWriter writer) throws IllegalArgumentException {
        TagletOutput output = writer.getOutputInstance();

        try {
            String[] argv = tag.text().trim().split("[\\p{Space}]+", 2);
            URI href = new URI(argv[0]);
            String text = (argv.length > 1) ? argv[1] : null;

            output.setOutput(toString(HTML.a(document, text, href)));
        } catch (Exception exception) {
            throw new IllegalArgumentException(tag.position().toString(),
                                               exception);
        }

        return output;
    }
}
