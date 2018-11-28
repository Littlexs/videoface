package com.yunche.android.yunchevideosdk.param;

/**
 * Created by shengxiao on 2018/6/21.
 */

public class VideoParam {

    /**
     * orderId : 1805091706154620919
     * guaranteeCompanyId : 1
     * guaranteeCompanyName : 云车金融
     * customerId : 821
     * customerName : 小东东
     * customerIdCard : 421822199608080706
     * path : /2018/06/20/1.jpg
     * type : 1
     * auditorId : 1
     * auditorName : 阿东
     * action : 1
     * latlon : 39.01,125.67
     * address : 杭州市民中心
     * carDetailId : 155
     * carName : 特斯拉 9000
     * carPrice : 500000
     * expectLoanAmount : 399999
     * photoSimilarityDegree : 80.65
     */

    private String orderId;
    private Long customerId;
    private String customerName;
    private String customerIdCard;
    private String bankId;
    private String path;
    private int type;//面签类型
    private Integer auditorId;//审核人员  null
    private String auditorName;  //null
    private Integer action;  //null
    private String latlon;
    private String address;
    private String carDetailId;
    private String carName;
    private String carPrice;
    private int expectLoanAmount;
    private double videoSize;//mb

    public double getVideoSize() {
        return videoSize;
    }

    public void setVideoSize(double videoSize) {
        this.videoSize = videoSize;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerIdCard() {
        return customerIdCard;
    }

    public void setCustomerIdCard(String customerIdCard) {
        this.customerIdCard = customerIdCard;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Integer getAuditorId() {
        return auditorId;
    }

    public void setAuditorId(Integer auditorId) {
        this.auditorId = auditorId;
    }

    public String getAuditorName() {
        return auditorName;
    }

    public void setAuditorName(String auditorName) {
        this.auditorName = auditorName;
    }

    public Integer getAction() {
        return action;
    }

    public void setAction(Integer action) {
        this.action = action;
    }

    public String getLatlon() {
        return latlon;
    }

    public void setLatlon(String latlon) {
        this.latlon = latlon;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCarDetailId() {
        return carDetailId;
    }

    public void setCarDetailId(String carDetailId) {
        this.carDetailId = carDetailId;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getCarPrice() {
        return carPrice;
    }

    public void setCarPrice(String carPrice) {
        this.carPrice = carPrice;
    }

    public int getExpectLoanAmount() {
        return expectLoanAmount;
    }

    public void setExpectLoanAmount(int expectLoanAmount) {
        this.expectLoanAmount = expectLoanAmount;
    }
}
