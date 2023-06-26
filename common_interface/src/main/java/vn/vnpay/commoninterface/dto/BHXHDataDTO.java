package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BHXHDataDTO {
    private String loaiHinhThu;
    private String cityCode;
    private String receiver;
    private String sender;
    private String benAccount;
    private String benName;
    private String benCurrency;
    private String benBranch;
    private String currency;
    private String si;
    private String siName;
    private String hiName;
    private String maCoQuanThu;
    private String siCode;
    private String siCodeName;
    private String siNumber;
    private String hiID;
    private String monthAmount;
    private double amount;
    private String siNumberName;
    private String phone;
    private String email;
    private String siID;
    private String irCode;
    private String dayActive;
    private String content;
    private TransactionDataDTO transaction;
}
