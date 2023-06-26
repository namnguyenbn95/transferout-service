package vn.vnpay.commoninterface.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class GrBatchDTO {
    private String sRefNo;
    private String sHdrFile;
    //SLI0001190
    private String user;
    private String branch;
    //4500
    private String batchNo;
    // 4500
    private String refNo;
    // 2019334S045005319009
    private int status;
    // 8
    private String folder;
    private String tranDate;
    private String seq;
    // 1190
    private String ftp;
    private String libImport;
    // example: SALIMPORT
    private String libExport; //*
    //example: SALEXPORT
    private String createdDate;
    //example: 71001258678
    private String downloadTryNo;
    // example: 5
    private String worker;
    private String pushDate;
    private String tranDateSS;
    private String date7;
    private String tellId;
    private String accFrm;
    private String amt;
    private String countSlTran2n;
    private String rsvt1;
    private String importStatus;
    private String exportStatus;
    private String step;

}
