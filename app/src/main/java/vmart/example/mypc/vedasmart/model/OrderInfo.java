package vmart.example.mypc.vedasmart.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class OrderInfo implements Serializable {

    @SerializedName("orderStatus")
    String orderStatus;

    @SerializedName("orderId")
    String orderId;

    @SerializedName("userId")
    String userId;

    @SerializedName("paymentType")
    String paymentType;

    @SerializedName("deliveryType")
    String deliveryType;

    @SerializedName("timeStamp")
    String timeStamp;

    @SerializedName("totalCost")
    String totalCost;

    @SerializedName("deliveryCharges")
    String deliveryCharges;

    @SerializedName("totalSavings")
    String totalSavings;

    @SerializedName("cartTotal")
    String cartTotal;

    @SerializedName("deliveryInfo")
    DeliveryInfo deliveryInfo;

    @SerializedName("refund")
    String refund;

    @SerializedName("reason_for_canceled_order")
    String reason;

    @SerializedName("email")
    String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRefund() {
        return refund;
    }

    public void setRefund(String refund) {
        this.refund = refund;
    }

    @SerializedName("itemsInfo")
    ArrayList<ItemsInfo> itemsInfo = new ArrayList<ItemsInfo>();

    public String getDunzoTaskId() {
        return dunzoTaskId;
    }

    public void setDunzoTaskId(String dunzoTaskId) {
        this.dunzoTaskId = dunzoTaskId;
    }

    @SerializedName("dunzoTaskId")
    String dunzoTaskId;

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    @SerializedName("task_id")
    String task_id;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @SerializedName("state")
    String status;

    public OrderInfo() {
    }

    public OrderInfo(String orderStatus, String orderId, String userId, String paymentType, String deliveryType,
                     String timeStamp, String totalCost, String deliveryCharges, String totalSavings,
                     String cartTotal, DeliveryInfo deliveryInfo, ArrayList<ItemsInfo> itemsInfo,String email) {
        this.orderStatus = orderStatus;
        this.orderId = orderId;
        this.userId = userId;
        this.paymentType = paymentType;
        this.deliveryType = deliveryType;
        this.timeStamp = timeStamp;
        this.totalCost = totalCost;
        this.deliveryCharges = deliveryCharges;
        this.totalSavings = totalSavings;
        this.cartTotal = cartTotal;
        this.deliveryInfo = deliveryInfo;
        this.itemsInfo = itemsInfo;
        this.email = email;

    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(String totalCost) {
        this.totalCost = totalCost;
    }

    public String getDeliveryCharges() {
        return deliveryCharges;
    }

    public void setDeliveryCharges(String deliveryCharges) {
        this.deliveryCharges = deliveryCharges;
    }

    public String getTotalSavings() {
        return totalSavings;
    }

    public void setTotalSavings(String totalSavings) {
        this.totalSavings = totalSavings;
    }

    public String getCartTotal() {
        return cartTotal;
    }

    public void setCartTotal(String cartTotal) {
        this.cartTotal = cartTotal;
    }

    public DeliveryInfo getDeliveryInfo() {
        return deliveryInfo;
    }

    public void setDeliveryInfo(DeliveryInfo deliveryInfo) {
        this.deliveryInfo = deliveryInfo;
    }

    public ArrayList<ItemsInfo> getItemsInfo() {
        return itemsInfo;
    }

    public void setItemsInfo(ArrayList<ItemsInfo> itemsInfo) {
        this.itemsInfo = itemsInfo;
    }
}
