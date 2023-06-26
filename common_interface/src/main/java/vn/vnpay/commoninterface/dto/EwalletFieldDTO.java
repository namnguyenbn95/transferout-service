package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EwalletFieldDTO {
    private String fieldID;
    private String fieldName;
    private String fieldNameEn;
    private String fieldType;
    //Dữ liệu sẵn có của trường
    private List<String> fieldValue;
    private String fieldCondition;
    private String fieldLength;
}
