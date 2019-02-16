/*
 * $Id$
 *
 * Copyright 2019 Allen D. Ball.  All rights reserved.
 */
package ball.xml;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.Notation;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

/**
 * Fluent {@link Node} interface Note: This interface is an implementation
 * detail of {@link FluentDocumentBuilder} and should not be implemented or
 * extended directly.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public interface FluentNode extends Node {

    /**
     * {@link Map} {@link Node#getNodeType()} to type ({@link Class})
     *
     * {@include #NODE_TYPE_MAP}
     */
    public static final Map<Short,Class<? extends Node>> NODE_TYPE_MAP =
        Stream.of(new Object[][] {
            { ATTRIBUTE_NODE, Attr.class },
            { CDATA_SECTION_NODE, CDATASection.class },
            { COMMENT_NODE, Comment.class },
            { DOCUMENT_FRAGMENT_NODE, DocumentFragment.class },
            { DOCUMENT_NODE, Document.class },
            { DOCUMENT_TYPE_NODE, DocumentType.class },
            { ELEMENT_NODE, Element.class },
            { ENTITY_NODE, Entity.class },
            { ENTITY_REFERENCE_NODE, EntityReference.class },
            { NOTATION_NODE, Notation.class },
            { PROCESSING_INSTRUCTION_NODE, ProcessingInstruction.class },
            { TEXT_NODE, Text.class }
        }).collect(Collectors.toMap(t -> (Short) t[0],
                                    t -> ((Class<?>) t[1])
                                             .asSubclass(Node.class)));

    Node[] toArray(Iterable<Node> nodes);

    /**
     * See {@link Node#getOwnerDocument()}.
     *
     * @return  The owner {@link FluentDocument}.
     */
    default FluentDocument owner() {
        return (FluentDocument) getOwnerDocument();
    }

    /**
     * See {@link #getNodeName()}.
     *
     * @return  {@link #getNodeName()}
     */
    default String name() { return getNodeName(); }

    /**
     * See {@link #getNodeValue()}.
     *
     * @return  {@link #getNodeValue()}
     */
    default String value() { return getNodeValue(); }

    /**
     * See {@link #setNodeValue(String)}.
     *
     * @param   value           The {@link Node} value.
     *
     * @return  {@link.this}
     */
    default FluentNode value(String value) {
        setNodeValue(value);

        return this;
    }

    /**
     * See {@link #getTextContent()}.
     *
     * @return  {@link #getTextContent()}
     */
    default String content() { return getTextContent(); }

    /**
     * See {@link #setTextContent(String)}.
     *
     * @param   content         The {@link Node} content.
     *
     * @return  {@link.this}
     */
    default FluentNode content(String content) {
        setTextContent(content);

        return this;
    }

    /**
     * Method to add {@link Node}s to {@link.this} {@link FluentNode}.
     *
     * @param   nodes           The {@link Iterable} of {@link Node}s to
     *                          add.
     *
     * @return  {@link.this}
     */
    default FluentNode add(Iterable<Node> nodes) {
        return add(toArray(nodes));
    }

    /**
     * Method to add {@link Node}s to {@link.this} {@link FluentNode}.
     *
     * @param   nodes           The {@link Node}s to add.
     *
     * @return  {@link.this}
     */
    default FluentNode add(Node... nodes) {
        for (Node node : nodes) {
            switch (node.getNodeType()) {
            case ATTRIBUTE_NODE:
                getAttributes().setNamedItem(node);
                break;

            default:
                appendChild(node);
                break;
            }
        }

        return this;
    }

    /**
     * Create an {@link DocumentFragment} {@link Node}.
     *
     * @param   nodes           The {@link Iterable} of {@link Node}s to
     *                          append to the newly created
     *                          {@link DocumentFragment}.
     *
     * @return  The newly created {@link DocumentFragment}.
     */
    default FluentNode fragment(Iterable<Node> nodes) {
        return fragment(toArray(nodes));
    }

    /**
     * Create an {@link DocumentFragment} {@link Node}.
     *
     * @param   nodes           The {@link Node}s to append to the newly
     *                          created {@link DocumentFragment}.
     *
     * @return  The newly created {@link DocumentFragment}.
     */
    default FluentNode fragment(Node... nodes) {
        return ((FluentNode) owner().createDocumentFragment()).add(nodes);
    }

    /**
     * Create an {@link Element} {@link Node}.
     *
     * @param   name            The {@link Element} name.
     * @param   nodes           The {@link Iterable} of {@link Node}s to
     *                          append to the newly created {@link Element}.
     *
     * @return  The newly created {@link Element}.
     */
    default FluentNode element(String name, Iterable<Node> nodes) {
        return element(name, toArray(nodes));
    }

    /**
     * Create an {@link Element} {@link Node}.
     *
     * @param   name            The {@link Element} name.
     * @param   nodes           The {@link Node}s to append to the newly
     *                          created {@link Element}.
     *
     * @return  The newly created {@link Element}.
     */
    default FluentNode element(String name, Node... nodes) {
        return ((FluentNode) owner().createElement(name)).add(nodes);
    }

    /**
     * Create an {@link Element} {@link Node}.
     *
     * @param   ns              The {@link Element} namespace.
     * @param   qn              The {@link Element} qualified name.
     * @param   nodes           The {@link Iterable} of {@link Node}s to
     *                          append to the newly created {@link Element}.
     *
     * @return  The newly created {@link Element}.
     */
    default FluentNode elementNS(String ns, String qn, Iterable<Node> nodes) {
        return elementNS(ns, qn, toArray(nodes));
    }

    /**
     * Create an {@link Element} {@link Node}.
     *
     * @param   ns              The {@link Element} namespace.
     * @param   qn              The {@link Element} qualified name.
     * @param   nodes           The {@link Node}s to append to the newly
     *                          created {@link Element}.
     *
     * @return  The newly created {@link Element}.
     */
    default FluentNode elementNS(String ns, String qn, Node... nodes) {
        return ((FluentNode) owner().createElementNS(ns, qn)).add(nodes);
    }

    /**
     * Create an {@link Attr} {@link Node}.
     *
     * @param   name            The {@link Attr} name.
     *
     * @return  The newly created {@link Attr}.
     */
    default FluentNode attr(String name) {
        return (FluentNode) owner().createAttribute(name);
    }

    /**
     * Create an {@link Attr} {@link Node}.
     *
     * @param   name            The {@link Attr} name.
     * @param   value           The {@link Attr} value.
     *
     * @return  The newly created {@link Attr}.
     */
    default FluentNode attr(String name, String value) {
        FluentNode node = attr(name);

        ((Attr) node).setValue(value);

        return node;
    }

    /**
     * Create an {@link Attr} {@link Node}.
     *
     * @param   ns              The {@link Attr} namespace.
     * @param   qn              The {@link Attr} qualified name.
     *
     * @return  The newly created {@link Attr}.
     */
    default FluentNode attrNS(String ns, String qn) {
        return (FluentNode) owner().createAttributeNS(ns, qn);
    }

    /**
     * Create an {@link Attr} {@link Node}.
     *
     * @param   ns              The {@link Attr} namespace.
     * @param   qn              The {@link Attr} qualified name.
     * @param   value           The {@link Attr} value.
     *
     * @return  The newly created {@link Attr}.
     */
    default FluentNode attrNS(String ns, String qn, String value) {
        FluentNode node = attrNS(ns, qn);

        ((Attr) node).setValue(value);

        return node;
    }

    /**
     * Create a {@link Text} {@link Node}.
     *
     * @param   content         The {@link Text} content.
     *
     * @return  The newly created {@link Text}.
     */
    default FluentNode text(String content) {
        return (FluentNode) owner().createTextNode(content);
    }

    /**
     * Create a {@link CDATASection} {@link Node}.
     *
     * @param   data            The {@link CDATASection} data.
     *
     * @return  The newly created {@link CDATASection}.
     */
    default FluentNode cdata(String data) {
        return (FluentNode) owner().createCDATASection(data);
    }

    /**
     * Create a {@link Comment} {@link Node}.
     *
     * @param   data            The {@link Comment} data.
     *
     * @return  The newly created {@link Comment}.
     */
    default FluentNode comment(String data) {
        return (FluentNode) owner().createComment(data);
    }
}
