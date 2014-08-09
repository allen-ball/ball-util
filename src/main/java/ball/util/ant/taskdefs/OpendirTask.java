/*
 * $Id$
 *
 * Copyright 2009 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import ball.io.IOUtil;
import ball.swing.table.ArrayListTableModel;
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
                log(new TableModelImpl(dirp, file));
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
        private static final long serialVersionUID = -7558131158505546679L;

        public TableModelImpl(DIR dirp, File file) {
            super(dirp, String.valueOf(file));
        }

        @Override
        public String getValueAt(String row, int x) { return row; }
    }
}
