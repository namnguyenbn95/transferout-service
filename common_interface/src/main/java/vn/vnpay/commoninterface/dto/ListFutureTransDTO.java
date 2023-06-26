package vn.vnpay.commoninterface.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
public class ListFutureTransDTO {
    private String tellerId;
    private String tellerBranch;
    private String transType;
    private String transDate;
    private String responseCode;
    private String responseMessage;
    private String transHostDate;
    private String transPcTime;
    private String transSequence;
    private String transTeller;
    private String batchId;
    private String transId;
    private String userId;
    private DebitAccountFutureTransDTO debitAccount;
    private DebitAccountFutureTransDTO creditAccount;
    private String creditChannel; //1 trong - 2 ngoài
    private BigDecimal amount;
    private String currency;
    private FeeDTO fee;
    private CronJobDTO cronJob;
    private String email;
    private String content;
    private int status;
    private String regDate; // checkerDate
    private String lastChangeDate;
    private String procStatus;  //Trạng thái chuyển tiền gần nhất POSTOK|POSTFAIL|HUY DV|POSTEX|PROCESSING
    private String userName;
    private BigDecimal amountVND;
    private String makerId;
    private String checkerId;
    private String remark;
    private List<TransLogsDTO> transLogs;
    private String confirmType;
    private String roleType;
    private String makeDate;
}
