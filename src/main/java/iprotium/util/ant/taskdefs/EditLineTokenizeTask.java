/*
 * $Id: EditLineTokenizeTask.java,v 1.2 2010-12-20 23:40:06 ball Exp $
 *
 * Copyright 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import iprotium.util.jni.EditLine;
import java.util.Arrays;
import org.apache.tools.ant.BuildException;

/**
 * <a href="http://ant.apache.org/">Ant</a>
 * {@link org.apache.tools.ant.Task} to call
 * {@link EditLine#tokenize(String)}.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.2 $
 */
public class EditLineTokenizeTask extends AbstractClasspathTask {
    private String string = null;

    /**
     * Sole constructor.
     */
    public EditLineTokenizeTask() { super(); }

    protected String getString() { return string; }
    public void setString(String string) { this.string = string; }

    @Override
    public void execute() throws BuildException {
        if (getString() == null) {
            throw new BuildException("`string' attribute must be specified");
        }

        try {
            String string = getString();

            log(String.valueOf(string));
            log(Arrays.toString(EditLine.tokenize(string)));
        } catch (BuildException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            exception.printStackTrace();
            throw exception;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BuildException(exception);
        }
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
