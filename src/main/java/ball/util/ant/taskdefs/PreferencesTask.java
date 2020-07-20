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
import java.util.prefs.Preferences;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.util.ClasspathUtils;

import static lombok.AccessLevel.PROTECTED;

/**
 * Abstract {@link.uri http://ant.apache.org/ Ant} {@link Task} base class
 * for {@link Preferences}-specific tasks.
 *
 * {@ant.task}
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@NoArgsConstructor(access = PROTECTED)
public abstract class PreferencesTask extends Task
                                      implements AnnotatedAntTask,
                                                 ClasspathDelegateAntTask,
                                                 ConfigurableAntTask {
    @Getter @Setter @Accessors(chain = true, fluent = true)
    private ClasspathUtils.Delegate delegate = null;
    @Getter
    private String type = null;

    public void setType(String string) {
        type = string;
        ClasspathDelegateAntTask.super.setClassname(type);
    }

    @Override
    public void init() throws BuildException {
        super.init();
        ClasspathDelegateAntTask.super.init();
        ConfigurableAntTask.super.init();
    }

    @Override
    public void execute() throws BuildException {
        super.execute();
        AnnotatedAntTask.super.execute();
    }

    /**
     * {@link Preferences} export {@link Task}.
     *
     * {@ant.task}
     */
    @NoArgsConstructor @ToString
    @AntTask("preferences-export")
    public static class Export extends PreferencesTask {
        @Override
        public void execute() throws BuildException {
            super.execute();

            try {
                Preferences system = Preferences.systemRoot();
                Preferences user = Preferences.userRoot();
                Class<?> type = null;

                if (getType() != null) {
                    type = getClassForName(getType());
                    system = Preferences.systemNodeForPackage(type);
                    user = Preferences.userNodeForPackage(type);
                }

                system.exportSubtree(System.out);
                System.out.println("");
                user.exportSubtree(System.out);
            } catch (BuildException exception) {
                throw exception;
            } catch (RuntimeException exception) {
                throw exception;
            } catch (Exception exception) {
                throw new BuildException(exception);
            }
        }
    }
}
