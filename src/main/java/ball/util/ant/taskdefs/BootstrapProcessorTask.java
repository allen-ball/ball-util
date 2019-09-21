/*
 * $Id$
 *
 * Copyright 2011 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.optional.depend.ClassFile;
import org.apache.tools.ant.taskdefs.optional.depend.ClassFileUtils;
import org.apache.tools.ant.taskdefs.optional.depend.DirectoryIterator;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.ClasspathUtils;

import static java.lang.reflect.Modifier.isAbstract;
import static java.util.Comparator.comparing;

/**
 * {@link.uri http://ant.apache.org/ Ant} {@link Task} to bootstrap
 * {@link javax.annotation.processing.Processor}s.  Creates and invokes
 * {@link Processor}s found on the class path.
 *
 * {@ant.task}
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@NoArgsConstructor @ToString
public class BootstrapProcessorTask extends Task
                                    implements AnnotatedAntTask,
                                               ClasspathDelegateAntTask {
    @Getter @Setter @Accessors(chain = true, fluent = true)
    private ClasspathUtils.Delegate delegate = null;
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
    public void init() throws BuildException {
        super.init();
        ClasspathDelegateAntTask.super.init();
    }

    @Override
    public void execute() throws BuildException {
        super.execute();
        AnnotatedAntTask.super.execute();

        try {
            if (getBasedir() == null) {
                setBasedir(getProject().resolveFile("."));
            }

            if (getDestdir() == null) {
                setDestdir(getBasedir());
            }

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
        } catch (RuntimeException exception) {
            throw exception;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw new BuildException(throwable);
        }
    }

    protected Set<Class<?>> getClassSet() throws BuildException {
        TreeSet<Class<?>> set = new TreeSet<>(comparing(t -> t.getName()));

        try {
            for (ClassFile file : new DirectoryIterator(getBasedir(), true)) {
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

            String path =
                ClassFileUtils.convertDotName(type.getCanonicalName());

            file =
                Stream.of(srcPath.list())
                .map(t -> new File(t, path + ".java"))
                .filter(File::isFile)
                .findFirst().orElse(null);
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
