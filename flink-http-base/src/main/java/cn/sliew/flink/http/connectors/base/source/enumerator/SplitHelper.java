package cn.sliew.flink.http.connectors.base.source.enumerator;

import cn.sliew.flink.http.connectors.base.params.HttpSourceParameters;
import cn.sliew.flink.http.connectors.base.source.HttpSourceSplit;
import cn.sliew.flink.http.connectors.base.source.meta.offset.CheckpointedPosition;

import java.util.Collection;
import java.util.function.Supplier;

public interface SplitHelper<SplitT extends HttpSourceSplit> {

    Collection<SplitT> split(Supplier<String> splitIdCreator, HttpSourceParameters parameters);

    CheckpointedPosition toCheckpoint(HttpSourceParameters parameters);
}
