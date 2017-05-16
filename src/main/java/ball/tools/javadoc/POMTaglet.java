/*
 * $Id$
 *
 * Copyright 2015 - 2017 Allen D. Ball.  All rights reserved.
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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Writer;
import java.util.Map;
import java.util.TreeMap;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.maven.model.Model;
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

    private static final TreeMap<File,Model> MODELS =
        new TreeMap<File,Model>();

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
        String name = tag.text().trim();

        if (! isNil(name)) {
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
     * Method to locate the POM {@link Model} from a {@link Tag}.
     *
     * @param   tag             The {@link Tag}.
     *
     * @return  The POM {@link Model}.
     *
     * @throws  Exception       If the POM {@link Model} cannot be loaded.
     */
    protected Model getModelFor(Tag tag) throws Exception {
        File file = getPomFileFor(tag).getAbsoluteFile();
        Model model = MODELS.get(file);

        if (model == null) {
            FileReader reader = null;

            try {
                reader = new FileReader(getPomFileFor(tag));

                model = new MavenXpp3Reader().read(reader);
                model.setPomFile(file);

                MODELS.put(file, model);
            } finally {
                IOUtil.close(reader);
            }
        }

        return model;
    }

    /**
     * {@link Taglet} to provide POM coordinates as a {@code <dependency/>}
     * element to include this documented {@link Class} or {@link Package}.
     *
     * <p>For example:</p>
     *
     * {@pom.dependency}
     */
    @ServiceProviderFor({ Taglet.class })
    @TagletName("pom.dependency")
    public static class Dependency extends POMTaglet {
        public static void register(Map<String,Taglet> map) {
            register(Dependency.class, map);
        }

        /**
         * Sole constructor.
         */
        public Dependency() { super(); }

        @Override
        public Content getTagletOutput(Tag tag,
                                       TagletWriter writer) throws IllegalArgumentException {
            setConfiguration(writer.configuration());

            Element element = null;

            try {
                Model model = getModelFor(tag);

                element =
                    dependency(model.getGroupId(),
                               model.getArtifactId(),
                               model.getVersion());

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
