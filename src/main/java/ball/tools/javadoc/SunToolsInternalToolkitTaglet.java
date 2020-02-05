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
import com.sun.javadoc.Doc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.formats.html.markup.RawHtml;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.taglets.Taglet;
import com.sun.tools.doclets.internal.toolkit.taglets.TagletWriter;
/* import com.sun.tools.doclets.internal.toolkit.util.Extern; */

/**
 * Default methods for legacy {@link Taglet} implementations.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public interface SunToolsInternalToolkitTaglet extends Taglet,
                                                       JavadocHTMLTemplates {
    void set(Configuration configuration);

    @Override
    default Content getTagletOutput(Doc doc,
                                    TagletWriter writer) throws IllegalArgumentException {
        set(writer.configuration());

        throw new IllegalArgumentException(doc.position() + ": "
                                           + "Method not supported in taglet "
                                           + getName() + ".");
    }

    @Override
    default Content getTagletOutput(Tag tag,
                                    TagletWriter writer) throws IllegalArgumentException {
        set(writer.configuration());

        Content content = writer.getOutputInstance();

        content.addContent(new RawHtml(((AbstractTaglet) this).toString(tag)));

        return content;
    }
}
