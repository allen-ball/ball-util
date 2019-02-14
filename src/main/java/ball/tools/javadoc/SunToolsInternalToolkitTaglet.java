/*
 * $Id$
 *
 * Copyright 2019 Allen D. Ball.  All rights reserved.
 */
package ball.tools.javadoc;

import ball.activation.ReaderWriterDataSource;
import ball.xml.XMLServices;
import com.sun.javadoc.Doc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.formats.html.markup.RawHtml;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.taglets.Taglet;
import com.sun.tools.doclets.internal.toolkit.taglets.TagletWriter;
import java.io.Writer;
import java.util.Arrays;
import org.w3c.dom.Node;

/**
 * Default methods for legacy {@link Taglet} implementations.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public interface SunToolsInternalToolkitTaglet extends Taglet, XMLServices {
    @Override
    default Content getTagletOutput(Tag tag,
                                    TagletWriter writer) throws IllegalArgumentException {
        throw new IllegalArgumentException(tag.position().toString());
    }

    @Override
    default Content getTagletOutput(Doc doc,
                                    TagletWriter writer) throws IllegalArgumentException {
        throw new IllegalArgumentException(doc.position().toString());
    }

    /**
     * Method to produce {@link Taglet} content.
     *
     * See {@link #getTagletOutput(Tag,TagletWriter)} and
     * {@link #getTagletOutput(Doc,TagletWriter)}.
     *
     * @param   writer          The {@link TagletWriter}.
     * @param   iterable        The {@link Iterable} of {@link Object}s to
     *                          translate to content.
     *
     * @return  The {@link Content}.
     */
    default Content content(TagletWriter writer, Iterable<?> iterable) {
        ReaderWriterDataSource ds = new ReaderWriterDataSource(null, null);

        try (Writer out = ds.getWriter()) {
            for (Object object : iterable) {
                if (object instanceof Node) {
                    out.write(render((Node) object));
                } else {
                    out.write(String.valueOf(object));
                }
            }

            out.flush();
        } catch (RuntimeException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }

        Content content = writer.getOutputInstance();

        content.addContent(new RawHtml(ds.toString()));

        return content;
    }

    /**
     * Method to produce {@link Taglet} content.
     *
     * See {@link #getTagletOutput(Tag,TagletWriter)} and
     * {@link #getTagletOutput(Doc,TagletWriter)}.
     *
     * @param   writer          The {@link TagletWriter}.
     * @param   objects         The {@link Object}s to translate to content.
     *
     * @return  The {@link Content}.
     */
    default Content content(TagletWriter writer, Object... objects) {
        return content(writer, Arrays.asList(objects));
    }
}
