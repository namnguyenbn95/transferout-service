package vn.vnpay.commoninterface.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FutureTransDataDTO {

    // 5078
    private String tellerId;

    // 6800
    private String tellerBranch;

    // FUTUREDATE
    private String transType;

    // 32165457
    private String transId;
    private String batchId;

    private String makerId;
    private String checkerId;

    private DebitAccountDTO debitAccount;

    private CreditAccountDTO creditAccount;

    // Kênh chuyển tiền trong/ngoài hệ thống
    private String creditChannel;

    private double amount;

    private String currency;

    private FeeDTO fee;

    private CronJobDTO cronJob;

    // Email của khách hàng
    private String email;

    // Nội dung giao dịch KH nhập
    private String content;

    private String remark;
}
