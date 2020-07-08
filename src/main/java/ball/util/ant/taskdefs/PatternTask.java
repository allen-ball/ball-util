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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.util.ClasspathUtils;

import static lombok.AccessLevel.PROTECTED;

/**
 * {@link.uri http://ant.apache.org/ Ant} {@link Task} to test regular
 * ({@link Pattern}) expressions.
 *
 * {@ant.task}
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@NoArgsConstructor(access = PROTECTED)
public abstract class PatternTask extends Task
                                  implements AnnotatedAntTask,
                                             ClasspathDelegateAntTask,
                                             ConfigurableAntTask {
    @Getter @Setter @Accessors(chain = true, fluent = true)
    private ClasspathUtils.Delegate delegate = null;
    @NotNull @Getter @Setter
    private String pattern = null;
    @NotNull @Getter @Setter
    private String input = null;

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
     * {@link Pattern} {@link Matcher#matches()} {@link Task}.
     *
     * {@ant.task}
     */
    @NoArgsConstructor @ToString
    @AntTask("pattern-matches")
    public static class Matches extends PatternTask {
        @Override
        public void execute() throws BuildException {
            super.execute();

            try {
                Pattern pattern = Pattern.compile(getPattern());

                log(String.valueOf(pattern));

                String input = getInput();

                log(input);

                Matcher matcher = pattern.matcher(getInput());
                boolean matches = matcher.matches();

                log(String.valueOf(matches));

                if (matches) {
                    for (int i = 0, n = matcher.groupCount(); i <= n; i += 1) {
                        log(i + ": " + matcher.group(i));
                    }
                }
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
