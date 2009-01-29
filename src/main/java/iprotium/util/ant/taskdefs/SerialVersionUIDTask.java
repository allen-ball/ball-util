/*
 * $Id: SerialVersionUIDTask.java,v 1.5 2009-01-29 05:38:40 ball Exp $
 *
 * Copyright 2008, 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import java.io.File;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import org.apache.tools.ant.BuildException;

/**
 * Ant Task to generate serialVersionUID members for classes that implement
 * Serializable but do not explicitly define serialVersionUID.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.5 $
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

        Map<File,Class> map = getMatchingClassFileMap();

        for (Map.Entry<File,Class> entry : map.entrySet()) {
            File file = entry.getKey();
            Class<?> type = entry.getValue();

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
                    log("");
                    log(getJavaFile(map, file), 1, type.getName());
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
 */
