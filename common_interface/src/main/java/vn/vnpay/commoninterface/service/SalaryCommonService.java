package vn.vnpay.commoninterface.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.vnpay.dbinterface.common.Constants;
import vn.vnpay.dbinterface.entity.batch_salary.BatchFile;
import vn.vnpay.dbinterface.entity.batch_salary.BatchItem;
import vn.vnpay.dbinterface.repository.SalaryBatchFileRepository;
import vn.vnpay.dbinterface.repository.SalaryBatchItemRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SalaryCommonService {

    @Autowired
    SalaryBatchFileRepository salaryBatchFileRepository;

    @Autowired
    SalaryBatchItemRepository salaryBatchItemRepository;

    /**
     * insert new batch file
     *
     * @param username
     * @param batchName
     * @param batchLocalFile
     * @param cifNo
     * @return
     */
    public String insertNewbatch(String username,
                                 String batchName,
                                 String batchLocalFile,
                                 String cifNo) {
        BatchFile batchFile = new BatchFile();
        batchFile.setFileName(batchName);
        batchFile.setFilePath(batchLocalFile);
        batchFile.setCcy(Constants.Currency.VND);
        batchFile.setCreatedUser(username);
        batchFile.setCreatedDate(LocalDateTime.now());
        batchFile.setCifNo(cifNo);
        batchFile.setTotalFee(BigDecimal.ZERO);
        batchFile.setTotalAmount(BigDecimal.ZERO);
        batchFile.setTotalAmountIn(BigDecimal.ZERO);
        batchFile.setTotalAmountOut(BigDecimal.ZERO);
        batchFile.setTotalAmountID(BigDecimal.ZERO);
        batchFile.setStatus("1");
        salaryBatchFileRepository.saveAndFlush(batchFile);
        String batchId = String.valueOf(batchFile.getId());
        return batchId;
    }

    /**
     * Get all
     *
     * @param fromDate
     * @param toDate
     * @param cifNo
     * @return
     */
    public List<BatchFile> getBatchFiles(LocalDateTime fromDate,
                                         LocalDateTime toDate,
                                         String cifNo) {
        return salaryBatchFileRepository.findByCreatedDateBetweenAndCifNo(fromDate,
                toDate,
                cifNo);
    }

    /**
     * get batch detail via id
     *
     * @param batchId
     * @return
     */
    public BatchFile getBatchInfo(long batchId) {
        Optional<BatchFile> opt = salaryBatchFileRepository.findById(batchId);
        return opt.isPresent() ? opt.get() : null;
    }

    /**
     * get all item from batch
     *
     * @param batchId
     * @return
     */
    public List<BatchItem> getBatchItems(long batchId) {
        return salaryBatchItemRepository.findByBatchId(batchId);
    }

    /**
     * get batch item detail via id
     *
     * @param batchItemId
     * @return
     */
    public BatchItem getBatchItemDetail(long batchItemId) {
        Optional<BatchItem> opt = salaryBatchItemRepository.findById(batchItemId);
        if (opt.isPresent()) {
            return opt.get();
        }
        return null;
    }

    /**
     * @param newBatchFile
     * @return
     */
    public void saveBatchFile(BatchFile newBatchFile) {
        salaryBatchFileRepository.save(newBatchFile);
    }

    /**
     * @param newBatchItem
     */
    public void saveBatchItem(BatchItem newBatchItem) {
        salaryBatchItemRepository.save(newBatchItem);
    }

    /**
     * Get item of batch in list id
     *
     * @param items
     * @param batchId
     * @return
     */
    public List<BatchItem> getSomeItem(ArrayList<Long> items,
                                       Long batchId) {
        return salaryBatchItemRepository.findAllByIdInAndBatchId(items, batchId);
    }

    /**
     * delete item via id
     *
     * @param id
     */
    public void deleteItem(Long id) {
        salaryBatchItemRepository.deleteById(id);
    }

    /**
     * check all item in batch are success
     *
     * @param batchId
     * @return
     */
    public boolean batchSuccess(Long batchId) {
        List<BatchItem> ifs = salaryBatchItemRepository.findByBatchIdAndStatus(batchId, "0");
        return (ifs == null || ifs.isEmpty());
    }

    /**
     * Get all batch in list id
     *
     * @param ids
     * @return
     */
    public List<BatchFile> getSomeBatch(ArrayList<Long> ids) {
        return salaryBatchFileRepository.findByIdIn(ids);
    }
}
