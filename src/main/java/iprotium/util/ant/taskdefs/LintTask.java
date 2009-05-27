/*
 * $Id: LintTask.java,v 1.1 2009-05-27 04:50:45 ball Exp $
 *
 * Copyright 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import java.io.File;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.tools.ant.BuildException;

/**
 * Ant Task to provide additional compile-time ("lint") checks.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
 */
public class LintTask extends AbstractClassFileTask {
    private List<Check> list =
        Arrays.<Check>asList(new SerialVersionUIDCheck());
    private transient Map<File,Class> map = null;

    /**
     * Sole constructor.
     */
    public LintTask() { super(); }

    @Override
    public void execute() throws BuildException {
        super.execute();

        map = getMatchingClassFileMap();

        for (Map.Entry<File,Class> entry : map.entrySet()) {
            for (Check check : list) {
                run(check, entry.getKey(), entry.getValue());
            }
        }
    }

    private void run(Check check, File file, Class<?> type) {
        check.check(file, type);

        for (Field member : type.getFields()) {
            check.check(file, type, member);
        }

        for (Constructor member : type.getConstructors()) {
            check.check(file, type, member);
        }

        for (Method member : type.getMethods()) {
            check.check(file, type, member);
        }

        for (Class member : type.getClasses()) {
            check.check(file, type, member);
        }
    }

    private abstract class Check {
        protected Check() { }

        public void check(File file, Class<?> type) { }

        public void check(File file, Class<?> type, Field member) { }

        public void check(File file, Class<?> type, Constructor member) { }

        public void check(File file, Class<?> type, Method member) { }

        public void check(File file, Class<?> type, Class<?> member) { }
    }

    private static final int MODIFIERS =
        Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL;
    private static final Class TYPE = Long.TYPE;
    private static final String SERIALVERSIONUID = "serialVersionUID";

    public class SerialVersionUIDCheck extends Check {
        public SerialVersionUIDCheck() { super(); }

        @Override
        public void check(File file, Class<?> type) {
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

        private String getSerialVersionUIDDeclaration(Class<?> type) {
            long serialVersionUID =
                ObjectStreamClass.lookup(type).getSerialVersionUID();

            return (Modifier.toString(MODIFIERS) + " " + TYPE.getName()
                    + " " + SERIALVERSIONUID + " = "
                    + String.valueOf(serialVersionUID) + "L;");
        }
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
