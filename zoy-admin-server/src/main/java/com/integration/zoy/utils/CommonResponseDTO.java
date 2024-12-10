package com.integration.zoy.utils;

import java.util.List;

public class CommonResponseDTO<T> {
	private List<T> data;
    private int count;
    
    public CommonResponseDTO() {}

    public CommonResponseDTO(List<T> data, int count) {
        this.data = data;
        this.count = count;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
