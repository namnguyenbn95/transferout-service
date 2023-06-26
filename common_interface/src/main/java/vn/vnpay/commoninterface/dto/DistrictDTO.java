package vn.vnpay.commoninterface.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
public class DistrictDTO {
    private String title;
    private long id;
    private long provinceId;
    List<OfficesDTO> lstOffice;
    List<ATMsDTO> lstATM;

}
