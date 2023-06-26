package vn.vnpay.commoninterface.dto;

import lombok.Builder;

@Builder
public class DebitAccountFutureTransDTO {
    private int cif;
    private String accountNo;
    private String accountAlias;
    private String currency;
    private String accountHolderName;
    private String branch;
    private double amountVND;
    private double originAmount;
    private String accountType;
    private double rate;
    private String bankCode;
    private String bankName;
    private double amountVnd;

    public int getCif() {
        return cif;
    }

    public void setCif(int cif) {
        this.cif = cif;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getAccountAlias() {
        return accountAlias;
    }

    public void setAccountAlias(String accountAlias) {
        this.accountAlias = accountAlias;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public double getAmountVND() {
        return amountVND;
    }

    public void setAmountVND(double amountVND) {
        this.amountVND = amountVND;
    }

    public double getOriginAmount() {
        return originAmount;
    }

    public void setOriginAmount(double originAmount) {
        this.originAmount = originAmount;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public double getAmountVnd() {
        return amountVnd;
    }

    public void setAmountVnd(double amountVnd) {
        this.amountVnd = amountVnd;
    }
}
