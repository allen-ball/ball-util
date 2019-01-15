/*
 * $Id$
 *
 * Copyright 2008 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.util.ClasspathUtils;

import static lombok.AccessLevel.PROTECTED;

/**
 * Abstract base class for {@link.uri http://ant.apache.org/ Ant}
 * {@link MatchingTask} implementations.
 *
 * {@bean.info}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@NoArgsConstructor(access = PROTECTED)
public abstract class AbstractMatchingTask extends MatchingTask
                                           implements AnnotatedAntTask,
                                                      ClasspathDelegateAntTask {
    @Getter @Setter @Accessors(chain = true, fluent = true)
    private ClasspathUtils.Delegate delegate = null;
    @Getter @Setter
    private File basedir = null;
    @Getter @Setter
    private File file = null;

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

        if (getBasedir() == null && getFile() == null) {
            setBasedir(getProject().resolveFile("."));
        }
    }

    /**
     * Method to get {@link.this} {@link MatchingTask}'s {@link File}s as a
     * {@link Set}.
     *
     * @return  The {@link Set} of matching {@link File}s.
     */
    protected Set<File> getMatchingFileSet() {
        TreeSet<File> set = new TreeSet<>();
        File base = getBasedir();

        if (base != null) {
            for (String path : getDirectoryScanner(base).getIncludedFiles()) {
                set.add(new File(base, path));
            }
        }

        File file = getFile();

        if (file != null) {
            set.add(file);
        }

        return set;
    }
}
