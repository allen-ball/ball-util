package ball.tools.javadoc;
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
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.RootDoc;
import com.sun.tools.doclets.Taglet;
import com.sun.tools.doclets.standard.Standard;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ServiceLoader;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * WORK IN PROGRESS: {@link Doclet} implementation to intercept calls to the
 * {@link Standard} {@link Doclet}.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@NoArgsConstructor @ToString
public class MavenBootstrapDoclet extends Standard {

    /**
     * See {@link Doclet#validOptions(String[][],DocErrorReporter)}.
     */
    public static final ThreadLocal<String[][]> OPTIONS = new ThreadLocal<>();

    /**
     * See {@link Doclet#validOptions(String[][],DocErrorReporter)}.
     */
    public static final ThreadLocal<DocErrorReporter> REPORTER =
        new ThreadLocal<>();

    /**
     * See {@link Doclet#start(RootDoc)}.
     */
    public static final ThreadLocal<RootDoc> ROOT = new ThreadLocal<>();

    /**
     * See {@link Doclet#validOptions(String[][],DocErrorReporter)}.
     *
     * @param   options         The {@code options} and their arguments.
     * @param   reporter        The {@link DocErrorReporter}.
     *
     * @return  {@code true} if the {@code options} are valid; {@code false}
     *          otherwise.
     */
    public static boolean validOptions(String[][] options,
                                       DocErrorReporter reporter) {
        Boolean result = null;

        ArrayList<String[]> list = new ArrayList<>(Arrays.asList(options));
/*
        for (Taglet taglet :
                 ServiceLoader.load(Taglet.class,
                                    MavenBootstrapDoclet.class
                                    .getClassLoader())) {
            list.add(new String[] { "-taglet", taglet.getClass().getName() });
        }
*/
        list.add(new String[] {
                     "-taglet", MavenBootstrapTaglet.class.getName()
                 });

        options = list.toArray(new String[][] { });

        OPTIONS.set(options);
        REPORTER.set(reporter);

        result = Standard.validOptions(options, reporter);

        return (result != null) ? result : false;
    }

    /**
     * See {@link Doclet#start(RootDoc)}.
     *
     * @param   root            The {@link RootDoc}.
     *
     * @return  {@code true} on success; {@code false} otherwise.
     */
    public static boolean start(RootDoc root) {
        Boolean result = null;

        ROOT.set(root);

        result = Standard.start(root);

        return (result != null) ? result : false;
    }
}
