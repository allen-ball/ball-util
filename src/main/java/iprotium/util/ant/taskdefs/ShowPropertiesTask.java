/*
 * $Id: ShowPropertiesTask.java,v 1.1 2008-10-27 00:10:03 ball Exp $
 *
 * Copyright 2008 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import iprotium.util.Property;
import iprotium.util.StringProperty;
import java.lang.reflect.Field;
import org.apache.tools.ant.BuildException;

/**
 * Ant Task to find and display static Property members.
 *
 * Warning:
 *
 *      <code>if (object instanceof Property<?>) {</code>
 *
 * does not work as expected.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
 */
public class ShowPropertiesTask extends AbstractClassFileTask {
    public static final Property<?> PROPERTY =
        new StringProperty("PROPERTY-NAME", "DEFAULT-VALUE");

    /**
     * Sole constructor.
     */
    public ShowPropertiesTask() { super(); }

    @Override
    public void execute() throws BuildException {
        if (getBasedir() == null) {
            setBasedir(getProject().resolveFile("."));
        }

        try {
            for (Class<?> type : getMatchingClassFileMap().values()) {
                for (Field field : type.getDeclaredFields()) {
                    if (isPublic(field) && isStatic(field)) {
                        Object object = field.get(null);

                        if (object instanceof Property<?>) {
                            Property<?> property = (Property<?>) object;

                            log(property.getName()
                                + ": " + property.getDefaultValue());
                        }
                    }
                }
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
