/*
 * $Id$
 *
 * Copyright 2019 Allen D. Ball.  All rights reserved.
 */
package ball.xml;

import java.net.URI;
import java.util.stream.Stream;
import org.w3c.dom.Node;

/**
 * Common HTML templates
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public interface HTMLTemplates extends XMLServices {

    /**
     * {@code <a href="}{@link URI#toASCIIString() href.toASCIIString()}{@code ">}{@link URI#toString() href.toString()}{@code </a>}
     *
     * @param   href            {@link URI}
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode a(URI href) { return a(href, (String) null); }

    /**
     * {@code <a href="}{@link URI#toASCIIString() href.toASCIIString()}{@code ">}{@link Node node}{@code </a>}
     *
     * @param   href            {@link URI}
     * @param   node            {@link Node}
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode a(URI href, Node node) {
        FluentNode a = element("a", node);

        if (href != null) {
            a.add(attr("href", href.toASCIIString()));
        }

        return a;
    }

    /**
     * {@code <a href="}{@link URI#toASCIIString() href.toASCIIString()}{@code ">}{@link #text(String) text(content)}{@code </a>}
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
     * {@code <b>}{@link Node node}{@code </b>}
     *
     * @param   node            {@link Node}
     *
     * @return  {@code <b/>} {@link org.w3c.dom.Element}
     */
    default FluentNode b(Node node) { return element("b", node); }

    /**
     * {@code <b>}{@link #text(String) text(content)}{@code </b>}
     *
     * @param   content         {@link org.w3c.dom.Text} content
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode b(String content) { return b(text(content)); }

    /**
     * {@code <code>}{@link String content}{@code </code>}}
     *
     * @param   content         {@link org.w3c.dom.Text} content
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode code(String content) {
        return element("code").content(content);
    }

    /**
     * {@code <code>}{@link String#valueOf(boolean) String.valueOf(content)}{@code </code>}}
     *
     * @param   content         {@link org.w3c.dom.Text} content
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode code(boolean content) {
        return code(String.valueOf(content));
    }

    /**
     * {@code <div>}{@link Node nodes...}{@code </div>}
     *
     * @param   stream          The {@link Stream} of {@link Node}s to
     *                          append to the newly created
     *                          {@link org.w3c.dom.Element}.
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode div(Stream<Node> stream) {
        return div(stream.toArray(Node[]::new));
    }

    /**
     * {@code <div>}{@link Node nodes...}{@code </div>}
     *
     * @param   iterable        The {@link Iterable} of {@link Node}s to
     *                          append to the newly created
     *                          {@link org.w3c.dom.Element}.
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode div(Iterable<Node> iterable) {
        return div(FluentNode.toArray(iterable));
    }

    /**
     * {@code <div>}{@link Node nodes...}{@code </div>}
     *
     * @param   nodes           The {@link Node}s to append to the newly
     *                          created
     *                          {@link org.w3c.dom.Element}.
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode div(Node... nodes) { return element("div", nodes); }

    /**
     * {@code <p>}{@link Node nodes...}{@code </p>}
     *
     * @param   stream          The {@link Stream} of {@link Node}s to
     *                          append to the newly created
     *                          {@link org.w3c.dom.Element}.
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode p(Stream<Node> stream) {
        return p(stream.toArray(Node[]::new));
    }

    /**
     * {@code <p>}{@link Node nodes...}{@code </p>}
     *
     * @param   iterable        The {@link Iterable} of {@link Node}s to
     *                          append to the newly created
     *                          {@link org.w3c.dom.Element}.
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode p(Iterable<Node> iterable) {
        return p(FluentNode.toArray(iterable));
    }

    /**
     * {@code <p>}{@link Node nodes...}{@code </p>}
     *
     * @param   nodes           The {@link Node}s to append to the newly
     *                          created
     *                          {@link org.w3c.dom.Element}.
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode p(Node... nodes) { return element("p", nodes); }

    /**
     * {@code <p>}{@link #text(String) text(content)}{@code </p>}
     *
     * @param   content         {@link org.w3c.dom.Text} content
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode p(String content) { return p(text(content)); }

    /**
     * {@code <pre}{@code >}{@link String content}{@code <}{@code /pre>}
     *
     * @param   content         {@link org.w3c.dom.Text} content
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode pre(String content) {
        return element("pre").content(content);
    }

    /**
     * {@code <pre lang="lang"}{@code >}{@link String content}{@code <}{@code /pre>}
     *
     * @param   lang            {@link org.w3c.dom.Attr} {@code lang} value
     * @param   content         {@link org.w3c.dom.Text} content
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode pre(String lang, String content) {
        return pre(content).add(attr("lang", lang));
    }

    /**
     * {@code <table>}{@link Node nodes...}{@code </table>}
     *
     * @param   stream          The {@link Stream} of {@link Node}s to
     *                          append to the newly created
     *                          {@link org.w3c.dom.Element}.
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode table(Stream<Node> stream) {
        return table(stream.toArray(Node[]::new));
    }

    /**
     * {@code <table>}{@link Node nodes...}{@code </table>}
     *
     * @param   iterable        The {@link Iterable} of {@link Node}s to
     *                          append to the newly created
     *                          {@link org.w3c.dom.Element}.
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode table(Iterable<Node> iterable) {
        return table(FluentNode.toArray(iterable));
    }

    /**
     * {@code <table>}{@link Node nodes...}{@code </table>}
     *
     * @param   nodes           The {@link Node}s to append to the newly
     *                          created
     *                          {@link org.w3c.dom.Element}.
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode table(Node... nodes) {
        return element("table", nodes);
    }

    /**
     * {@code <tr>}{@link Node nodes...}{@code </tr>}
     *
     * @param   stream          The {@link Stream} of {@link Node}s to
     *                          append to the newly created
     *                          {@link org.w3c.dom.Element}.
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode tr(Stream<Node> stream) {
        return tr(stream.toArray(Node[]::new));
    }

    /**
     * {@code <tr>}{@link Node nodes...}{@code </tr>}
     *
     * @param   iterable        The {@link Iterable} of {@link Node}s to
     *                          append to the newly created
     *                          {@link org.w3c.dom.Element}.
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode tr(Iterable<Node> iterable) {
        return tr(FluentNode.toArray(iterable));
    }

    /**
     * {@code <tr>}{@link Node nodes...}{@code </tr>}
     *
     * @param   nodes           The {@link Node}s to append to the newly
     *                          created
     *                          {@link org.w3c.dom.Element}.
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode tr(Node... nodes) { return element("tr", nodes); }

    /**
     * {@code <th>}{@link Node node}{@code </th>}
     *
     * @param   node            {@link Node}
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode th(Node node) { return element("th", node); }

    /**
     * {@code <th>}{@link #text(String) text(content)}{@code </th>}
     *
     * @param   content         {@link org.w3c.dom.Text} content
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode th(String content) { return th(text(content)); }

    /**
     * {@code <td>}{@link Node node}{@code </td>}
     *
     * @param   node            {@link Node}
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode td(Node node) { return element("td", node); }

    /**
     * {@code <td>content</td>}
     *
     * @param   content         {@link org.w3c.dom.Text} content
     *
     * @return  {@code <td/>} {@link org.w3c.dom.Element}
     */
    default FluentNode td(String content) { return td(text(content)); }

    /**
     * {@code <u>}{@link Node node}{@code </u>}
     *
     * @param   node            {@link Node}
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode u(Node node) { return element("u", node); }

    /**
     * {@code <u>}{@link #text(String) text(content)}{@code </u>}
     *
     * @param   content         {@link org.w3c.dom.Text} content
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode u(String content) { return u(text(content)); }
}
