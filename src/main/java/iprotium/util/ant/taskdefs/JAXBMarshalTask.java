/*
 * $Id$
 *
 * Copyright 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import iprotium.annotation.AntTask;
import iprotium.xml.bind.JAXBDataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import org.apache.tools.ant.BuildException;

/**
 * {@link.uri http://ant.apache.org/ Ant} {@link org.apache.tools.ant.Task}
 * to get an instance of a specified {@link Class} and then marshal with a
 * {@link Marshaller}.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
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
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw new BuildException(throwable);
        }
    }
}
