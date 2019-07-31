package com.travel.taxi.ApiResponse.Service;

import java.util.List;

public class ServiceResponse {
    List<Service> services;

    public ServiceResponse(List<Service> services) {
        this.services = services;
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }
}
