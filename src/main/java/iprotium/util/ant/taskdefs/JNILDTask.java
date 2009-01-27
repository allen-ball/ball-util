/*
 * $Id: JNILDTask.java,v 1.2 2009-01-27 22:00:19 ball Exp $
 *
 * Copyright 2008, 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import java.util.ResourceBundle;
import org.apache.tools.ant.BuildException;

/**
 * Ant Task to link JNI shared libraries.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.2 $
 */
public class JNILDTask extends AbstractJNIExecuteOnTask {

    /**
     * Sole constructor.
     */
    public JNILDTask() {
        super(ResourceBundle.getBundle(JNILDTask.class.getName()));
    }
}
/*
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2008/11/18 07:36:44  ball
 * Interim check-in.
 *
 */
