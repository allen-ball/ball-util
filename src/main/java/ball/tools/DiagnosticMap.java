/*
 * $Id$
 *
 * Copyright 2011 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.tools;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;

/**
 * {@link DiagnosticListener} and {@link LinkedHashMap} implementation for
 * collecting {@link Diagnostic}s as keys and informational/prescriptive
 * messages as values.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class DiagnosticMap
             extends LinkedHashMap<Diagnostic<? extends JavaFileObject>,String>
             implements DiagnosticListener<JavaFileObject> {
    private static final long serialVersionUID = -5544734926408904555L;

    /**
     * Sole constructor.
     */
    public DiagnosticMap() { super(); }

    /**
     * Method to get a map of {@link javax.tools.Diagnostic.Kind} counts.
     *
     * @return  A {@link SortedMap} of {@link javax.tools.Diagnostic.Kind}
     *          counts.
     */
    public SortedMap<Diagnostic.Kind,Integer> getKindCountMap() {
        return Collections.unmodifiableSortedMap(new KindCountMap());
    }

    @Override
    public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
        put(diagnostic, null);
    }

    private class KindCountMap extends TreeMap<Diagnostic.Kind,Integer> {
        private static final long serialVersionUID = -4017911930546289731L;

        public KindCountMap() {
            super();

            for (Diagnostic<?> diagnostic : DiagnosticMap.this.keySet()) {
                count(diagnostic.getKind(), 1);
            }
        }

        public void count(Diagnostic.Kind key) { count(key, 1); }

        public void count(Diagnostic.Kind key, int count) {
            put(key, count + (containsKey(key) ? get(key) : 0));
        }
    }
}
