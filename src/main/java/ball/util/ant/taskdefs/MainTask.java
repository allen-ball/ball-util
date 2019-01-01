/*
 * $Id$
 *
 * Copyright 2015 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import ball.util.ant.types.OptionalTextType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ExitException;
import org.apache.tools.ant.util.optional.NoExitSecurityManager;

import static ball.util.StringUtil.NIL;
import static ball.util.StringUtil.isNil;
import static java.lang.reflect.Modifier.isStatic;

/**
 * {@link.uri http://ant.apache.org/ Ant} {@link org.apache.tools.ant.Task}
 * to invoke a static {@code main(String[])} function.
 *
 * {@bean.info}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@AntTask("main")
public class MainTask extends TypeTask {
    private final ArrayList<OptionalTextType> list = new ArrayList<>();

    /**
     * Sole constructor.
     */
    public MainTask() { super(); }

    public void addConfiguredArgument(OptionalTextType argument) {
        list.add(argument);
    }

    @Override
    public void execute() throws BuildException {
        super.execute();

        try {
            ArrayList<String> argv = new ArrayList<>(list.size());

            for (OptionalTextType argument : list) {
                if (argument.isActive(getProject())) {
                    argv.add(argument.toString());
                }
            }

            Class<?> type = Class.forName(getType(), false, getClassLoader());
            String[] array = argv.toArray(new String[] { });
            Method method = type.getDeclaredMethod("main", array.getClass());

            log(method.toString());

            if (! isStatic(method.getModifiers())) {
                throw new BuildException(method + " is not static");
            }

            log(String.valueOf(Arrays.asList(array)));

            try {
                SecurityManager manager = System.getSecurityManager();

                System.setSecurityManager(new NoExitSecurityManagerImpl());

                try {
                    Object object = method.invoke(null, (Object) array);

                    log("return " + String.valueOf(object)
                        + " without call to System.exit(int)");
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

    private class NoExitSecurityManagerImpl extends NoExitSecurityManager {
        public NoExitSecurityManagerImpl() { super(); }

        @Override
        public void checkPermission(Permission permission, Object context) {
        }

        @Override
        public String toString() { return super.toString(); }
    }
}
