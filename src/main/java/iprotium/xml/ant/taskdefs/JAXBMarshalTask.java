/*
 * $Id$
 *
 * Copyright 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.xml.ant.taskdefs;

import iprotium.annotation.AntTask;
import iprotium.util.ant.taskdefs.InstanceOfTask;
import iprotium.xml.bind.JAXBDataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import org.apache.tools.ant.BuildException;

/**
 * <a href="http://ant.apache.org/">Ant</a>
 * {@link org.apache.tools.ant.Task} to get an instance of a specified
 * {@link Class} and then marshal with a {@link Marshaller}.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
@AntTask("jaxb-marshal")
public class JAXBMarshalTask extends InstanceOfTask {
    private String context = null;

    /**
     * Sole constructor.
     */
    public JAXBMarshalTask() { super(); }

    protected String getContext() { return context; }
    public void setContext(String context) { this.context = context; }

    @Override
    public void execute() throws BuildException {
        super.execute();

        try {
            JAXBContext context = null;

            if (getContext() != null) {
                context =
                    JAXBContext.newInstance(getContext(), getClassLoader());
            } else {
                context = JAXBContext.newInstance(instance.getClass());
            }

            JAXBDataSource ds = new JAXBDataSource();

            ds.marshal(context, instance);

            log(ds);
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
