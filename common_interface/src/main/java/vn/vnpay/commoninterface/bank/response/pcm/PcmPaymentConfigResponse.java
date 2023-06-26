package vn.vnpay.commoninterface.bank.response.pcm;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.bank.response.BaseBankResponse;
import vn.vnpay.dbinterface.entity.pcm.BillField;
import vn.vnpay.dbinterface.entity.pcm.SvcIdent;

import java.util.ArrayList;

@Getter
@Setter
public class PcmPaymentConfigResponse extends BaseBankResponse {
    SvcIdent svcIdent;
    String pmtType;
    String isAdhocPmt;
    String pmtRestriction;
    String pmtSeq;
    String minAmtLimitAdhocPay;
    String maxAmtLimitAdhocPay;
    String minAmtLimitBillPay;
    ArrayList<BillField> billField;
}
