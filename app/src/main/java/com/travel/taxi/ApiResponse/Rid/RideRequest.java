package com.travel.taxi.ApiResponse.Rid;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RideRequest {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("request_id")
    @Expose
    private Integer requestId;
    @SerializedName("current_provider")
    @Expose
    private Integer currentProvider;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getRequestId() {
        return requestId;
    }

    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    public Integer getCurrentProvider() {
        return currentProvider;
    }

    public void setCurrentProvider(Integer currentProvider) {
        this.currentProvider = currentProvider;
    }

    @Override
    public String toString() {
        return "RideRequest{" +
                "message='" + message + '\'' +
                ", requestId=" + requestId +
                ", currentProvider=" + currentProvider +
                '}';
    }
}