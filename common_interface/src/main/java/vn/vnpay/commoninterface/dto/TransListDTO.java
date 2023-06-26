package vn.vnpay.commoninterface.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class TransListDTO {
    private long totalRecords;
    private List<TransactionDTO> listTrans;
}
