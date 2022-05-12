package cn.sliew.flink.http.connectors.base.source.util;

import cn.sliew.flink.http.connectors.base.source.HttpSourceSplit;

import java.util.Collection;
import java.util.function.Supplier;

public interface SplitHelper<SplitT extends HttpSourceSplit> {

    Collection<SplitT> split(Supplier<String> splitIdCreator, HttpSourceParameters parameters);

    CheckpointedPosition toCheckpoint(HttpSourceParameters parameters);
}
