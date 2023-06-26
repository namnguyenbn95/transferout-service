package vn.vnpay.commoninterface.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class PayloadEncryptOpenBankingDTO {
    private List<DataObjOpenBankingDTO> data;
    private String encryptedData;
}
