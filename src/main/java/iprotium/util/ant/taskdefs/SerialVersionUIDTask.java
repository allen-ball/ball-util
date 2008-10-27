/*
 * $Id: SerialVersionUIDTask.java,v 1.1 2008-10-27 00:10:03 ball Exp $
 *
 * Copyright 2008 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.apache.tools.ant.BuildException;

/**
 * Ant Task to generate serialVersionUID members for classes that implement
 * Serializable but do not explicitly define serialVersionUID.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
 */
public class SerialVersionUIDTask extends AbstractClassFileTask {
    private static final int MODIFIERS =
        Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL;
    private static final Class TYPE = Long.TYPE;
    private static final String SERIALVERSIONUID = "serialVersionUID";

    /**
     * Sole constructor.
     */
    public SerialVersionUIDTask() { super(); }

    @Override
    public void execute() throws BuildException {
        if (getBasedir() == null) {
            setBasedir(getProject().resolveFile("."));
        }

        for (Class<?> type : getMatchingClassFileMap().values()) {
            if (Serializable.class.isAssignableFrom(type)
                && (! Modifier.isAbstract(type.getModifiers()))) {
                if (! Enum.class.isAssignableFrom(type)) {
                    try {
                        Field field = type.getDeclaredField(SERIALVERSIONUID);

                        if (! (isStatic(field)
                               && TYPE.equals(field.getType()))) {
                            throw new NoSuchFieldException(SERIALVERSIONUID);
                        }
                    } catch (NoSuchFieldException exception) {
                        log(type.getName() + ":");
                        log(getSerialVersionUIDDeclaration(type));
                    }
                }
            }
        }
    }

    private String getSerialVersionUIDDeclaration(Class<?> type) {
        long serialVersionUID =
            ObjectStreamClass.lookup(type).getSerialVersionUID();

        return (Modifier.toString(MODIFIERS) + " " + TYPE.getName()
                + " " + SERIALVERSIONUID + " = "
                + String.valueOf(serialVersionUID) + "L;");
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
