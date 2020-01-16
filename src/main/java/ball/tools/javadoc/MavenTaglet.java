/*
 * $Id$
 *
 * Copyright 2015 - 2020 Allen D. Ball.  All rights reserved.
 */
package ball.tools.javadoc;

import ball.annotation.ServiceProviderFor;
/* import ball.tools.maven.EmbeddedMaven; */
import ball.tools.maven.POMProperties;
import ball.util.PropertiesImpl;
import ball.xml.FluentNode;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;

import static ball.tools.maven.POMProperties.ARTIFACT_ID;
import static ball.tools.maven.POMProperties.GROUP_ID;
import static ball.tools.maven.POMProperties.VERSION;
import static lombok.AccessLevel.PROTECTED;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Abstract base class for inline {@link Taglet}s that load the
 * {@link org.apache.maven.Maven} POM.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@NoArgsConstructor(access = PROTECTED)
public abstract class MavenTaglet extends AbstractInlineTaglet
                                  implements SunToolsInternalToolkitTaglet {
    private static final String POM_XML = "pom.xml";
    private static final String DEPENDENCY = "dependency";

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
     * Method to get the {@link Model} from a {@link Tag}.
     *
     * @param   tag             The {@link Tag}.
     *
     * @return  The POM {@link Model}.
     *
     * @throws  Exception       If the POM {@link Model} cannot be loaded.
     */
    protected Model getModelFor(Tag tag) throws Exception {
        Model model = null;
        File file = getPomFileFor(tag).getAbsoluteFile();

        try {
            MavenProject project = getProjectFor(file);

            if (project != null) {
                model = project.getModel();
            }
        } catch (Throwable throwable) {
            /* throwable.printStackTrace(System.err); */
        } finally {
            if (model == null) {
                try (FileReader reader = new FileReader(file)) {
                    model = new MavenXpp3Reader().read(reader);
                    model.setPomFile(file);
                }
            }
        }

        return model;
    }

    /**
     * Method to load the {@link MavenProject} from a POM {@link File}.
     *
     * @param   file            The POM {@link File}.
     *
     * @return  The {@link MavenProject}.
     *
     * @throws  Exception       If the POM {@link Model} cannot be loaded.
     */
    protected MavenProject getProjectFor(File file) throws Exception {
        return null /* new EmbeddedMaven(file).getProject() */;
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
            String groupId = "unknown";
            String artifactId = "unknown";
            String version = "unknown";
            Class<?> type = null;

            if (tag.holder() instanceof PackageDoc) {
                type = Class.forName(tag.holder().name() + ".package-info");
            } else {
                type = getClassFor(getContainingClassDocFor(tag));
            }

            String name =
                "/" + String.join("/", type.getName().split("[.]")) + ".class";
            URL url = type.getResource(name);

            if (url.getProtocol().equalsIgnoreCase("file")) {
                Model model = getModelFor(tag);

                groupId = model.getGroupId();
                artifactId = model.getArtifactId();
                version = model.getVersion();

                if (isEmpty(version)) {
                    version =
                        new POMProperties(groupId, artifactId)
                        .getVersion();
                }
            } else if (url.getProtocol().equalsIgnoreCase("jar")) {
                JarFile jar =
                    ((JarURLConnection) url.openConnection()).getJarFile();
                JarEntry entry =
                    jar.stream()
                    .filter(t -> t.getName().matches("META-INF/maven/[^/]+/[^/]+/pom.properties"))
                    .findFirst().orElse(null);

                if (entry != null) {
                    PropertiesImpl properties = new PropertiesImpl();

                    properties.load(jar.getInputStream(entry));

                    groupId = properties.getProperty(GROUP_ID);
                    artifactId = properties.getProperty(ARTIFACT_ID);
                    version = properties.getProperty(VERSION);
                }
            }

            return pre("xml",
                       render(element(DEPENDENCY,
                                      element(GROUP_ID).content(groupId),
                                      element(ARTIFACT_ID).content(artifactId),
                                      element(VERSION).content(version)),
                              2));
        }
    }
}
