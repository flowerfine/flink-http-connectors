package cn.sliew.flink.http.connectors.base.source;

import cn.sliew.flink.http.connectors.base.source.meta.offset.CheckpointedPosition;
import cn.sliew.milky.common.exception.Rethrower;
import org.apache.flink.util.InstantiationUtil;

import java.io.IOException;

public class HttpSourceSplitState<SplitT extends HttpSourceSplit> {

    private final SplitT split;
    private CheckpointedPosition position;

    public HttpSourceSplitState(SplitT split) {
        this.split = split;
        this.position = split.getPosition().orElse(null);
    }

    public CheckpointedPosition getPosition() {
        return position;
    }

    public void setPosition(CheckpointedPosition position) {
        this.position = position;
    }

    public SplitT toSourceSplit() {
        try {
            CheckpointedPosition copyed = InstantiationUtil.clone(position);
            return (SplitT) split.updateWithCheckpointedPosition(copyed);
        } catch (IOException | ClassNotFoundException e) {
            Rethrower.throwAs(e);
        }
        return null;
    }
}
