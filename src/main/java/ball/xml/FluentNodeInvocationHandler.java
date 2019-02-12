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
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ClassUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import static ball.xml.FluentNode.NODE_TYPE_MAP;

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
     * Static method to wrap a {@link Document} in a {@link FluentDocument}.
     * Sole entry-point to {@link FluentNode}.
     *
     * @param   document        The {@link Document}.
     *
     * @return  The {@link FluentDocument} facade.
     */
    public static FluentDocument wrap(Document document) {
        FluentNode node =
            new FluentNodeInvocationHandler().wrap((Node) document);

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
            result = wrap((Node) result);
        }

        return result;
    }

    private FluentNode wrap(Node node) {
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

    private Node unwrap(FluentNode proxy) {
        Node node =
            ((FluentNodeInvocationHandler) Proxy.getInvocationHandler(proxy))
            .realMap.getOrDefault(proxy, proxy);

        return node;
    }

    private FluentNode newProxyFor(Node node) {
        ArrayList<Class<?>> list = new ArrayList<>();

        list.add(node.getClass());
        list.addAll(ClassUtils.getAllInterfaces(node.getClass()));

        Class<? extends Node> type =
            NODE_TYPE_MAP.getOrDefault(node.getNodeType(), Node.class);

        list.add(type);
        list.addAll(ClassUtils.getAllInterfaces(type));

        Set<Class<? extends Node>> set =
            list.stream()
            .filter(t -> t.isInterface())
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

    private Set<Class<? extends FluentNode>> fluentTypes(Collection<Class<? extends Node>> collection) {
        Set<Class<? extends FluentNode>> set =
            collection.stream()
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

    private Object[] underlying(Object[] argv) {
        Object[] result = null;

        if (argv != null) {
            result = new Object[argv.length];

            for (int i = 0; i < result.length; i += 1) {
                if (argv[i] instanceof FluentNode) {
                    result[i] = unwrap((FluentNode) argv[i]);
                } else {
                    result[i] = argv[i];
                }
            }
        }

        return result;
    }
}
