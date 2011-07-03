/*
 * $Id$
 *
 * Copyright 2010, 2011 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import iprotium.util.Combinations;
import java.util.Collection;
import java.util.List;
import org.apache.tools.ant.BuildException;

/**
 * Abstract <a href="http://ant.apache.org/">Ant</a>
 * {@link org.apache.tools.ant.Task} to analyze {@link Combinations} of a
 * {@link Collection}.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
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
     * <a href="http://ant.apache.org/">Ant</a>
     * {@link org.apache.tools.ant.Task} to count {@link Combinations}.
     */
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

                log("");
                log(count + " combinations of " + getCount());
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

    /**
     * <a href="http://ant.apache.org/">Ant</a>
     * {@link org.apache.tools.ant.Task} to show the {@link Combinations}.
     */
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

                log("");

                for (List<?> list :
                         new Combinations<Object>(collection, getCount())) {
                    log(String.valueOf(list));
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
}
