package cn.sliew.flink.http.connectors.base.source.util;

import cn.sliew.flink.http.connectors.base.source.meta.offset.CheckpointedPosition;
import lombok.Getter;

import java.util.Collection;

@Getter
public class RecordsAndPosition<E> {

    private final Collection<E> records;
    private final CheckpointedPosition position;

    public RecordsAndPosition(Collection<E> records, CheckpointedPosition position) {
        this.records = records;
        this.position = position;
    }
}
