package cn.sliew.flink.http.connectors.base.params;

public enum Pagination {

    LIMIT_OFFSET(Parameters.LIMIT, Parameters.OFFSET),
    INDEX_SIZE(Parameters.PAGE_INDEX, Parameters.PAGE_SIZE),
    CURSOR(Parameters.CURSOR),
    ;

    private Parameters[] parameters;

    Pagination(Parameters... parameters) {
        this.parameters = parameters;
    }
}
