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
 * Fluent {@link Node} interface
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

    /**
     * See {@link Node#getOwnerDocument()}.
     *
     * @return  The owner {@link FluentDocument}.
     */
    default FluentDocument document() {
        return (FluentDocument) getOwnerDocument();
    }
}
