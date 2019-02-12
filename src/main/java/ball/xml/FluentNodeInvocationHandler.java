/*
 * $Id$
 *
 * Copyright 2019 Allen D. Ball.  All rights reserved.
 */
package ball.xml;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
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
 * Protocol {@link InvocationHandler} for fluent {@link Node}s.  The same
 * {@link FluentNodeInvocationHandler instance} is used for all
 * {@link FluentNode}s of a {@link Document}.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class FluentNodeInvocationHandler implements InvocationHandler {

    /**
     * Static method to add a {@link FluentDocument} facade to a
     * {@link Document}.  Sole entrypoint to {@link FluentNode}.
     *
     * @param   document        The {@link Document}.
     *
     * @return  The {@link FluentDocument} facade.
     */
    public static FluentDocument asFluentDocument(Document document) {
        FluentNode node =
            new FluentNodeInvocationHandler().asFluentNode((Node) document);

        return (FluentDocument) node;
    }

    private final IdentityHashMap<Node,Node> proxyMap =
        new IdentityHashMap<>();
    private final IdentityHashMap<Node,Node> realMap =
        new IdentityHashMap<>();

    private FluentNodeInvocationHandler() { }

    @Override
    public Object invoke(Object proxy,
                         Method method, Object[] argv) throws Throwable {
        Object result = null;
        Class<?> declarer = method.getDeclaringClass();

        if (method.isDefault()) {
            Constructor<MethodHandles.Lookup> constructor =
                MethodHandles.Lookup.class.getDeclaredConstructor(Class.class);

            constructor.setAccessible(true);

            result =
                constructor.newInstance(declarer)
                .in(declarer)
                .unreflectSpecial(method, declarer)
                .bindTo(proxy)
                .invokeWithArguments(argv);
        } else if (FluentNode.class.isAssignableFrom(declarer)) {
            throw new IllegalStateException(method
                                            + " / " + Arrays.toString(argv));
        } else if (Node.class.getPackage().equals(declarer.getPackage())) {
            result = method.invoke(realMap.get(proxy), underlying(argv));
        } else if (declarer.equals(Object.class)) {
            result = method.invoke(realMap.get(proxy), argv);
        } else {
            throw new IllegalStateException(method
                                            + " / " + Arrays.toString(argv));
        }

        if (result instanceof Node && (! (result instanceof FluentNode))) {
            result = asFluentNode((Node) result);
        }

        return result;
    }

    private Object underlying(Object argument) {
        if (argument instanceof FluentNode) {
            argument =
                ((FluentNodeInvocationHandler)
                     Proxy.getInvocationHandler(argument))
                .realMap
                .getOrDefault(argument, (Node) argument);
        }

        return argument;
    }

    private Object[] underlying(Object[] argv) {
        Object[] result = null;

        if (argv != null) {
            result = new Object[argv.length];

            for (int i = 0; i < result.length; i += 1) {
                result[i] = underlying(argv[i]);
            }
        }

        return result;
    }

    private FluentNode asFluentNode(Node node) {
        FluentNode proxy = null;

        if (! (node instanceof FluentNode)) {
            proxy =
                (FluentNode)
                proxyMap.computeIfAbsent(node, k -> newProxyFor(k));

            realMap.put(proxy, node);
        } else {
            proxy = (FluentNode) node;
        }

        return proxy;
    }

    private FluentNode newProxyFor(Node node) {
        Set<Class<? extends Node>> set =
            interfacesOf(getClassForNode(node), node.getClass())
            .stream()
            .filter(t -> t.getPackage().equals(Node.class.getPackage()))
            .filter(t -> Node.class.isAssignableFrom(t))
            .map(t -> t.asSubclass(Node.class))
            .collect(Collectors.toSet());

        set.addAll(fluentTypes(set));

        new ArrayList<>(set)
            .stream()
            .forEach(t -> set.removeAll(Arrays.asList(t.getInterfaces())));

        Object proxy =
            Proxy.newProxyInstance(getClass().getClassLoader(),
                                   set.toArray(new Class<?>[] { }), this);

        return (FluentNode) proxy;
    }

    private Set<Class<?>> interfacesOf(Class<?>... types) {
        Set<Class<?>> set = new HashSet<>();

        for (Class<?> type : types) {
            if (type != null) {
                if (type.isInterface()) {
                    set.add(type);
                }

                for (Class<?> supertype : type.getInterfaces()) {
                    set.add(supertype);
                    set.addAll(interfacesOf(supertype));
                }
            }
        }

        return set;
    }

    private static final Map<Short,Class<? extends Node>> NODE_TYPE_MAP =
        Stream.of(new Object[][] {
            { Node.ATTRIBUTE_NODE, Attr.class },
            { Node.CDATA_SECTION_NODE, CDATASection.class },
            { Node.COMMENT_NODE, Comment.class },
            { Node.DOCUMENT_FRAGMENT_NODE, DocumentFragment.class },
            { Node.DOCUMENT_NODE, Document.class },
            { Node.DOCUMENT_TYPE_NODE, DocumentType.class },
            { Node.ELEMENT_NODE, Element.class },
            { Node.ENTITY_NODE, Entity.class },
            { Node.ENTITY_REFERENCE_NODE, EntityReference.class },
            { Node.NOTATION_NODE, Notation.class },
            { Node.PROCESSING_INSTRUCTION_NODE, ProcessingInstruction.class },
            { Node.TEXT_NODE, Text.class }
        }).collect(Collectors.toMap(t -> (Short) t[0],
                                    t -> ((Class<?>) t[1])
                                             .asSubclass(Node.class)));

    private Class<? extends Node> getClassForNode(Node node) {
        return NODE_TYPE_MAP.getOrDefault(node.getNodeType(), Node.class);
    }

    private Set<Class<? extends FluentNode>> fluentTypes(Collection<Class<? extends Node>> collection) {
        Set<Class<? extends FluentNode>> set =
            collection
            .stream()
            .map(t -> fluentType(t))
            .filter(t -> t != null)
            .collect(Collectors.toSet());

        return set;
    }

    private Class<? extends FluentNode> fluentType(Class<? extends Node> type) {
        Class<? extends FluentNode> fluent = null;

        try {
            fluent =
                Class.forName(String.format("%s.Fluent%s",
                                            FluentNode.class.getPackage().getName(),
                                            type.getSimpleName()))
                .asSubclass(FluentNode.class);
        } catch (Exception exception) {
        }

        return fluent;
    }
}
