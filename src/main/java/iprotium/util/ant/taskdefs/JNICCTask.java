/*
 * $Id: JNICCTask.java,v 1.2 2009-01-27 21:58:48 ball Exp $
 *
 * Copyright 2008, 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.DirSet;

/**
 * Ant Task to compile JNI shared objects.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.2 $
 */
public class JNICCTask extends AbstractJNIExecuteOnTask {
    private List<DirSet> list = new ArrayList<DirSet>();

    /**
     * Sole constructor.
     */
    public JNICCTask() {
        super(ResourceBundle.getBundle(JNICCTask.class.getName()));
    }

    @Override
    public void addDirset(DirSet dirSet) { list.add(dirSet); }

    @Override
    public void init() throws BuildException {
        super.init();
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
