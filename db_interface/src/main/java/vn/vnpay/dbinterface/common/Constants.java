package vn.vnpay.dbinterface.common;

public class Constants {
    public static final String CACHE_SME_ENDPOINT = "vcb_sme_endpoint";
    public static final String MONGO_LOG_COLL = "sme_log";
    public static final String MONGO_PHONEBOOK_COLL = "sme_phonebook_benefit";

    public static class AccountType {
        public static final String D = "D";     // Thanh toán
        public static final String S = "S";     // Tiết kiệm không kỳ hạn
        public static final String T = "T";     // Tiết kiệm có kỳ hạn
        public static final String L = "L";     // Vay
        public static final String C = "C";     // Thẻ
    }

    public static class MBServiceType {
        public static final String BILLING = "20";      // Thanh toán hóa đơn
        public static final String TOPUP = "21";        // Nạp tiền điện tử
        public static final String AUTODEBIT = "22";    // THanh toán tự động
    }

    public static class Currency {
        public static final String VND = "VND";
        public static final String USD = "USD";
        public static final String EUR = "EUR";
    }
}
