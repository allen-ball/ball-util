/*
 * $Id$
 *
 * Copyright 2019 Allen D. Ball.  All rights reserved.
 */
package ball.xml;

import ball.activation.ReaderWriterDataSource;
import java.io.Writer;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Node;

/**
 * Common XML services
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public interface XMLServices {
    Transformer transformer();
    FluentDocument document();

    /**
     * Method to render a {@link Node} with the {@link #transformer()} to a
     * {@link String} (suitable for output).
     *
     * @param   node            The {@link Node}.
     *
     * @return  The {@link String} representation.
     */
    default String render(Node node) {
        ReaderWriterDataSource ds = new ReaderWriterDataSource(null, null);

        try (Writer out = ds.getWriter()) {
            transformer()
                .transform(new DOMSource(node),
                           new StreamResult(out));
        } catch (RuntimeException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }

        return ds.toString();
    }

    /**
     * Create a {@link org.w3c.dom.DocumentFragment} {@link Node}
     * (see {@link FluentNode#fragment(Node...)}.
     *
     * @param   nodes           The {@link Iterable} of {@link Node}s to
     *                          append to the newly created
     *                          {@link org.w3c.dom.DocumentFragment}.
     *
     * @return  The newly created {@link org.w3c.dom.DocumentFragment}.
     */
    default FluentNode fragment(Iterable<Node> nodes) {
        return document().fragment(nodes);
    }

    /**
     * Create a {@link org.w3c.dom.DocumentFragment} {@link Node}
     * (see {@link FluentNode#fragment(Node...)}.
     *
     * @param   nodes           The {@link Node}s to append to the newly
     *                          created
     *                          {@link org.w3c.dom.DocumentFragment}.
     *
     * @return  The newly created {@link org.w3c.dom.DocumentFragment}.
     */
    default FluentNode fragment(Node... nodes) {
        return document().fragment(nodes);
    }

    /**
     * Create an {@link org.w3c.dom.Element} {@link Node}
     * (see {@link FluentNode#element(String,Node...)}.
     *
     * @param   name            The {@link org.w3c.dom.Element} name.
     * @param   nodes           The {@link Iterable} of {@link Node}s to
     *                          append to the newly created
     *                          {@link org.w3c.dom.Element}.
     *
     * @return  The newly created {@link org.w3c.dom.Element}.
     */
    default FluentNode element(String name, Iterable<Node> nodes) {
        return document().element(name, nodes);
    }

    /**
     * Create an {@link org.w3c.dom.Element} {@link Node}
     * (see {@link FluentNode#element(String,Node...)}.
     *
     * @param   name            The {@link org.w3c.dom.Element} name.
     * @param   nodes           The {@link Node}s to append to the newly
     *                          created {@link org.w3c.dom.Element}.
     *
     * @return  The newly created {@link org.w3c.dom.Element}.
     */
    default FluentNode element(String name, Node... nodes) {
        return document().element(name, nodes);
    }

    /**
     * Create an {@link org.w3c.dom.Attr} {@link Node}
     * (see {@link FluentNode#attribute(String)}.
     *
     * @param   name            The {@link org.w3c.dom.Attr} name.
     *
     * @return  The newly created {@link org.w3c.dom.Attr}.
     */
    default FluentNode attribute(String name) {
        return document().attribute(name);
    }

    /**
     * Create an {@link org.w3c.dom.Text} {@link Node}
     * (see {@link FluentNode#text(String)}.
     *
     * @param   content         The {@link org.w3c.dom.Text} content.
     *
     * @return  The newly created {@link org.w3c.dom.Text}.
     */
    default FluentNode text(String content) {
        return document().text(content);
    }

    /**
     * Create an {@link org.w3c.dom.Attr} {@link Node}
     * (see {@link FluentNode#attribute(String,String)}.
     *
     * @param   name            The {@link org.w3c.dom.Attr} name.
     * @param   value           The {@link org.w3c.dom.Attr} value.
     *
     * @return  The newly created {@link org.w3c.dom.Attr}.
     */
    default FluentNode attribute(String name, String value) {
        return document().attribute(name, value);
    }
}
