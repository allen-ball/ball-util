/*
 * $Id$
 *
 * Copyright 2019 Allen D. Ball.  All rights reserved.
 */
package ball.tools.javadoc;

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
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public interface Doclet {

    /**
     * Check {@code options} have correct arguments.
     *
     * The {@link com.sun.tools.doclets.standard.Standard} supplies an
     * instance of {@link com.sun.tools.javadoc.Messager} {@link Doclet} for
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
     * instance of {@link com.sun.tools.javadoc.RootDocImpl} {@link Doclet}
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
