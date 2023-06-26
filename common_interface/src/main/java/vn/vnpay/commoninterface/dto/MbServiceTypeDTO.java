package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.dbinterface.entity.MbService;

import java.util.List;

@Getter
@Setter
public class MbServiceTypeDTO {
    private String serviceTypeCode;
    private String serviceTypeName;
    private List<MbService> lstService;
    private List<MbService> lstBillService;
}
