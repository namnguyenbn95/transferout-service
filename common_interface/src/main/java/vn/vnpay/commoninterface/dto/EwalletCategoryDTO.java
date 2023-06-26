package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class EwalletCategoryDTO {
    String categoryID;
    String categoryName;
    String categoryNameEn;
    ArrayList<EwalletPartnerDTO> listEwalletPartnerObj;

}
