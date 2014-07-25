/*
 * $Id$
 *
 * Copyright 2008 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import ball.annotation.AntTask;
import ball.util.SuperclassSet;
import java.lang.reflect.TypeVariable;
import org.apache.tools.ant.BuildException;

import static ball.lang.Punctuation.GT;
import static ball.lang.Punctuation.LT;
import static ball.util.ClassOrder.INHERITANCE;

/**
 * {@link.uri http://ant.apache.org/ Ant} {@link org.apache.tools.ant.Task}
 * to display superclasses of a specified {@link Class}.
 *
 * {@bean-info}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@AntTask("superclasses-of")
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
            Class<?> type = Class.forName(getType(), false, getClassLoader());

            for (Class<?> superclass :
                     INHERITANCE.asList(new SuperclassSet(type))) {
                log(toString(superclass));
            }
        } catch (BuildException exception) {
            throw exception;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw new BuildException(throwable);
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