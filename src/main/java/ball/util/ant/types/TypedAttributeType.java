package ball.util.ant.types;
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
import ball.util.Factory;
import ball.util.ant.taskdefs.NotNull;
import java.beans.ConstructorProperties;
import org.apache.tools.ant.BuildException;

/**
 * Class to provide a typed name-value (attribute) for
 * {@link.uri http://ant.apache.org/ Ant} {@link org.apache.tools.ant.Task}
 * implementations.
 *
 * {@bean.info}
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public class TypedAttributeType extends StringAttributeType {
    private String type = String.class.getName();

    /**
     * @param   name            The attribute name.
     */
    @ConstructorProperties({ "name" })
    public TypedAttributeType(String name) { super(name); }

    /**
     * No-argument constructor.
     */
    public TypedAttributeType() { this(null); }

    @NotNull
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Object getTypedValue() throws BuildException {
        return getTypedValue(getClass().getClassLoader());
    }

    public Object getTypedValue(ClassLoader loader) throws BuildException {
        Object object = null;

        try {
            Class<?> type = Class.forName(getType(), false, loader);
            Factory<?> factory = new Factory<>(type);

            object = factory.getInstance(getValue());
        } catch (BuildException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new BuildException(exception);
        }

        return object;
    }
}
