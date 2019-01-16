/*
 * $Id$
 *
 * Copyright 2011 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import ball.util.ClassOrder;
import java.io.File;
import java.util.Set;
import java.util.TreeSet;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.optional.depend.ClassFile;
import org.apache.tools.ant.taskdefs.optional.depend.ClassFileUtils;
import org.apache.tools.ant.taskdefs.optional.depend.DirectoryIterator;
import org.apache.tools.ant.types.Path;

import static java.lang.reflect.Modifier.isAbstract;

/**
 * {@link.uri http://ant.apache.org/ Ant} {@link org.apache.tools.ant.Task}
 * to bootstrap {@link javax.annotation.processing.Processor}s.  Creates and
 * invokes {@link Processor}s found on the class path.
 *
 * {@bean.info}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@NoArgsConstructor @ToString
public class BootstrapProcessorTask extends AbstractClasspathTask {
    private static final String _JAVA = ".java";

    @Getter @Setter
    private File basedir = null;
    @Getter
    private Path srcPath = null;
    @Getter @Setter
    private File destdir = null;

    public void setSrcdir(Path srcdir) {
        if (srcPath == null) {
            srcPath = srcdir;
        } else {
            srcPath.append(srcdir);
        }
    }

    public Path createSrc() {
        if (srcPath == null) {
            srcPath = new Path(getProject());
        }

        return srcPath.createPath();
    }

    @Override
    public void execute() throws BuildException {
        super.execute();

        if (getBasedir() == null) {
            setBasedir(getProject().resolveFile("."));
        }

        if (getDestdir() == null) {
            setDestdir(getBasedir());
        }

        try {
            for (Class<?> type : getClassSet()) {
                if (Processor.class.isAssignableFrom(type)) {
                    if (! isAbstract(type.getModifiers())) {
                        type.asSubclass(Processor.class).newInstance()
                            .process(getClassSet(), getDestdir());
                    }
                }
            }
        } catch (BuildException exception) {
            throw exception;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw new BuildException(throwable);
        }
    }

    protected Set<Class<?>> getClassSet() throws BuildException {
        TreeSet<Class<?>> set = new TreeSet<>(ClassOrder.NAME);

        try {
            DirectoryIterator iterator =
                new DirectoryIterator(getBasedir(), true);
            ClassFile file = null;

            while ((file = iterator.getNextClassFile()) != null) {
                set.add(getClassForName(file.getFullClassName()));
            }
        } catch (BuildException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new BuildException(exception);
        }

        return set;
    }

    protected File getJavaFile(Class<?> type) {
        File file = null;

        if (srcPath != null && type != null) {
            while (type.getDeclaringClass() != null) {
                type = type.getDeclaringClass();
            }

            while (type.getEnclosingClass() != null) {
                type = type.getEnclosingClass();
            }

            String child =
                ClassFileUtils.convertDotName(type.getCanonicalName()) + _JAVA;

            for (String parent : srcPath.list()) {
                file = new File(parent, child);

                if (file.isFile()) {
                    break;
                } else {
                    file = null;
                }
            }
        }

        return file;
    }

    /**
     * Bootstrap processor interface.
     */
    public interface Processor {

        /**
         * Bootstrap method called by this {@link org.apache.tools.ant.Task}.
         *
         * @param       set     The {@link Set} of {@link Class}es to
         *                      examine.
         * @param       destdir The root of the hierarchy to record any
         *                      output artifacts.
         *
         * @throws      Exception
         *                      If the implementation throws an
         *                      {@link Exception}.
         */
        public void process(Set<Class<?>> set, File destdir) throws Exception;
    }
}
