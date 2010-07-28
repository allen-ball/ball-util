/*
 * $Id: LintTask.java,v 1.3 2010-07-28 05:46:06 ball Exp $
 *
 * Copyright 2009, 2010 Allen D. Ball.  All rights reserved.
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

import static iprotium.util.ClassUtil.isAbstract;
import static iprotium.util.ClassUtil.isStatic;

/**
 * Ant {@link org.apache.tools.ant.Task} to provide additional compile-time
 * ("lint") checks.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.3 $
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

    public abstract class Check {
        protected Check() { }

        public void check(File file, Class<?> type) { }

        public void check(File file, Class<?> type, Field member) { }

        public void check(File file, Class<?> type, Constructor member) { }

        public void check(File file, Class<?> type, Method member) { }

        public void check(File file, Class<?> type, Class<?> member) { }
    }

    public class SerialVersionUIDCheck extends Check {
        private static final int MODIFIERS =
            Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL;
        private static final String SERIALVERSIONUID = "serialVersionUID";

        public SerialVersionUIDCheck() { super(); }

        @Override
        public void check(File file, Class<?> type) {
            if (Serializable.class.isAssignableFrom(type)
                && (! isAbstract(type))
                && (! Enum.class.isAssignableFrom(type))) {
                try {
                    Field field = type.getDeclaredField(SERIALVERSIONUID);

                    if (! (isStatic(field)
                           && Long.TYPE.equals(field.getType()))) {
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

            return (Modifier.toString(MODIFIERS)
                    + " " + Long.TYPE.getName() + " " + SERIALVERSIONUID
                    + " = " + String.valueOf(serialVersionUID) + "L;");
        }
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
