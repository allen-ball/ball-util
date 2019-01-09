/*
 * $Id$
 *
 * Copyright 2013 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import ball.activation.JAXBDataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.tools.ant.BuildException;

/**
 * {@link.uri http://ant.apache.org/ Ant} {@link org.apache.tools.ant.Task}
 * to get an instance of a specified {@link Class} and then marshal with a
 * {@link Marshaller}.
 *
 * {@bean.info}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@AntTask("jaxb-marshal")
@NoArgsConstructor @ToString
public class JAXBMarshalTask extends InstanceOfTask {
    @Getter @Setter
    private String context = null;

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
