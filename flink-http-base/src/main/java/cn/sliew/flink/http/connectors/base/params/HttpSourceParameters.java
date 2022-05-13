package cn.sliew.flink.http.connectors.base.params;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
public class HttpSourceParameters implements Serializable {

    private String urlTemplate;
    private ObjectNode parameters;
    private Map<String, String> headers;
    private ObjectNode querys;
    private ObjectNode payloads;

    public HttpSourceParameters() {
    }

    public HttpSourceParameters(String urlTemplate, ObjectNode parameters, Map<String, String> headers, ObjectNode querys, ObjectNode payloads) {
        this.urlTemplate = urlTemplate;
        this.parameters = parameters;
        this.headers = headers;
        this.querys = querys;
        this.payloads = payloads;
    }
}
