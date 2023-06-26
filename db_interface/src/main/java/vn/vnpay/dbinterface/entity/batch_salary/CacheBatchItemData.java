package vn.vnpay.dbinterface.entity.batch_salary;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.vnpay.dbinterface.entity.SmeTrans;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CacheBatchItemData {
    List<BatchItem> batchItems;
    List<SmeTrans> smeTransList;
    Long totalItemFail;
    long batchId;
    BigDecimal totalAmount;
    BigDecimal totalFee;
    String fileName;
    String batchStatus;
}
