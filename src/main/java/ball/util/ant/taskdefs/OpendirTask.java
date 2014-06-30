/*
 * $Id$
 *
 * Copyright 2009 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import ball.annotation.AntTask;
import ball.io.IOUtil;
import ball.text.ArrayListTableModel;
import ball.text.TextTable;
import ball.util.jni.DIR;
import ball.util.jni.POSIX;
import java.io.File;
import org.apache.tools.ant.BuildException;

import static ball.util.StringUtil.NIL;

/**
 * {@link.uri http://ant.apache.org/ Ant} {@link org.apache.tools.ant.Task}
 * to run {@link POSIX#opendir(File)}.
 *
 * {@bean-info}
 *
 * @see POSIX#opendir(File)
 * @see DIR
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@AntTask("opendir")
public class OpendirTask extends AbstractMatchingTask {

    /**
     * Sole constructor.
     */
    public OpendirTask() { super(); }

    @Override
    public void execute() throws BuildException {
        super.execute();

        for (File file : getMatchingFileSet()) {
            DIR dirp = null;

            try {
                dirp = POSIX.opendir(file);

                log(NIL);

                for (String line :
                         new TextTable(new TableModelImpl(dirp, file))) {
                    log(line);
                }
            } finally {
                try {
                    IOUtil.close(dirp);
                } finally {
                    dirp = null;
                }
            }
        }
    }

    private class TableModelImpl extends ArrayListTableModel<String> {
        private static final long serialVersionUID = -7344745156450390992L;

        public TableModelImpl(DIR dirp, File file) {
            super(dirp, String.valueOf(file));
        }

        @Override
        public String getValueAt(String row, int x) { return row; }
    }
}
