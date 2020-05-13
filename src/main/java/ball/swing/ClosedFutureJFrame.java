package ball.swing;
/*-
 * ##########################################################################
 * Utilities
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2008 - 2020 Allen D. Ball
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ##########################################################################
 */
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.swing.JFrame;

/**
 * {@link JFrame} implementation that provides a {@link Future} indicating
 * when the {@link JFrame} is closed (or made invisible).
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public class ClosedFutureJFrame extends JFrame {
    private static final long serialVersionUID = -1292559417822522869L;

    /** @serial */ private final Object lock = new Object();
    /** @serial */ private boolean isStarted = false;
    /** @serial */ private boolean isCancelled = false;
    /** @serial */ private final FutureImpl future = new FutureImpl();

    /**
     * Sole constructor.
     *
     * @param   title           The frame title.
     */
    public ClosedFutureJFrame(String title) {
        super(title);

        setDefaultCloseOperation(HIDE_ON_CLOSE);
        addWindowListener(new WindowListenerImpl());
    }

    @Override
    public void setVisible(boolean visible) {
        synchronized (lock) {
            isStarted |= (visible && (! isCancelled));
            super.setVisible(visible && (! isCancelled));

            if (! isVisible()) {
                lock.notifyAll();
            }
        }
    }

    /**
     * Method to get {@link Future} indicating when {@link.this}
     * {@link JFrame} is closed.
     *
     * @return  The closed {@link Future}.
     */
    public Future<Boolean> closedFuture() { return future; }

    private class WindowListenerImpl extends WindowAdapter {
        public WindowListenerImpl() { super(); }

        @Override
        public void windowClosing(WindowEvent event) { setVisible(false); }

        @Override
        public String toString() { return super.toString(); }
    }

    private class FutureImpl implements Future<Boolean> {
        public FutureImpl() { }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            boolean cancelled = false;

            if (isStarted) {
                if (mayInterruptIfRunning) {
                    cancelled = isVisible();
                    isCancelled |= true;
                    setVisible(false);
                }
            } else {
                cancelled = true;
                isCancelled |= true;
            }

            return cancelled;
        }

        @Override
        public boolean isCancelled() { return isCancelled; }

        @Override
        public boolean isDone() { return (isCancelled() || (! isVisible())); }

        @Override
        public Boolean get() throws InterruptedException, ExecutionException {
            Boolean result = null;

            try {
                result = get(0, TimeUnit.MILLISECONDS);
            } catch (TimeoutException exception) {
                throw new IllegalStateException(exception);
            }

            return result;
        }

        @Override
        public Boolean get(long timeout,
                           TimeUnit unit) throws InterruptedException,
                                                 ExecutionException,
                                                 TimeoutException {
            Boolean result = null;

            synchronized (lock) {
                if (! isDone()) {
                    lock.wait(unit.toMillis(timeout),
                              (int) (unit.toNanos(timeout) % 1000000));
                }

                result = isDone() ? true : null;
            }

            return result;
        }

        @Override
        public String toString() { return super.toString(); }
    }
}
