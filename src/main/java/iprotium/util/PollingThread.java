/*
 * $Id$
 *
 * Copyright 2010, 2011 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

/**
 * Abstract base class for {@link Thread} implementations that periodically
 * poll.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public abstract class PollingThread extends ThreadImpl {
    private long interval = 0;
    private transient volatile long start = 0;

    /**
     * Creates a {@link PollingThread} instance that polls no less than
     * every <b>interval</b> <b>units</b>.
     *
     * @param   interval        The minimum interval
     *                          (in {@link TimeUnitEnum}) between calls to
     *                          the {@link #poll()} method.
     * @param   units           The interval's units.
     *
     * @throws  IllegalArgumentException
     *                          If interval is not greater than or equal to
     *                          zero.
     */
    protected PollingThread(long interval, TimeUnitEnum units) {
        super();

        setInterval(interval, units);

        String name = getClass().getName();

        if (name != null) {
            setName(name);
        }
    }

    /**
     * Method to get the polling interval (in milliseconds).
     *
     * @return  The polling interval.
     */
    public long getInterval() { return interval; }

    /**
     * Method to set the polling interval.
     *
     * @param   interval        The minimum interval (in milliseconds)
     *                          between calls to the {@link #poll()}
     *                          method.
     *
     * @throws  IllegalArgumentException
     *                          If interval is not greater than or equal to
     *                          zero.
     */
    protected void setInterval(long interval) {
        if (interval >= 0) {
            this.interval = interval;
        } else {
            throw new IllegalArgumentException("interval=" + interval);
        }
    }

    /**
     * Method to set the polling interval.
     *
     * @param   interval        The minimum interval (in
     *                          {@link TimeUnitEnum}) between calls to the
     *                          poll() method.
     * @param   units           The interval's units.
     *
     * @throws  IllegalArgumentException
     *                          If interval is not greater than or equal to
     *                          zero.
     */
    protected void setInterval(long interval, TimeUnitEnum units) {
        if (units != null) {
            setInterval(units.toMilliseconds(interval));
        } else {
            throw new NullPointerException("units");
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see #begin()
     * @see #poll()
     * @see #finish()
     */
    @Override
    public void run() {
        try {
            begin();

            while (! isShutdown()) {
                setStartTime(now());
                poll();

                long throttle = getRemainingTime();

                if (throttle > 0) {
                    try {
                        synchronized (this){
                            wait(throttle);
                        }
                    } catch (InterruptedException exception) {
                    }
                }
            }
        } finally {
            if (! isShutdown()) {
                shutdown();
            }

            finish();
        }
    }

    /**
     * Method called before starting the poll loop.  Default implementation
     * does nothing.  May be overridden by subclasses.
     */
    protected void begin() { }

    /**
     * Abstract method overridden by subclasses to do any work during each
     * poll cycle.
     */
    protected abstract void poll();

    /**
     * Method called upon completing the poll loop.  Default implementation
     * does nothing.  May be overridden by subclasses.
     */
    protected void finish() { }

    /**
     * Method to get the start time of the current polling interval.
     *
     * @return  The start time of the current polling interval.
     */
    protected long getStartTime() { return start; }
    private void setStartTime(long start) { this.start = start; }

    /**
     * Method to get the elapsed time of the current polling interval.
     *
     * @return  The elapsed time of the current polling interval.
     */
    protected long getElapsedTime() { return getElapsedTime(getStartTime()); }

    /**
     * Method to get the remaining time on the current polling interval.
     *
     * @return  The remaining time on the current polling interval; negative
     *          if no time is left.
     */
    protected long getRemainingTime() {
        return getInterval() - getElapsedTime();
    }

    /**
     * Method to determine if there is time remaining on the current polling
     * interval.
     *
     * @return  {@code true} if there is time remaining on the current
     *          polling interval; {@code false} if there is no time is
     *          left.
     *
     * @see #isShutdown()
     */
    protected boolean hasRemainingTime() {
        return (! isShutdown()) && (getRemainingTime() > 0);
    }
}
