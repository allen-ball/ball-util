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
import ball.util.UUIDFactory;
import java.util.UUID;
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
 * {@link.uri http://ant.apache.org/ Ant} {@link Task} to generate new
 * unique {@link UUID}s.
 *
 * {@ant.task}
 *
 * @see Generate
 * @see GenerateRandom
 * @see GenerateTime
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@NoArgsConstructor(access = PROTECTED)
public abstract class UUIDTask extends Task
                               implements AnnotatedAntTask,
                                          ClasspathDelegateAntTask,
                                          ConfigurableAntTask,
                                          PropertySetterAntTask {
    @Getter @Setter @Accessors(chain = true, fluent = true)
    private ClasspathUtils.Delegate delegate = null;
    @Getter @Setter
    private String property = null;

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
        PropertySetterAntTask.super.execute();
    }

    @Override
    public abstract UUID getPropertyValue() throws Exception;

    /**
     * {@link.uri http://ant.apache.org/ Ant} {@link Task} to generate a
     * {@link UUID} with {@link UUID#fromString(String)}.
     *
     * {@ant.task}
     */
    @AntTask("uuid-from")
    @NoArgsConstructor @ToString
    public static class From extends UUIDTask {
        @NotNull @Getter @Setter
        private String string = null;

        @Override
        public UUID getPropertyValue() {
            return UUID.fromString(getString());
        }
    }

    /**
     * {@link.uri http://ant.apache.org/ Ant} {@link Task} to generate a new
     * {@link UUID} with {@link UUIDFactory#generate()}.
     *
     * {@ant.task}
     */
    @AntTask("uuid-generate")
    @NoArgsConstructor @ToString
    public static class Generate extends UUIDTask {
        @Override
        public UUID getPropertyValue() {
            return UUIDFactory.getDefault().generate();
        }
    }

    /**
     * {@link.uri http://ant.apache.org/ Ant} {@link Task} to generate a new
     * {@link UUID} with {@link UUIDFactory#generateRandom()}.
     *
     * {@ant.task}
     */
    @AntTask("uuid-generate-random")
    @NoArgsConstructor @ToString
    public static class GenerateRandom extends UUIDTask {
        @Override
        public UUID getPropertyValue() {
            return UUIDFactory.getDefault().generateRandom();
        }
    }

    /**
     * {@link.uri http://ant.apache.org/ Ant} {@link Task} to generate a new
     * {@link UUID} with {@link UUIDFactory#generateTime()}.
     *
     * {@ant.task}
     */
    @AntTask("uuid-generate-time")
    @NoArgsConstructor @ToString
    public static class GenerateTime extends UUIDTask {
        @Override
        public UUID getPropertyValue() {
            return UUIDFactory.getDefault().generateTime();
        }
    }

    /**
     * {@link.uri http://ant.apache.org/ Ant} {@link Task} to generate a
     * {@code null} {@link UUID} with {@link UUIDFactory#generateNull()}.
     *
     * {@ant.task}
     */
    @AntTask("uuid-null")
    @NoArgsConstructor @ToString
    public static class Null extends UUIDTask {
        @Override
        public UUID getPropertyValue() {
            return UUIDFactory.getDefault().generateNull();
        }
    }
}
