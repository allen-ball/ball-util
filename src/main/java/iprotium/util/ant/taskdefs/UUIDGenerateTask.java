/*
 * $Id$
 *
 * Copyright 2011 - 2014 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import iprotium.annotation.AntTask;
import iprotium.util.UUIDFactory;
import java.util.UUID;
import org.apache.tools.ant.Task;

/**
 * {@link.uri http://ant.apache.org/ Ant} {@link Task} to generate new
 * unique {@link UUID}s.
 *
 * {@bean-info}
 *
 * @see Random
 * @see Time
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@AntTask("uuid-generate")
public class UUIDGenerateTask extends AbstractPropertyTask {

    /**
     * Sole constructor.
     */
    public UUIDGenerateTask() { super(); }

    protected UUID generate() { return UUIDFactory.getDefault().generate(); }

    @Override
    protected String getPropertyValue() {
        return generate().toString().toUpperCase();
    }

    /**
     * {@link.uri http://ant.apache.org/ Ant} {@link Task} to generate a new
     * {@link UUID} with {@link UUIDFactory#generateRandom()}.
     *
     * {@bean-info}
     */
    @AntTask("uuid-generate-random")
    public static class Random extends UUIDGenerateTask {

        /**
         * Sole constructor.
         */
        public Random() { super(); }

        @Override
        protected UUID generate() {
            return UUIDFactory.getDefault().generateRandom();
        }
    }

    /**
     * {@link.uri http://ant.apache.org/ Ant} {@link Task} to generate a new
     * {@link UUID} with {@link UUIDFactory#generateTime()}.
     *
     * {@bean-info}
     */
    @AntTask("uuid-generate-time")
    public static class Time extends UUIDGenerateTask {

        /**
         * Sole constructor.
         */
        public Time() { super(); }

        @Override
        protected UUID generate() {
            return UUIDFactory.getDefault().generateTime();
        }
    }
}
