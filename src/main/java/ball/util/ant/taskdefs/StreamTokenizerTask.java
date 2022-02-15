package ball.util.ant.taskdefs;
/*-
 * ##########################################################################
 * Utilities
 * %%
 * Copyright (C) 2008 - 2022 Allen D. Ball
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
import ball.swing.table.SimpleTableModel;
import java.io.StreamTokenizer;
import java.io.StringReader;
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
 * for {@link StreamTokenizer}-specific tasks.
 *
 * {@ant.task}
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 */
@NoArgsConstructor(access = PROTECTED)
public abstract class StreamTokenizerTask extends Task implements AnnotatedAntTask,
                                                                  ClasspathDelegateAntTask, ConfigurableAntTask {
    @Getter @Setter @Accessors(chain = true, fluent = true)
    private ClasspathUtils.Delegate delegate = null;

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
     * {@link StreamTokenizer} from {@link String} {@link Task}.
     *
     * {@ant.task}
     */
    @NoArgsConstructor @ToString
    @AntTask("tokenize-string")
    public static class FromString extends StreamTokenizerTask {
        @NotNull @Getter @Setter
        private String string = null;

        @Override
        public void execute() throws BuildException {
            super.execute();

            try {
                String string = getString();
                String[] lines = string.split("\\R");
                SimpleTableModel input = new SimpleTableModel(new Object[][] { }, 2);

                for (int i = 0; i < lines.length; i += 1) {
                    input.row(String.valueOf(i + 1), lines[i]);
                }

                log(input);
                log("");

                try (StringReader reader = new StringReader(string)) {
                    StreamTokenizer tokenizer = new StreamTokenizer(reader);

                    for (;;) {
                        tokenizer.nextToken();

                        log(tokenizer.toString());

                        if (tokenizer.ttype == StreamTokenizer.TT_EOF) {
                            break;
                        }
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
