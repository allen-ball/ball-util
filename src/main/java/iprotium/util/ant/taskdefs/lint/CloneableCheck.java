/*
 * $Id: CloneableCheck.java,v 1.1 2010-08-23 03:24:39 ball Exp $
 *
 * Copyright 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs.lint;

import iprotium.util.ant.taskdefs.LintTask;
import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import static iprotium.lang.java.Keyword.THROWS;
import static iprotium.util.ClassUtil.isAbstract;
import static java.lang.reflect.Modifier.PUBLIC;

/**
 * {@link iprotium.util.ant.taskdefs.LintTask.Check} implementation to
 * verify {@link Cloneable} implementations override {@link Object#clone()}
 * properly.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
 */
public class CloneableCheck extends LintTask.Check {
    private static final int MODIFIERS = PUBLIC;
    private static final Method CLONE;

    static {
        try {
            CLONE = Object.class.getDeclaredMethod("clone");
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    /**
     * Sole constructor.
     *
     * @param   task            The {@link LintTask} instance.
     */
    public CloneableCheck(LintTask task) { super(task); }

    @Override
    public void check(File file, Class<?> type) {
        if (Arrays.asList(type.getInterfaces()).contains(Cloneable.class)
            && (! isAbstract(type))) {
            try {
                Method method =
                    type.getDeclaredMethod(CLONE.getName(),
                                           CLONE.getParameterTypes());
            } catch (NoSuchMethodException exception) {
                log("");
                log(getJavaFile(file), 1, type.getName());
                log(Modifier.toString(MODIFIERS)
                    + SPACE + type.getSimpleName()
                    + SPACE + CLONE.getName() + LP + RP
                    + SPACE + THROWS
                    + SPACE + CloneNotSupportedException.class.getSimpleName()
                    + SPACE + LB);
                log(RB);
            }
        }
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
