/*
 * $Id$
 *
 * Copyright 2018 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import ball.util.PropertiesImpl;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * Provides default {@link Task#init()} method to initialize {@link Task}
 * attributes from project properties.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public interface ConfigurableAntTask {

    /**
     * Default method to configure {@link Task} attributes.
     */
    default void configure() throws BuildException {
        try {
            PropertiesImpl properties = new PropertiesImpl();

            properties.putAll(((Task) this).getProject().getProperties());
            properties.keySet()
                .removeAll(((Task) this).getRuntimeConfigurableWrapper()
                           .getAttributeMap().keySet());
            properties.configure(this);
        } catch (BuildException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new BuildException(exception);
        }
    }
}
