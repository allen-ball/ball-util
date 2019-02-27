/*
 * $Id$
 *
 * Copyright 2019 Allen D. Ball.  All rights reserved.
 */
package ball.xml;

import ball.lang.reflect.DefaultInvocationHandler;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import static ball.xml.FluentNode.NODE_TYPE_MAP;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.ClassUtils.getAllInterfaces;

/**
 * {@link FluentDocument} {@link DocumentBuilder}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class FluentDocumentBuilder extends DocumentBuilder {
    private final DocumentBuilder builder;

    /**
     * Sole constructor.
     *
     * @param   builder         The "wrapped" {@link DocumentBuilder}.
     */
    protected FluentDocumentBuilder(DocumentBuilder builder) {
        super();

        this.builder = requireNonNull(builder);
    }

    @Override
    public FluentDocument newDocument() {
        Document document = builder.newDocument();

        return (FluentDocument) new InvocationHandler().proxy(document);
    }

    @Override
    public Document parse(InputSource in) throws SAXException, IOException {
        Document document = builder.parse(in);

        return (FluentDocument) new InvocationHandler().proxy(document);
    }

    @Override
    public boolean isNamespaceAware() { return builder.isNamespaceAware(); }

    @Override
    public boolean isValidating() { return builder.isValidating(); }

    @Override
    public void setEntityResolver(EntityResolver resolver) {
        builder.setEntityResolver(resolver);
    }

    @Override
    public void setErrorHandler(ErrorHandler handler) {
        builder.setErrorHandler(handler);
    }

    @Override
    public DOMImplementation getDOMImplementation() {
        return builder.getDOMImplementation();
    }

    private class InvocationHandler extends DefaultInvocationHandler {
        private final ProxyMap map = new ProxyMap();

        protected InvocationHandler() { super(); }

        protected FluentNode proxy(Node node) {
            FluentNode proxy = null;

            if (! (node instanceof FluentNode)) {
                proxy =
                    (FluentNode)
                    map.computeIfAbsent(node,
                                        k -> newProxyInstance(getProxyInterfaces(k)));
            } else {
                proxy = (FluentNode) node;
            }

            return proxy;
        }

        private Class<?>[] getProxyInterfaces(Object object) {
            Class<? extends Node> spec =
                NODE_TYPE_MAP
                .getOrDefault(((Node) object).getNodeType(), Node.class);
            Class<? extends Node> impl =
                ((Node) object).getClass().asSubclass(Node.class);
            /*
             * Get all implemented org.w3c.dom.Node interfaces (and
             * subclasses) in org.w3c.dom package, ...
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
             * Remove redundant super-interfaces implemented by
             * sub-interfaces in the set.
             */
            new ArrayList<>(interfaces)
                .stream()
                .forEach(t -> interfaces.removeAll(Arrays.asList(t.getInterfaces())));

            return interfaces.toArray(new Class<?>[] { });
        }

        private Class<? extends FluentNode> fluentNodeType(Class<? extends Node> nodeType) {
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

        protected Node unproxy(FluentNode proxy) {
            Node node =
                (Node)
                ((InvocationHandler) Proxy.getInvocationHandler(proxy))
                .map.reverse().get(proxy);

            return node;
        }

        private Object unproxy(Class<?> type, Object in) {
            Object out = in;

            if (in instanceof FluentNode) {
                if (! FluentNode.class.isAssignableFrom(type)) {
                    out = unproxy((FluentNode) in);
                }
            } else if (in instanceof Object[]) {
                if (type.isArray()) {
                    int length = ((Object[]) in).length;

                    out = Array.newInstance(type.getComponentType(), length);

                    for (int i = 0; i < length; i += 1) {
                        ((Object[]) out)[i] =
                            unproxy(type.getComponentType(),
                                   ((Object[]) in)[i]);
                    }
                }
            }

            return out;
        }

        private Object[] unproxy(Method method, Object[] in) {
            return unproxy(method.getParameterTypes(), in);
        }

        private Object[] unproxy(Class<?>[] types, Object[] in) {
            Object[] out = in;

            if (in != null) {
                out = new Object[in.length];

                for (int i = 0; i < out.length; i += 1) {
                    out[i] = unproxy(types[i], in[i]);
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
                result = super.invoke(proxy, method, unproxy(method, argv));
            } else if (Node.class.getPackage().equals(declarer.getPackage())) {
                result =
                    method.invoke(unproxy(declarer, proxy),
                                  unproxy(method, argv));
            } else if (declarer.equals(Object.class)) {
                result = method.invoke(unproxy(declarer, proxy), argv);
            } else {
                result = super.invoke(proxy, method, argv);
            }

            if (result instanceof Node) {
                result = proxy((Node) result);
            }

            return result;
        }

        /**
         * {@link DefaultInvocationHandler} for
         * {@link FluentNode#toArray(Iterable)}.
         */
        private Node[] toArray(Iterable<Node> iterable) {
            return(StreamSupport
                   .stream(Optional
                           .ofNullable(iterable)
                           .orElse(Collections.emptyList())
                           .spliterator(),
                           false)
                   .toArray(Node[]::new));
        }

        private class ProxyMap extends IdentityHashMap<Object,Object> {
            private static final long serialVersionUID = 9122828186943671155L;

            private final IdentityHashMap<Object,Object> reverse =
                new IdentityHashMap<>();

            public ProxyMap() { super(); }

            public IdentityHashMap<Object,Object> reverse() { return reverse; }

            @Override
            public Object put(Object key, Object value) {
                reverse().put(value, key);

                return super.put(key, value);
            }
        }
    }
}
