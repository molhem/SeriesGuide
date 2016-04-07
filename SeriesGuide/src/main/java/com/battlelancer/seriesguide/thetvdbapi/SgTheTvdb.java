/*
 * Copyright 2016 Uwe Trottmann
 *
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
 */

package com.battlelancer.seriesguide.thetvdbapi;

import android.content.Context;
import com.battlelancer.seriesguide.BuildConfig;
import com.battlelancer.seriesguide.util.ServiceUtils;
import com.uwetrottmann.thetvdb.TheTvdb;
import java.io.File;
import java.util.concurrent.TimeUnit;
import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * Extends {@link TheTvdb} to use our own caching OkHttp client.
 */
public class SgTheTvdb extends TheTvdb {

    private static final String CACHE_DIRECTORY = "thetvdb-cache";

    private static OkHttpClient cachingHttpClient;

    private final Context context;

    public SgTheTvdb(Context context) {
        super(BuildConfig.TVDB_API_KEY);
        this.context = context.getApplicationContext();
    }

    @Override
    protected synchronized OkHttpClient okHttpClient() {
        if (cachingHttpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            setOkHttpClientDefaults(builder);
            builder.connectTimeout(ServiceUtils.CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
            builder.readTimeout(ServiceUtils.READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
            File cacheDir = ServiceUtils.createApiCacheDir(context, CACHE_DIRECTORY);
            builder.cache(new Cache(cacheDir, ServiceUtils.calculateApiDiskCacheSize(cacheDir)));
            cachingHttpClient = builder.build();
        }
        return cachingHttpClient;
    }
}
