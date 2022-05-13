/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.sliew.flink.http.connectors.base.source.impl;

import cn.sliew.flink.http.connectors.base.params.HttpSourceParameters;
import cn.sliew.flink.http.connectors.base.source.HttpSourceSplit;
import cn.sliew.flink.http.connectors.base.source.HttpSourceSplitState;
import cn.sliew.flink.http.connectors.base.source.reader.BulkFormat;
import cn.sliew.flink.http.connectors.base.source.util.RecordsAndPosition;
import org.apache.flink.annotation.Internal;
import org.apache.flink.api.connector.source.SourceReader;
import org.apache.flink.api.connector.source.SourceReaderContext;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.base.source.reader.SingleThreadMultiplexSourceReaderBase;

import java.util.Map;

/**
 * A {@link SourceReader} that read records from {@link HttpSourceSplit}.
 */
@Internal
public final class HttpSourceReader<T, SplitT extends HttpSourceSplit>
        extends SingleThreadMultiplexSourceReaderBase<RecordsAndPosition<T>, T, SplitT, HttpSourceSplitState<SplitT>> {

    public HttpSourceReader(
            SourceReaderContext readerContext,
            BulkFormat<T, SplitT> readerFormat,
            HttpSourceParameters parameters,
            Configuration config) {
        super(
                () -> new HttpSourceSplitReader<>(config, parameters, readerFormat),
                new HttpSourceRecordEmitter<>(),
                config,
                readerContext);
    }

    @Override
    public void start() {
        // we request a split only if we did not get splits during the checkpoint restore
        if (getNumberOfCurrentlyAssignedSplits() == 0) {
            context.sendSplitRequest();
        }
    }

    @Override
    protected void onSplitFinished(Map<String, HttpSourceSplitState<SplitT>> finishedSplitIds) {
        context.sendSplitRequest();
    }

    @Override
    protected HttpSourceSplitState<SplitT> initializedState(SplitT split) {
        return new HttpSourceSplitState<>(split);
    }

    @Override
    protected SplitT toSplitType(String splitId, HttpSourceSplitState<SplitT> splitState) {
        return splitState.toSourceSplit();
    }
}
