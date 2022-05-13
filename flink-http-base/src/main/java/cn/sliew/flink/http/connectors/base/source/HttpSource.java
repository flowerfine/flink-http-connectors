package cn.sliew.flink.http.connectors.base.source;

import cn.sliew.flink.http.connectors.base.source.assigners.HttpSourceSplitAssigner;
import cn.sliew.flink.http.connectors.base.source.assigners.SimpleHttpSourceSplitAssigner;
import cn.sliew.flink.http.connectors.base.source.enumerator.HttpSourceSplitEnumerator;
import cn.sliew.flink.http.connectors.base.source.impl.HttpEnumerator;
import cn.sliew.flink.http.connectors.base.source.impl.HttpSourceReader;
import cn.sliew.flink.http.connectors.base.source.reader.BulkFormat;
import cn.sliew.flink.http.connectors.base.source.meta.offset.CheckpointedPosition;
import cn.sliew.flink.http.connectors.base.params.HttpSourceParameters;
import org.apache.flink.api.connector.source.*;
import org.apache.flink.core.io.SimpleVersionedSerializer;

import java.util.ArrayList;
import java.util.Collection;

public class HttpSource<T, SplitT extends HttpSourceSplit>
        implements Source<T, SplitT, PendingSplitsCheckpoint<SplitT>> {

    private final HttpSourceParameters parameters;
    private final CheckpointedPosition.Provider positionFactory;

    private final HttpSourceSplitSerializer httpSourceSplitSerializer;

    private final HttpSourceSplitEnumerator.Provider splitEnumeratorFactory = null;
    private final HttpSourceSplitAssigner.Provider splitAssignerFactory = splits -> new SimpleHttpSourceSplitAssigner(new ArrayList(splits));

    private final BulkFormat<T, SplitT> readerFormat = null;

    public HttpSource(HttpSourceParameters parameters,
                      CheckpointedPosition.Provider positionFactory) {
        this.parameters = parameters;
        this.positionFactory = positionFactory;
        this.httpSourceSplitSerializer = new HttpSourceSplitSerializer(positionFactory);
    }

    @Override
    public Boundedness getBoundedness() {
        return Boundedness.CONTINUOUS_UNBOUNDED;
    }

    @Override
    public SourceReader<T, SplitT> createReader(SourceReaderContext context) throws Exception {
        return new HttpSourceReader<>(
                context, readerFormat, parameters, context.getConfiguration());
    }

    @Override
    public SplitEnumerator<SplitT, PendingSplitsCheckpoint<SplitT>> createEnumerator(SplitEnumeratorContext<SplitT> context) throws Exception {
        HttpSourceSplitEnumerator splitEnumerator = splitEnumeratorFactory.create();
        Collection<SplitT> splits = splitEnumerator.enumerateSplits(parameters);
        return createSplitEnumerator(context, splitEnumerator, splits);
    }

    @Override
    public SplitEnumerator<SplitT, PendingSplitsCheckpoint<SplitT>> restoreEnumerator(SplitEnumeratorContext<SplitT> context, PendingSplitsCheckpoint<SplitT> checkpoint) throws Exception {
        HttpSourceSplitEnumerator splitEnumerator = splitEnumeratorFactory.create();
        Collection<SplitT> splits = checkpoint.getSplits();
        return createSplitEnumerator(context, splitEnumerator, splits);
    }

    private SplitEnumerator<SplitT, PendingSplitsCheckpoint<SplitT>> createSplitEnumerator(
            SplitEnumeratorContext<SplitT> context,
            HttpSourceSplitEnumerator splitEnumerator,
            Collection<SplitT> splits) {

        HttpSourceSplitAssigner splitAssigner = splitAssignerFactory.create((Collection<HttpSourceSplit>) splits);
        return new HttpEnumerator<>(context, splitEnumerator, splitAssigner, parameters);
    }

    @Override
    public SimpleVersionedSerializer<SplitT> getSplitSerializer() {
        return (SimpleVersionedSerializer<SplitT>) httpSourceSplitSerializer;
    }

    @Override
    public SimpleVersionedSerializer<PendingSplitsCheckpoint<SplitT>> getEnumeratorCheckpointSerializer() {
        return new PendingSplitsCheckpointSerializer(getSplitSerializer());
    }
}
