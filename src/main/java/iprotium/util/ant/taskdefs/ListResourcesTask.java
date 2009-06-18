/*
 * $Id: ListResourcesTask.java,v 1.3 2009-06-18 06:35:17 ball Exp $
 *
 * Copyright 2008, 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import org.apache.tools.ant.BuildException;

/**
 * Ant Task to list the resources that match a specific name.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.3 $
 */
public class ListResourcesTask extends AbstractClasspathTask {
    private String name = null;

    /**
     * Sole constructor.
     */
    public ListResourcesTask() { super(); }

    protected String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public void execute() throws BuildException {
        super.execute();

        if (getName() == null) {
            throw new BuildException("`name' attribute must be specified");
        }

        try {
            log(getName());

            List<URL> list =
                Collections.list(getClassLoader().getResources(getName()));

            for (URL url : list) {
                log(String.valueOf(url));
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
