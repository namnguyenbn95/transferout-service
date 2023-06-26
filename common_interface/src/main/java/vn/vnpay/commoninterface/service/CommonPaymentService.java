package vn.vnpay.commoninterface.service;

import com.google.gson.Gson;
import feign.RetryableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.vnpay.commoninterface.bank.entity.billing.AutoDebitBillingInfo;
import vn.vnpay.commoninterface.bank.entity.billing.AutoDebitCustomerInfo;
import vn.vnpay.commoninterface.bank.request.TransferInBankRequest;
import vn.vnpay.commoninterface.bank.request.pcm.*;
import vn.vnpay.commoninterface.bank.response.BaseBankResponse;
import vn.vnpay.commoninterface.bank.response.TransferInBankResponse;
import vn.vnpay.commoninterface.bank.response.pcm.BLGWPayResponse;
import vn.vnpay.commoninterface.bank.response.pcm.ListAutoDebitUserResponse;
import vn.vnpay.commoninterface.bank.response.pcm.PcmBillInfoInqResponse;
import vn.vnpay.commoninterface.bank.response.pcm.PcmCustomerBillingInqResponse;
import vn.vnpay.commoninterface.common.Constants;
import vn.vnpay.commoninterface.dto.DebitAccountDTO;
import vn.vnpay.commoninterface.dto.TransactionMetaDataDTO;
import vn.vnpay.commoninterface.feignclient.DigiCoreTransClient;
import vn.vnpay.commoninterface.feignclient.MiscClient;
import vn.vnpay.commoninterface.feignclient.VCBServiceGWClient;
import vn.vnpay.commoninterface.request.BaseClientRequest;
import vn.vnpay.commoninterface.request.BaseConfirmRq;
import vn.vnpay.commoninterface.response.BaseClientResponse;
import vn.vnpay.commoninterface.response.BaseTransactionResponse;
import vn.vnpay.dbinterface.common.CommonUtils;
import vn.vnpay.dbinterface.entity.BillProvider;
import vn.vnpay.dbinterface.entity.MbService;
import vn.vnpay.dbinterface.entity.MbServiceType;
import vn.vnpay.dbinterface.entity.SmeTrans;
import vn.vnpay.dbinterface.entity.pcm.*;
import vn.vnpay.dbinterface.repository.BillProviderRepository;
import vn.vnpay.dbinterface.repository.MbServiceRepository;
import vn.vnpay.dbinterface.repository.MbServiceTypeRepository;
import vn.vnpay.dbinterface.repository.SmeTransRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CommonPaymentService {
    @Autowired
    VCBServiceGWClient vcbServiceGWClient;

    @Autowired
    MiscClient miscClient;

    @Autowired
    Gson gson;

    @Autowired
    MbServiceRepository mbServiceRepository;

    @Autowired
    private CommonService commonService;

    @Autowired
    private DigiCoreTransClient digiCoreTransClient;

    @Autowired
    private SmeTransRepository smeTransRepository;

    @Autowired
    MbServiceTypeRepository mbServiceTypeRepository;

    @Autowired
    BillProviderRepository billProviderRepository;

    @Autowired
    RedisCacheService cacheService;

    /**
     * exac pay bill via billing gateway
     *
     * @param baseResp
     * @param cachedSmeTrans
     * @param hostDate
     * @return
     */
    public BaseClientResponse execBLGWPayBill(BaseClientResponse baseResp,
                                              SmeTrans cachedSmeTrans,
                                              String hostDate,
                                              BaseConfirmRq rq) {
        String metaStr = cachedSmeTrans.getMetadata();
        TransactionMetaDataDTO metaData = gson.fromJson(metaStr, TransactionMetaDataDTO.class);

        String teller = metaData.getTellerId();
        int seq = (int) (cachedSmeTrans.getId() % 100000);
        String pcTime = CommonUtils.TimeUtils.format("HHmmss", new Date());
        metaData.setSequence(seq);
        cachedSmeTrans.setTeller(teller);
        cachedSmeTrans.setMetadata(gson.toJson(metaData));
        // thực hiện chuyển khoản vào tk ghi có dịch vụ
        try {
            TransferInBankRequest transfer =
                    TransferInBankRequest.builder()
                            .content(cachedSmeTrans.getTranxContent())
                            .creditAccount(metaData.getCreditAccount())
                            .debitAccount(metaData.getDebitAccount())
                            .fee(metaData.getFee())
                            .originAmount(metaData.getOriginAmount())
                            .amountVND(metaData.getAmountVND())
                            .originCurrency(cachedSmeTrans.getCcy())
                            .pcTime(pcTime)
                            .remark(cachedSmeTrans.getTranxRemark().replace("$1", String.valueOf(cachedSmeTrans.getId())))
                            .sequence(seq)
                            .tellerBranch(6800)
                            .tellerId(teller)
                            .txnType(Constants.TransType.TRANSFER)
                            .advice(metaData.getCreditAdviceFlag())
                            .content(cachedSmeTrans.getTranxRemark().replace("$1", String.valueOf(cachedSmeTrans.getId())))
                            .build();

            if (transfer.getRemark().length() > 254) {
                transfer.setRemark(transfer.getRemark().substring(0, 254));
            }
            if (transfer.getContent().length() > 254) {
                transfer.setContent(transfer.getContent().substring(0, 254));
            }

            // Gọi bank hạch toán
            TransferInBankResponse transferInBankResponse = digiCoreTransClient.transferIn(transfer);
            if (!"0".equals(transferInBankResponse.getResponseStatus().getResCode())) {
                // Cập nhật trạng thái giao dịch
                String code = "0169";
                if (transferInBankResponse.getResponseStatus().getIsTimeout()) {
                    cachedSmeTrans.setStatus(Constants.TransStatus.TRANSFER_TIMEOUT);
                    cachedSmeTrans.setTranxNote("Trừ tiền timed out");
                } else {
                    code = transferInBankResponse.getResponseStatus().getResCode();
                    cachedSmeTrans.setStatus(Constants.TransStatus.TRANSFER_FAIL);
                    cachedSmeTrans.setTranxNote("Trừ tiền lỗi");
                }
                baseResp.setCode(code);
                baseResp.setMessage(commonService.getMessage("BLGW-PAY-" + code, rq.getLang()));
                cachedSmeTrans.setResBankCode(transferInBankResponse.getResponseStatus().getResCode());
                cachedSmeTrans.setResBankDesc(transferInBankResponse.getResponseStatus().getResMessage());
                cacheService.pushTxn(rq, rq.getTranToken(), cachedSmeTrans);
                return baseResp;
            }

            BLGWPayRequest payRequest =
                    BLGWPayRequest.builder()
                            .creditAccountNo(cachedSmeTrans.getToAcc())
                            .debitAccountNo(cachedSmeTrans.getFromAcc())
                            .debitAccountName(metaData.getDebitAccount().getAccountHolderName())
                            .amount(new BigDecimal(cachedSmeTrans.getAmount()))
                            .customerCode(metaData.getCusRefCode())
                            .vcbCode(metaData.getVcbCode())
                            .tellerId(teller)
                            .sequence(seq)
                            .branch(6800)
                            .pcTime(CommonUtils.TimeUtils.getNow("HHmmss"))
                            .hostDate(hostDate)
                            .build();

            BLGWPayResponse payResponse = vcbServiceGWClient.blgwPay(payRequest);
            if (!payResponse.getResponseStatus().getResCode().equals("0")) {
                // Cập nhật trạng thái giao dịch
                String code = "0169";
                if (payResponse.getResponseStatus().getIsTimeout()) {
                    cachedSmeTrans.setStatus(Constants.TransStatus.PAY_BILL_TIMEOUT);
                    cachedSmeTrans.setTranxNote("Gạch nợ hóa đơn timed out");
                } else {
                    BigDecimal seqRf = smeTransRepository.getTranSeqNextVal();
                    transfer.setTxnType(Constants.TransType.REVERT);
                    transfer.setOrgSequence(transfer.getSequence());
                    transfer.setSequence((int) (seqRf.longValue() % 100000));
                    transferInBankResponse = digiCoreTransClient.transferIn(transfer);
                    if (!"0".equals(transferInBankResponse.getResponseStatus().getResCode())) {
                        // Cập nhật trạng thái giao dịch
                        code = "0169";
                        if (transferInBankResponse.getResponseStatus().getIsTimeout()) {
                            cachedSmeTrans.setStatus(Constants.TransStatus.REVERT_TIMEOUT);
                            cachedSmeTrans.setTranxNote("Hoàn tiền timed out");
                        } else {
                            code = transferInBankResponse.getResponseStatus().getResCode();
                            cachedSmeTrans.setStatus(Constants.TransStatus.REVERT_FAIL);
                            cachedSmeTrans.setTranxNote("Hoàn tiền lỗi");
                        }
                        baseResp.setCode(code);
                        baseResp.setMessage(commonService.getMessage("BLGW-PAY-" + code, rq.getLang()));
                        cachedSmeTrans.setResBankCode(transferInBankResponse.getResponseStatus().getResCode());
                        cachedSmeTrans.setResBankDesc(transferInBankResponse.getResponseStatus().getResMessage());
                    } else {
                        code = "0170";
                        cachedSmeTrans.setStatus(Constants.TransStatus.REVERT_SUCCESS);
                        cachedSmeTrans.setTranxNote("Hoàn tiền thành công");
                    }
                }
                baseResp.setCode(code);
                baseResp.setMessage(commonService.getMessage("BLGW-PAY-" + code, rq.getLang()));
                cachedSmeTrans.setResBankCode(payResponse.getResponseStatus().getResCode());
                cachedSmeTrans.setResBankDesc(payResponse.getResponseStatus().getResMessage());
            } else {
                // Update trạng thái giao dich
                cachedSmeTrans.setStatus(Constants.TransStatus.SUCCESS);
                cachedSmeTrans.setTranxNote("Thành công");
                cachedSmeTrans.setResBankCode(payResponse.getResponseStatus().getResCode());
                cachedSmeTrans.setResBankDesc(payResponse.getResponseStatus().getResMessage());

                BaseTransactionResponse data = new BaseTransactionResponse();
                data.setTranDate(
                        vn.vnpay.commoninterface.common.CommonUtils.formatLocalDateTime(LocalDateTime.now()));
                baseResp.setData(data);
            }
        } catch (RetryableException ex) {
            log.info("Error: ", ex);
            cachedSmeTrans.setStatus(Constants.TransStatus.TIMEOUT);
            cachedSmeTrans.setTranxNote("Giao dịch timed out");
            baseResp.setCode(Constants.ResCode.ERROR_96);
            baseResp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, rq.getLang()));
        } catch (Exception ex) {
            log.info("Error: ", ex);
            cachedSmeTrans.setStatus(Constants.TransStatus.FAIL);
            cachedSmeTrans.setTranxNote("Giao dịch lỗi");
            baseResp.setCode(Constants.ResCode.ERROR_96);
            baseResp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, rq.getLang()));
        }
        cacheService.pushTxn(rq, rq.getTranToken(), cachedSmeTrans);
        smeTransRepository.save(cachedSmeTrans);
        return baseResp;
    }

    /**
     * Pay bill of pcm
     *
     * @param baseResp
     * @param cachedSmeTrans
     * @param hostDate
     * @return
     */
    public BaseClientResponse execPcmPayBill(BaseClientResponse baseResp,
                                             SmeTrans cachedSmeTrans,
                                             String hostDate,
                                             BaseConfirmRq rq) {
        String metaStr = cachedSmeTrans.getMetadata();
        TransactionMetaDataDTO metaData = gson.fromJson(metaStr, TransactionMetaDataDTO.class);

        MbService mbService =
                mbServiceRepository.findByServiceCode(cachedSmeTrans.getTranxType()).get();

        String teller = mbService.getTellerId();
        int seq = (int) (cachedSmeTrans.getId() % 100000);
        metaData.setSequence(seq);
        cachedSmeTrans.setTeller(teller);
        cachedSmeTrans.setMetadata(gson.toJson(metaData));

        // build pcm pay bill request
        String channelId = "MB".equals(rq.getSource())
                ? Constants.PcmChannel.MB_CHANNEL : Constants.PcmChannel.IB_CHANNEL;
        PcmPaymentAddRequest pcmReq = new PcmPaymentAddRequest(channelId);
        pcmReq.setCusRefCode(metaData.getCusRefCode());
        pcmReq.setCifNo(String.valueOf(metaData.getDebitAccount().getCif()));
        PmtInfo pmtInfo = new PmtInfo();

        RemitInfo remitInfo = new RemitInfo();
        HostInfo hostInfo = new HostInfo();
        hostInfo.setTellerId(teller);
        hostInfo.setSeqNo(String.valueOf(seq));
        hostInfo.setHostDt(hostDate.split("T")[0].replace("-", ""));
        hostInfo.setPcTime(CommonUtils.TimeUtils.getNow("HHmmss"));
        hostInfo.setBranchNo("06800");
        remitInfo.setHostInfo(hostInfo);
        PmtInstruction pmtInstruction = new PmtInstruction();
        pmtInstruction.setFromAcct(PmtAccount.builder()
                .acctNo(metaData.getDebitAccount().getAccountNo())
                .acctType(metaData.getDebitAccount().getAccountType())
                .acctName(metaData.getDebitAccount().getAccountHolderName())
                .curCode(metaData.getDebitAccount().getCurrency()).build());
        pmtInstruction.setPmtAccRefNum("");
        pmtInstruction.setBillerCreditAccRefNum("");
        pmtInstruction.setToAcct(PmtAccount.builder()
                .acctNo("")
                .acctType("")
                .acctName("")
                .curCode("").build());
        pmtInstruction.setFromAmt(PmtAmount.builder()
                .curAmt(new BillAmt(new BigDecimal(metaData.getAmountVND()), "VND"))
                .lceAmt(new BigDecimal(metaData.getAmountVND()))
                .exchangeRate(BigDecimal.ONE).build());
        pmtInstruction.setToAmt(PmtAmount.builder()
                .curAmt(new BillAmt(new BigDecimal(metaData.getAmountVND()), "VND"))
                .lceAmt(new BigDecimal(metaData.getAmountVND()))
                .exchangeRate(BigDecimal.ONE).build());
        pmtInstruction.setPmtMethod("A");
        pmtInstruction.setPayerInstructions("");
        pmtInstruction.setRemark(cachedSmeTrans.getTranxRemark());
        String internalRefNo = hostInfo.toString();
        pmtInstruction.setInternalRefNo(internalRefNo);
        pmtInstruction.setTrnDt("");
        pmtInstruction.setDepositSlipNumber("");
        FeeChargeAlloc fee = new FeeChargeAlloc();
        fee.setChargeRegulation("PAYER");
        PmtFeeInfo feeA = new PmtFeeInfo();
        feeA.setFeeType("FEE");
        feeA.setFeeIncomeGL("430101009");
        feeA.setCurAmt(
                new BillAmt(BigDecimal.ZERO, "VND"));
        fee.setFee(
                new ArrayList<PmtFeeInfo>() {
                    {
                        add(feeA);
                    }
                });
        PmtTaxInfo tax = new PmtTaxInfo();
        tax.setTaxType("VAT");
        tax.setTaxIncomeGL("280202002");
        tax.setCurAmt(
                new BillAmt(BigDecimal.ZERO, "VND"));
        fee.setTaxInfo(
                new ArrayList<PmtTaxInfo>() {
                    {
                        add(tax);
                    }
                });
        pmtInstruction.setFeeChargeAlloc(fee);
        remitInfo.setPmtInstruction(pmtInstruction);
        pmtInfo.setRemitInfo(remitInfo);
        BillRef billRef = new BillRef();
        billRef.setSvcIdent(metaData.getSvcIdent());
        ArrayList<BillRec> list = metaData.getBillRec();
        list.stream()
                .forEach(
                        br -> {
                            ArrayList<BillField> nbf = new ArrayList<>();
                            br.getBillInfo().getBillField().stream()
                                    .forEach(
                                            f -> {
                                                nbf.add(BillField.builder()
                                                        .id(f.getId())
                                                        .value(f.getValue()).build());
                                            });
                            br.getBillInfo().setBillField(nbf);
                        });
        billRef.setBillRec(list);
        pmtInfo.setBillRef(billRef);
        pcmReq.setPmtInfo(pmtInfo);
        pcmReq.setAddlFieldMetaData(metaData.getAddlFieldMetaData());

        try {
            PcmBillInfoInqResponse res = vcbServiceGWClient.pcmPayBill(pcmReq);
            if (!res.getResponseStatus().getResCode().equals("0")) {
                // Update trạng thái giao dich
                String code = "0169";
                if (res.getResponseStatus().getIsTimeout()) {
                    cachedSmeTrans.setStatus(Constants.TransStatus.TIMEOUT);
                    cachedSmeTrans.setTranxNote("Giao dịch timed out");
                } else {
                    code = res.getResponseStatus().getResCode();
                    List<String> timeoutCode = new ArrayList<String>() {{
                        add("8297");
                        add("8298");
                        add("8299");
                    }};
                    if (timeoutCode.contains(code)) {
                        cachedSmeTrans.setStatus(Constants.TransStatus.TIMEOUT);
                        cachedSmeTrans.setTranxNote("Giao dịch timed out");
                    } else {
                        cachedSmeTrans.setStatus(Constants.TransStatus.FAIL);
                        cachedSmeTrans.setTranxNote("Giao dịch lỗi");
                    }
                }
                baseResp.setCode(code);
                baseResp.setMessage(commonService.getMessage("PCM-PAY-" + code, rq.getLang()));
                cachedSmeTrans.setResBankCode(res.getResponseStatus().getResCode());
                cachedSmeTrans.setResBankDesc(res.getResponseStatus().getResMessage());
            } else {
                // Update trạng thái giao dich
                cachedSmeTrans.setStatus(Constants.TransStatus.SUCCESS);
                cachedSmeTrans.setTranxNote("Thành công");
                cachedSmeTrans.setResBankCode(res.getResponseStatus().getResCode());
                cachedSmeTrans.setResBankDesc(res.getResponseStatus().getResMessage());
                baseResp.setData(res.getPmtRec());
            }
        } catch (RetryableException ex) {
            log.info("Error: ", ex);
            cachedSmeTrans.setStatus(Constants.TransStatus.TIMEOUT);
            cachedSmeTrans.setTranxNote("Giao dịch timed out");
            baseResp.setCode(Constants.ResCode.ERROR_96);
            baseResp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, rq.getLang()));
        } catch (Exception ex) {
            log.info("Error: ", ex);
            cachedSmeTrans.setStatus(Constants.TransStatus.FAIL);
            cachedSmeTrans.setTranxNote("Giao dịch lỗi");
            baseResp.setCode(Constants.ResCode.ERROR_96);
            baseResp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, rq.getLang()));
        }
        cacheService.pushTxn(rq, rq.getTranToken(), cachedSmeTrans);
        smeTransRepository.save(cachedSmeTrans);
        return baseResp;
    }

    /**
     * Register auto debit via billing gateway
     *
     * @param baseResp
     * @param cachedSmeTrans
     * @param rq
     * @return
     */
    public BaseClientResponse blgwRegPayer(BaseClientResponse baseResp,
                                           SmeTrans cachedSmeTrans,
                                           BaseConfirmRq rq) {
        String metaStr = cachedSmeTrans.getMetadata();
        TransactionMetaDataDTO metaData = gson.fromJson(metaStr, TransactionMetaDataDTO.class);

        MbService mbService =
                mbServiceRepository.findByServiceCode(cachedSmeTrans.getTranxType()).get();

        String teller = mbService.getTellerId();
        int seq = (int) (cachedSmeTrans.getId() % 100000);

        RegisterAutoDebitUserRequest reqData = new RegisterAutoDebitUserRequest();
        reqData.setBillingInfo(metaData.getAdBillingInfo());
        reqData.setAccountData(metaData.getDebitAccount());
        reqData.setTellerId(metaData.getTellerId());
        metaData.setSequence(seq);
        cachedSmeTrans.setTeller(teller);
        cachedSmeTrans.setMetadata(gson.toJson(metaData));

        try {
            BaseBankResponse res = miscClient.regBLWPayer(reqData);
            if (!res.getResponseStatus().getResCode().equals("0")) {
                // Update trạng thái giao dich
                String code = "0169";
                if (res.getResponseStatus().getIsTimeout()) {
                    cachedSmeTrans.setStatus(Constants.TransStatus.TIMEOUT);
                    cachedSmeTrans.setTranxNote("Giao dịch timed out");
                } else {
                    cachedSmeTrans.setStatus(Constants.TransStatus.FAIL);
                    cachedSmeTrans.setTranxNote("Giao dịch lỗi");
                }
                baseResp.setCode(code);
                baseResp.setMessage(commonService.getMessage("PCM-PAY-" + code, rq.getLang()));
                cachedSmeTrans.setResBankCode(res.getResponseStatus().getResCode());
                cachedSmeTrans.setResBankDesc(res.getResponseStatus().getResMessage());
            } else {
                // Update trạng thái giao dich
                cachedSmeTrans.setStatus(Constants.TransStatus.SUCCESS);
                cachedSmeTrans.setTranxNote("Thành công");
                cachedSmeTrans.setResBankCode(res.getResponseStatus().getResCode());
                cachedSmeTrans.setResBankDesc(res.getResponseStatus().getResMessage());
            }
        } catch (RetryableException ex) {
            log.info("Error: ", ex);
            cachedSmeTrans.setStatus(Constants.TransStatus.TIMEOUT);
            cachedSmeTrans.setTranxNote("Giao dịch timed out");
            baseResp.setCode(Constants.ResCode.ERROR_96);
            baseResp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, rq.getLang()));
        } catch (Exception ex) {
            log.info("Error: ", ex);
            cachedSmeTrans.setStatus(Constants.TransStatus.FAIL);
            cachedSmeTrans.setTranxNote("Giao dịch lỗi");
            baseResp.setCode(Constants.ResCode.ERROR_96);
            baseResp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, rq.getLang()));
        }
        cacheService.pushTxn(rq, rq.getTranToken(), cachedSmeTrans);
        smeTransRepository.save(cachedSmeTrans);
        return baseResp;
    }

    /**
     * Register auto debit via pcm
     *
     * @param baseResp
     * @param cachedSmeTrans
     * @param rq
     * @return
     */
    public BaseClientResponse pcmCustomerBilling(BaseClientResponse baseResp,
                                                 SmeTrans cachedSmeTrans,
                                                 BaseConfirmRq rq,
                                                 String channelId) {
        String metaStr = cachedSmeTrans.getMetadata();
        TransactionMetaDataDTO metaData = gson.fromJson(metaStr, TransactionMetaDataDTO.class);

        MbService mbService =
                mbServiceRepository.findByServiceCode(cachedSmeTrans.getTranxType()).get();
        String teller = mbService.getTellerId();
        int seq = (int) (cachedSmeTrans.getId() % 100000);
        metaData.setSequence(seq);
        cachedSmeTrans.setTeller(teller);
        cachedSmeTrans.setMetadata(gson.toJson(metaData));
        PCMCustomerBillingRequest reqData = new PCMCustomerBillingRequest();

        reqData.setMsgHdr(new MsgHdr(String.valueOf(System.currentTimeMillis()), channelId));
        reqData.setCifNo(String.valueOf(metaData.getCif()));
        reqData.setSvcIdent(metaData.getSvcIdent());
        reqData.setRegistrationType("R");
        reqData.setCusRefCode(metaData.getCusRefCode());
        reqData.setFullName(metaData.getCusName());
        reqData.setBillPmtStatusCode("Y");
        reqData.setPayerBank("W");
        reqData.setPayerAcctID(
                PayerAcctId.builder()
                        .acctID(AcctId.builder()
                                .acctNo(metaData.getDebitAccount().getAccountNo())
                                .acctType(metaData.getDebitAccount().getAccountType())
                                .curCode(metaData.getDebitAccount().getCurrency())
                                .build())
                        .bankInfo(new BankInfo())
                        .build());
        reqData.setPayerInfo(metaData.getPayerInfo());
        ArrayList<BillField> nbf = new ArrayList<>();
        if (metaData.getBillField() != null)
            metaData.getBillField().stream().forEach(bf -> {
                nbf.add(BillField.builder()
                        .id(bf.getId())
                        .value(bf.getValue()).build());
            });
        reqData.setBillField(nbf);
        reqData.setPmtType("W");
        reqData.setPrepayDays("0");

        try {
            BaseBankResponse res = vcbServiceGWClient.customerBilling(reqData);
            if (!res.getResponseStatus().getResCode().equals("0")) {
                // Update trạng thái giao dich
                String code = "0169";
                if (res.getResponseStatus().getIsTimeout()) {
                    cachedSmeTrans.setStatus(Constants.TransStatus.TIMEOUT);
                    cachedSmeTrans.setTranxNote("Giao dịch timed out");
                } else {
                    cachedSmeTrans.setStatus(Constants.TransStatus.FAIL);
                    cachedSmeTrans.setTranxNote("Giao dịch lỗi");
                }
                baseResp.setCode(code);
                baseResp.setMessage(commonService.getMessage("PCM-PAY-" + code, rq.getLang()));
                cachedSmeTrans.setResBankCode(res.getResponseStatus().getResCode());
                cachedSmeTrans.setResBankDesc(res.getResponseStatus().getResMessage());
            } else {
                // Update trạng thái giao dich
                cachedSmeTrans.setStatus(Constants.TransStatus.SUCCESS);
                cachedSmeTrans.setTranxNote("Thành công");
                cachedSmeTrans.setResBankCode(res.getResponseStatus().getResCode());
                cachedSmeTrans.setResBankDesc(res.getResponseStatus().getResMessage());
            }
        } catch (RetryableException ex) {
            log.info("Error: ", ex);
            cachedSmeTrans.setStatus(Constants.TransStatus.TIMEOUT);
            cachedSmeTrans.setTranxNote("Giao dịch timed out");
            baseResp.setCode(Constants.ResCode.ERROR_96);
            baseResp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, rq.getLang()));
        } catch (Exception ex) {
            log.info("Error: ", ex);
            cachedSmeTrans.setStatus(Constants.TransStatus.FAIL);
            cachedSmeTrans.setTranxNote("Giao dịch lỗi");
            baseResp.setCode(Constants.ResCode.ERROR_96);
            baseResp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, rq.getLang()));
        }
        cacheService.pushTxn(rq, rq.getTranToken(), cachedSmeTrans);
        smeTransRepository.save(cachedSmeTrans);
        return baseResp;
    }

    public ArrayList<AutoDebitCustomerInfo> listAutoDebitViaCif(String cif,
                                                                BaseClientRequest rq,
                                                                String channelId) {
        ArrayList<AutoDebitCustomerInfo> listReturn = new ArrayList<>();

        // Get from billing gateway
        try {
            ListAutoDebitUserRequest req = new ListAutoDebitUserRequest();
            req.setCif(Long.parseLong(cif));
            ListAutoDebitUserResponse response = miscClient.listAutoDebit(req);
            for (AutoDebitCustomerInfo auto : response.getAutoDebitUserList()) {
                AutoDebitBillingInfo bill = auto.getBillingInfo();
                auto.setCompanyCode("SME");
                ArrayList<BillProvider> providersList =
                        billProviderRepository.findAllByProviderAutoDebitOrderByOrderNumber(bill.getProviderCode());
                Optional<BillProvider> billProvider = providersList.stream()
                        .filter(v -> ("1").equals(v.getIsAutoDebitIB()) || ("1").equals(v.getIsAutoDebitMB())).findFirst();
                if (billProvider.isPresent()) {
                    log.info("Bill provider of providerAutoDebit ({}) ", billProvider.get());
                    bill.setBillServiceCode(billProvider.get().getBillServiceCode());
                    bill.setBillProviderCode(billProvider.get().getBillProviderCode());
                } else {
                    log.info("Bill provider of providerAutoDebit ({}) Not Present ", bill.getProviderCode());
                }
            }
            listReturn.addAll(response.getAutoDebitUserList());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        //"pcm"
        try {
            List<MbServiceType> listServiceType = mbServiceTypeRepository.findByStatus("1");
            String pcmCat =
                    listServiceType.stream()
                            .filter(
                                    t ->
                                            vn.vnpay.dbinterface.common.Constants.MBServiceType.AUTODEBIT
                                                    .equals(t.getServicetypeCode()))
                            .findFirst()
                            .get()
                            .getPcmCode();

            PCMCustomerBillingRequest reqPcm = new PCMCustomerBillingRequest();
            reqPcm.setMsgHdr(new MsgHdr(String.valueOf(System.currentTimeMillis()), channelId));
            reqPcm.setCifNo(cif);
            reqPcm.setSvcIdent(SvcIdent.builder()
                    .svcCategory(pcmCat).build());
            PcmCustomerBillingInqResponse pcmRes = vcbServiceGWClient.customerBillingInq(reqPcm);
            if (pcmRes.getResponseStatus().getIsSuccess()
                    && pcmRes.getPayerRegRec() != null) {
                pcmRes.getPayerRegRec().removeIf(
                        a -> "De-Registered".equals(a.getStatus()));
                if (!pcmRes.getPayerRegRec().isEmpty()) {
                    pcmRes.getPayerRegRec().stream()
                            .forEach(rec -> {
                                SvcIdent svc = rec.getSvcIdent();

                                AutoDebitCustomerInfo ue = new AutoDebitCustomerInfo();
                                ue.setAccountData(
                                        DebitAccountDTO.builder()
                                                .accountNo(rec.getPayerAcctID().getAcctID().getAcctNo())
                                                .currency(rec.getPayerAcctID().getAcctID().getCurCode())
                                                .cif(Integer.parseInt(rec.getCifNo()))
                                                .build());
                                AutoDebitBillingInfo bi = new AutoDebitBillingInfo();
                                bi.setVcbServiceCode(svc.getSvcType());
                                bi.setProviderCode(svc.getBillerId());
                                //bi.setVcbServiceCode(svc.getBillerDivisionId());
                                bi.setBillServiceCode(svc.getSvcType());
                                bi.setBillProviderCode(svc.getBillerId());
                                bi.setCustomerCode(rec.getCusRefCode());
                                bi.setCustomerName(rec.getFullName());
                                bi.setServiceName(
                                        "vi".equalsIgnoreCase(rq.getLang())
                                                ? svc.getBillerDivisionVnName()
                                                : svc.getBillerDivisionName());
                                try {
                                    bi.setCustomerAddress(
                                            rec.getPayerInfo().getPersonInfo().getContact().get(0).getPostAddr().getAddr1());
                                } catch (Exception e) {

                                }
                                ue.setBillingInfo(bi);
                                ue.setCompanyCode("PCM");
                                ue.setPayerRegRec(rec);
                                listReturn.add(ue);
                            });
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return listReturn;
    }


    /**
     * Register auto debit via pcm
     *
     * @param baseResp
     * @param adUserInfo
     * @param rq
     * @return
     */
    public BaseClientResponse cancelCustomerBilling(BaseClientResponse baseResp,
                                                    AutoDebitCustomerInfo adUserInfo,
                                                    BaseClientRequest rq,
                                                    String channelId) {
        PCMCustomerBillingRequest reqData = new PCMCustomerBillingRequest();

        reqData.setMsgHdr(new MsgHdr(String.valueOf(System.currentTimeMillis()), channelId));
        reqData.setCifNo(String.valueOf(adUserInfo.getAccountData().getCif()));
        reqData.setSvcIdent(adUserInfo.getPayerRegRec().getSvcIdent());
        reqData.setRegistrationType("D");
        reqData.setCusRefCode(adUserInfo.getPayerRegRec().getCusRefCode());
        reqData.setFullName(adUserInfo.getPayerRegRec().getFullName());
        reqData.setBillPmtStatusCode("Y");
        reqData.setPayerBank("W");
        reqData.setPayerAcctID(adUserInfo.getPayerRegRec().getPayerAcctID());
        reqData.setPayerInfo(adUserInfo.getPayerRegRec().getPayerInfo());
        reqData.setPmtType("W");
        reqData.setPrepayDays("0");

        BaseBankResponse res = vcbServiceGWClient.customerBilling(reqData);
        if (!res.getResponseStatus().getResCode().equals("0")) {
            // Update trạng thái giao dich
            String code = "0169";
            baseResp.setCode(code);
            baseResp.setMessage(commonService.getMessage("PCM-PAY-" + code, rq.getLang()));
        }
        return baseResp;
    }

    /**
     * Register auto debit via pcm
     *
     * @param baseResp
     * @param adUserInfo
     * @param rq
     * @return
     */
    public BaseClientResponse unRegBLGWAutoDebit(BaseClientResponse baseResp,
                                                 AutoDebitCustomerInfo adUserInfo,
                                                 BaseClientRequest rq,
                                                 String channelId) {
        PCMCustomerBillingRequest reqData = new PCMCustomerBillingRequest();

        reqData.setMsgHdr(new MsgHdr(String.valueOf(System.currentTimeMillis()), channelId));
        reqData.setCifNo(String.valueOf(adUserInfo.getAccountData().getCif()));
        reqData.setSvcIdent(adUserInfo.getPayerRegRec().getSvcIdent());
        reqData.setRegistrationType("D");
        reqData.setCusRefCode(adUserInfo.getPayerRegRec().getCusRefCode());
        reqData.setFullName(adUserInfo.getPayerRegRec().getFullName());
        reqData.setBillPmtStatusCode("Y");
        reqData.setPayerBank("W");
        reqData.setPayerAcctID(adUserInfo.getPayerRegRec().getPayerAcctID());
        reqData.setPayerInfo(adUserInfo.getPayerRegRec().getPayerInfo());
        reqData.setPmtType("W");
        reqData.setPrepayDays("0");

        BaseBankResponse res = vcbServiceGWClient.customerBilling(reqData);
        if (!res.getResponseStatus().getResCode().equals("0")) {
            // Update trạng thái giao dich
            String code = "0169";
            baseResp.setCode(code);
            baseResp.setMessage(commonService.getMessage("PCM-PAY-" + code, rq.getLang()));
        }
        return baseResp;
    }
}
