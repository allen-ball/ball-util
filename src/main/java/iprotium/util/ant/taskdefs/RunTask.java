/*
 * $Id: RunTask.java,v 1.1 2010-12-21 15:33:56 ball Exp $
 *
 * Copyright 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import org.apache.tools.ant.BuildException;

/**
 * <a href="http://ant.apache.org/">Ant</a>
 * {@link org.apache.tools.ant.Task} to instantiate a {@link Runnable} and
 * then invoke its {@link Runnable.run()} method.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
 */
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
        } catch (RuntimeException exception) {
            exception.printStackTrace();
            throw exception;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BuildException(exception);
        }
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
