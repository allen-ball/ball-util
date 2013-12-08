/*
 * $Id$
 *
 * Copyright 2008 - 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import iprotium.annotation.AntTask;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.apache.tools.ant.BuildException;

/**
 * {@link.uri http://ant.apache.org/ Ant} {@link org.apache.tools.ant.Task}
 * to display members of a specified {@link Class}.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@AntTask("members-of")
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
        if (getType() == null) {
            throw new BuildException("`type' attribute must be specified");
        }

        try {
            Class<?> type = Class.forName(getType(), false, getClassLoader());

            log(String.valueOf(type));

            for (Constructor<?> constructor : type.getDeclaredConstructors()) {
                log(constructor.toGenericString());
            }

            for (Field field : type.getDeclaredFields()) {
                log(field.toGenericString());
            }

            for (Method method : type.getDeclaredMethods()) {
                log(method.toGenericString());
            }

            for (Class<?> cls : type.getDeclaredClasses()) {
                log(cls.toString());
            }
        } catch (BuildException exception) {
            throw exception;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw new BuildException(throwable);
        }
    }
}
