/*
 * $Id: OpendirTask.java,v 1.2 2009-10-25 21:19:52 ball Exp $
 *
 * Copyright 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import iprotium.io.IOUtil;
import iprotium.util.jni.DIR;
import iprotium.util.jni.POSIX;
import java.io.File;
import org.apache.tools.ant.BuildException;

/**
 * Ant {@link org.apache.tools.ant.Task} to run
 * {@link POSIX#opendir(File)}.
 *
 * @see POSIX#opendir(File)
 * @see DIR
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.2 $
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
            log("");
            log(String.valueOf(file) + ":");

            DIR dirp = null;

            try {
                dirp = POSIX.opendir(file);

                for (String name : dirp.list()) {
                    log(name);
                }
            } finally {
                IOUtil.close(dirp);
            }
        }
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
