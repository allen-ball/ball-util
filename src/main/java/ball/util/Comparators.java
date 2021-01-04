package ball.util;
/*-
 * ##########################################################################
 * Utilities
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2008 - 2021 Allen D. Ball
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * {@link Comparator}s.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public abstract class Comparators {
    private Comparators() { }

    /**
     * Static method to return a {@link Comparator} ordering members in the
     * same order as the {@link List}.
     *
     * @param   list            The {@link List} describing the order.
     * @param   <T>             The type to be ordered.
     *
     * @return  A {@link Comparator} enforcing the specified order.
     */
    public static <T> Comparator<T> orderedBy(List<T> list) {
        return new OrderedComparator<T>(list);
    }

    private static class OrderedComparator<T> extends HashMap<T,Integer>
                                              implements Comparator<T> {
        private static final long serialVersionUID = 4745150194266705710L;

        public OrderedComparator(List<T> list) {
            for (int i = 0, n = list.size(); i < n; i += 1) {
                put(list.get(i), i);
            }
        }

        @Override
        public int compare(T left, T right) {
            return Integer.compare(getOrDefault(left, -1),
                                   getOrDefault(right, size()));
        }
    }
}
