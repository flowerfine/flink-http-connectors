package cn.sliew.flink.http.connectors.base.source;

import cn.sliew.flink.http.connectors.base.source.meta.offset.CheckpointedPosition;
import org.apache.flink.api.connector.source.SourceSplit;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Optional;

public class HttpSourceSplit implements SourceSplit, Serializable {

    private static final long serialVersionUID = 6348086794567295161L;

    private final String id;

    @Nullable
    private CheckpointedPosition position;

    /**
     * The splits are frequently serialized into checkpoints. Caching the byte representation makes
     * repeated serialization cheap. This field is used by {@link HttpSourceSplitSerializer}.
     */
    @Nullable
    transient byte[] serializedFormCache;

    public HttpSourceSplit(String id) {
        this(id, null);
    }

    public HttpSourceSplit(String id, CheckpointedPosition position) {
        this(id, position, null);
    }

    public HttpSourceSplit(String id, @Nullable CheckpointedPosition position, @Nullable byte[] serializedFormCache) {
        this.id = id;
        this.position = position;
        this.serializedFormCache = serializedFormCache;
    }

    @Override
    public String splitId() {
        return id;
    }

    public Optional<CheckpointedPosition> getPosition() {
        return Optional.ofNullable(position);
    }

    public HttpSourceSplit updateWithCheckpointedPosition(CheckpointedPosition position) {
        return new HttpSourceSplit(id, position);
    }
}
