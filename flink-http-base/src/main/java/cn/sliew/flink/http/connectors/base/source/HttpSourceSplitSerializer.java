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

package cn.sliew.flink.http.connectors.base.source;

import cn.sliew.flink.http.connectors.base.source.meta.offset.CheckpointedPosition;
import cn.sliew.flink.http.connectors.base.source.meta.offset.CheckpointedPositionSerializer;
import org.apache.flink.core.io.SimpleVersionedSerializer;
import org.apache.flink.core.memory.DataInputDeserializer;
import org.apache.flink.core.memory.DataOutputSerializer;

import java.io.IOException;
import java.util.Optional;

import static org.apache.flink.util.Preconditions.checkArgument;

/**
 * A serializer for the {@link HttpSourceSplit}.
 */
public final class HttpSourceSplitSerializer
        implements SimpleVersionedSerializer<HttpSourceSplit>, CheckpointedPositionSerializer {

    private static final ThreadLocal<DataOutputSerializer> SERIALIZER_CACHE =
            ThreadLocal.withInitial(() -> new DataOutputSerializer(64));

    private static final int VERSION = 1;

    private final CheckpointedPosition.Provider positionFactory;

    // ------------------------------------------------------------------------

    public HttpSourceSplitSerializer(CheckpointedPosition.Provider positionFactory) {
        this.positionFactory = positionFactory;
    }

    @Override
    public CheckpointedPosition.Provider getPositionProvider() {
        return positionFactory;
    }

    @Override
    public int getVersion() {
        return VERSION;
    }

    @Override
    public byte[] serialize(HttpSourceSplit split) throws IOException {
        checkArgument(
                split.getClass() == HttpSourceSplit.class,
                "Cannot serialize subclasses of HttpSourceSplit");

        // optimization: the splits lazily cache their own serialized form
        if (split.serializedFormCache != null) {
            return split.serializedFormCache;
        }

        final DataOutputSerializer out = SERIALIZER_CACHE.get();
        out.writeUTF(split.splitId());

        final Optional<CheckpointedPosition> position = split.getPosition();
        out.writeBoolean(position.isPresent());
        if (position.isPresent()) {
            writePosition(position.get(), out);
        }

        final byte[] result = out.getCopyOfBuffer();
        out.clear();

        // optimization: cache the serialized from, so we avoid the byte work during repeated
        // serialization
        split.serializedFormCache = result;

        return result;
    }

    @Override
    public HttpSourceSplit deserialize(int version, byte[] serialized) throws IOException {
        if (version == getVersion()) {
            return deserializeV1(serialized);
        }
        throw new IOException("Unknown version: " + version);
    }

    private HttpSourceSplit deserializeV1(byte[] serialized) throws IOException {
        final DataInputDeserializer in = new DataInputDeserializer(serialized);
        final String splitId = in.readUTF();

        CheckpointedPosition position = null;
        if (in.readBoolean()) {
            position = readPosition(in);
        }

        // instantiate a new split and cache the serialized form
        return new HttpSourceSplit(splitId, position, serialized);
    }

}
