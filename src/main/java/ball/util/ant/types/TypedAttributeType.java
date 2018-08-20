/*
 * $Id$
 *
 * Copyright 2016 - 2018 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.types;

import ball.util.Factory;
import ball.util.ant.taskdefs.NotNull;
import java.beans.ConstructorProperties;
import org.apache.tools.ant.BuildException;

/**
 * Class to provide a typed name-value (attribute) for
 * {@link.uri http://ant.apache.org/ Ant} {@link org.apache.tools.ant.Task}
 * implementations.
 *
 * {@bean.info}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class TypedAttributeType extends StringAttributeType {
    private String type = String.class.getName();

    /**
     * @param   name            The attribute name.
     */
    @ConstructorProperties({ "name" })
    public TypedAttributeType(String name) { super(name); }

    /**
     * No-argument constructor.
     */
    public TypedAttributeType() { this(null); }

    @NotNull
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Object getTypedValue() throws BuildException {
        return getTypedValue(getClass().getClassLoader());
    }

    public Object getTypedValue(ClassLoader loader) throws BuildException {
        Object object = null;

        try {
            Class<?> type = Class.forName(getType(), false, loader);
            Factory<?> factory = new Factory<Object>(type);

            object = factory.getInstance(getValue());
        } catch (BuildException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new BuildException(exception);
        }

        return object;
    }
}
