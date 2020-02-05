package ball.tools.javadoc;
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
import ball.annotation.ServiceProviderFor;
import ball.util.PropertiesImpl;
import ball.xml.FluentNode;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static javax.xml.xpath.XPathConstants.NODESET;
import static lombok.AccessLevel.PROTECTED;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Abstract base class for inline {@link Taglet}s that load
 * {@link.uri https://maven.apache.org/index.html Maven} artifacts.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@NoArgsConstructor(access = PROTECTED)
public abstract class MavenTaglet extends AbstractInlineTaglet
                                  implements SunToolsInternalToolkitTaglet {
    private static final String PLUGIN_XML_PATH = "META-INF/maven/plugin.xml";
    private static final String PLUGIN_MOJO_EXPRESSION_FORMAT =
        "/plugin/mojos/mojo[implementation='%s']%s";
    private static final XPath XPATH = XPathFactory.newInstance().newXPath();

    private static final String POM_XML_NAME = "pom.xml";
    private static final String DEPENDENCY = "dependency";
    private static final String GROUP_ID = "groupId";
    private static final String ARTIFACT_ID = "artifactId";
    private static final String VERSION = "version";

    protected XPathExpression compile(String format, Object... argv) {
        XPathExpression expression = null;

        try {
            expression = XPATH.compile(String.format(format, argv));
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }

        return expression;
    }

    /**
     * Method to locate the POM from a {@link Tag}.
     *
     * @param   tag             The {@link Tag}.
     *
     * @return  The POM {@link File}.
     *
     * @throws  Exception       If the POM {@link File} cannot be found.
     */
    protected File getPomFileFor(Tag tag) throws Exception {
        File parent = tag.position().file().getParentFile();
        String name = defaultIfBlank(tag.text().trim(), POM_XML_NAME);
        File file = new File(parent, name);

        while (parent != null) {
            file = new File(parent, name);

            if (file.isFile()) {
                break;
            } else {
                file = null;
            }

            parent = parent.getParentFile();
        }

        if (file == null || (! file.isFile())) {
            throw new FileNotFoundException(name);
        }

        return file;
    }

    /**
     * Inline {@link Taglet} to provide a report of fields whose values are
     * configured by the {@link.uri https://maven.apache.org/index.html Maven}
     * {@link.uri https://maven.apache.org/plugin-developers/index.html Plugin}
     * {@code plugin.xml}.
     *
     * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
     * @version $Revision$
     */
    @ServiceProviderFor({ Taglet.class })
    @TagletName("maven.plugin.fields")
    @NoArgsConstructor @ToString
    public static class PluginFields extends MavenTaglet {
        private static final PluginFields INSTANCE = new PluginFields();

        public static void register(Map<Object,Object> map) {
            register(map, INSTANCE);
        }

        @Override
        public FluentNode toNode(Tag tag) throws Throwable {
            ClassDoc doc = null;
            String[] argv = tag.text().trim().split("[\\p{Space}]+", 2);

            if (isNotEmpty(argv[0])) {
                doc = getClassDocFor(tag, argv[0]);
            } else {
                doc = getContainingClassDocFor(tag);
            }

            Class<?> type = getClassFor(doc);
            URL url = getResourceURLOf(type);
            Document document = null;

            if (url.getProtocol().equalsIgnoreCase("file")) {
                document =
                    DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(url.getPath()
                           .replaceAll(Pattern.quote(getResourcePathOf(type)),
                                       PLUGIN_XML_PATH));
            } else if (url.getProtocol().equalsIgnoreCase("jar")) {
                try (JarFile jar =
                         ((JarURLConnection) url.openConnection()).getJarFile()) {
                    ZipEntry entry = jar.getEntry(PLUGIN_XML_PATH);

                    try (InputStream in = jar.getInputStream(entry)) {
                        document =
                            DocumentBuilderFactory.newInstance()
                            .newDocumentBuilder()
                            .parse(in);
                    }
                }
            } else {
                throw new IllegalStateException("Cannot find "
                                                + PLUGIN_XML_PATH);
            }

            NodeList parameters =
                (NodeList)
                compile(PLUGIN_MOJO_EXPRESSION_FORMAT,
                        type.getCanonicalName(),
                        "/parameters/parameter")
                .evaluate(document, NODESET);
            NodeList configuration =
                (NodeList)
                compile(PLUGIN_MOJO_EXPRESSION_FORMAT,
                        type.getCanonicalName(),
                        "/configuration/*")
                .evaluate(document, NODESET);

            return div(attr("class", "summary"),
                       h3("Maven Plugin Field Summary"),
                       table(tag, type, configuration));
        }

        private FluentNode table(Tag tag, Class<?> type, NodeList list) {
            return table(thead(tr(th(EMPTY), th("Field"), th("Default"))),
                         tbody(asStream(list)
                               .map(t -> tr(tag, type, (Element) t))));
        }

        private FluentNode tr(Tag tag, Class<?> type, Element element) {
            FluentNode tr = fragment();
            Field field =
                FieldUtils.getField(type, element.getNodeName(), true);

            if (field != null) {
                tr =
                    tr(td((! type.equals(field.getDeclaringClass()))
                              ? type(tag, field.getDeclaringClass())
                              : text(EMPTY)),
                       td(declaration(tag, field)),
                       td(code(element.getAttribute("default-value"))));
            }

            return tr;
        }
    }

    /**
     * {@link Taglet} to provide
     * {@link.uri https://maven.apache.org/pom.html POM} POM coordinates as
     * a {@code <dependency/>} element to include this documented
     * {@link Class} or {@link Package}.
     *
     * <p>For example:</p>
     *
     * {@pom.coordinates}
     */
    @ServiceProviderFor({ Taglet.class })
    @TagletName("pom.coordinates")
    @NoArgsConstructor @ToString
    public static class Coordinates extends MavenTaglet {
        private static final Coordinates INSTANCE = new Coordinates();

        public static void register(Map<Object,Object> map) {
            register(map, INSTANCE);
        }

        @Override
        public FluentNode toNode(Tag tag) throws Throwable {
            POMProperties properties = new POMProperties();
            Class<?> type = null;

            if (tag.holder() instanceof PackageDoc) {
                type = getClassFor((PackageDoc) tag.holder());
            } else {
                type = getClassFor(getContainingClassDocFor(tag));
            }

            URL url = getResourceURLOf(type);

            if (url.getProtocol().equalsIgnoreCase("file")) {
                Document document =
                    DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(getPomFileFor(tag));

                Stream.of(GROUP_ID, ARTIFACT_ID, VERSION)
                    .forEach(t -> properties.load(t, document, "/project/"));
                Stream.of(VERSION)
                    .forEach(t -> properties.load(t, document, "/project/parent/"));
            } else if (url.getProtocol().equalsIgnoreCase("jar")) {
                try (JarFile jar =
                         ((JarURLConnection) url.openConnection()).getJarFile()) {
                    JarEntry entry =
                        jar.stream()
                        .filter(t -> t.getName().matches("META-INF/maven/[^/]+/[^/]+/pom.properties"))
                        .findFirst().orElse(null);

                    if (entry != null) {
                        try (InputStream in = jar.getInputStream(entry)) {
                            properties.load(in);
                        }
                    }
                }
            }

            return pre("xml",
                       render(element(DEPENDENCY,
                                      Stream.of(GROUP_ID, ARTIFACT_ID, VERSION)
                                      .map(t -> element(t).content(properties.getProperty(t, "unknown")))),
                              2));
        }
    }

    private static class POMProperties extends PropertiesImpl {
        private static final long serialVersionUID = -590870165719527159L;

        public String load(String key, Document document, String prefix) {
            if (document != null) {
                computeIfAbsent(key, k -> evaluate(prefix + k, document));
            }

            return super.getProperty(key);
        }

        private String evaluate(String expression, Document document) {
            String value = null;

            try {
                value = XPATH.evaluate(expression, document);
            } catch (Exception exception) {
            }

            return isNotEmpty(value) ? value : null;
        }
    }
}
