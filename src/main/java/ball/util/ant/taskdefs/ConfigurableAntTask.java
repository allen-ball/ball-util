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
import ball.util.PropertiesImpl;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * Provides default {@link Task#init()} method to initialize {@link Task}
 * attributes from project properties.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 */
public interface ConfigurableAntTask extends AntTaskMixIn {

    /**
     * Default implementation for {@link Task} subclasses.
     */
    default void init() throws BuildException { configure(); }

    /**
     * Default method to configure {@link Task} attributes.
     */
    default void configure() throws BuildException {
        try {
            PropertiesImpl properties = new PropertiesImpl();

            properties.putAll(((Task) this).getProject().getProperties());
            properties.keySet().removeAll(((Task) this).getRuntimeConfigurableWrapper().getAttributeMap().keySet());
            properties.configure(this);
        } catch (BuildException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new BuildException(exception);
        }
    }
}
