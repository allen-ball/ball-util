/*
 * $Id: SuperclassesOfTask.java,v 1.7 2009-09-04 17:13:43 ball Exp $
 *
 * Copyright 2008, 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import iprotium.util.SuperclassSet;
import org.apache.tools.ant.BuildException;

import static iprotium.util.ClassOrder.INHERITANCE;

/**
 * Ant {@link org.apache.tools.ant.Task} to display superclasses of a
 * specified {@link Class}.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.7 $
 */
public class SuperclassesOfTask extends AbstractClasspathTask {
    private String type = null;

    /**
     * Sole constructor.
     */
    public SuperclassesOfTask() { super(); }

    protected String getType() { return type; }
    public void setType(String type) { this.type = type; }

    @Override
    public void execute() throws BuildException {
        if (getType() == null) {
            throw new BuildException("`type' attribute must be specified");
        }

        try {
            Class type = getClass(getType());

            for (Object object : INHERITANCE.asList(new SuperclassSet(type))) {
                log(String.valueOf(object));
            }
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
