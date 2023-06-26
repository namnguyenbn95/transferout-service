package vn.vnpay.dbinterface.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import vn.vnpay.dbinterface.common.Constants;
import vn.vnpay.dbinterface.entity.pcm.BillField;

import java.util.ArrayList;
import java.util.Date;

@Data
@Builder
@Document(collection = Constants.MONGO_PHONEBOOK_COLL)
public class MongoPhoneBookBenefitEntity {
    @Id
    private String id;
    private String username;
    private String beneName;
    private String accountNo;
    private String serviceCode;
    private String serviceName;
    private String beneBankCode;
    private String beneBankName;
    private String remindName;
    private String cityCode;
    private String cityName;
    private String branchCode;
    private String branchName;
    private String cardNo;
    private String idType;
    private String idNo;
    private String idIssueDate;
    private String idIssuePlace;
    private String idIssuePlaceCode;
    private String fullName;
    private String vcbToken;
    private Date createdTime;
    // for bill
    private String billServiceCode;
    private String billServiceName;
    private String billServiceNameEn;
    private String billProviderCode;
    private String billProviderName;
    private String billProviderNameEn;
    private String billSubProviderCode;
    private String billSubProviderName;
    private String billSubProviderNameEn;
    private String invoiceNo;
    private String expandData;
    private ArrayList<BillField> billFields;

    // NSNN
    private String taxCode; // MST; Số sổ BHXH; Mã đối tượng BHXH; Số thẻ BHYT
    private String loaiHinhThu;  // BHXH
    private String labelTextVn;
    private String labelTextEn;
    private String maDoiTuong;
    private String tenLoaiHinhThu;
    private String tenLoaiHinhThuEn;
    private String siName; // ten co quan BHXH
    private String siCode; // ma co quan BHXH
    private String transferVia; // phan biet ck qua the hay qua stk (1 ck qua stk, 2 ck qua the)
    private Long soThangGiaHanBhxh; // số tháng gia hạn bhxh
}
