package vn.vnpay.commoninterface.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.vnpay.dbinterface.entity.LimitTransEntity;

import java.util.List;

@Getter
@Setter
@Builder
public class TransNonbankMetaDataDTO {
    private List<LimitTransEntity> listLimitTransDTO;
}
