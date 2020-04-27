package ball.net;
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
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.CacheRequest;
import java.net.CacheResponse;
import java.net.ResponseCache;
import java.net.URI;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

import static java.nio.file.attribute.PosixFilePermissions.asFileAttribute;
import static java.nio.file.attribute.PosixFilePermissions.fromString;
import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * {@link ResponseCache} implementation.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@NoArgsConstructor(access = PRIVATE) @ToString
public class ResponseCacheImpl extends ResponseCache {

    /**
     * Default {@link ResponseCacheImpl}.
     */
    public static final ResponseCacheImpl DEFAULT = new ResponseCacheImpl();

    private static final String BODY = "BODY";
    private static final String HEADERS = "HEADERS";

    private final Path cache =
        Paths.get(System.getProperty("user.home"), ".config", "java", "cache");

    {
        try {
            Files.createDirectories(cache,
                                    asFileAttribute(fromString("rwx------")));
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    @Override
    public CacheResponse get(URI uri, String method,
                             Map<String,List<String>> headers) {
        CacheResponseImpl response = null;

        if (isCached(uri)) {
            response = new CacheResponseImpl(cache(uri));
        }

        return response;
    }

    @Override
    public CacheRequest put(URI uri, URLConnection connection) {
        CacheRequestImpl request = null;

        if (isCacheable(uri)) {
            if (! connection.getAllowUserInteraction()) {
                request =
                    new CacheRequestImpl(cache(uri),
                                         connection.getHeaderFields());
            }
        }

        return request;
    }

    private Path cache(URI uri) {
        Path path = cache.resolve(uri.getScheme().toLowerCase());
        String host = uri.getHost().toLowerCase();
        int port = uri.getPort();

        if (port > 0) {
            host += ":" + String.valueOf(port);
        }

        path = path.resolve(host);

        String string = uri.getPath();

        if (string != null) {
            for (String substring : string.split("[/]+")) {
                if (isNotEmpty(substring)) {
                    path = path.resolve(substring);
                }
            }
        }

        return path.normalize();
    }

    private boolean isCached(URI uri) {
        return isCacheable(uri) && Files.exists(cache(uri).resolve(BODY));
    }

    private boolean isCacheable(URI uri) {
        return (uri.isAbsolute()
                && (! uri.isOpaque())
                && uri.getUserInfo() == null
                && uri.getQuery() == null
                && uri.getFragment() == null);
    }

    private void delete(Path path) throws IOException {
        Files.deleteIfExists(path.resolve(HEADERS));
        Files.deleteIfExists(path.resolve(BODY));
        Files.deleteIfExists(path);
    }

    @AllArgsConstructor(access = PRIVATE) @ToString
    public class CacheRequestImpl extends CacheRequest {
        @NonNull private final Path path;
        @NonNull private final Map<String,List<String>> headers;

        @Override
        public OutputStream getBody() throws IOException {
            Files.createDirectories(path);

            XMLEncoder encoder =
                new XMLEncoder(Files.newOutputStream(path.resolve(HEADERS)));

            encoder.writeObject(headers);
            encoder.close();

            return Files.newOutputStream(path.resolve(BODY));
        }

        @Override
        public void abort() {
            try {
                delete(path);
            } catch (Exception exception) {
                throw new IllegalStateException(exception);
            }
        }
    }

    @AllArgsConstructor(access = PRIVATE) @ToString
    public class CacheResponseImpl extends CacheResponse {
        @NonNull private final Path path;

        @Override
        public Map<String,List<String>> getHeaders() throws IOException {
            XMLDecoder decoder =
                new XMLDecoder(Files.newInputStream(path.resolve(HEADERS)));
            @SuppressWarnings("unchecked")
            Map<String,List<String>> headers =
                (Map<String,List<String>>) decoder.readObject();

            decoder.close();

            return headers;
        }

        @Override
        public InputStream getBody() throws IOException {
            return Files.newInputStream(path.resolve(BODY));
        }
    }
}
