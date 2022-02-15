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
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * Interface to provide common default methods for {@link Task}s that may
 * assign property values.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 */
public interface PropertySetterAntTask extends AntTaskMixIn {
    String getProperty();
    void setProperty(String property);

    /**
     * Method to get the value to assign to the property.
     *
     * @return  The property value.
     *
     * @throws  Exception       As specified by implementing subclass.
     */
    Object getPropertyValue() throws Exception;

    default void execute() throws BuildException {
        try {
            Task task = (Task) this;
            String key = getProperty();
            Object value = getPropertyValue();

            if (key != null) {
                if (value != null) {
                    task.getProject().setNewProperty(key, value.toString());
                }
            } else {
                task.log(String.valueOf(value));
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
