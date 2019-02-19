/*
 * $Id$
 *
 * Copyright 2019 Allen D. Ball.  All rights reserved.
 */
package ball.tools.javadoc;

import com.sun.javadoc.Doc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.formats.html.markup.RawHtml;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.taglets.Taglet;
import com.sun.tools.doclets.internal.toolkit.taglets.TagletWriter;

/**
 * Default methods for legacy {@link Taglet} implementations.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public interface SunToolsInternalToolkitTaglet extends Taglet,
                                                       JavadocHTMLTemplates {
    void set(Configuration configuration);

    @Override
    default Content getTagletOutput(Doc doc,
                                    TagletWriter writer) throws IllegalArgumentException {
        set(writer.configuration());

        throw new IllegalArgumentException(doc.position() + ": "
                                           + "Method not supported in taglet "
                                           + getName() + ".");
    }

    @Override
    default Content getTagletOutput(Tag tag,
                                    TagletWriter writer) throws IllegalArgumentException {
        set(writer.configuration());

        Content content = writer.getOutputInstance();

        content.addContent(new RawHtml(((AbstractTaglet) this).toString(tag)));

        return content;
    }
}
