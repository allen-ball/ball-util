package ball.util;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.IntStream;

/**
 * {@link Integer} {@link java.util.List} implementation useful for testing
 * {@link ball.util.stream.Combinations} and
 * {@link ball.util.stream.Permutations} {@link java.util.stream.Stream}s.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public class IntegerList extends ArrayList<Integer> {
    private static final long serialVersionUID = 5581619270177831511L;

    /**
     * @param   count           The element count.
     */
    public IntegerList(Integer count) {
        super(count);

        Collections.addAll(this,
                           IntStream.range(0, count)
                           .boxed()
                           .toArray(Integer[]::new));
    }

    @Override
    public Integer[] toArray() { return toArray(new Integer[] { }); }

    @Override
    public IntegerList clone() { return (IntegerList) super.clone(); }
}
