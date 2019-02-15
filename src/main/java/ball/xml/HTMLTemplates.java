/*
 * $Id$
 *
 * Copyright 2019 Allen D. Ball.  All rights reserved.
 */
package ball.xml;

import java.net.URI;
import org.w3c.dom.Node;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

/**
 * Common HTML templates
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public interface HTMLTemplates extends XMLServices {

    /**
     * {@code <a href="...">href</a>}
     *
     * @param   href            {@link URI}
     *
     * @return  {@code <a/>} {@link org.w3c.dom.Element}
     */
    default FluentNode a(URI href) { return a(href, (String) null); }

    /**
     * {@code <a href="...">node</a>}
     *
     * @param   href            {@link URI}
     * @param   node            {@link Node}
     *
     * @return  {@code <a/>} {@link org.w3c.dom.Element}
     */
    default FluentNode a(URI href, Node node) {
        return element("a",
                       attr("href",
                            (href != null) ? href.toASCIIString() : null),
                       node);
    }

    /**
     * {@code <a href="...">content</a>}
     *
     * @param   href            {@link URI}
     * @param   content         {@link org.w3c.dom.Text} content
     *
     * @return  {@code <a/>} {@link org.w3c.dom.Element}
     */
    default FluentNode a(URI href, String content) {
        return a(href, text((content != null) ? content : href.toString()));
    }

    /**
     * {@code <b>node</b>}
     *
     * @param   node            {@link Node}
     *
     * @return  {@code <b/>} {@link org.w3c.dom.Element}
     */
    default FluentNode b(Node node) {
        return element("b", node);
    }

    /**
     * {@code <b>content</b>}
     *
     * @param   content         {@link org.w3c.dom.Text} content
     *
     * @return  {@code <b/>} {@link org.w3c.dom.Element}
     */
    default FluentNode b(String content) { return b(text(content)); }

    /**
     * {@code <code>content</code>}
     *
     * @param   content         {@link org.w3c.dom.Text} content
     *
     * @return  {@code <code/>} {@link org.w3c.dom.Element}
     */
    default FluentNode code(String content) {
        return element("code").content(content);
    }

    /**
     * {@code <code>false|true</code>}
     *
     * @param   content         {@link org.w3c.dom.Text} content
     *
     * @return  {@code <code/>} {@link org.w3c.dom.Element}
     */
    default FluentNode code(boolean content) {
        return code(String.valueOf(content));
    }

    /**
     * {@code <p><node/>...</p>}
     *
     * @param   nodes           The {@link Iterable} of {@link Node}s to
     *                          append to the newly created
     *                          {@link org.w3c.dom.Element}.
     *
     * @return  {@code <p><node/>...</p>}
     *          {@link org.w3c.dom.Element}
     */
    default FluentNode p(Iterable<Node> nodes) { return element("p", nodes); }

    /**
     * {@code <p><node/>...</p>}
     *
     * @param   nodes           The {@link Node}s to append to the newly
     *                          created
     *                          {@link org.w3c.dom.Element}.
     *
     * @return  {@code <p><node/>...</p>}
     *          {@link org.w3c.dom.Element}
     */
    default FluentNode p(Node... nodes) {
        return p((nodes != null) ? asList(nodes) : emptyList());
    }

    /**
     * {@code <p>content</p>}
     *
     * @param   content         {@link org.w3c.dom.Text} content
     *
     * @return  {@code <p/>} {@link org.w3c.dom.Element}
     */
    default FluentNode p(String content) { return p(text(content)); }

    /**
     * {@code <pre}{@code >content<}{@code /pre>}
     *
     * @param   content         {@link org.w3c.dom.Text} content
     *
     * @return  {@code <}{@code pre/}{@code >} {@link org.w3c.dom.Element}
     */
    default FluentNode pre(String content) {
        return element("pre").content(content);
    }

    /**
     * {@code <table><node/>...</table>}
     *
     * @param   nodes           The {@link Iterable} of {@link Node}s to
     *                          append to the newly created
     *                          {@link org.w3c.dom.Element}.
     *
     * @return  {@code <table><node/>...</table>}
     *          {@link org.w3c.dom.Element}
     */
    default FluentNode table(Iterable<Node> nodes) {
        return element("table", nodes);
    }

    /**
     * {@code <table><node/>...</table>}
     *
     * @param   nodes           The {@link Node}s to append to the newly
     *                          created
     *                          {@link org.w3c.dom.Element}.
     *
     * @return  {@code <table><node/>...</table>}
     *          {@link org.w3c.dom.Element}
     */
    default FluentNode table(Node... nodes) {
        return table((nodes != null) ? asList(nodes) : emptyList());
    }

    /**
     * {@code <tr><node/>...</tr>}
     *
     * @param   nodes           The {@link Iterable} of {@link Node}s to
     *                          append to the newly created
     *                          {@link org.w3c.dom.Element}.
     *
     * @return  {@code <tr><node/>...</tr>} {@link org.w3c.dom.Element}
     */
    default FluentNode tr(Iterable<Node> nodes) {
        return element("tr", nodes);
    }

    /**
     * {@code <tr><node/>...</tr>}
     *
     * @param   nodes           The {@link Node}s to append to the newly
     *                          created
     *                          {@link org.w3c.dom.Element}.
     *
     * @return  {@code <tr><node/>...</tr>} {@link org.w3c.dom.Element}
     */
    default FluentNode tr(Node... nodes) {
        return tr((nodes != null) ? asList(nodes) : emptyList());
    }

    /**
     * {@code <th>node</th>}
     *
     * @param   node            {@link Node}
     *
     * @return  {@code <th/>} {@link org.w3c.dom.Element}
     */
    default FluentNode th(Node node) {
        return element("th", node);
    }

    /**
     * {@code <th>content</th>}
     *
     * @param   content         {@link org.w3c.dom.Text} content
     *
     * @return  {@code <th/>} {@link org.w3c.dom.Element}
     */
    default FluentNode th(String content) { return th(text(content)); }

    /**
     * {@code <td>node</td>}
     *
     * @param   node            {@link Node}
     *
     * @return  {@code <td/>} {@link org.w3c.dom.Element}
     */
    default FluentNode td(Node node) {
        return element("td", node);
    }

    /**
     * {@code <td>content</td>}
     *
     * @param   content         {@link org.w3c.dom.Text} content
     *
     * @return  {@code <td/>} {@link org.w3c.dom.Element}
     */
    default FluentNode td(String content) { return td(text(content)); }

    /**
     * {@code <u>node</u>}
     *
     * @param   node            {@link Node}
     *
     * @return  {@code <u/>} {@link org.w3c.dom.Element}
     */
    default FluentNode u(Node node) {
        return element("u", node);
    }

    /**
     * {@code <u>content</u>}
     *
     * @param   content         {@link org.w3c.dom.Text} content
     *
     * @return  {@code <u/>} {@link org.w3c.dom.Element}
     */
    default FluentNode u(String content) { return u(text(content)); }
}
