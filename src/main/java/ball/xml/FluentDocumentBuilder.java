/*
 * $Id$
 *
 * Copyright 2019 Allen D. Ball.  All rights reserved.
 */
package ball.xml;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Array;
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
import java.util.stream.Stream;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import static ball.xml.FluentNode.NODE_TYPE_MAP;
import static org.apache.commons.lang3.ClassUtils.getAllInterfaces;

/**
 * {@link FluentDocument} builder
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class FluentDocumentBuilder {

    /**
     * Static method to wrap a {@link Document} in a {@link FluentDocument}.
     *
     * @param   document        The {@link Document}.
     *
     * @return  The {@link FluentDocument} facade.
     */
    public static FluentDocument wrap(Document document) {
        return (FluentDocument) new InvocationHandlerImpl().wrap(document);
    }

    private FluentDocumentBuilder() { }

    private static Class<?>[] getProxyInterfaces(Node node) {
        Class<? extends Node> spec =
            NODE_TYPE_MAP.getOrDefault(node.getNodeType(), Node.class);
        Class<? extends Node> impl = node.getClass();
        /*
         * Get all implemented org.w3c.dom.Node interfaces (and subclasses)
         * in org.w3c.dom package, ...
         */
        Set<Class<? extends Node>> implemented =
            Stream.concat(Stream.concat(Stream.of(spec),
                                        getAllInterfaces(spec).stream()),
                          Stream.concat(Stream.of(impl),
                                        getAllInterfaces(impl).stream()))
            .filter(t -> t.isInterface())
            .filter(t -> t.getPackage().equals(Node.class.getPackage()))
            .filter(t -> Node.class.isAssignableFrom(t))
            .map(t -> t.asSubclass(Node.class))
            .collect(Collectors.toSet());
        /*
         * ... find any corresponding FluentNode subclasses through
         * reflection, and, ...
         */
        Set<Class<? extends Node>> interfaces =
            implemented.stream()
            .map(t -> fluentNodeType(t))
            .filter(t -> t != null)
            .collect(Collectors.toSet());
        /*
         * ... combine.
         */
        interfaces.addAll(implemented);
        /*
         * Remove redundant super-interfaces implemented by sub-interfaces
         * in the set.
         */
        new ArrayList<>(interfaces)
            .stream()
            .forEach(t -> interfaces.removeAll(Arrays.asList(t.getInterfaces())));

        return interfaces.toArray(new Class<?>[] { });
    }

    private static Class<? extends FluentNode> fluentNodeType(Class<? extends Node> nodeType) {
        Class<? extends FluentNode> fluentType = null;

        try {
            String name =
                String.format("%s.Fluent%s",
                              FluentNode.class.getPackage().getName(),
                              nodeType.getSimpleName());

            fluentType = Class.forName(name).asSubclass(FluentNode.class);
        } catch (Exception exception) {
        }

        return fluentType;
    }

    private static class InvocationHandlerImpl implements InvocationHandler {
        private final IdentityHashMap<Node,Node> proxyMap =
            new IdentityHashMap<>();
        private final IdentityHashMap<Node,Node> realMap =
            new IdentityHashMap<>();

        protected InvocationHandlerImpl() { }

        protected FluentNode wrap(Node node) {
            FluentNode proxy = null;

            if (! (node instanceof FluentNode)) {
                proxy =
                    (FluentNode)
                    proxyMap.computeIfAbsent(node, k -> compute(k));
            } else {
                proxy = (FluentNode) node;
            }

            return proxy;
        }

        private FluentNode compute(Node node) {
            FluentNode proxy =
                (FluentNode)
                Proxy.newProxyInstance(FluentNode.class.getClassLoader(),
                                       getProxyInterfaces(node), this);

            realMap.put(proxy, node);

            return proxy;
        }

        protected Node unwrap(FluentNode proxy) {
            Node node =
                ((InvocationHandlerImpl) Proxy.getInvocationHandler(proxy))
                .realMap.get(proxy);

            return node;
        }

        private Object unwrap(Class<?> type, Object in) {
            Object out = in;

            if (in instanceof FluentNode) {
                if (! FluentNode.class.isAssignableFrom(type)) {
                    out = unwrap((FluentNode) in);
                }
            } else if (in instanceof Object[]) {
                if (type.isArray()) {
                    int length = ((Object[]) in).length;

                    out = Array.newInstance(type.getComponentType(), length);

                    for (int i = 0; i < length; i += 1) {
                        ((Object[]) out)[i] =
                            unwrap(type.getComponentType(),
                                   ((Object[]) in)[i]);
                    }
                }
            }

            return out;
        }

        private Object[] unwrap(Method method, Object[] in) {
            return unwrap(method.getParameterTypes(), in);
        }

        private Object[] unwrap(Class<?>[] types, Object[] in) {
            Object[] out = in;

            if (in != null) {
                out = new Object[in.length];

                for (int i = 0; i < out.length; i += 1) {
                    out[i] = unwrap(types[i], in[i]);
                }
            }

            return out;
        }

        @Override
        public Object invoke(Object proxy,
                             Method method, Object[] argv) throws Throwable {
            Object result = null;
            Class<?> declarer = method.getDeclaringClass();

            if (FluentNode.class.isAssignableFrom(declarer)) {
                if (method.isDefault()) {
                    Constructor<MethodHandles.Lookup> constructor =
                        MethodHandles.Lookup.class
                        .getDeclaredConstructor(Class.class);

                    constructor.setAccessible(true);

                    result =
                        constructor.newInstance(declarer)
                        .in(declarer)
                        .unreflectSpecial(method, declarer)
                        .bindTo(proxy)
                        .invokeWithArguments(unwrap(method, argv));
                } else {
                    throw new IllegalStateException(method + " in " + declarer
                                                    + " is not default");
                }
            } else if (Node.class.getPackage().equals(declarer.getPackage())) {
                result =
                    method.invoke(unwrap(declarer, proxy),
                                  unwrap(method, argv));
            } else /* if (declarer.equals(Object.class)) */ {
                result = method.invoke(unwrap(declarer, proxy), argv);
            }

            if (result instanceof Node) {
                result = wrap((Node) result);
            }

            return result;
        }
    }
}
