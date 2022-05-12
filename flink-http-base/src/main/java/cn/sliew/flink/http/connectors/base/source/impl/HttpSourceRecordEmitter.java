package cn.sliew.flink.http.connectors.base.source.impl;

import cn.sliew.flink.http.connectors.base.source.HttpSourceSplit;
import cn.sliew.flink.http.connectors.base.source.HttpSourceSplitState;
import cn.sliew.flink.http.connectors.base.source.util.RecordsAndPosition;
import org.apache.flink.api.connector.source.SourceOutput;
import org.apache.flink.connector.base.source.reader.RecordEmitter;

public class HttpSourceRecordEmitter<T, SplitT extends HttpSourceSplit>
        implements RecordEmitter<RecordsAndPosition<T>, T, HttpSourceSplitState<SplitT>> {

    @Override
    public void emitRecord(
            RecordsAndPosition<T> elements,
            SourceOutput<T> output,
            HttpSourceSplitState<SplitT> splitState) throws Exception {
        elements.getRecords().forEach(output::collect);
        splitState.setPosition(elements.getPosition());
    }
}
