package cn.sliew.flink.http.connectors.base.source.meta.offset;

import org.apache.flink.core.memory.DataInputDeserializer;
import org.apache.flink.core.memory.DataOutputSerializer;

import java.io.IOException;
import java.io.Serializable;

@FunctionalInterface
public interface CheckpointedPositionSerializer extends Serializable {

    CheckpointedPosition.Provider getPositionProvider();

    default CheckpointedPosition readPosition(DataInputDeserializer in) throws IOException {
        CheckpointedPosition.Provider positionFactory = getPositionProvider();
        CheckpointedPosition position = positionFactory.create();
        position.read(in);
        return position;
    }

    default void writePosition(CheckpointedPosition position, DataOutputSerializer out) throws IOException {
        position.write(out);
    }
}
