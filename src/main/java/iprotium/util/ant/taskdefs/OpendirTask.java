/*
 * $Id$
 *
 * Copyright 2009 - 2011 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import iprotium.io.IOUtil;
import iprotium.text.ArrayListTableModel;
import iprotium.text.TextTable;
import iprotium.util.jni.DIR;
import iprotium.util.jni.POSIX;
import java.io.File;
import org.apache.tools.ant.BuildException;

/**
 * <a href="http://ant.apache.org/">Ant</a>
 * {@link org.apache.tools.ant.Task} to run {@link POSIX#opendir(File)}.
 *
 * @see POSIX#opendir(File)
 * @see DIR
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
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

                log("");

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
