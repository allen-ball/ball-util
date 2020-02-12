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
