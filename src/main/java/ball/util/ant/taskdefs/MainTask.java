/*
 * $Id$
 *
 * Copyright 2015 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ExitException;
import org.apache.tools.ant.util.optional.NoExitSecurityManager;

import static ball.util.ClassUtil.isStatic;

/**
 * {@link.uri http://ant.apache.org/ Ant} {@link org.apache.tools.ant.Task}
 * to invoke a static {@code main(String[])} function.
 *
 * {@bean-info}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@AntTask("main")
public class MainTask extends TypeTask {
    private final ArrayList<Argument> list = new ArrayList<Argument>();

    /**
     * Sole constructor.
     */
    public MainTask() { super(); }

    public void addConfiguredArgument(Argument argument) {
        list.add(argument);
    }

    @Override
    public void execute() throws BuildException {
        super.execute();

        try {
            String[] argv = new String[list.size()];

            for (int i = 0; i < argv.length; i += 1) {
                argv[i] = list.get(i).getValue();
            }

            Class<?> type = Class.forName(getType(), false, getClassLoader());
            Method method = type.getDeclaredMethod("main", argv.getClass());

            log(method.toString());

            if (! isStatic(method)) {
                throw new BuildException(method + " is not static");
            }

            log(String.valueOf(Arrays.asList(argv)));

            try {
                SecurityManager manager = System.getSecurityManager();

                System.setSecurityManager(new NoExitSecurityManager());

                try {
                    method.invoke(null, (Object) argv);
                    log("return without call to System.exit(int)");
                } catch (InvocationTargetException exception) {
                    Throwable cause = exception.getCause();

                    if (cause != null) {
                        throw cause;
                    } else {
                        throw exception;
                    }
                } finally {
                    System.setSecurityManager(manager);
                }
            } catch (ExitException exception) {
                log("System.exit(" + exception.getStatus() + ")");
            }
        } catch (BuildException exception) {
            throw exception;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw new BuildException(throwable);
        }
    }

    /**
     * {@link MainTask} argument.
     */
    public static class Argument {
        private String value = null;

        /**
         * No-argument constructor.
         */
        public Argument() { }

        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }

        @Override
        public String toString() { return getValue(); }
    }
}
