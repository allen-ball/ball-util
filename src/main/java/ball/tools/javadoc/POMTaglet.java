/*
 * $Id$
 *
 * Copyright 2015, 2016 Allen D. Ball.  All rights reserved.
 */
package ball.tools.javadoc;

import ball.activation.ReaderWriterDataSource;
import ball.annotation.ServiceProviderFor;
import ball.io.IOUtil;
import ball.xml.HTML;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.taglets.Taglet;
import com.sun.tools.doclets.internal.toolkit.taglets.TagletWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Map;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
/* import org.apache.maven.project.DefaultProjectBuilder; */
/* import org.apache.maven.project.DefaultProjectBuildingRequest; */
import org.apache.maven.project.MavenProject;
import org.apache.maven.model.Model;
/* import org.apache.maven.project.ProjectBuildingResult; */
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.w3c.dom.Element;

import static ball.util.StringUtil.isNil;

/**
 * Abstract base class for inline {@link Taglet}s that load the Maven POM.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class POMTaglet extends AbstractInlineTaglet {
    private static final String POM_XML = "pom.xml";

    private static final String DEPENDENCY = "dependency";
    private static final String GROUP_ID = "groupId";
    private static final String ARTIFACT_ID = "artifactId";
    private static final String VERSION = "version";

    /**
     * Sole constructor.
     */
    protected POMTaglet() { super(); }

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
        String relative = tag.text().trim();

        if (! isNil(relative)) {
            file = new File(tag.position().file().getParentFile(), relative);
        } else {
            File parent = tag.position().file().getParentFile();

            while (parent != null) {
                file = new File(parent, POM_XML);

                if (file.isFile()) {
                    break;
                } else {
                    file = null;
                }

                parent = parent.getParentFile();
            }
        }

        if (file == null || (! file.isFile())) {
            throw new FileNotFoundException();
        }

        return file;
    }

    /**
     * {@link Taglet} to provide POM "coordinates"
     * (as a {@code <dependency/>} element) to include this documented
     * {@link Class} or {@link Package}.
     *
     * <p>For example:</p>
     *
     * {@pom.coordinates}
     */
    @ServiceProviderFor({ Taglet.class })
    @TagletName("pom.coordinates")
    public static class Coordinates extends POMTaglet {
        public static void register(Map<String,Taglet> map) {
            register(Coordinates.class, map);
        }

        /**
         * Sole constructor.
         */
        public Coordinates() { super(); }

        @Override
        public Content getTagletOutput(Tag tag,
                                       TagletWriter writer) throws IllegalArgumentException {
            setConfiguration(writer.configuration());

            Element element = null;
            InputStream in = null;

            try {
                File pom = getPomFileFor(tag);

                in = new FileInputStream(pom);

                Model model = new MavenXpp3Reader().read(in, true);

                model.setPomFile(pom);

                MavenProject project = new MavenProject(model);

                project.setParentFile(pom.getParentFile());
                project.setPomFile(pom);
                /*
                 * DefaultProjectBuildingRequest request =
                 *    new DefaultProjectBuildingRequest();
                 * ProjectBuildingResult result =
                 *    new DefaultProjectBuilder()
                 *    .build(getPomFileFor(tag), request);
                 * MavenProject project = result.getProject();
                 */
                element =
                    dependency(project.getGroupId(),
                               project.getArtifactId(),
                               project.getVersion());

                ReaderWriterDataSource ds =
                    new ReaderWriterDataSource(null, null);
                Writer out = ds.getWriter();

                transformer.transform(new DOMSource(element),
                                      new StreamResult(out));

                out.flush();
                out.close();

                element = HTML.pre(document, ds.toString());
            } catch (Exception exception) {
                throw new IllegalArgumentException(tag.position().toString(),
                                                   exception);
            } finally {
                IOUtil.close(in);
            }

            return content(writer, element);
        }

        private Element dependency(String groupId,
                                   String artifactId, String version) {
            Element element = HTML.element(document, DEPENDENCY);

            element.appendChild(HTML.element(document, GROUP_ID, groupId));
            element.appendChild(HTML.element(document, ARTIFACT_ID, artifactId));
            element.appendChild(HTML.element(document, VERSION, version));

            return element;
        }
    }
}
