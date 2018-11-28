package com.yunche.android.yunchevideosdk.param;

/**
 * Created by shengxiao on 2018/6/22.
 */

public class LocationParam {
    //String jsonStr = "{\"address\":" + address + ",\"bankName\":" + loanerListItem.getBankName() + ",\"pcAnyChatUserId\":" + dwUserId + ",\"latlon\":" + local + "}";
    private String address;
    private String bankName;
    private int pcAnyChatUserId;
    private String latlon;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public int getPcAnyChatUserId() {
        return pcAnyChatUserId;
    }

    public void setPcAnyChatUserId(int pcAnyChatUserId) {
        this.pcAnyChatUserId = pcAnyChatUserId;
    }

    public String getLatlon() {
        return latlon;
    }

    public void setLatlon(String latlon) {
        this.latlon = latlon;
    }
}
