/*
 * $Id: SuperclassesOfTask.java,v 1.9 2010-12-08 04:48:28 ball Exp $
 *
 * Copyright 2008 - 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import iprotium.util.SuperclassSet;
import java.lang.reflect.TypeVariable;
import org.apache.tools.ant.BuildException;

import static iprotium.lang.java.Punctuation.GT;
import static iprotium.lang.java.Punctuation.LT;
import static iprotium.util.ClassOrder.INHERITANCE;

/**
 * <a href="http://ant.apache.org/">Ant</a>
 * {@link org.apache.tools.ant.Task} to display superclasses of a specified
 * {@link Class}.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.9 $
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

            for (Class<?> superclass :
                     INHERITANCE.asList(new SuperclassSet(type))) {
                log(toString(superclass));
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

    private String toString(Class<?> type) {
        StringBuilder buffer = new StringBuilder(type.getName());

        if (type.getTypeParameters().length > 0) {
            buffer.append(LT.lexeme());

            for (TypeVariable<?> parameter : type.getTypeParameters()) {
                buffer.append(parameter);
            }

            buffer.append(GT.lexeme());
        }

        return buffer.toString();
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
