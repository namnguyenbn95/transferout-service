package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FeeComboDTO {
    String feeCode;
    //Mã gói phí

    String description;
    //Tên gói phí

    int vipAccTotal;
    //Số lượng TK số đẹp

    int feeDiscountRate;
    //Tỷ lệ miễn/ giảm phí QLTK

    int serviceChrgPlan;
    //Mã phí trên core

    int cardTDQTTotal;
    //Số lượng thẻ TDQT được miễn phí

    int cardGNQTTotal;
    //Số lượng thẻ GNQT được miễn phí

    int waiveFeeLimit;
    //Mức số dư bình quân phải duy trì

    int amount;
    //Mức phí duy trì gói

    String digiPromoCode;
    //Mã phí trên Digi

    List<VipAccTypeDTO> vipAccType;
}
