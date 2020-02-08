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
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.LanguageVersion;

/**
 * {@link com.sun.javadoc.Doclet} "interface."  {@link Doclet}s are not
 * actually part of a class hierarchy.  The methods described here are
 * {@code static} in the implementations.  This interface is provided to
 * document the methods and to use as a possible basis for creating
 * {@link java.lang.reflect.Proxy}-based solutions.
 *
 * The standard {@code OpenJDK} {@link javax.tools.DocumentationTool}
 * dispatches to {@link com.sun.tools.doclets.standard.Standard} which
 * in-turn dispatches to
 * {@link com.sun.tools.doclets.formats.html.HtmlDoclet}.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public interface Doclet {

    /**
     * Check {@code options} have correct arguments.
     *
     * The {@link com.sun.tools.doclets.standard.Standard} supplies an
     * instance of {@code com.sun.tools.javadoc.Messager} {@link Doclet} for
     * {@link DocErrorReporter}.
     *
     * @param   options         The {@code options} and their arguments.
     * @param   reporter        The {@link DocErrorReporter}.
     *
     * @return  {@code true} if the {@code options} are valid; {@code false}
     *          otherwise.
     */
    public boolean validOptions(String[][] options, DocErrorReporter reporter);

    /**
     * Check for {@link Doclet} added options.  E.g., {@code "-d docs"}
     * should return {@code 2}.
     *
     * @param   option          The {@link String option} to check.
     *
     * @return  Number of arguments required to specify; {@code 0} means the
     *          option is unknown and a negative value indicates an error.
     */
    public int optionLength(String option);

    /**
     * Starting point for document generation.
     *
     * The {@link com.sun.tools.doclets.standard.Standard} supplies an
     * instance of {@code com.sun.tools.javadoc.RootDocImpl} {@link Doclet}
     * for {@link RootDoc}.
     *
     * @param   root            The {@link RootDoc}.
     *
     * @return  {@code true} on success; {@code false} otherwise.
     */
    public boolean start(RootDoc root);

    /**
     * Return the version of the Java Programming Language supported
     * by this {@link Doclet}.
     *
     * @return  The minimum {@link LanguageVersion} supported by this
     *          {@link Doclet}.
     */
    public LanguageVersion languageVersion();
}
