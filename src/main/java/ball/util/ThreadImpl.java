/*
 * $Id$
 *
 * Copyright 2010 - 2016 Allen D. Ball.  All rights reserved.
 */
package ball.util;

/**
 * Abstract base class for {@link java.lang.Thread} subclass
 * implementations.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class ThreadImpl extends Thread {
    private transient volatile boolean started = false;
    private transient volatile boolean shutdown = false;

    /**
     * Sole constructor.
     *
     */
    protected ThreadImpl() {
        super();

        String name = getClass().getName();

        if (name != null) {
            setName(name);
        }
    }

    @Override
    public void start() {
        synchronized (this) {
            started = true;
            super.start();
        }
    }

    /**
     * Method to determine if the {@link java.lang.Thread} has been started.
     *
     * @return  {@code true} if the start() method has been called;
     *          {@code false} otherwise.
     */
    public boolean isStarted() { return started; }

    /**
     * Method to start orderly shutdown of this thread.
     */
    public void shutdown() {
        synchronized (this) {
            shutdown = true;
            notifyAll();
        }
    }

    /**
     * Method to determine if the {@link java.lang.Thread} has started
     * shutdown.
     *
     * @return  {@code true} if the shutdown() method has been called;
     *          {@code false} otherwise.
     */
    public boolean isShutdown() { return shutdown; }

    @Override
    public abstract void run();

    /**
     * Method to get the elapsed time since a start time.
     *
     * @param   start           The start time.
     *
     * @return  The elapsed time since the specified start.
     */
    protected static long getElapsedTime(long start) { return now() - start; }

    /**
     * See {@link System#currentTimeMillis()}.
     *
     * @return  The difference, measured in milliseconds, between the
     *          current time and midnight, January 1, 1970 UTC.
     */
    protected static long now() { return System.currentTimeMillis(); }

    /**
     * See {@link Runtime#addShutdownHook(Thread)}.
     */
    protected class ShutdownHookThread extends Thread {
        public ShutdownHookThread() {
            super();

            setDaemon(false);
            setName(getClass().getName());
        }

        @Override
        public void run() { shutdown(); }
    }
}
