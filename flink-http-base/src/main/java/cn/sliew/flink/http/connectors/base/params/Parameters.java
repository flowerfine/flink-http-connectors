package cn.sliew.flink.http.connectors.base.params;

public enum Parameters {

    PAGE_START("pageStart"),
    PAGE_END("pageEnd"),

    PAGE_INDEX("pageIndex"),
    PAGE_SIZE("pageSize"),
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
