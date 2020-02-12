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
import ball.annotation.ServiceProviderFor;
import ball.xml.FluentNode;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;
import java.net.URI;
import java.util.Map;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Inline {@link Taglet} providing links to external RFCs.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Taglet.class })
@TagletName("link.rfc")
@NoArgsConstructor @ToString
public class LinkRFCTaglet extends AbstractInlineTaglet
                           implements SunToolsInternalToolkitTaglet {
    private static final LinkRFCTaglet INSTANCE = new LinkRFCTaglet();

    public static void register(Map<Object,Object> map) {
        register(map, INSTANCE);
    }

    private static final String TEXT = "RFC%d";
    private static final String PROTOCOL = "https";
    private static final String HOST = "www.rfc-editor.org";
    private static final String PATH = "/rfc/rfc%d.txt";

    @Override
    public FluentNode toNode(Tag tag) throws Throwable {
        FluentNode node = fragment();
        int rfc = Integer.valueOf(tag.text().trim());

        node =
            a(new URI(PROTOCOL, HOST, String.format(PATH, rfc), null),
              String.format(TEXT, rfc))
            .add(attr("target", "newtab"));

        return node;
    }
}
