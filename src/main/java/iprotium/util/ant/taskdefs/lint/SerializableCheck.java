/*
 * $Id: SerializableCheck.java,v 1.1 2010-08-23 03:24:39 ball Exp $
 *
 * Copyright 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs.lint;

import iprotium.util.ant.taskdefs.LintTask;
import java.io.File;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static iprotium.util.ClassUtil.isAbstract;
import static iprotium.util.ClassUtil.isStatic;
import static java.lang.reflect.Modifier.FINAL;
import static java.lang.reflect.Modifier.PRIVATE;
import static java.lang.reflect.Modifier.STATIC;

/**
 * {@link iprotium.util.ant.taskdefs.LintTask.Check} implementation to
 * verify {@link Serializable} implementations define
 * {@code serialVersionUID}.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
 */
public class SerializableCheck extends LintTask.Check {
    private static final int MODIFIERS = PRIVATE | STATIC | FINAL;
    private static final Class<?> TYPE = Long.TYPE;
    private static final String SERIALVERSIONUID = "serialVersionUID";
    private static final String L = "L";

    /**
     * Sole constructor.
     *
     * @param   task            The {@link LintTask} instance.
     */
    public SerializableCheck(LintTask task) { super(task); }

    @Override
    public void check(File file, Class<?> type) {
        if (Serializable.class.isAssignableFrom(type) && (! isAbstract(type))
            && (! Enum.class.isAssignableFrom(type))) {
            try {
                Field field = type.getDeclaredField(SERIALVERSIONUID);

                if (! (isStatic(field) && TYPE.equals(field.getType()))) {
                    throw new NoSuchFieldException(SERIALVERSIONUID);
                }
            } catch (NoSuchFieldException exception) {
                long serialVersionUID =
                    ObjectStreamClass.lookup(type).getSerialVersionUID();

                log("");
                log(getJavaFile(file), 1, type.getName());
                log(Modifier.toString(MODIFIERS) + SPACE + TYPE.getName()
                    + SPACE + SERIALVERSIONUID + SPACE + EQUALS + SPACE
                    + String.valueOf(serialVersionUID) + L + SEMICOLON);
            }
        }
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
