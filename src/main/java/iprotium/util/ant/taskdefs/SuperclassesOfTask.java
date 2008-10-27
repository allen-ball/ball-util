/*
 * $Id: SuperclassesOfTask.java,v 1.2 2008-10-27 22:04:26 ball Exp $
 *
 * Copyright 2008 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import iprotium.util.SuperclassSet;
import org.apache.tools.ant.BuildException;

import static iprotium.util.ClassOrder.INHERITANCE;

/**
 * Ant Task to display superclasses of a specified Class.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.2 $
 */
public class SuperclassesOfTask extends AbstractClasspathTask {
    private String name = null;

    /**
     * Sole constructor.
     */
    public SuperclassesOfTask() { super(); }

    protected String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public void execute() throws BuildException {
        try {
            if (getName() == null) {
                throw new BuildException("`name' attribute must be specified");
            }

            Class type =
                Class.forName(getName(), false, delegate.getClassLoader());

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
