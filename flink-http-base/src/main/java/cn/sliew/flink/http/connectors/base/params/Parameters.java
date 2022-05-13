package cn.sliew.flink.http.connectors.base.params;

public enum Parameters {

    CURSOR("cursor"),

    LIMIT("limit"),
    OFFSET("offset"),

    PAGE_INDEX("pageIndex"),
    PAGE_SIZE("pageSize"),

    START_TIME("startTime"),
    END_TIME("endTime"),
    ;

    private final String name;

    Parameters(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
