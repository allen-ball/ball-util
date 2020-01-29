/*
 * $Id$
 *
 * Copyright 2019, 2020 Allen D. Ball.  All rights reserved.
 */
package ball.xml;

import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Common XML services.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public interface XMLServices {
    FluentDocument document();

    /**
     * Create a {@link org.w3c.dom.DocumentFragment} {@link Node}
     * (see {@link FluentNode#fragment(Node...)}).
     *
     * @param   stream          The {@link Stream} of {@link Node}s to
     *                          append to the newly created
     *                          {@link org.w3c.dom.DocumentFragment}.
     *
     * @return  The newly created {@link org.w3c.dom.DocumentFragment}.
     */
    default FluentNode fragment(Stream<Node> stream) {
        return document().fragment(stream);
    }

    /**
     * Create a {@link org.w3c.dom.DocumentFragment} {@link Node}
     * (see {@link FluentNode#fragment(Node...)}).
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
     * (see {@link FluentNode#element(String,Node...)}).
     *
     * @param   name            The {@link org.w3c.dom.Element} name.
     * @param   stream          The {@link Stream} of {@link Node}s to
     *                          append to the newly created
     *                          {@link org.w3c.dom.Element}.
     *
     * @return  The newly created {@link org.w3c.dom.Element}.
     */
    default FluentNode element(String name, Stream<Node> stream) {
        return document().element(name, stream);
    }

    /**
     * Create an {@link org.w3c.dom.Element} {@link Node}
     * (see {@link FluentNode#element(String,Node...)}).
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
     * (see {@link FluentNode#attr(String)}).
     *
     * @param   name            The {@link org.w3c.dom.Attr} name.
     *
     * @return  The newly created {@link org.w3c.dom.Attr}.
     */
    default FluentNode attr(String name) { return document().attr(name); }

    /**
     * Create an {@link org.w3c.dom.Attr} {@link Node}
     * (see {@link FluentNode#attr(String,String)}).
     *
     * @param   name            The {@link org.w3c.dom.Attr} name.
     * @param   value           The {@link org.w3c.dom.Attr} value.
     *
     * @return  The newly created {@link org.w3c.dom.Attr}.
     */
    default FluentNode attr(String name, String value) {
        return document().attr(name, value);
    }

    /**
     * Create a {@link org.w3c.dom.Text} {@link Node}
     * (see {@link FluentNode#text(String)}).
     *
     * @param   content         The {@link org.w3c.dom.Text} content.
     *
     * @return  The newly created {@link org.w3c.dom.Text}.
     */
    default FluentNode text(String content) {
        return document().text(content);
    }

    /**
     * Create a {@link org.w3c.dom.CDATASection} {@link Node}
     * (see {@link FluentNode#cdata(String)}).
     *
     * @param   data            The {@link org.w3c.dom.CDATASection} data.
     *
     * @return  The newly created {@link org.w3c.dom.CDATASection}.
     */
    default FluentNode cdata(String data) {
        return document().cdata(data);
    }

    /**
     * Create a {@link org.w3c.dom.Comment} {@link Node}
     * (see {@link FluentNode#comment(String)}).
     *
     * @param   data            The {@link org.w3c.dom.Comment} data.
     *
     * @return  The newly created {@link org.w3c.dom.Comment}.
     */
    default FluentNode comment(String data) {
        return document().comment(data);
    }

    /**
     * Convert a {@link NodeList} to a {@link Stream}.
     *
     * @param   list            The {@link NodeList}.
     *
     * @return  A {@link Stream} of {@link Node}s.
     */
    default Stream<Node> asStream(NodeList list) {
        return IntStream.range(0, list.getLength()).mapToObj(list::item);
    }
}
