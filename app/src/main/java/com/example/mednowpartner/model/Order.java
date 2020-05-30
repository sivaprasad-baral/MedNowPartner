package com.example.mednowpartner.model;

import java.util.Map;

public class Order {

    private String partnerId,customerId,orderId,orderDate,orderTime;
    private double cost;
    private Map<String,Object> medicines;
    private int orderStatus;

    public Order() {
    }

    public Order(String partnerId, String customerId, String orderId, double cost, Map<String, Object> medicines) {
        this.partnerId = partnerId;
        this.customerId = customerId;
        this.orderId = orderId;
        this.cost = cost;
        this.medicines = medicines;
    }

    public Order(String partnerId, String customerId, String orderId, String orderDate, String orderTime, double cost, Map<String, Object> medicines, int orderStatus) {
        this.partnerId = partnerId;
        this.customerId = customerId;
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.orderTime = orderTime;
        this.cost = cost;
        this.medicines = medicines;
        this.orderStatus = orderStatus;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public Map<String, Object> getMedicines() {
        return medicines;
    }

    public void setMedicines(Map<String, Object> medicines) {
        this.medicines = medicines;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }
}
