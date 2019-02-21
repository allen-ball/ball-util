/*
 * $Id$
 *
 * Copyright 2010 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import ball.util.Combinations;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.tools.ant.BuildException;

import static lombok.AccessLevel.PROTECTED;
import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * Abstract {@link.uri http://ant.apache.org/ Ant}
 * {@link org.apache.tools.ant.Task} to analyze {@link Combinations} of a
 * {@link Collection}.
 *
 * {@ant.task}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@NoArgsConstructor(access = PROTECTED)
public abstract class CombinationsTask extends InstanceOfTask {
    @NotNull @Getter @Setter
    private Integer count = null;

    /**
     * {@link.uri http://ant.apache.org/ Ant}
     * {@link org.apache.tools.ant.Task} to count {@link Combinations}.
     *
     * {@ant.task}
     */
    @AntTask("combinations-count")
    @NoArgsConstructor @ToString
    public static class Count extends CombinationsTask {
        @Override
        public void execute() throws BuildException {
            super.execute();

            try {
                Collection<?> collection = Collection.class.cast(instance);
                long count = 0;

                for (List<?> list :
                         new Combinations<>(collection, getCount())) {
                    count += 1;
                }

                log(EMPTY);
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
     * {@ant.task}
     */
    @AntTask("combinations-of")
    @NoArgsConstructor @ToString
    public static class Of extends CombinationsTask {
        @Override
        public void execute() throws BuildException {
            super.execute();

            try {
                Collection<?> collection = Collection.class.cast(instance);

                log(EMPTY);

                for (List<?> list :
                         new Combinations<>(collection, getCount())) {
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
