/*
 * $Id$
 *
 * Copyright 2011 - 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import iprotium.annotation.AntTask;
import iprotium.util.UUIDFactory;
import java.util.UUID;
import org.apache.tools.ant.Task;

/**
 * <a href="http://ant.apache.org/">Ant</a> {@link Task} to generate new
 * unique {@link UUID}s.
 *
 * @see Random
 * @see Time
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
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
     * <a href="http://ant.apache.org/">Ant</a> {@link Task} to generate a
     * new {@link UUID} with {@link UUIDFactory#generateRandom()}.
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
     * <a href="http://ant.apache.org/">Ant</a> {@link Task} to generate a
     * new {@link UUID} with {@link UUIDFactory#generateTime()}.
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
