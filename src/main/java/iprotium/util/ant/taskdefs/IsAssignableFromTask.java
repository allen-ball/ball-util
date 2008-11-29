/*
 * $Id: IsAssignableFromTask.java,v 1.2 2008-10-30 07:50:34 ball Exp $
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
public class IsAssignableFromTask extends AbstractClasspathTask {
    private String type = null;
    private String subtype = null;

    /**
     * Sole constructor.
     */
    public IsAssignableFromTask() { super(); }

    protected String getType() { return type; }
    public void setType(String type) { this.type = type; }

    protected String getSubtype() { return subtype; }
    public void setSubtype(String subtype) { this.subtype = subtype; }

    @Override
    public void execute() throws BuildException {
        super.execute();

        if (getType() == null) {
            throw new BuildException("`type' attribute must be specified");
        }

        if (getSubtype() == null) {
            throw new BuildException("`subtype' attribute must be specified");
        }

        try {
            Class<?> supertype = getClass(getType());
            Class<?> subtype = getClass(getSubtype());

            log(supertype.getName() + " is "
                + (supertype.isAssignableFrom(subtype) ? "" : "not ")
                + "assignable from " + subtype.getName());
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