/*
 * $Id: SerializableCheck.java,v 1.2 2010-12-08 04:51:50 ball Exp $
 *
 * Copyright 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs.lint;

import iprotium.util.SuperclassSet;
import iprotium.util.ant.taskdefs.LintTask;
import java.io.File;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static iprotium.lang.java.Punctuation.EQUALS;
import static iprotium.lang.java.Punctuation.SEMICOLON;
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
 * @version $Revision: 1.2 $
 */
public class SerializableCheck extends LintTask.Check {
    private static final int MODIFIERS = PRIVATE | STATIC | FINAL;
    private static final Class<?> TYPE = Long.TYPE;
    private static final String SERIALVERSIONUID = "serialVersionUID";
    private static final String L = "L";

    private static final String SPACE = " ";

    /**
     * Sole constructor.
     *
     * @param   task            The {@link LintTask} instance.
     */
    public SerializableCheck(LintTask task) { super(task); }

    @Override
    public void check(File file, Class<?> type) {
        SuperclassSet superclasses = new SuperclassSet(type);

        if ((! isAbstract(type))
            && superclasses.contains(Serializable.class)
            && (! superclasses.contains(Enum.class))) {
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
                    + SPACE + SERIALVERSIONUID + SPACE + EQUALS.lexeme()
                    + SPACE + String.valueOf(serialVersionUID) + L
                    + SEMICOLON.lexeme());
            }
        }
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
