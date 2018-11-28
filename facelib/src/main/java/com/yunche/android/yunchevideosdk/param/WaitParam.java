package com.yunche.android.yunchevideosdk.param;

/**
 * Created by shengxiao on 2018/6/15.
 */

public class WaitParam {
/**
 * {
 "bankName": "中国工商银行杭州城站支行",     // 银行名称
 "userId": 1,                            // PC端：系统登录用户ID； APP端：customerId（所选客户ID）；
 "type": 1,                              // 终端类型： 1-PC端; 2-APP端;
 "anyChatUserId": 1,                     // anyChat系统返回的userId
 "orderId": 1805161656579917727,         // 客户的订单ID（仅APP端有；PC端无，不传；）
 }
 */
    private String bankName;
    private Long userId;
    private int type;
    private String anyChatUserId;
    private String orderId;
    private String bankPeriodPrincipal;
    private String bankId;

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getBankPeriodPrincipal() {
        return bankPeriodPrincipal;
    }

    public void setBankPeriodPrincipal(String bankPeriodPrincipal) {
        this.bankPeriodPrincipal = bankPeriodPrincipal;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAnyChatUserId() {
        return anyChatUserId;
    }

    public void setAnyChatUserId(String anyChatUserId) {
        this.anyChatUserId = anyChatUserId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
