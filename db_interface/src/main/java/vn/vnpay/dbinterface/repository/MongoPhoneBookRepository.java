package vn.vnpay.dbinterface.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import vn.vnpay.dbinterface.entity.MongoPhoneBookBenefitEntity;

import java.util.List;

public interface MongoPhoneBookRepository extends MongoRepository<MongoPhoneBookBenefitEntity, String> {
    List<MongoPhoneBookBenefitEntity> findByUsername(String username);

    List<MongoPhoneBookBenefitEntity> findByUsernameAndRemindName(String username, String remindName);

    List<MongoPhoneBookBenefitEntity> findByUsernameAndAccountNoIgnoreCase(String username, String accNo);

    List<MongoPhoneBookBenefitEntity> findByUsernameAndAccountNoIgnoreCaseAndServiceCode(String username, String accNo, String serviceCode);

    List<MongoPhoneBookBenefitEntity> findByUsernameAndAccountNoIgnoreCaseAndBeneBankCodeAndServiceCode(String username, String accNo, String bankCode, String serviceCode);

    List<MongoPhoneBookBenefitEntity> findByUsernameAndIdNo(String username, String idNo);

    List<MongoPhoneBookBenefitEntity> findByUsernameAndCardNo(String username, String cardNo);

    List<MongoPhoneBookBenefitEntity> findByUsernameAndBillProviderCodeAndInvoiceNo(String username, String billProviderCode, String invoiceNo);

    List<MongoPhoneBookBenefitEntity> findByUsernameAndInvoiceNoAndLoaiHinhThu(String username, String invoiceNo, String loaiHinhThu);

    List<MongoPhoneBookBenefitEntity> findByIdIn(List<String> ids);

    List<MongoPhoneBookBenefitEntity> findByUsernameAndVcbToken(String username, String vcbtoken);

    List<MongoPhoneBookBenefitEntity> findByUsernameAndId(String username, String id);

    List<MongoPhoneBookBenefitEntity> findByUsernameAndServiceCodeAndInvoiceNo(String username, String serviceCode, String invoiceNo);

    List<MongoPhoneBookBenefitEntity> findByUsernameAndInvoiceNoAndLoaiHinhThuAndServiceCode(String username, String invoiceNo, String loaiHinhThu, String serviceCode);

    List<MongoPhoneBookBenefitEntity> findByUsernameAndIdNoAndServiceCode(String username, String idNo, String serviceCode);

    List<MongoPhoneBookBenefitEntity> findByUsernameAndVcbTokenAndServiceCode(String username, String vcbtoken, String serviceCode);

    List<MongoPhoneBookBenefitEntity> findByUsernameAndTaxCode(String username, String taxCode);
}
