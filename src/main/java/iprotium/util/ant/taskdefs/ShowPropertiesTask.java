/*
 * $Id: ShowPropertiesTask.java,v 1.4 2008-11-04 04:12:36 ball Exp $
 *
 * Copyright 2008 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import iprotium.util.Property;
import java.util.Collection;
import org.apache.tools.ant.BuildException;

/**
 * Ant Task to find and display static Property members.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.4 $
 */
public class ShowPropertiesTask extends AbstractClassFileTask {
    private static final String TAB = "\t";

    /**
     * Sole constructor.
     */
    public ShowPropertiesTask() { super(); }

    @Override
    public void execute() throws BuildException {
        super.execute();

        try {
            for (Class type : getMatchingClassFileMap().values()) {
                Collection<Property> collection =
                    Property.getStaticPropertyFields(type);

                if (! collection.isEmpty()) {
                    log("");
                    log(type.getName());

                    for (Property property : collection) {
                        log(property.getName()
                            + TAB + property.isRequired()
                            + TAB + property.getDefaultValueAsString());
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
