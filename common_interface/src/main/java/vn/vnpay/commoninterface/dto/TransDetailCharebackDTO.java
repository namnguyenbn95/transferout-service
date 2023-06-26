package vn.vnpay.commoninterface.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TransDetailCharebackDTO {
    private String serviceCode;         //mã loại giao dịch
    private String serviceName;         //tên loại giao dịch
    private String chanel;              //kênh giao dịch
    private String createdDate;
    private String approverDate;
    private String createdUser;
    private String approvedUser;
    private double amount;
    private String ccy;
    private String creditName;          //tên người hưởng
    private String beneBankCode;        //mã NH hưởng
    private String beneBankName;        //tên NH hưởng
    private String remark;
    private String idNo;
    private String issueDate;
    private String issuePlace;
    private String creditAcc;
}
