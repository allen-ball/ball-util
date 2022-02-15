package ball.util;
/*-
 * ##########################################################################
 * Utilities
 * %%
 * Copyright (C) 2021, 2022 Allen D. Ball
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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * {@link List}-Order {@link Comparator}.
 *
 * {@bean.info}
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 */
public class ListOrderComparator<T> implements Comparator<T> {
    private final List<T> list;

    /**
     * Construct a {@link Comparator} from the argument {@link List}.
     *
     * @param   list            The {@link List} with the elements in ranked
     *                          order.
     */
    public ListOrderComparator(List<T> list) {
        this.list = Objects.requireNonNull(list);
    }

    /**
     * Construct a {@link Comparator} from the argument array.
     *
     * @param   array           The array with the elements in ranked
     *                          order.
     */
    @SafeVarargs @SuppressWarnings({ "varargs" })
    public ListOrderComparator(T... array) { this(Arrays.asList(array)); }

    @Override
    public int compare(T left, T right) { return rank(left) - rank(right); }

    private int rank(T object) {
        int index = list.indexOf(object);

        return (index >= 0) ? index : list.size();
    }
}
