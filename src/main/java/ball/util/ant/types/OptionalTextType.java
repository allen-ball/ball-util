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
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.tools.ant.Project;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Class to provide a {@link String} base text type for
 * {@link.uri http://ant.apache.org/ Ant} {@link org.apache.tools.ant.Task}
 * implementations.  Support "if" and "unless" property predicates.
 *
 * {@bean.info}
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@NoArgsConstructor @ToString
public class OptionalTextType {
    private String text = null;
    private String ifP = null;
    private String unlessP = null;

    public void addText(String text) {
        this.text = (isNotEmpty(this.text) ? this.text : EMPTY) + text;
    }

    /**
     * Sets the "if" condition to test on execution.  If the named property
     * is set, the {@link OptionalTextType} should be included.
     *
     * @param   ifP             The property condition to test on execution.
     *                          If the value is {@code null} no "if" test
     *                          will not be performed.
     */
    public void setIf(String ifP) { this.ifP = ifP; }
    public String getIf() { return ifP; }

    /**
     * Sets the "unless" condition to test on execution.  If the named
     * property is set, the {@link OptionalTextType} should not be included.
     *
     * @param   unlessP         The property condition to test on execution.
     *                          If the value is {@code null} no "unlessP"
     *                          test will not be performed.
     */
    public void setUnless(String unlessP) { this.unlessP = unlessP; }
    public String getUnless() { return unlessP; }

    /**
     * Method to determine if the "if" and "unless" tests have been
     * satisfied.
     *
     * @param       project The {@link Project}.
     *
     * @return      {@code true} if the "if" and "unless" tests have
     *              been satisfied; {@code false} otherwise.
     */
    public boolean isActive(Project project) {
        return ((getIf() == null
                 || project.getProperty(getIf()) != null)
                && (getUnless() == null
                    || project.getProperty(getUnless()) == null));
    }
}
