package vn.vnpay.dbinterface.entity.batch_salary;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.dbinterface.entity.BatchTransfer;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CacheBatchFileData {
    List<BatchFile> batchFiles;
    List<BatchTransfer> batchTransfers;

    // info init
    BigDecimal totalAmount;
    BigDecimal totalFee;

    // wait confirm
    BigDecimal totalAmountWC;
    BigDecimal totalFeeWC;

    // wait confirm
    BigDecimal totalAmountDone;
    BigDecimal totalFeeDone;
}
