/*
 * $Id$
 *
 * Copyright 2011 - 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import iprotium.annotation.processing.AntTaskProcessor;
import iprotium.annotation.processing.ManifestSectionProcessor;
import iprotium.annotation.processing.ServiceProviderForProcessor;
import java.io.File;
import org.apache.tools.ant.BuildException;

/**
 * <a href="http://ant.apache.org/">Ant</a>
 * {@link org.apache.tools.ant.Task} to bootstrap
 * {@link javax.annotation.processing.Processor}s.
 *
 * @see AntTaskProcessor#bootstrap(Set,File)
 * @see ManifestSectionProcessor#bootstrap(Set,File)
 * @see ServiceProviderForProcessor#bootstrap(Set,File)
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class BootstrapProcessorTask extends AbstractClassFileTask {
    private File destdir = null;

    /**
     * Sole constructor.
     */
    public BootstrapProcessorTask() { super(); }

    protected File getDestdir() { return destdir; }
    public void setDestdir(File destdir) { this.destdir = destdir; }

    @Override
    public void execute() throws BuildException {
        super.execute();

        if (getBasedir() == null) {
            setBasedir(getProject().resolveFile("."));
        }

        if (getDestdir() == null) {
            setDestdir(getBasedir());
        }

        try {
            new AntTaskProcessor()
                .bootstrap(getClassSet(), getDestdir());
            new ManifestSectionProcessor()
                .bootstrap(getClassSet(), getDestdir());
            new ServiceProviderForProcessor()
                .bootstrap(getClassSet(), getDestdir());
        } catch (BuildException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            exception.printStackTrace();
            throw exception;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BuildException(exception);
        }
    }
}
