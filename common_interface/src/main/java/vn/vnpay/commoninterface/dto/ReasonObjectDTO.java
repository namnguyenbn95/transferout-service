package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReasonObjectDTO {
    private double reasonID;
    private String reasonName;
    private String reasonName_EN;
    private DepartmentInfoDTO departmentInfo;
    private List<String> listFieldCanChange;
    private double feeTSOL = 0.0;
}
