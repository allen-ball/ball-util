/*
 * $Id: MembersOfTask.java,v 1.1 2008-10-27 00:10:03 ball Exp $
 *
 * Copyright 2008 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import java.lang.reflect.Member;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * Ant Task to display members of a specified Class.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
 */
public class MembersOfTask extends AbstractClasspathTask {
    private String name = null;

    /**
     * Sole constructor.
     */
    public MembersOfTask() { super(); }

    protected String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public void execute() throws BuildException {
        try {
            if (getName() == null) {
                throw new BuildException("`name' attribute must be specified");
            }

            Class<?> type =
                Class.forName(getName(), false, delegate.getClassLoader());

            log(String.valueOf(type));

            for (Member member : type.getDeclaredConstructors()) {
                log(String.valueOf(member));
            }

            for (Member member : type.getDeclaredFields()) {
                log(String.valueOf(member));
            }

            for (Member member : type.getDeclaredMethods()) {
                log(String.valueOf(member));
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
