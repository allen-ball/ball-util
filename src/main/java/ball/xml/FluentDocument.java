/*
 * $Id$
 *
 * Copyright 2019, 2020 Allen D. Ball.  All rights reserved.
 */
package ball.xml;

import java.io.IOException;
import java.util.stream.Stream;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import static java.util.Objects.requireNonNull;

/**
 * Fluent {@link Document} interface.  Note: This interface is an
 * implementation detail of {@link FluentDocument.Builder} and should not be
 * extended directly.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public interface FluentDocument extends FluentNode, Document {
    @Override
    default FluentDocument owner() { return this; }

    @Override
    default FluentDocument add(Stream<Node> stream) {
        return add(stream.toArray(Node[]::new));
    }

    @Override
    default FluentDocument add(Iterable<Node> iterable) {
        return (FluentDocument) FluentNode.super.add(iterable);
    }

    @Override
    default FluentDocument add(Node... nodes) {
        return (FluentDocument) FluentNode.super.add(nodes);
    }

    /**
     * {@link FluentDocument} {@link DocumentBuilder}.
     */
    public class Builder extends DocumentBuilder {
        private final DocumentBuilder builder;

        /**
         * Sole constructor.
         *
         * @param   builder         The "wrapped" {@link DocumentBuilder}.
         */
        protected Builder(DocumentBuilder builder) {
            super();

            this.builder = requireNonNull(builder);
        }

        @Override
        public FluentDocument newDocument() {
            Document document = builder.newDocument();

            return (FluentDocument) new InvocationHandler().enhance(document);
        }

        @Override
        public Document parse(InputSource in) throws SAXException,
                                                     IOException {
            Document document = builder.parse(in);

            return (FluentDocument) new InvocationHandler().enhance(document);
        }

        @Override
        public boolean isNamespaceAware() {
            return builder.isNamespaceAware();
        }

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

        @Override
        public String toString() { return super.toString(); }
    }
}
