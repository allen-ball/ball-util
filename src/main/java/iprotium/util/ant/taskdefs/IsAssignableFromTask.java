/*
 * $Id$
 *
 * Copyright 2008 - 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import iprotium.annotation.AntTask;
import iprotium.util.SuperclassSet;
import org.apache.tools.ant.BuildException;

import static iprotium.util.ClassOrder.INHERITANCE;

/**
 * <a href="http://ant.apache.org/">Ant</a>
 * {@link org.apache.tools.ant.Task} to display superclasses of a specified
 * {@link Class}.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
@AntTask("is-assignable-from")
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
        if (getType() == null) {
            throw new BuildException("`type' attribute must be specified");
        }

        if (getSubtype() == null) {
            throw new BuildException("`subtype' attribute must be specified");
        }

        try {
            Class<?> supertype =
                Class.forName(getType(), false, getClassLoader());
            Class<?> subtype =
                Class.forName(getSubtype(), false, supertype.getClassLoader());

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
