package cn.sliew.flink.http.connectors.base.source.impl;

import cn.sliew.flink.http.connectors.base.source.HttpSourceSplit;
import cn.sliew.flink.http.connectors.base.source.reader.BulkFormat;
import cn.sliew.flink.http.connectors.base.source.meta.offset.CheckpointedPosition;
import cn.sliew.flink.http.connectors.base.params.HttpSourceParameters;
import cn.sliew.flink.http.connectors.base.source.util.RecordsAndPosition;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.base.source.reader.RecordsWithSplitIds;
import org.apache.flink.connector.base.source.reader.splitreader.SplitReader;
import org.apache.flink.connector.base.source.reader.splitreader.SplitsAddition;
import org.apache.flink.connector.base.source.reader.splitreader.SplitsChange;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;

@Slf4j
public class HttpSourceSplitReader <T, SplitT extends HttpSourceSplit>
        implements SplitReader<RecordsAndPosition<T>, SplitT> {

    private final Configuration config;
    private final HttpSourceParameters parameters;
    private final BulkFormat<T, SplitT> readerFactory;

    private final Queue<SplitT> splits;

    @Nullable
    private BulkFormat.Reader<T> currentReader;
    @Nullable
    private String currentSplitId;

    public HttpSourceSplitReader(Configuration config,HttpSourceParameters parameters, BulkFormat<T, SplitT> readerFactory) {
        this.config = config;
        this.parameters = parameters;
        this.readerFactory = readerFactory;
        this.splits = new ArrayDeque<>();
    }

    @Override
    public RecordsWithSplitIds<RecordsAndPosition<T>> fetch() throws IOException {
        checkSplitOrStartNext();

        final BulkFormat.RecordIterator<T> nextBatch = currentReader.readBatch();
        return nextBatch == null
                ? finishSplit()
                : HttpRecords.forRecords(currentSplitId, nextBatch);
    }

    @Override
    public void handleSplitsChanges(SplitsChange<SplitT> splitsChange) {
        if (!(splitsChange instanceof SplitsAddition)) {
            throw new UnsupportedOperationException(
                    String.format(
                            "The SplitChange type of %s is not supported.",
                            splitsChange.getClass()));
        }

        log.debug("Handling split change {}", splitsChange);
        splits.addAll(splitsChange.splits());
    }

    @Override
    public void wakeUp() {

    }

    @Override
    public void close() throws Exception {
        if (currentReader != null) {
            currentReader.close();
        }
    }

    private void checkSplitOrStartNext() throws IOException {
        if (currentReader != null) {
            return;
        }

        final SplitT nextSplit = splits.poll();
        if (nextSplit == null) {
            throw new IOException("Cannot fetch from another split - no split remaining");
        }

        currentSplitId = nextSplit.splitId();

        final Optional<CheckpointedPosition> position = nextSplit.getPosition();
        currentReader =
                position.isPresent()
                        ? readerFactory.restoreReader(config, nextSplit, parameters, position.get())
                        : readerFactory.createReader(config, nextSplit, parameters);
    }

    private HttpRecords<T> finishSplit() throws IOException {
        if (currentReader != null) {
            currentReader.close();
            currentReader = null;
        }

        final HttpRecords<T> finishRecords = HttpRecords.finishedSplit(currentSplitId);
        currentSplitId = null;
        return finishRecords;
    }
}
