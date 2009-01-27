/*
 * $Id: SerialVersionUIDTask.java,v 1.4 2009-01-27 22:00:19 ball Exp $
 *
 * Copyright 2008, 2009 Allen D. Ball.  All rights reserved.
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
 * @version $Revision: 1.4 $
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
        super.execute();

        for (Class<?> type : getMatchingClassFileMap().values()) {
            if (Serializable.class.isAssignableFrom(type)
                && (! isAbstract(type))
                && (! Enum.class.isAssignableFrom(type))) {
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
 * Revision 1.3  2008/11/01 19:58:55  ball
 * Use superclass isAbstract() and isStatic() methods.
 *
 */
