package ball.util.ant.taskdefs;
/*-
 * ##########################################################################
 * Utilities
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2008 - 2020 Allen D. Ball
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ##########################################################################
 */
import ball.annotation.processing.ClassFileProcessor;
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
 * {@link ClassFileProcessor}s found on the class path.
 *
 * {@ant.task}
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@NoArgsConstructor @ToString
public class ProcessClassesTask extends Task
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
                if (ClassFileProcessor.class.isAssignableFrom(type)) {
                    if (! isAbstract(type.getModifiers())) {
                        type.asSubclass(ClassFileProcessor.class).newInstance()
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
        TreeSet<Class<?>> set = new TreeSet<>(comparing(Class::getName));

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
}
