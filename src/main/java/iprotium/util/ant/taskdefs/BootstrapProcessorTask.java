/*
 * $Id$
 *
 * Copyright 2011 - 2014 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import org.apache.tools.ant.BuildException;

import static iprotium.util.ClassUtil.isAbstract;

/**
 * {@link.uri http://ant.apache.org/ Ant} {@link org.apache.tools.ant.Task}
 * to bootstrap {@link javax.annotation.processing.Processor}s.  Creates and
 * invokes {@link Processor}s found on the class path.
 *
 * {@bean-info}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
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
            for (Class<?> type : getClassSet()) {
                if (Processor.class.isAssignableFrom(type)) {
                    if (! isAbstract(type)) {
                        type.asSubclass(Processor.class).newInstance()
                            .process(getClassSet(), getDestdir());
                    }
                }
            }
        } catch (BuildException exception) {
            throw exception;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw new BuildException(throwable);
        }
    }

    /**
     * Bootstrap processor interface.
     */
    public interface Processor {

        /**
         * Bootstrap method called by this {@link org.apache.tools.ant.Task}.
         *
         * @param       set     The {@link Set} of {@link Class}es to
         *                      examine.
         * @param       destdir The root of the hierarchy to record any
         *                      output artifacts.
         */
        public void process(Set<Class<?>> set, File destdir)
            throws IOException;
    }
}
