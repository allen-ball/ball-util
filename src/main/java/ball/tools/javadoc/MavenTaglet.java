/*
 * $Id$
 *
 * Copyright 2015 - 2020 Allen D. Ball.  All rights reserved.
 */
package ball.tools.javadoc;

import ball.annotation.ServiceProviderFor;
import ball.util.PropertiesImpl;
import ball.xml.FluentNode;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.w3c.dom.Document;

import static lombok.AccessLevel.PROTECTED;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Abstract base class for inline {@link Taglet}s that load the
 * {@link.uri https://maven.apache.org/pom.html Maven POM}.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@NoArgsConstructor(access = PROTECTED)
public abstract class MavenTaglet extends AbstractInlineTaglet
                                  implements SunToolsInternalToolkitTaglet {
    private static final String POM_XML = "pom.xml";
    private static final String DEPENDENCY = "dependency";
    private static final String GROUP_ID = "groupId";
    private static final String ARTIFACT_ID = "artifactId";
    private static final String VERSION = "version";

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
        File file = null;
        String name = tag.text().trim();

        if (isNotEmpty(name)) {
            file = new File(tag.position().file().getParentFile(), name);
        } else {
            name = POM_XML;

            File parent = tag.position().file().getParentFile();

            while (parent != null) {
                file = new File(parent, name);

                if (file.isFile()) {
                    break;
                } else {
                    file = null;
                }

                parent = parent.getParentFile();
            }
        }

        if (file == null || (! file.isFile())) {
            throw new FileNotFoundException(name);
        }

        return file;
    }

    /**
     * Method to load the {@link Document} from a POM {@link File}.
     *
     * @param   file            The POM {@link File}.
     *
     * @return  The {@link Document}.
     *
     * @throws  Exception       If the {@link Document} cannot be loaded.
     */
    protected Document getProjectFor(File file) throws Exception {
        Document document =
            DocumentBuilderFactory.newInstance()
            .newDocumentBuilder()
            .parse(file);

        return document;
    }

    /**
     * {@link Taglet} to provide POM coordinates as a {@code <dependency/>}
     * element to include this documented {@link Class} or {@link Package}.
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
            map.putIfAbsent(INSTANCE.getName(), INSTANCE);
        }

        @Override
        public FluentNode toNode(Tag tag) throws Throwable {
            POMProperties properties = new POMProperties();
            Class<?> type = null;

            if (tag.holder() instanceof PackageDoc) {
                type = Class.forName(tag.holder().name() + ".package-info");
            } else {
                type = getClassFor(getContainingClassDocFor(tag));
            }

            String name =
                "/"
                + String.join("/", type.getName().split(Pattern.quote(".")))
                + ".class";
            URL url = type.getResource(name);

            if (url.getProtocol().equalsIgnoreCase("file")) {
                Document document = getProjectFor(getPomFileFor(tag));

                Stream.of(GROUP_ID, ARTIFACT_ID, VERSION)
                    .forEach(t -> properties
                                  .getProperty(t, document, "/project/" + t));

                properties
                    .getProperty(VERSION, document, "/project/parent/" + VERSION);
            } else if (url.getProtocol().equalsIgnoreCase("jar")) {
                JarFile jar =
                    ((JarURLConnection) url.openConnection()).getJarFile();
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

            return pre("xml",
                       render(element(DEPENDENCY,
                                      Stream.of(GROUP_ID, ARTIFACT_ID, VERSION)
                                      .map(t -> element(t).content(properties.getProperty(t, "unknown")))),
                              2));
        }
    }

    private static class POMProperties extends PropertiesImpl {
        private static final long serialVersionUID = -708598891243199947L;

        private static final XPath XPATH =
            XPathFactory.newInstance().newXPath();

        public String getProperty(String key,
                                  Document document, String expression) {
            if (document != null) {
                computeIfAbsent(key, k -> evaluate(expression, document));
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
