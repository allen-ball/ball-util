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
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import static java.util.Objects.requireNonNull;

/**
 * {@link FluentDocument.Builder} {@link DocumentBuilderFactory}.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public class FluentDocumentBuilderFactory extends DocumentBuilderFactory {
    private final DocumentBuilderFactory factory;

    /**
     * See {@link DocumentBuilderFactory#newInstance()}.
     *
     * @return  {@link FluentDocumentBuilderFactory}
     */
    public static FluentDocumentBuilderFactory newInstance() throws FactoryConfigurationError {
        return new FluentDocumentBuilderFactory();
    }

    /**
     * See {@link DocumentBuilderFactory#newInstance(String,ClassLoader)}.
     *
     * @param   name            The {@link Class} name.
     * @param   loader          The {@link ClassLoader}.
     *
     * @return  {@link FluentDocumentBuilderFactory}
     */
    public static FluentDocumentBuilderFactory newInstance(String name,
                                                           ClassLoader loader) throws FactoryConfigurationError {
        DocumentBuilderFactory factory =
            DocumentBuilderFactory.newInstance(name, loader);

        if (! (factory instanceof FluentDocumentBuilderFactory)) {
            factory = new FluentDocumentBuilderFactory(factory);
        }

        return (FluentDocumentBuilderFactory) factory;
    }

    /**
     * Sole public constructor.
     */
    public FluentDocumentBuilderFactory() throws FactoryConfigurationError {
        this(DocumentBuilderFactory.newInstance());
    }

    private FluentDocumentBuilderFactory(DocumentBuilderFactory factory) {
        super();

        this.factory = requireNonNull(factory);
    }

    @Override
    public void setAttribute(String name,
                             Object value) throws IllegalArgumentException {
        factory.setAttribute(name, value);
    }

    @Override
    public Object getAttribute(String name) throws IllegalArgumentException {
        return factory.getAttribute(name);
    }

    @Override
    public FluentDocument.Builder newDocumentBuilder() throws ParserConfigurationException {
        return new FluentDocument.Builder(factory.newDocumentBuilder());
    }

    @Override
    public void setFeature(String name,
                           boolean value) throws ParserConfigurationException {
        factory.setFeature(name, value);
    }

    @Override
    public boolean getFeature(String name) throws ParserConfigurationException {
        return factory.getFeature(name);
    }
}
