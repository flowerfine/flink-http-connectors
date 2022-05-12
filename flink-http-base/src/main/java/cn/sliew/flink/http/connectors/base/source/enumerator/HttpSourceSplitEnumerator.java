package cn.sliew.flink.http.connectors.base.source.enumerator;

import cn.sliew.flink.http.connectors.base.source.HttpSourceSplit;
import cn.sliew.flink.http.connectors.base.source.util.HttpSourceParameters;

import java.io.Serializable;
import java.util.Collection;

public interface HttpSourceSplitEnumerator<SplitT extends HttpSourceSplit> {

    Collection<SplitT> enumerateSplits(HttpSourceParameters parameters);

    /**
     * Factory for the {@code HttpSourceEnumerator}.
     */
    @FunctionalInterface
    interface Provider extends Serializable {

        HttpSourceSplitEnumerator create();
    }
}
