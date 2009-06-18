/*
 * $Id: MembersOfTask.java,v 1.4 2009-06-18 06:35:17 ball Exp $
 *
 * Copyright 2008, 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import java.lang.reflect.Member;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * Ant Task to display members of a specified Class.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.4 $
 */
public class MembersOfTask extends AbstractClasspathTask {
    private String type = null;

    /**
     * Sole constructor.
     */
    public MembersOfTask() { super(); }

    protected String getType() { return type; }
    public void setType(String type) { this.type = type; }

    @Override
    public void execute() throws BuildException {
        super.execute();

        if (getType() == null) {
            throw new BuildException("`type' attribute must be specified");
        }

        try {
            Class<?> type = getClass(getType());

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
