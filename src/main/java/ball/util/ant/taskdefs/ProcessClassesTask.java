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
import java.util.List;
import java.util.stream.Stream;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.ClasspathUtils;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static javax.tools.StandardLocation.CLASS_OUTPUT;
import static javax.tools.StandardLocation.CLASS_PATH;
import static javax.tools.StandardLocation.PLATFORM_CLASS_PATH;
import static javax.tools.StandardLocation.SOURCE_PATH;

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

    private StandardJavaFileManager fm = null;

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

        try {
            fm =
                ToolProvider.getSystemJavaCompiler()
                .getStandardFileManager(null, null, null);
        } catch (BuildException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw exception;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw new BuildException(throwable);
        }
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

            List<File> srcPaths =
                Stream.of(createSrc().list())
                .map(File::new)
                .collect(toList());
            List<File> classPaths =
                Stream.of(delegate.getClasspath().list())
                .map(File::new)
                .collect(toList());

            fm.setLocation(SOURCE_PATH, srcPaths);
            fm.setLocation(PLATFORM_CLASS_PATH, classPaths);
            fm.setLocation(CLASS_PATH, classPaths);
            fm.setLocation(CLASS_OUTPUT, asList(getDestdir()));

            ClassFileProcessor.process(fm);
        } catch (BuildException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw exception;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw new BuildException(throwable);
        }
    }
}
