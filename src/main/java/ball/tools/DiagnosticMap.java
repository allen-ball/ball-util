package ball.tools;
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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;
import lombok.NoArgsConstructor;

/**
 * {@link DiagnosticListener} and {@link LinkedHashMap} implementation for
 * collecting {@link Diagnostic}s as keys and informational/prescriptive
 * messages as values.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@NoArgsConstructor
public class DiagnosticMap
             extends LinkedHashMap<Diagnostic<? extends JavaFileObject>,String>
             implements DiagnosticListener<JavaFileObject> {
    private static final long serialVersionUID = -5544734926408904555L;

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
            put(key, computeIfAbsent(key, k -> 0) + count);
        }
    }
}
