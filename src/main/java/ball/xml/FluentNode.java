package ball.xml;
/*-
 * ##########################################################################
 * Utilities
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2008 - 2020 Allen D. Ball
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ##########################################################################
 */
import ball.lang.reflect.FacadeProxyInvocationHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.NoArgsConstructor;
import lombok.ToString;
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

import static lombok.AccessLevel.PROTECTED;

/**
 * Fluent {@link Node} interface Note: This interface is an implementation
 * detail of {@link FluentDocument.Builder} and should not be implemented or
 * extended directly.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public interface FluentNode extends Node {

    /**
     * {@link Map} {@link Node#getNodeType()} to type ({@link Class}).
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
     * @param   stream          The {@link Stream} of {@link Node}s to
     *                          add.
     *
     * @return  {@link.this}
     */
    default FluentNode add(Stream<Node> stream) {
        return add(stream.toArray(Node[]::new));
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
     * @param   stream          The {@link Stream} of {@link Node}s to
     *                          append to the newly created
     *                          {@link DocumentFragment}.
     *
     * @return  The newly created {@link DocumentFragment}.
     */
    default FluentNode fragment(Stream<Node> stream) {
        return fragment(stream.toArray(Node[]::new));
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
     * @param   stream          The {@link Stream} of {@link Node}s to
     *                          append to the newly created {@link Element}.
     *
     * @return  The newly created {@link Element}.
     */
    default FluentNode element(String name, Stream<Node> stream) {
        return element(name, stream.toArray(Node[]::new));
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
     * @param   stream          The {@link Stream} of {@link Node}s to
     *                          append to the newly created {@link Element}.
     *
     * @return  The newly created {@link Element}.
     */
    default FluentNode elementNS(String ns, String qn, Stream<Node> stream) {
        return elementNS(ns, qn, stream.toArray(Node[]::new));
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

    /**
     * {@link FluentNode} {@link java.lang.reflect.InvocationHandler}.
     */
    @NoArgsConstructor(access = PROTECTED) @ToString
    public class InvocationHandler extends FacadeProxyInvocationHandler {
        private final HashMap<List<Class<?>>,Class<?>> map =
            new HashMap<>();

        /**
         * Implementation provides {@link java.lang.reflect.Proxy} class if
         * {@link Object} implements {@link Node} with the corresponding
         * {@link FluentNode} sub-interface(s).
         *
         * {@inheritDoc}
         */
        @Override
        protected Class<?> getProxyClassFor(Object object) {
            Class<?> type = null;

            if (object instanceof Node && (! (object instanceof FluentNode))) {
                Node node = (Node) object;
                List<Class<?>> key =
                    Arrays.asList(NODE_TYPE_MAP
                                  .getOrDefault(node.getNodeType(),
                                                Node.class),
                                  node.getClass());

                type = map.computeIfAbsent(key, k -> compute(k));
            }

            return type;
        }

        private Class<?> compute(List<Class<?>> key) {
            LinkedHashSet<Class<?>> implemented =
                key.stream()
                .flatMap(t -> getImplementedInterfacesOf(t).stream())
                .filter(t -> Node.class.isAssignableFrom(t))
                .filter(t -> Node.class.getPackage().equals(t.getPackage()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
            LinkedHashSet<Class<?>> interfaces =
                implemented.stream()
                .map(t -> fluent(t))
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));

            interfaces.addAll(implemented);

            new ArrayList<>(interfaces)
                .stream()
                .forEach(t -> interfaces.removeAll(Arrays.asList(t.getInterfaces())));

            return getProxyClass(interfaces.toArray(new Class<?>[] { }));
        }

        private Class<?> fluent(Class<?> type) {
            Class<?> fluent = null;

            if (Node.class.isAssignableFrom(type)
                && Node.class.getPackage().equals(type.getPackage())) {
                try {
                    String name =
                        String.format("%s.Fluent%s",
                                      FluentNode.class.getPackage().getName(),
                                      type.getSimpleName());

                    fluent = Class.forName(name).asSubclass(FluentNode.class);
                } catch (Exception exception) {
                }
            }

            return fluent;
        }
    }
}
