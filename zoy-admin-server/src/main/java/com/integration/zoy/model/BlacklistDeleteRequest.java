package com.integration.zoy.model;

import java.util.List;

public class BlacklistDeleteRequest {
    private List<String> ids;

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }
}
