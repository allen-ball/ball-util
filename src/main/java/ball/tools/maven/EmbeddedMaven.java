/*
 * $Id$
 *
 * Copyright 2018 Allen D. Ball.  All rights reserved.
 */
package ball.tools.maven;

import java.io.File;
import org.apache.maven.Maven;
import org.apache.maven.cli.MavenCli;
import org.apache.maven.cli.configuration.ConfigurationProcessor;
import org.apache.maven.cli.event.DefaultEventSpyContext;
import org.apache.maven.cli.internal.extension.model.CoreExtension;
import org.apache.maven.eventspy.internal.EventSpyDispatcher;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequestPopulator;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.extension.internal.CoreExtensionEntry;
import org.apache.maven.model.building.ModelProcessor;
import org.apache.maven.project.MavenProject;
import org.apache.maven.toolchain.building.ToolchainsBuilder;
import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcher;

/**
 * Embedded {@link Maven} loader.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class EmbeddedMaven extends EmbeddedPlexusCore {
    private static final String MAVEN = "maven";

    private ClassRealm realm = null;
    private PlexusContainer container = null;
    private MavenProject project = null;

    /**
     * Sole constructor.
     *
     * @param   file            The POM {@link File}.
     */
    public EmbeddedMaven(File file) {
        super();

        System.setProperty("maven.multiModuleProjectDirectory",
                           file.getParentFile().getAbsolutePath());
        new MavenCli() {
            @Override
            protected void customizeContainer(PlexusContainer container) {
                try {
                    Maven maven = container.lookup(Maven.class);
                    DefaultMavenExecutionRequest request =
                        new DefaultMavenExecutionRequest();

                    container.lookup(MavenExecutionRequestPopulator.class)
                        .populateDefaults(request);

                    MavenExecutionResult result = maven.execute(request);

                    EmbeddedMaven.this.project = result.getProject();
                } catch (Exception exception) {
                    throw new ExceptionInInitializerError(exception);
                }
            }
        }
            .doMain(new String[] {
                    "-B", "dependency:build-classpath"
                    },
                    file.getParentFile().getAbsolutePath(),
                    System.out, System.err);
    }

    @Override
    public ClassRealm getClassRealm() {
        synchronized (this) {
            if (realm == null) {
                /*
                 * If Maven extension or core extensions specified,
                 *
                 * realm = getClassWorld().newRealm("maven.ext", null);
                 *
                 * and add to the Realm.
                 */
                realm = super.getClassRealm();
            }
        }

        return realm;
    }

    /**
     * Method to get and initialize the Maven container.
     *
     * @return  The Maven {@link PlexusContainer}.
     *
     * @throws  Exception       If the {@link PlexusContainer} cannot be
     *                          created and/or initialized.
     */
    public PlexusContainer getPlexusContainer() throws Exception {
        synchronized (this) {
            if (container == null) {
                realm = super.getClassRealm();

                ContainerConfiguration configuration =
                    new DefaultContainerConfiguration()
                    .setClassWorld(super.getClassWorld())
                    .setRealm(realm)
                    .setClassPathScanning(PlexusConstants.SCANNING_INDEX)
                    .setAutoWiring(true)
                    .setJSR250Lifecycle(true)
                    .setName(MAVEN);

                container = new DefaultPlexusContainer(configuration);
/*
                EventSpyDispatcher dispatcher =
                    container.lookup(EventSpyDispatcher.class);
                DefaultEventSpyContext context = new DefaultEventSpyContext();

                context.getData().put("plexus", container);
                dispatcher.init(context);
*/
            }
        }

        return container;
    }

    /**
     * Method to get the {@link MavenProject} instance.
     *
     * @return  The {@link MavenProject}.
     */
    public MavenProject getProject() { return project; }
}
