package com.jstream.model;

public class Subscription {
    private int id;
    private int userId;
    private String plan;
    private String startDate;
    private String endDate;
    private boolean active;

    public Subscription() {
    }

    public Subscription(int id, int userId, String plan, String startDate, String endDate, boolean active) {
        this.id = id;
        this.userId = userId;
        this.plan = plan;
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
