/*
 * $Id$
 *
 * Copyright 2011 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.util.ClasspathUtils;

import static lombok.AccessLevel.PROTECTED;

/**
 * Abstract {@link.uri http://ant.apache.org/ Ant} base class for
 * {@link Task}s that may assign property values.
 *
 * {@bean.info}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@NoArgsConstructor(access = PROTECTED)
public abstract class AbstractPropertyTask extends Task
                                           implements AnnotatedAntTask,
                                                      ClasspathDelegateAntTask,
                                                      ConfigurableAntTask,
                                                      AntTaskLogMethods {
    @Getter @Setter @Accessors(chain = true, fluent = true)
    private ClasspathUtils.Delegate delegate = null;
    @Getter @Setter
    private String property = null;

    /**
     * Method to get the value to assign to the property.
     *
     * @return  The property value.
     *
     * @throws  Throwable       As specified by implementing subclass.
     */
    protected abstract String getPropertyValue() throws Throwable;

    @Override
    public void init() throws BuildException {
        super.init();
        ClasspathDelegateAntTask.super.init();
        ConfigurableAntTask.super.init();
    }

    @Override
    public void execute() throws BuildException {
        super.execute();
        AnnotatedAntTask.super.execute();

        String key = getProperty();
        String value = null;

        try {
            value = getPropertyValue();
        } catch (BuildException exception) {
            throw exception;
        } catch (Throwable throwable) {
            if (key == null) {
                throw new BuildException(throwable);
            }
        }

        if (key != null) {
            if (value != null) {
                getProject().setProperty(key, value);
            }
        } else {
            log(value);
        }
    }
}
