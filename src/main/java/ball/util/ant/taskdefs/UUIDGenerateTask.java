/*
 * $Id$
 *
 * Copyright 2011 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import ball.util.UUIDFactory;
import java.util.UUID;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.tools.ant.Task;

import static lombok.AccessLevel.PROTECTED;

/**
 * {@link.uri http://ant.apache.org/ Ant} {@link Task} to generate new
 * unique {@link UUID}s.
 *
 * {@bean.info}
 *
 * @see Random
 * @see Time
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@AntTask("uuid-generate")
@NoArgsConstructor(access = PROTECTED)
public class UUIDGenerateTask extends AbstractPropertyTask {
    protected UUID generate() { return UUIDFactory.getDefault().generate(); }

    @Override
    protected String getPropertyValue() {
        return generate().toString().toUpperCase();
    }

    /**
     * {@link.uri http://ant.apache.org/ Ant} {@link Task} to generate a new
     * {@link UUID} with {@link UUIDFactory#generateRandom()}.
     *
     * {@bean.info}
     */
    @AntTask("uuid-generate-random")
    @NoArgsConstructor @ToString
    public static class Random extends UUIDGenerateTask {
        @Override
        protected UUID generate() {
            return UUIDFactory.getDefault().generateRandom();
        }
    }

    /**
     * {@link.uri http://ant.apache.org/ Ant} {@link Task} to generate a new
     * {@link UUID} with {@link UUIDFactory#generateTime()}.
     *
     * {@bean.info}
     */
    @AntTask("uuid-generate-time")
    @NoArgsConstructor @ToString
    public static class Time extends UUIDGenerateTask {
        @Override
        protected UUID generate() {
            return UUIDFactory.getDefault().generateTime();
        }
    }
}
