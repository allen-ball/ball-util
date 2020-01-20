/*
 * $Id$
 *
 * Copyright 2010 - 2020 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import ball.util.stream.Permutations;
import java.util.Collection;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.tools.ant.BuildException;

import static lombok.AccessLevel.PROTECTED;

/**
 * Abstract {@link.uri http://ant.apache.org/ Ant}
 * {@link org.apache.tools.ant.Task} to analyze {@link Permutations} of a
 * {@link Collection}.
 *
 * {@ant.task}
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@NoArgsConstructor(access = PROTECTED)
public abstract class PermutationsTask extends InstanceOfTask {

    /**
     * {@link.uri http://ant.apache.org/ Ant}
     * {@link org.apache.tools.ant.Task} to count {@link Permutations}.
     *
     * {@ant.task}
     */
    @AntTask("permutations-count")
    @NoArgsConstructor @ToString
    public static class Count extends PermutationsTask {
        @Override
        public void execute() throws BuildException {
            super.execute();

            try {
                Collection<?> collection = Collection.class.cast(instance);

                log();
                log(Permutations.of(collection).count() + " permutations");
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
     * {@link org.apache.tools.ant.Task} to show the {@link Permutations}.
     *
     * {@ant.task}
     */
    @AntTask("permutations-of")
    @NoArgsConstructor @ToString
    public static class Of extends PermutationsTask {
        @Override
        public void execute() throws BuildException {
            super.execute();

            try {
                Collection<?> collection = Collection.class.cast(instance);

                log();

                Permutations.of(collection)
                    .forEach(t -> log(String.valueOf(t)));
            } catch (BuildException exception) {
                throw exception;
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                throw new BuildException(throwable);
            }
        }
    }
}
