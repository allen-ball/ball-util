/*
 * $Id$
 *
 * Copyright 2009 - 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import iprotium.annotation.AntTask;
import iprotium.text.MapTable;
import iprotium.util.jni.POSIX;
import java.io.File;
import java.util.LinkedHashMap;
import org.apache.tools.ant.BuildException;

/**
 * <a href="http://ant.apache.org/">Ant</a>
 * {@link org.apache.tools.ant.Task} to read symbolic link targets.
 *
 * @see POSIX#readlink(File)
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
@AntTask("readlink")
public class ReadlinkTask extends AbstractMatchingTask {

    /**
     * Sole constructor.
     */
    public ReadlinkTask() { super(); }

    @Override
    public void execute() throws BuildException {
        super.execute();

        LinkedHashMap<File,File> map = new LinkedHashMap<File,File>();

        for (File source : getMatchingFileSet()) {
            File target = POSIX.readlink(source);

            if (target != null) {
                map.put(source, target);
            }
        }

        if (! map.isEmpty()) {
            for (String line :
                     new MapTable<File,File>(map, "Link", "Target")) {
                log(line);
            }
        }
    }
}
