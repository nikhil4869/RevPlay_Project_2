package com.example.demo.dto.analytics;

public class ListenerInsightsDTO {

    private long uniqueListeners;

    public ListenerInsightsDTO(long uniqueListeners) {
        this.uniqueListeners = uniqueListeners;
    }

    public long getUniqueListeners() {
        return uniqueListeners;
    }
}