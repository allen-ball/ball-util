/*
 * $Id$
 *
 * Copyright 2011 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * Interface to provide common default methods for {@link Task}s that may
 * assign property values.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public interface PropertySetterAntTask extends AntTaskMixIn {
    String getProperty();
    void setProperty(String property);

    /**
     * Method to get the value to assign to the property.
     *
     * @return  The property value.
     *
     * @throws  Exception       As specified by implementing subclass.
     */
    Object getPropertyValue() throws Exception;

    default void execute() throws BuildException {
        try {
            Task task = (Task) this;
            String key = getProperty();
            Object value = getPropertyValue();

            if (key != null) {
                task.getProject().setNewProperty(key, value.toString());
            } else {
                task.log(String.valueOf(value));
            }
        } catch (BuildException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new BuildException(exception);
        }
    }
}
