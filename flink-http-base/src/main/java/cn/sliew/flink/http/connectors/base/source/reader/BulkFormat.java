package cn.sliew.flink.http.connectors.base.source.reader;

import cn.sliew.flink.http.connectors.base.source.HttpSourceSplit;
import cn.sliew.flink.http.connectors.base.source.util.CheckpointedPosition;
import cn.sliew.flink.http.connectors.base.source.util.HttpSourceParameters;
import cn.sliew.flink.http.connectors.base.source.util.RecordsAndPosition;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.ResultTypeQueryable;
import org.apache.flink.configuration.Configuration;

import javax.annotation.Nullable;
import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;

public interface BulkFormat<T, SplitT extends HttpSourceSplit>
        extends Serializable, ResultTypeQueryable<T> {

    Reader<T> createReader(Configuration config, SplitT split, HttpSourceParameters parameters) throws IOException;

    Reader<T> restoreReader(Configuration config, SplitT split, HttpSourceParameters parameters, CheckpointedPosition position) throws IOException;

    boolean isSplittable();

    @Override
    TypeInformation<T> getProducedType();

    // ------------------------------------------------------------------------

    /**
     * The actual reader that reads the batches of records.
     */
    interface Reader<T> extends Closeable {

        @Nullable
        RecordIterator<T> readBatch() throws IOException;
    }

    // ------------------------------------------------------------------------

    interface RecordIterator<T> {

        @Nullable
        RecordsAndPosition<T> next();

        void releaseBatch();
    }
}
