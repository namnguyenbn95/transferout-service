package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RewardAccountDTO {
    private String txn_ref;
    private double txn_amount;
    private String cif_id;
    private String txn_type_id;
    private String location_txn;
    private String product_id;
    private String bin_code;
    private String service_id;
    private String card_mcc_id;
    private String provider_id;
    private String period;
    private String period_type;
    private String txn_info;
    private String agency_id;
}
