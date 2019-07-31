package com.travel.taxi.ApiResponse.Trip;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

class ServiceType {
        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("provider_name")
        @Expose
        private String providerName;
        @SerializedName("image")
        @Expose
        private String image;
        @SerializedName("capacity")
        @Expose
        private Integer capacity;
        @SerializedName("bags")
        @Expose
        private String bags;
        @SerializedName("fixed")
        @Expose
        private Integer fixed;
        @SerializedName("price")
        @Expose
        private Float price;
        @SerializedName("minute")
        @Expose
        private Integer minute;
        @SerializedName("distance")
        @Expose
        private Integer distance;
        @SerializedName("calculator")
        @Expose
        private String calculator;
        @SerializedName("description")
        @Expose
        private Object description;
        @SerializedName("status")
        @Expose
        private Integer status;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getProviderName() {
            return providerName;
        }

        public void setProviderName(String providerName) {
            this.providerName = providerName;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public Integer getCapacity() {
            return capacity;
        }

        public void setCapacity(Integer capacity) {
            this.capacity = capacity;
        }

        public String getBags() {
            return bags;
        }

        public void setBags(String bags) {
            this.bags = bags;
        }

        public Integer getFixed() {
            return fixed;
        }

        public void setFixed(Integer fixed) {
            this.fixed = fixed;
        }

        public Float getPrice() {
            return price;
        }

        public void setPrice(Float price) {
            this.price = price;
        }

        public Integer getMinute() {
            return minute;
        }

        public void setMinute(Integer minute) {
            this.minute = minute;
        }

        public Integer getDistance() {
            return distance;
        }

        public void setDistance(Integer distance) {
            this.distance = distance;
        }

        public String getCalculator() {
            return calculator;
        }

        public void setCalculator(String calculator) {
            this.calculator = calculator;
        }

        public Object getDescription() {
            return description;
        }

        public void setDescription(Object description) {
            this.description = description;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

    }
