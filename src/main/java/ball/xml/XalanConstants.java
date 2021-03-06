package ball.xml;
/*-
 * ##########################################################################
 * Utilities
 * %%
 * Copyright (C) 2008 - 2022 Allen D. Ball
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
import javax.xml.namespace.QName;

/**
 * {@link.uri https://xml.apache.org/xalan-j/ Xalan} Constants.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 */
public interface XalanConstants extends XMLConstants {

    /**
     * {@link javax.xml.transform.Transformer} output property name.
     */
    public static final QName XALAN_INDENT_AMOUNT = new QName("http://xml.apache.org/xalan", "indent-amount");
}
