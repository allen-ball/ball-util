/*
 * $Id$
 *
 * Copyright 2014 Allen D. Ball.  All rights reserved.
 */
package iprotium.tools.javadoc;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.internal.toolkit.taglets.Taglet;
import com.sun.tools.doclets.internal.toolkit.taglets.TagletOutput;
import com.sun.tools.doclets.internal.toolkit.taglets.TagletWriter;
import iprotium.activation.ReaderWriterDataSource;
import iprotium.annotation.ServiceProviderFor;
import iprotium.io.IOUtil;
import iprotium.xml.HTML;
import java.io.InputStream;
import java.util.Map;
import org.w3c.dom.Element;

/**
 * Inline {@link Taglet} to include a resource in the Javadoc output.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Taglet.class })
@TagletName("include")
public class IncludeTaglet extends AbstractInlineTaglet {
    public static void register(Map<String,Taglet> map) {
        register(IncludeTaglet.class, map);
    }

    /**
     * Sole constructor.
     */
    public IncludeTaglet() { super(); }

    @Override
    public TagletOutput getTagletOutput(Tag tag,
                                        TagletWriter writer) throws IllegalArgumentException {
        Element element = null;
        InputStream in = null;

        try {
            Class<?> type = getClassFor(getContainingClassDoc(tag.holder()));
            String name = tag.text().trim();

            in = type.getResourceAsStream(name);

            ReaderWriterDataSource ds = new ReaderWriterDataSource(null, null);

            IOUtil.copy(in, ds);

            element = HTML.pre(document, ds.toString());
        } catch (Exception exception) {
            throw new IllegalArgumentException(tag.position().toString(),
                                               exception);
        } finally {
            IOUtil.close(in);
        }

        return output(writer, element);
    }
}
