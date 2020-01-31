/*
 * $Id$
 *
 * Copyright 2018 - 2020 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import ball.text.TextTable;
import java.io.BufferedReader;
import java.io.File;
import java.util.stream.Stream;
import javax.swing.table.TableModel;
import org.apache.tools.ant.Project;

import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * Interface to provide common default logging methods for
 * {@link org.apache.tools.ant.Task}s.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public interface AntTaskLogMethods extends AntTaskMixIn {
    void log(String message);
    void log(String message, int messageLevel);
    void log(String message, Throwable throwable, int messageLevel);
    void log(Throwable throwable, int messageLevel);

    /**
     * See {@link #log(Stream)}.
     *
     * @param   model           The {@link TableModel} to log.
     */
    default void log(TableModel model) { log(model, Project.MSG_INFO); }

    /**
     * See {@link #log(Stream,int)}.
     *
     * @param   model           The {@link TableModel} to log.
     * @param   msgLevel        The log message level.
     */
    default void log(TableModel model, int msgLevel) {
        try (BufferedReader reader =
                 new TextTable(model).getBufferedReader()) {
            log(reader.lines(), Project.MSG_INFO);
        } catch (Throwable throwable) {
            throw new IllegalStateException(throwable);
        }
    }

    /**
     * See {@link org.apache.tools.ant.Task#log(String)}.
     *
     * @param   stream          The {@link Stream} of {@link String}s to
     *                          log.
     */
    default void log(Stream<String> stream) {
        log(stream, Project.MSG_INFO);
    }

    /**
     * See {@link org.apache.tools.ant.Task#log(String,int)}.
     *
     * @param   stream          The {@link Stream} of {@link String}s to
     *                          log.
     * @param   msgLevel        The log message level.
     */
    default void log(Stream<String> stream, int msgLevel) {
        stream.forEach(t -> log(t, msgLevel));
    }

    /**
     * See {@link org.apache.tools.ant.Task#log(String)}.
     *
     * @param   file            The {@link File}.
     * @param   lineno          The line number in the {@link File}.
     * @param   message         The message ({@link String}).
     */
    default void log(File file, int lineno, String message) {
        log(String.valueOf(file) + ":" + String.valueOf(lineno)
            + ": " + message);
    }

    /**
     * Log an empty {@link String}.
     */
    default void log() { log(Project.MSG_INFO); }

    /**
     * Log an empty {@link String}.
     *
     * @param   msgLevel        The log message level.
     */
    default void log(int msgLevel) { log(EMPTY, msgLevel); }
}
