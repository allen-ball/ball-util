/*
 * $Id$
 *
 * Copyright 2008 - 2017 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import org.apache.tools.ant.BuildException;

/**
 * {@link.uri http://ant.apache.org/ Ant} {@link org.apache.tools.ant.Task}
 * to display superclasses of a specified {@link Class}.
 *
 * {@bean.info}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@AntTask("is-assignable-from")
public class IsAssignableFromTask extends TypeTask {
    private String subtype = null;

    /**
     * Sole constructor.
     */
    public IsAssignableFromTask() { super(); }

    @NotNull
    public String getSubtype() { return subtype; }
    public void setSubtype(String subtype) { this.subtype = subtype; }

    @Override
    public void execute() throws BuildException {
        super.execute();

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
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw new BuildException(throwable);
        }
    }
}
