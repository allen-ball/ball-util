/*
 * $Id$
 *
 * Copyright 2009 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import ball.annotation.AntTask;
import ball.text.MapTable;
import ball.util.jni.POSIX;
import java.io.File;
import java.util.LinkedHashMap;
import org.apache.tools.ant.BuildException;

/**
 * {@link.uri http://ant.apache.org/ Ant} {@link org.apache.tools.ant.Task}
 * to read symbolic link targets.
 *
 * {@bean-info}
 *
 * @see POSIX#readlink(File)
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
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
