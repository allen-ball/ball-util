/*
 * $Id$
 *
 * Copyright 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.xml;

import java.net.URI;
import java.util.Arrays;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import static iprotium.util.StringUtil.isNil;

/**
 * Static methods to create {@link HTML} {@link Element}s.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class HTML {
    private static final String A = "a";
    private static final String B = "b";
    private static final String TABLE = "table";
    private static final String TD = "td";
    private static final String TR = "tr";
    private static final String U = "u";

    private static final String HREF = "href";

    private HTML() { }

    /**
     * Method to create an HTML {@code <a/>} {@link Element}.
     *
     * @param   document        The owner {@link Document}.
     * @param   href            The href {@link org.w3c.dom.Attribute}
     *                          value.
     * @param   value           The {@link Element} content.
     *
     * @return  The HTML {@code <a/>} {@link Element}.
     */
    public static Element a(Document document, URI href, Object value) {
        Element a =
            element(document, A, (value != null) ? value : href.toString());

        a.setAttribute(HREF, href.toASCIIString());

        return a;
    }

    /**
     * Method to create an HTML {@code <b/>} {@link Element}.
     *
     * @param   document        The owner {@link Document}.
     * @param   value           The value {@link Object}.
     *
     * @return  The HTML {@code <b/>} {@link Element}.
     */
    public static Element b(Document document, Object value) {
        return (isElement(value, B)
                    ? ((Element) value)
                    : element(document, B, value));
    }

    /**
     * Method to create an HTML {@code <b/>} {@link Element} array.
     *
     * @param   document        The owner {@link Document}.
     * @param   values          The value {@link Object}s.
     *
     * @return  The array of HTML {@code <b/>} {@link Element}s.
     */
    public static Element[] b(Document document, Object... values) {
        Element[] elements = new Element[values.length];

        for (int i = 0; i < elements.length; i += 1) {
            elements[i] = b(document, values[i]);
        }

        return elements;
    }

    /**
     * Method to create an HTML {@code <table/>} {@link Element}.
     *
     * @param   document        The owner {@link Document}.
     *
     * @return  The HTML {@code <table/>} {@link Element}.
     */
    public static Element table(Document document) {
        return element(document, TABLE);
    }

    /**
     * Method to create an HTML table row ({@code <tr/>}) {@link Element}.
     *
     * @param   table           The table {@link Element}.
     * @param   iterable        The {@link Iterable} of row values.
     *
     * @return  The array of HTML {@code <tr/>} {@link Element}s.
     */
    public static Element tr(Element table, Iterable<?> iterable) {
        if (! isElement(table, TABLE)) {
            throw new IllegalArgumentException("element is not a 'table'");
        }

        Element tr = element(table, TR);

        for (Object object : iterable) {
            element(tr, TD, object);
        }

        return tr;
    }

    /**
     * Method to create an HTML table row ({@code <tr/>}) {@link Element}.
     *
     * @param   table           The table {@link Element}.
     * @param   objects         The array of row values.
     *
     * @return  The array of HTML {@code <tr/>} {@link Element}s.
     */
    public static Element tr(Element table, Object... objects) {
        return tr(table, Arrays.asList(objects));
    }

    /**
     * Method to create an HTML {@code <u/>} {@link Element}.
     *
     * @param   document        The owner {@link Document}.
     * @param   value           The value {@link Object}.
     *
     * @return  The HTML {@code <u/>} {@link Element}.
     */
    public static Element u(Document document, Object value) {
        return (isElement(value, U)
                    ? ((Element) value)
                    : element(document, U, value));
    }

    /**
     * Method to create an HTML {@code <u/>} {@link Element} array.
     *
     * @param   document        The owner {@link Document}.
     * @param   values          The value {@link Object}s.
     *
     * @return  The array of HTML {@code <u/>} {@link Element}s.
     */
    public static Element[] u(Document document, Object... values) {
        Element[] elements = new Element[values.length];

        for (int i = 0; i < elements.length; i += 1) {
            elements[i] = u(document, values[i]);
        }

        return elements;
    }

    private static Element element(Document document, String name) {
        return element(document, name, null);
    }

    private static Element element(Document document,
                                   String name, Object value) {
        Element element = document.createElement(name);

        if (value != null) {
            if (value instanceof Node) {
                element.appendChild((Node) value);
            } else {
                element.setTextContent(String.valueOf(value));
            }
        }

        return element;
    }

    private static Element element(Node node, String name) {
        return element(node, name, null);
    }

    private static Element element(Node node, String name, Object value) {
        Element element = element(node.getOwnerDocument(), name, value);

        node.appendChild(element);

        return element;
    }

    private static boolean isElement(Object object) {
        return (object instanceof Element);
    }

    private static boolean isElement(Object object, String name) {
        return isElement(object) && ((Node) object).getNodeName().equals(name);
    }
}
