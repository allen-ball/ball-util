/*
 * $Id$
 *
 * Copyright 2010 - 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import iprotium.annotation.AntTask;
import org.apache.tools.ant.BuildException;

/**
 * <a href="http://ant.apache.org/">Ant</a>
 * {@link org.apache.tools.ant.Task} to instantiate a {@link Runnable} and
 * then invoke its {@link Runnable#run()} method.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
@AntTask("run")
public class RunTask extends InstanceOfTask {

    /**
     * Sole constructor.
     */
    public RunTask() { super(); }

    @Override
    public void execute() throws BuildException {
        super.execute();

        try {
            Runnable.class.cast(instance).run();
        } catch (BuildException exception) {
            throw exception;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw new BuildException(throwable);
        }
    }
}
