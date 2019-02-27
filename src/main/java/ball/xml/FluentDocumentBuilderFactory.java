/*
 * $Id$
 *
 * Copyright 2019 Allen D. Ball.  All rights reserved.
 */
package ball.xml;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import static java.util.Objects.requireNonNull;

/**
 * {@link FluentDocumentBuilder} {@link DocumentBuilderFactory}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
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
    public FluentDocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
        return new FluentDocumentBuilder(factory.newDocumentBuilder());
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
