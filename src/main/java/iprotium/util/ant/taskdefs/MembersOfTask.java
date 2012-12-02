/*
 * $Id$
 *
 * Copyright 2008 - 2012 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.apache.tools.ant.BuildException;

/**
 * <a href="http://ant.apache.org/">Ant</a>
 * {@link org.apache.tools.ant.Task} to display members of a specified
 * {@link Class}.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
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
        } catch (RuntimeException exception) {
            exception.printStackTrace();
            throw exception;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BuildException(exception);
        }
    }
}
