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
import java.net.URI;
import java.util.stream.Stream;
import org.w3c.dom.Node;

/**
 * Common HTML templates.
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
     * @param   nodes           The {@link Node}s to append to the newly
     *                          created
     *                          {@link org.w3c.dom.Element}.
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode div(Node... nodes) { return element("div", nodes); }

    /**
     * {@code <h1>}{@link Node nodes...}{@code </h1>}
     *
     * @param   stream          The {@link Stream} of {@link Node}s to
     *                          append to the newly created
     *                          {@link org.w3c.dom.Element}.
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode h1(Stream<Node> stream) {
        return h1(stream.toArray(Node[]::new));
    }

    /**
     * {@code <h1>}{@link Node nodes...}{@code </h1>}
     *
     * @param   nodes           The {@link Node}s to append to the newly
     *                          created
     *                          {@link org.w3c.dom.Element}.
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode h1(Node... nodes) { return element("h1", nodes); }

    /**
     * {@code <h1>}{@link #text(String) text(content)}{@code </h1>}
     *
     * @param   content         {@link org.w3c.dom.Text} content
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode h1(String content) { return h1(text(content)); }

    /**
     * {@code <h2>}{@link Node nodes...}{@code </h2>}
     *
     * @param   stream          The {@link Stream} of {@link Node}s to
     *                          append to the newly created
     *                          {@link org.w3c.dom.Element}.
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode h2(Stream<Node> stream) {
        return h2(stream.toArray(Node[]::new));
    }

    /**
     * {@code <h2>}{@link Node nodes...}{@code </h2>}
     *
     * @param   nodes           The {@link Node}s to append to the newly
     *                          created
     *                          {@link org.w3c.dom.Element}.
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode h2(Node... nodes) { return element("h2", nodes); }

    /**
     * {@code <h2>}{@link #text(String) text(content)}{@code </h2>}
     *
     * @param   content         {@link org.w3c.dom.Text} content
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode h2(String content) { return h2(text(content)); }

    /**
     * {@code <h3>}{@link Node nodes...}{@code </h3>}
     *
     * @param   stream          The {@link Stream} of {@link Node}s to
     *                          append to the newly created
     *                          {@link org.w3c.dom.Element}.
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode h3(Stream<Node> stream) {
        return h3(stream.toArray(Node[]::new));
    }

    /**
     * {@code <h3>}{@link Node nodes...}{@code </h3>}
     *
     * @param   nodes           The {@link Node}s to append to the newly
     *                          created
     *                          {@link org.w3c.dom.Element}.
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode h3(Node... nodes) { return element("h3", nodes); }

    /**
     * {@code <h3>}{@link #text(String) text(content)}{@code </h3>}
     *
     * @param   content         {@link org.w3c.dom.Text} content
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode h3(String content) { return h3(text(content)); }

    /**
     * {@code <h4>}{@link Node nodes...}{@code </h4>}
     *
     * @param   stream          The {@link Stream} of {@link Node}s to
     *                          append to the newly created
     *                          {@link org.w3c.dom.Element}.
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode h4(Stream<Node> stream) {
        return h4(stream.toArray(Node[]::new));
    }

    /**
     * {@code <h4>}{@link Node nodes...}{@code </h4>}
     *
     * @param   nodes           The {@link Node}s to append to the newly
     *                          created
     *                          {@link org.w3c.dom.Element}.
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode h4(Node... nodes) { return element("h4", nodes); }

    /**
     * {@code <h4>}{@link #text(String) text(content)}{@code </h4>}
     *
     * @param   content         {@link org.w3c.dom.Text} content
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode h4(String content) { return h4(text(content)); }

    /**
     * {@code <h5>}{@link Node nodes...}{@code </h5>}
     *
     * @param   stream          The {@link Stream} of {@link Node}s to
     *                          append to the newly created
     *                          {@link org.w3c.dom.Element}.
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode h5(Stream<Node> stream) {
        return h5(stream.toArray(Node[]::new));
    }

    /**
     * {@code <h5>}{@link Node nodes...}{@code </h5>}
     *
     * @param   nodes           The {@link Node}s to append to the newly
     *                          created
     *                          {@link org.w3c.dom.Element}.
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode h5(Node... nodes) { return element("h5", nodes); }

    /**
     * {@code <h5>}{@link #text(String) text(content)}{@code </h5>}
     *
     * @param   content         {@link org.w3c.dom.Text} content
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode h5(String content) { return h5(text(content)); }

    /**
     * {@code <h6>}{@link Node nodes...}{@code </h6>}
     *
     * @param   stream          The {@link Stream} of {@link Node}s to
     *                          append to the newly created
     *                          {@link org.w3c.dom.Element}.
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode h6(Stream<Node> stream) {
        return h6(stream.toArray(Node[]::new));
    }

    /**
     * {@code <h6>}{@link Node nodes...}{@code </h6>}
     *
     * @param   nodes           The {@link Node}s to append to the newly
     *                          created
     *                          {@link org.w3c.dom.Element}.
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode h6(Node... nodes) { return element("h6", nodes); }

    /**
     * {@code <h6>}{@link #text(String) text(content)}{@code </h6>}
     *
     * @param   content         {@link org.w3c.dom.Text} content
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode h6(String content) { return h6(text(content)); }

    /**
     * {@code <ol>}{@link Node nodes...}{@code </ol>}
     *
     * @param   stream          The {@link Stream} of {@link Node}s to
     *                          append to the newly created
     *                          {@link org.w3c.dom.Element}.
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode ol(Stream<Node> stream) {
        return ol(stream.toArray(Node[]::new));
    }

    /**
     * {@code <ol>}{@link Node nodes...}{@code </ol>}
     *
     * @param   nodes           The {@link Node}s to append to the newly
     *                          created
     *                          {@link org.w3c.dom.Element}.
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode ol(Node... nodes) { return element("ol", nodes); }

    /**
     * {@code <ul>}{@link Node nodes...}{@code </ul>}
     *
     * @param   stream          The {@link Stream} of {@link Node}s to
     *                          append to the newly created
     *                          {@link org.w3c.dom.Element}.
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode ul(Stream<Node> stream) {
        return ul(stream.toArray(Node[]::new));
    }

    /**
     * {@code <ul>}{@link Node nodes...}{@code </ul>}
     *
     * @param   nodes           The {@link Node}s to append to the newly
     *                          created
     *                          {@link org.w3c.dom.Element}.
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode ul(Node... nodes) { return element("ul", nodes); }

    /**
     * {@code <li>}{@link Node nodes...}{@code </li>}
     *
     * @param   stream          The {@link Stream} of {@link Node}s to
     *                          append to the newly created
     *                          {@link org.w3c.dom.Element}.
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode li(Stream<Node> stream) {
        return li(stream.toArray(Node[]::new));
    }

    /**
     * {@code <li>}{@link Node nodes...}{@code </li>}
     *
     * @param   nodes           The {@link Node}s to append to the newly
     *                          created
     *                          {@link org.w3c.dom.Element}.
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode li(Node... nodes) { return element("li", nodes); }

    /**
     * {@code <li>}{@link #text(String) text(content)}{@code </li>}
     *
     * @param   content         {@link org.w3c.dom.Text} content
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode li(String content) { return li(text(content)); }

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
     * @param   nodes           The {@link Node}s to append to the newly
     *                          created
     *                          {@link org.w3c.dom.Element}.
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode table(Node... nodes) { return element("table", nodes); }

    /**
     * {@code <caption>}{@link String content}{@code </caption>}}
     *
     * @param   content         {@link org.w3c.dom.Text} content
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode caption(String content) {
        return element("caption").content(content);
    }

    /**
     * {@code <thead>}{@link Node nodes...}{@code </thead>}
     *
     * @param   stream          The {@link Stream} of {@link Node}s to
     *                          append to the newly created
     *                          {@link org.w3c.dom.Element}.
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode thead(Stream<Node> stream) {
        return thead(stream.toArray(Node[]::new));
    }

    /**
     * {@code <thead>}{@link Node nodes...}{@code </thead>}
     *
     * @param   nodes           The {@link Node}s to append to the newly
     *                          created
     *                          {@link org.w3c.dom.Element}.
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode thead(Node... nodes) { return element("thead", nodes); }

    /**
     * {@code <tbody>}{@link Node nodes...}{@code </tbody>}
     *
     * @param   stream          The {@link Stream} of {@link Node}s to
     *                          append to the newly created
     *                          {@link org.w3c.dom.Element}.
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode tbody(Stream<Node> stream) {
        return tbody(stream.toArray(Node[]::new));
    }

    /**
     * {@code <tbody>}{@link Node nodes...}{@code </tbody>}
     *
     * @param   nodes           The {@link Node}s to append to the newly
     *                          created
     *                          {@link org.w3c.dom.Element}.
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode tbody(Node... nodes) { return element("tbody", nodes); }

    /**
     * {@code <tfoot>}{@link Node nodes...}{@code </tfoot>}
     *
     * @param   stream          The {@link Stream} of {@link Node}s to
     *                          append to the newly created
     *                          {@link org.w3c.dom.Element}.
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode tfoot(Stream<Node> stream) {
        return tfoot(stream.toArray(Node[]::new));
    }

    /**
     * {@code <tfoot>}{@link Node nodes...}{@code </tfoot>}
     *
     * @param   nodes           The {@link Node}s to append to the newly
     *                          created
     *                          {@link org.w3c.dom.Element}.
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode tfoot(Node... nodes) { return element("tfoot", nodes); }

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
