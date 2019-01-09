/*
 * $Id$
 *
 * Copyright 2008 - 2019 Allen D. Ball.  All rights reserved.
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
 * Abstract base class for {@link.uri http://ant.apache.org/ Ant}
 * {@link Task} implementations that require a classpath.
 *
 * {@bean.info}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@NoArgsConstructor(access = PROTECTED)
public abstract class AbstractClasspathTask extends Task
                                            implements AnnotatedAntTask,
                                                       ClasspathDelegateAntTask,
                                                       AntTaskLogMethods {
    @Getter @Setter @Accessors(chain = true, fluent = true)
    private ClasspathUtils.Delegate delegate = null;

    /**
     * {@inheritDoc}
     *
     * Invokes {@link ConfigurableAntTask#configure()} if {@link.this}
     * implements {@link ConfigurableAntTask}.
     */
    @Override
    public void init() throws BuildException {
        super.init();

        /* ClasspathDelegateAntTask.super.init(); */
        if (this instanceof ClasspathDelegateAntTask) {
            if (delegate() == null) {
                delegate(ClasspathUtils.getDelegate(this));
            }
        }

        /* ConfigurableAntTask.super.init(); */
        if (this instanceof ConfigurableAntTask) {
            ((ConfigurableAntTask) this).configure();
        }
    }

    /**
     * {@inheritDoc}
     *
     * Invokes {@link AnnotatedAntTask#validate()} if {@link.this}
     * implements {@link AnnotatedAntTask}.
     */
    @Override
    public void execute() throws BuildException {
        super.execute();

        /* AnnotatedAntTask.super.execute(); */
        if (this instanceof AnnotatedAntTask) {
            ((AnnotatedAntTask) this).validate();
        }
    }
}
