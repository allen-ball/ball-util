/*
 * $Id$
 *
 * Copyright 2019 Allen D. Ball.  All rights reserved.
 */
package ball.tools.javadoc;

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
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
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
