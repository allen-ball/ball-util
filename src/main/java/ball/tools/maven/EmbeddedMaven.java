/*
 * $Id$
 *
 * Copyright 2018 Allen D. Ball.  All rights reserved.
 */
package ball.tools.maven;

import java.io.PrintStream;
import ball.activation.ReaderWriterDataSource;
import java.io.File;
import org.apache.maven.Maven;
import org.apache.maven.cli.CliRequest;
import org.apache.maven.cli.MavenCli;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequestPopulationException;
import org.apache.maven.execution.MavenExecutionRequestPopulator;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.model.building.ModelProcessor;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

/**
 * Embedded {@link Maven} loader.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class EmbeddedMaven {
    private final File file;
    private MavenProject project = null;

    /**
     * Sole constructor.
     *
     * @param   file            The POM {@link File}.
     */
    public EmbeddedMaven(File file) {
        if (file != null) {
            this.file = file;
        } else {
            throw new NullPointerException("file");
        }
    }

    /**
     * Method to get the {@link MavenProject} instance.
     *
     * @return  The {@link MavenProject}.
     */
    public MavenProject getProject() {
        synchronized (this) {
            if (project == null) {
/*
                MavenCliImpl cli = new MavenCliImpl();

                cli.doMain(file.getParentFile(), "-B", "help:effective-pom");
*/
System.out.println(cli.getOutput());
            }
        }

        return project;
    }

    private class MavenCliImpl extends MavenCli {
        private final ReaderWriterDataSource ds =
            new ReaderWriterDataSource(null, null);
        private final PrintStream out;

        public MavenCliImpl() {
            super();

            try {
                out = ds.getPrintStream();
            } catch (Exception exception) {
                throw new ExceptionInInitializerError(exception);
            }
        }

        public int doMain(File directory, String... argv) {
System.out.println(directory);
            System.setProperty("maven.multiModuleProjectDirectory",
                               directory.getAbsolutePath());

            return super.doMain(argv,
                                directory.getAbsolutePath(),
                                out, out);
        }

        public String getOutput() { return ds.toString(); }

        @Override
        protected ModelProcessor createModelProcessor(PlexusContainer container) throws ComponentLookupException {
System.out.println(String.valueOf("INTERCEPT"));
            ModelProcessor processor =
                super.createModelProcessor(container);
System.out.println(String.valueOf(processor));
/*
            Maven maven = container.lookup(Maven.class);
System.out.println(maven);
            DefaultMavenExecutionRequest request =
                new DefaultMavenExecutionRequest();

            try {
                container
                    .lookup(MavenExecutionRequestPopulator.class)
                    .populateDefaults(request);
            } catch (MavenExecutionRequestPopulationException exception) {
                throw new ComponentLookupException(exception, null, null);
            }

            MavenExecutionResult result = maven.execute(request);

            EmbeddedMaven.this.project = result.getProject();
System.out.println(EmbeddedMaven.this.project);
*/
            return processor;
        }
    }
}
