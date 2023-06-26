package vn.vnpay.commoninterface.bank.entity.billing;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AutoDebitBillingInfo {
    String providerCode;
    String vcbServiceCode;
    String customerCode;
    String customerName;
    String customerAddress;
    String serviceName;
    String providerName;
    String phoneNumber;
    long sequence;
    String billServiceName;
    String serviceNameEn;
    String billServiceNameEn;
    String providerNameEn;
    String billServiceCode;
    String billProviderCode;
}
