package com.yunche.android.yunchevideosdk.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 创建人 ： shengxiao
 * 创建时间 ：2018/3/8
 * 类描述 ：
 * 备注 ：
 */

public class LoanerListItem implements Parcelable {

    private boolean select;//是否已选

    private String id;
    private String customer;
    private String salesman;
    private String partner;
    private String idCard;
    private String mobile;
    private String orderGmtCreate;
    private String taskStatus;
    private String currentTask;
    private int overdueNum;
    private int repayStatus;
    private boolean canCreditSupplement;//是否可以征信追加
    private boolean canUpdateLoanApply;//是否可以编辑主共贷人互换和金融方案

    private String creditGmtCreate;
    private String loanGmtCreate;
    private String bank;
    private String departmentName;
    private String loanAmount;
    private String bankPeriodPrincipal;
    private String signRate;
    private int carType;
    private String licensePlateNumber;
    private String loanTime;
    private String downPaymentMoney;
    private int supplementType;
    private String supplementTypeText;
    private String supplementContent;
    private String supplementStartTime;
    private long supplementOrderId;
    private String rejectReason;

    private String carName;
    private String carPrice;
    private String loanRatio;
    private String customerId;
    private String bankId;
    private String bankName;
    private String carDetailId;


    private long salesmanId;
    private long partnerId;
    private String taskTypeText;
    private boolean canVideoFace;

    private String dataFlowTypeText;
    private Long dataFlowId;
    private int taskType;
    private String taskKey;
    private String bankRepayImpRecordId;
    private String processId;
    private String visitDoorId;
    private String refund_amount;
    private String refund_end_time;
    private String refund_start_time;
    private String refund_id;

    //customerId customer  bankName
    // bankId  bankPeriodPrincipal  id  partner carDetailId  carName
    //carPrice  idCard

    public String getRefund_id() {
        return refund_id;
    }

    public void setRefund_id(String refund_id) {
        this.refund_id = refund_id;
    }

    public String getRefund_amount() {
        return refund_amount;
    }

    public void setRefund_amount(String refund_amount) {
        this.refund_amount = refund_amount;
    }

    public String getRefund_end_time() {
        return refund_end_time;
    }

    public void setRefund_end_time(String refund_end_time) {
        this.refund_end_time = refund_end_time;
    }

    public String getRefund_start_time() {
        return refund_start_time;
    }

    public void setRefund_start_time(String refund_start_time) {
        this.refund_start_time = refund_start_time;
    }

    public String getVisitDoorId() {
        return visitDoorId;
    }

    public void setVisitDoorId(String visitDoorId) {
        this.visitDoorId = visitDoorId;
    }

    public String getBankRepayImpRecordId() {
        return bankRepayImpRecordId;
    }

    public void setBankRepayImpRecordId(String bankRepayImpRecordId) {
        this.bankRepayImpRecordId = bankRepayImpRecordId;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getSalesman() {
        return salesman;
    }

    public void setSalesman(String salesman) {
        this.salesman = salesman;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getOrderGmtCreate() {
        return orderGmtCreate;
    }

    public void setOrderGmtCreate(String orderGmtCreate) {
        this.orderGmtCreate = orderGmtCreate;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getCurrentTask() {
        return currentTask;
    }

    public void setCurrentTask(String currentTask) {
        this.currentTask = currentTask;
    }

    public int getOverdueNum() {
        return overdueNum;
    }

    public void setOverdueNum(int overdueNum) {
        this.overdueNum = overdueNum;
    }

    public int getRepayStatus() {
        return repayStatus;
    }

    public void setRepayStatus(int repayStatus) {
        this.repayStatus = repayStatus;
    }

    public boolean isCanCreditSupplement() {
        return canCreditSupplement;
    }

    public void setCanCreditSupplement(boolean canCreditSupplement) {
        this.canCreditSupplement = canCreditSupplement;
    }

    public boolean isCanUpdateLoanApply() {
        return canUpdateLoanApply;
    }

    public void setCanUpdateLoanApply(boolean canUpdateLoanApply) {
        this.canUpdateLoanApply = canUpdateLoanApply;
    }

    public String getCreditGmtCreate() {
        return creditGmtCreate;
    }

    public void setCreditGmtCreate(String creditGmtCreate) {
        this.creditGmtCreate = creditGmtCreate;
    }

    public String getLoanGmtCreate() {
        return loanGmtCreate;
    }

    public void setLoanGmtCreate(String loanGmtCreate) {
        this.loanGmtCreate = loanGmtCreate;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(String loanAmount) {
        this.loanAmount = loanAmount;
    }

    public String getBankPeriodPrincipal() {
        return bankPeriodPrincipal;
    }

    public void setBankPeriodPrincipal(String bankPeriodPrincipal) {
        this.bankPeriodPrincipal = bankPeriodPrincipal;
    }

    public String getSignRate() {
        return signRate;
    }

    public void setSignRate(String signRate) {
        this.signRate = signRate;
    }

    public int getCarType() {
        return carType;
    }

    public void setCarType(int carType) {
        this.carType = carType;
    }

    public String getLicensePlateNumber() {
        return licensePlateNumber;
    }

    public void setLicensePlateNumber(String licensePlateNumber) {
        this.licensePlateNumber = licensePlateNumber;
    }

    public String getLoanTime() {
        return loanTime;
    }

    public void setLoanTime(String loanTime) {
        this.loanTime = loanTime;
    }

    public String getDownPaymentMoney() {
        return downPaymentMoney;
    }

    public void setDownPaymentMoney(String downPaymentMoney) {
        this.downPaymentMoney = downPaymentMoney;
    }

    public int getSupplementType() {
        return supplementType;
    }

    public void setSupplementType(int supplementType) {
        this.supplementType = supplementType;
    }

    public String getSupplementTypeText() {
        return supplementTypeText;
    }

    public void setSupplementTypeText(String supplementTypeText) {
        this.supplementTypeText = supplementTypeText;
    }

    public String getSupplementContent() {
        return supplementContent;
    }

    public void setSupplementContent(String supplementContent) {
        this.supplementContent = supplementContent;
    }

    public String getSupplementStartTime() {
        return supplementStartTime;
    }

    public void setSupplementStartTime(String supplementStartTime) {
        this.supplementStartTime = supplementStartTime;
    }

    public long getSupplementOrderId() {
        return supplementOrderId;
    }

    public void setSupplementOrderId(long supplementOrderId) {
        this.supplementOrderId = supplementOrderId;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
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

    public String getLoanRatio() {
        return loanRatio;
    }

    public void setLoanRatio(String loanRatio) {
        this.loanRatio = loanRatio;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getCarDetailId() {
        return carDetailId;
    }

    public void setCarDetailId(String carDetailId) {
        this.carDetailId = carDetailId;
    }

    public long getSalesmanId() {
        return salesmanId;
    }

    public void setSalesmanId(long salesmanId) {
        this.salesmanId = salesmanId;
    }

    public long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(long partnerId) {
        this.partnerId = partnerId;
    }

    public String getTaskTypeText() {
        return taskTypeText;
    }

    public void setTaskTypeText(String taskTypeText) {
        this.taskTypeText = taskTypeText;
    }

    public boolean isCanVideoFace() {
        return canVideoFace;
    }

    public void setCanVideoFace(boolean canVideoFace) {
        this.canVideoFace = canVideoFace;
    }

    public String getDataFlowTypeText() {
        return dataFlowTypeText;
    }

    public void setDataFlowTypeText(String dataFlowTypeText) {
        this.dataFlowTypeText = dataFlowTypeText;
    }

    public Long getDataFlowId() {
        return dataFlowId;
    }

    public void setDataFlowId(Long dataFlowId) {
        this.dataFlowId = dataFlowId;
    }

    public int getTaskType() {
        return taskType;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }

    public String getTaskKey() {
        return taskKey;
    }

    public void setTaskKey(String taskKey) {
        this.taskKey = taskKey;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.select ? (byte) 1 : (byte) 0);
        dest.writeString(this.id);
        dest.writeString(this.customer);
        dest.writeString(this.salesman);
        dest.writeString(this.partner);
        dest.writeString(this.idCard);
        dest.writeString(this.mobile);
        dest.writeString(this.orderGmtCreate);
        dest.writeString(this.taskStatus);
        dest.writeString(this.currentTask);
        dest.writeInt(this.overdueNum);
        dest.writeInt(this.repayStatus);
        dest.writeByte(this.canCreditSupplement ? (byte) 1 : (byte) 0);
        dest.writeByte(this.canUpdateLoanApply ? (byte) 1 : (byte) 0);
        dest.writeString(this.creditGmtCreate);
        dest.writeString(this.loanGmtCreate);
        dest.writeString(this.bank);
        dest.writeString(this.departmentName);
        dest.writeString(this.loanAmount);
        dest.writeString(this.bankPeriodPrincipal);
        dest.writeString(this.signRate);
        dest.writeInt(this.carType);
        dest.writeString(this.licensePlateNumber);
        dest.writeString(this.loanTime);
        dest.writeString(this.downPaymentMoney);
        dest.writeInt(this.supplementType);
        dest.writeString(this.supplementTypeText);
        dest.writeString(this.supplementContent);
        dest.writeString(this.supplementStartTime);
        dest.writeLong(this.supplementOrderId);
        dest.writeString(this.rejectReason);
        dest.writeString(this.carName);
        dest.writeString(this.carPrice);
        dest.writeString(this.loanRatio);
        dest.writeString(this.customerId);
        dest.writeString(this.bankId);
        dest.writeString(this.bankName);
        dest.writeString(this.carDetailId);
        dest.writeLong(this.salesmanId);
        dest.writeLong(this.partnerId);
        dest.writeString(this.taskTypeText);
        dest.writeByte(this.canVideoFace ? (byte) 1 : (byte) 0);
        dest.writeString(this.dataFlowTypeText);
        dest.writeValue(this.dataFlowId);
        dest.writeInt(this.taskType);
        dest.writeString(this.taskKey);
        dest.writeString(this.bankRepayImpRecordId);
        dest.writeString(this.processId);
        dest.writeString(this.visitDoorId);
        dest.writeString(this.refund_amount);
        dest.writeString(this.refund_end_time);
        dest.writeString(this.refund_start_time);
        dest.writeString(this.refund_id);
    }

    public LoanerListItem() {
    }

    protected LoanerListItem(Parcel in) {
        this.select = in.readByte() != 0;
        this.id = in.readString();
        this.customer = in.readString();
        this.salesman = in.readString();
        this.partner = in.readString();
        this.idCard = in.readString();
        this.mobile = in.readString();
        this.orderGmtCreate = in.readString();
        this.taskStatus = in.readString();
        this.currentTask = in.readString();
        this.overdueNum = in.readInt();
        this.repayStatus = in.readInt();
        this.canCreditSupplement = in.readByte() != 0;
        this.canUpdateLoanApply = in.readByte() != 0;
        this.creditGmtCreate = in.readString();
        this.loanGmtCreate = in.readString();
        this.bank = in.readString();
        this.departmentName = in.readString();
        this.loanAmount = in.readString();
        this.bankPeriodPrincipal = in.readString();
        this.signRate = in.readString();
        this.carType = in.readInt();
        this.licensePlateNumber = in.readString();
        this.loanTime = in.readString();
        this.downPaymentMoney = in.readString();
        this.supplementType = in.readInt();
        this.supplementTypeText = in.readString();
        this.supplementContent = in.readString();
        this.supplementStartTime = in.readString();
        this.supplementOrderId = in.readLong();
        this.rejectReason = in.readString();
        this.carName = in.readString();
        this.carPrice = in.readString();
        this.loanRatio = in.readString();
        this.customerId = in.readString();
        this.bankId = in.readString();
        this.bankName = in.readString();
        this.carDetailId = in.readString();
        this.salesmanId = in.readLong();
        this.partnerId = in.readLong();
        this.taskTypeText = in.readString();
        this.canVideoFace = in.readByte() != 0;
        this.dataFlowTypeText = in.readString();
        this.dataFlowId = (Long) in.readValue(Long.class.getClassLoader());
        this.taskType = in.readInt();
        this.taskKey = in.readString();
        this.bankRepayImpRecordId = in.readString();
        this.processId = in.readString();
        this.visitDoorId = in.readString();
        this.refund_amount = in.readString();
        this.refund_end_time = in.readString();
        this.refund_start_time = in.readString();
        this.refund_id = in.readString();
    }

    public static final Creator<LoanerListItem> CREATOR = new Creator<LoanerListItem>() {
        @Override
        public LoanerListItem createFromParcel(Parcel source) {
            return new LoanerListItem(source);
        }

        @Override
        public LoanerListItem[] newArray(int size) {
            return new LoanerListItem[size];
        }
    };
}
