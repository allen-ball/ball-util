/*
 * $Id: PermutationsTask.java,v 1.1 2010-11-22 02:05:54 ball Exp $
 *
 * Copyright 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import iprotium.util.Permutations;
import java.util.Collection;
import java.util.List;
import org.apache.tools.ant.BuildException;

/**
 * Abstract <a href="http://ant.apache.org/">Ant</a>
 * {@link org.apache.tools.ant.Task} to analyze {@link Permutations} of a
 * {@link Collection}.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
 */
public abstract class PermutationsTask extends InstanceOfTask {

    /**
     * Sole constructor.
     */
    protected PermutationsTask() { super(); }

    /**
     * <a href="http://ant.apache.org/">Ant</a>
     * {@link org.apache.tools.ant.Task} to count {@link Permutations}.
     */
    public static class Count extends PermutationsTask {

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

                for (List<?> list : new Permutations<Object>(collection)) {
                    count += 1;
                }

                log("");
                log(count + " permutations");
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
     * {@link org.apache.tools.ant.Task} to show the {@link Permutations}.
     */
    public static class Of extends PermutationsTask {

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

                for (List<?> list : new Permutations<Object>(collection)) {
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
/*
 * $Log: not supported by cvs2svn $
 */
