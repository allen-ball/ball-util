/*
 * $Id$
 *
 * Copyright 2010 - 2014 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import iprotium.annotation.AntTask;
import iprotium.util.Combinations;
import java.util.Collection;
import java.util.List;
import org.apache.tools.ant.BuildException;

import static iprotium.util.StringUtil.NIL;

/**
 * Abstract {@link.uri http://ant.apache.org/ Ant}
 * {@link org.apache.tools.ant.Task} to analyze {@link Combinations} of a
 * {@link Collection}.
 *
 * {@bean-info}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class CombinationsTask extends InstanceOfTask {
    private Integer count = null;

    /**
     * Sole constructor.
     */
    protected CombinationsTask() { super(); }

    protected Integer getCount() { return count; }
    public void setCount(Integer count) { this.count = count; }

    @Override
    public void execute() throws BuildException {
        if (getCount() == null) {
            throw new BuildException("`count' attribute must be specified");
        }

        super.execute();
    }

    /**
     * {@link.uri http://ant.apache.org/ Ant}
     * {@link org.apache.tools.ant.Task} to count {@link Combinations}.
     *
     * {@bean-info}
     */
    @AntTask("combinations-count")
    public static class Count extends CombinationsTask {

        /**
         * Sole constructor.
         */
        public Count() { super(); }

        @Override
        public void execute() throws BuildException {
            super.execute();

            try {
                Collection<?> collection = Collection.class.cast(instance);
                long count = 0;

                for (List<?> list :
                         new Combinations<Object>(collection, getCount())) {
                    count += 1;
                }

                log(NIL);
                log(count + " combinations of " + getCount());
            } catch (BuildException exception) {
                throw exception;
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                throw new BuildException(throwable);
            }
        }
    }

    /**
     * {@link.uri http://ant.apache.org/ Ant}
     * {@link org.apache.tools.ant.Task} to show the {@link Combinations}.
     *
     * {@bean-info}
     */
    @AntTask("combinations-of")
    public static class Of extends CombinationsTask {

        /**
         * Sole constructor.
         */
        public Of() { super(); }

        @Override
        public void execute() throws BuildException {
            super.execute();

            try {
                Collection<?> collection = Collection.class.cast(instance);

                log(NIL);

                for (List<?> list :
                         new Combinations<Object>(collection, getCount())) {
                    log(String.valueOf(list));
                }
            } catch (BuildException exception) {
                throw exception;
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                throw new BuildException(throwable);
            }
        }
    }
}
