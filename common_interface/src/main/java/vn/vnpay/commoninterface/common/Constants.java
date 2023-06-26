package vn.vnpay.commoninterface.common;

import java.util.Arrays;
import java.util.List;

public class Constants {

    public static final String CACHE_PREFIX = "vcb_sme_";
    public static final String CACHE_PREFIX_BANK_HUB = "vcb_sme_bank_hub_";
    public static final String SOURCE_MB = "MB";
    public static final String SOURCE_IB = "IB";
    public static final String SOURCE_BANK_HUB = "BANK-HUB";
    public static final List<String> ALLOWED_ACC_STT_DEBIT = Arrays.asList("1", "6", "9");
    public static final List<String> ALLOWED_ACC_STT_CREDIT = Arrays.asList("1", "6", "3");

    public static class UserStatus {
        public static final String PENDING_ACTIVE = "A";
        public static final String ACTIVE = "3";
        public static final String AUTO_LOCKED_A = "X";
        public static final String AUTO_LOCKED_B = "Y";
        public static final String AUTO_LOCKED_C = "Z";
        public static final String AUTO_LOCKED_D = "D";
        public static final String AUTO_LOCKED_E = "E";
        public static final String AUTO_LOCKED_F = "F";
        public static final String LOCKED = "K";
        public static final String CANCELED = "9";
    }

    public static class UserRole {
        public static final String MAKER = "1";
        public static final String CHECKER = "2";
        public static final String ADMIN = "3";
        public static final String ALL = "4";
    }

    public static class ResCode {
        public static final String INFO_00 = "00"; // Thành công
        public static final String INFO_01 = "01"; // Invalid pin format
        public static final String INFO_02 = "02"; // Invalid pin format
        public static final String INFO_03 = "03"; // Nhập sai PIN
        public static final String INFO_04 = "04"; // Nhập sai PIN A lần
        public static final String INFO_05 = "05"; // Nhập sai PIN B lần
        public static final String INFO_06 = "06"; // Nhập sai PIN C lần
        public static final String INFO_07 = "07"; // OTP không hợp lệ (Not found)
        public static final String INFO_08 = "08"; // Phiên đăng nhập không hợp lệ
        public static final String INFO_09 = "09"; // Mật khẩu hết hạn với user trạng thái A
        public static final String INFO_11 = "11"; // OTP hết hiệu lực (Hết hạn)
        public static final String INFO_12 = "12"; // AUTOLOCK_D
        public static final String INFO_13 = "13"; // AUTOLOCK_E
        public static final String INFO_14 = "14"; // AUTOLOCK_F
        public static final String INFO_15 = "15"; // Mật khẩu hết hạn với user trạng thái Active (3)
        public static final String INFO_16 = "16"; // Danh sách quyền giao dịch trống
        public static final String INFO_17 = "17"; // Mã truy cập đã được cài đặt phân quyền thanh toán đi thẳng
        public static final String INFO_18 = "18"; // Mã truy cập đã được cài đặt phân quyền thanh toán ECOM
        public static final String INFO_19 = "19"; // Serivce code does not belong to package code
        public static final String INFO_20 = "20"; // Tài khoản không được cấp quyền giao dịch
        public static final String INFO_21 = "21"; // Mã truy cập không được cấp quyền thực hiện dịch vụ
        public static final String INFO_22 = "22"; // Mã sản phẩm không hợp lệ
        public static final String INFO_23 = "23"; // Tài khoản debit không hợp lệ
        public static final String INFO_24 = "24"; // Mật khẩu cũ và mật khẩu mới trùng nhau
        public static final String INFO_25 = "25"; // Trùng mật khẩu trong n ngày
        public static final String INFO_26 = "26"; // Sai mật khẩu quá 5 lần liên tiếp khi đổi mật khẩu
        public static final String INFO_27 = "27"; // Hết phiên khi nhập sai mật khẩu 5 lần liên tiếp khi đổi password
        public static final String INFO_28 = "28"; // Sai mật khẩu quá MAX_AUTHEN_PIN khi cài đặt xác thực sinh trắc học
        public static final String INFO_29 = "29"; // Tài khoản trích nợ là tài khoản đồng chủ
        public static final String INFO_30 = "30"; // Tài khoản credit bị chặn ghi có
        public static final String INFO_31 = "31"; // Phản hồi client khi ấn nhận thông báo OTT
        public static final String INFO_32 = "32"; // Phản hồi client khi ấn hủy thông báo OTT
        public static final String INFO_33 = "33"; // Chưa được phân quyền sử dụng thông báo OTT
        public static final String INFO_34 = "34"; // Dấu vân tay không hợp lệ
        public static final String INFO_35 = "35"; // Nhận diện khuôn mặt không hợp lệ
        public static final String INFO_36 = "36"; // Mã sản phẩm credit không hợp lệ
        public static final String INFO_37 = "37"; // Trạng thái tài khoản debit không hợp lệ
        public static final String INFO_38 = "38"; // Trạng thái tài khoản credit không hợp lệ
        public static final String INFO_39 = "39"; // Số tài khoản credit không hợp lệ
        public static final String INFO_40 = "40"; // Checker không được cấp quyền duyệt đối với dịch vụ
        public static final String INFO_41 = "41"; // Số thẻ không hợp lệ
        public static final String INFO_42 = "42"; // Số tài khoản không hợp lệ
        public static final String INFO_44 = "44"; // User không được đk ecom
        public static final String INFO_45 = "45"; // Cảnh báo số dư không đủ để thực hiện giao dịch
        public static final String INFO_46 = "46"; // Mã truy cập không được cấp quyền thực hiện chức năng
        public static final String INFO_48 = "48"; // Cif không có mã số thuế
        public static final String INFO_49 = "49"; // Trạng thái không phải 1 6 9
        public static final String INFO_50 = "50"; // Tài khoản bị phong tỏa hoặc hạn chế ghi nợ
        public static final String INFO_51 = "51"; // Tài khoản đồng chủ
        public static final String INFO_52 = "52"; // Lock login IB
        public static final String INFO_53 = "53"; // User đang đăng nhập trên thiết bị khác
        public static final String INFO_54 = "54"; // Soft OTP lock BE
        public static final String INFO_55 = "55"; // Khong duoc dang nhap khi login ecom
        public static final String INFO_56 = "56"; // Remark length > 255 char
        public static final String INFO_57 = "57"; // Nhập thông tin thẻ sai quá số lần cho phép
        public static final String INFO_58 = "58"; // Thông tin thẻ không hợp lệ
        public static final String INFO_59 = "59"; // Card block code không hợp lệ
        public static final String INFO_60 = "60"; // Mã sp thẻ không hợp lệ
        public static final String INFO_61 = "61"; // Ten khach hang nhap khong dung voi bank tra ve
        public static final String INFO_62 = "62"; // Validate số tiền thanh toán thẻ tín dụng
        public static final String INFO_63 = "63"; // Validate số tiền thanh toán thẻ tín dụng
        public static final String INFO_64 = "64"; // Remark chứa ký tự đặc biệt
        public static final String INFO_66 = "66"; // Cif khong hop le
        public static final String INFO_65 = "65"; // UserChange khong thoa ma - thay doi tai khoan mac dinh
        public static final String INFO_67 = "67"; // BIN thẻ không hợp lệ
        public static final String INFO_68 = "68"; // Trạng thái thẻ không hợp lệ
        public static final String INFO_69 = "69"; // Ngày tra cứu số dư bình quân không hợp lệ
        public static final String INFO_70 = "70"; // Cif không có maker
        public static final String INFO_71 = "71"; // Không cập nhật được thông tin hạn mức hiện tại
        public static final String INFO_72 = "72"; // Chức năng cài đặt hạn mức giao dịch chưa được sử dụng
        public static final String INFO_73 = "73"; // Không có hạn mức cấu hình hợp lệ
        public static final String INFO_74 = "74"; // User không có quyền từ chối giao dịch
        public static final String INFO_75 = "75"; // Tài khoản vay không hợp lệ
        public static final String INFO_76 = "76"; // Lỗi cutoff tra cho client thanh toán khoản vay
        public static final String INFO_77 = "77"; // Maker khong duoc tao tra soat cho giao dich khac
        public static final String INFO_78 = "78"; //  Giao dich đã được khởi tạo tra soát
        public static final String INFO_79 = "79"; // Lỗi không trùng số tiền thanh toán khởi tạo khoản vay
        public static final String INFO_80 = "80"; // Lỗi nhập số tiền vượt dư nợ hiện tại
        public static final String INFO_81 = "81"; //  User không đc quyền tạo giao dịch tra soát với giao dịch cũ
        public static final String INFO_82 = "82"; //  data giao dịch không tồn tại
        public static final String INFO_111 = "111"; // Thông tin không hợp lệ
        public static final String INFO_120 = "120"; // Địa chỉ email mới không đúng định dạng
        public static final String INFO_121 = "121"; // Địa chỉ email hiện tại có thông tin không hợp lệ
        public static final String INFO_122 = "122"; // Danh sách chức năng yêu thích quá 2
        public static final String INFO_123 = "123"; // Email mới đã tồn tại DB
        public static final String INFO_130 = "130"; // Cif không có mã số thuế - vi dien tu
        public static final String INFO_131 = "131";    // Gói tài khoản không hợp lệ
        public static final String INFO_132 = "132";    // Không phải KH SME (KH bán lẻ)

        public static final String TSOL_15 = "TSOL_15";    // Mã giao dịch đang được chờ xử lý tra soát
        public static final String TSOL_2 = "TSOL_2";    // Mã giao dịch không tồn tại tra soát

        public static final String USER_100 = "0100"; // Invalid user status
        public static final String USER_101 = "101"; // Auto locked
        public static final String USER_102 = "102"; // Invalid user role
        public static final String USER_103 = "103"; // Thông tin user không hợp lệ
        public static final String USER_104 = "104"; // Trạng thái user inactive (Khóa từ kênh BE)
        public static final String USER_105 = "0105"; // Invalid user status (Can thiệp từ BE)
        public static final String USER_106 = "106"; // Dữ liệu người dùng có thể đã bị chỉnh sửa
        public static final String USER_107 = "107"; // Invalid user role forget pin
        public static final String USER_404 = "404"; // User not found
        public static final String USER_406 = "406"; // User maker status invalid

        public static final String ERROR_95 = "95"; // Input Validation Failed
        public static final String ERROR_96 = "96"; // Exception
        public static final String ERROR_98 = "98"; // Endpoint not supported
        public static final String ERROR_112 = "112"; // số dư không đủ
        public static final String ERROR_113 = "113"; // số tiền gd âm
        public static final String ERROR_114 = "114";
        public static final String ERROR_115 = "115";
        public static final String ERROR_116 = "116";
        public static final String ERROR_117 = "117";
        public static final String ERROR_118 = "118";
        public static final String ERROR_119 = "119";
        public static final String ERROR_120 = "120";
        public static final String ERROR_121 = "121";
        public static final String ERROR_122 = "122";
        public static final String ERROR_0201 = "0201";
        public static final String ERROR_0202 = "0202";
        public static final String ERROR_0203 = "0203";
        public static final String ERROR_0204 = "0204";
        public static final String ERROR_0205 = "0205";
        public static final String ERROR_0206 = "0206";
        public static final String ERROR_0207 = "0207";

        public static final String ERROR_0302 = "0302";
        public static final String ERROR_0303 = "0303";
        public static final String ERROR_0304 = "0304";
        public static final String ERROR_0305 = "0305";
        public static final String ERROR_0320 = "0320";
        public static final String ERROR_0321 = "0321";
        public static final String ERROR_0322 = "0322";
        public static final String ERROR_0323 = "0323";
        public static final String ERROR_0324 = "0324";

        public static final String FAVOR_FUNC_INSERT =
                "FAVOR_FUNC_INSERT"; // Danh sách chức năng yêu thích quá 2
        public static final String REGIS_BILL_RECEIVE =
                "REGIS_BILL_RECEIVE"; // Địa chỉ email hiện tại có thông tin không hợp lệ
        public static final String CANC_BILL_RECEIVE =
                "CANC_BILL_RECEIVE"; // Danh sách chức năng yêu thích quá 2

        // billing code
        public static final String ERROR_1001 = "1001";
        public static final String ERROR_1002 = "1002";
        public static final String ERROR_1003 = "1003"; // ko có sub provider hợp lệ
        public static final String ERROR_1004 = "1004"; // thiếu thông tin amount
        public static final String ERROR_1005 = "1005"; // thiếu thông tin cusName and addInfo
        public static final String ERROR_1006 = "1006"; // thiếu thông tin lưu cache pcm
        public static final String ERROR_1007 = "1007"; // tài khoản nguồn ko phải VND

        public static final String ERROR_NOT_MB = "ERROR_NOT_MB"; // Môi trường nguồn ko phải MB
        public static final String UNLOCKED_LOGIN_WEB =
                "UNLOCKED_LOGIN_WEB"; // Đã mở khóa đăng nhập web

        public static final String ERROR_2001 = "2001"; // tài khoản nguồn ko phải VND
    }

    public static class MessageCode {
        public static final String INFO_00 = "INFO-00"; // Thành công
        public static final String INFO_01 = "INFO-01"; // Invalid pin format
        public static final String INFO_02 = "INFO-02"; // Invalid pin format
        public static final String INFO_03 = "INFO-03"; // Sai PIN liên tiếp nhỏ hơn a lần
        public static final String INFO_04 = "INFO-04"; // Sai PIN liên tiếp a lần trở lên
        public static final String INFO_05 = "INFO-05"; // Sai PIN liên tiếp b lần trở lên
        public static final String INFO_06 = "INFO-06"; // Sai PIN liên tiếp c lần trở lên
        public static final String INFO_07 = "INFO-07"; // OTP không hợp lệ (Not found)
        public static final String INFO_08 = "INFO-08"; // Phiên đăng nhập không hợp lệ
        public static final String INFO_09 = "INFO-09"; // Mật khẩu hết hạn
        public static final String INFO_10 = "INFO-10"; // Đăng nhập thiết bị khác
        public static final String INFO_11 = "INFO-11"; // OTP hết hiệu lực (Hết hạn)
        public static final String INFO_12 = "INFO-12"; // AUTOLOCK_D
        public static final String INFO_13 = "INFO-13"; // AUTOLOCK_E
        public static final String INFO_14 = "INFO-14"; // AUTOLOCK_F
        public static final String INFO_15 = "INFO-15"; // Mật khẩu hết hạn với user trạng thái Active (3)
        public static final String INFO_16 = "INFO-16"; // Danh sách quyền giao dịch trống
        public static final String INFO_17 = "INFO-17"; // Mã truy cập đã được cài đặt phân quyền thanh toán đi thẳng
        public static final String INFO_18 = "INFO-18"; // Mã truy cập đã được cài đặt phân quyền thanh toán ECOM
        public static final String INFO_19 = "INFO-19"; // Serivce code does not belong to package code
        public static final String INFO_20 = "INFO-20"; // Tài khoản không được cấp quyền giao dịch
        public static final String INFO_21 = "INFO-21"; // Mã truy cập không được cấp quyền thực hiện dịch vụ
        public static final String INFO_22 = "INFO-22"; // Mã sản phẩm không hợp lệ
        public static final String INFO_23 = "INFO-23"; // Tài khoản debit không hợp lệ
        public static final String INFO_24 = "INFO-24"; // Mật khẩu cũ và mật khẩu mới trùng nhau
        public static final String INFO_25 = "INFO-25"; // Trùng mật khẩu trong n ngày
        public static final String INFO_26 = "INFO-26"; // Sai mật khẩu quá 5 lần liên tiếp khi đổi mật khẩu
        public static final String INFO_27 = "INFO-27"; // Hết phiên khi nhập sai mật khẩu 5 lần liên tiếp khi đổi password
        public static final String INFO_28 = "INFO-28"; // Sai mật khẩu quá MAX_AUTHEN_PIN khi cài đặt xác thực sinh trắc học
        public static final String INFO_29 = "INFO-29"; // Tài khoản trích nợ là tài khoản đồng chủ
        public static final String INFO_30 = "INFO-30"; // Tài khoản credit bị chặn ghi có
        public static final String INFO_31 = "INFO-31"; // Phản hồi client khi ấn nhận thông báo OTT
        public static final String INFO_32 = "INFO-32"; // Phản hồi client khi ấn hủy thông báo OTT
        public static final String INFO_33 = "INFO-33"; // Chưa được phân quyền sử dụng thông báo OTT
        public static final String INFO_34 = "INFO-34"; // Dấu vân tay không hợp lệ
        public static final String INFO_35 = "INFO-35"; // Nhận diện khuôn mặt không hợp lệ
        public static final String INFO_36 = "INFO-36"; // Mã sản phẩm credit không hợp lệ
        public static final String INFO_37 = "INFO-37"; // Trạng thái tài khoản debit không hợp lệ
        public static final String INFO_38 = "INFO-38"; // Trạng thái tài khoản credit không hợp lệ
        public static final String INFO_39 = "INFO-39"; // Số tài khoản credit không hợp lệ
        public static final String INFO_40 = "INFO-40"; // Checker không được cấp quyền duyệt đối với dịch vụ
        public static final String INFO_41 = "INFO-41"; // Số thẻ không hợp lệ
        public static final String INFO_42 = "INFO-42"; // Số tài khoản không hợp lệ
        public static final String INFO_43 = "INFO-43"; // Đăng ký nhận OTT tự động thành công
        public static final String INFO_44 = "INFO-44"; // User không được đk Ecom
        public static final String INFO_45 = "INFO-45"; // Cảnh báo số dư không đủ để thực hiện giao dịch
        public static final String INFO_46 = "INFO-46"; // Mã truy cập không được cấp quyền thực hiện chức năng
        public static final String INFO_47 = "INFO-47"; // Thực hiện Quên mật khẩu thành công
        public static final String INFO_48 = "INFO-48"; // Cif không có mã số thuế
        public static final String INFO_49 = "INFO-49"; // Trạng thái không phải 1 6 9
        public static final String INFO_50 = "INFO-50"; // Tài khoản bị phong tỏa hoặc hạn chế ghi nợ
        public static final String INFO_51 = "INFO-51"; // Tài khoản đồng chủ
        public static final String INFO_52 = "INFO-52"; // Lock login IB
        public static final String INFO_53 = "INFO-53"; // User đang đăng nhập trên thiết bị khác
        public static final String INFO_55 = "INFO-55"; // Khong duoc dang nhap khi su dung ecom
        public static final String INFO_56 = "INFO-56"; // Remark length > 255 char
        public static final String INFO_57 = "INFO-57"; // Nhập thông tin thẻ sai quá số lần cho phép
        public static final String INFO_58 = "INFO-58"; // Thông tin thẻ không hợp lệ
        public static final String INFO_59 = "INFO-59"; // Card block code không hợp lệ
        public static final String INFO_60 = "INFO-60"; // Mã sp thẻ không hợp lệ
        public static final String INFO_61 = "INFO-61"; // Ten khach hang nhap khong dung voi bank tra ve
        public static final String INFO_62 = "INFO-62"; // Validate số tiền thanh toán thẻ tín dụng
        public static final String INFO_63 = "INFO-63"; // Validate số tiền thanh toán thẻ tín dụng
        public static final String INFO_64 = "INFO-64"; // Remark chứa ký tự đặc biệt
        public static final String INFO_65 = "INFO-65"; // UserChange khong thoa ma - thay doi tai khoan mac dinh
        public static final String INFO_66 = "INFO-66"; // Cif khong hop le
        public static final String INFO_67 = "INFO-67"; // BIN thẻ không hợp lệ
        public static final String INFO_68 = "INFO-68"; // Trạng thái thẻ không hợp lệ
        public static final String INFO_69 = "INFO-69"; // Ngày tra cứu số dư bình quân không hợp lệ
        public static final String INFO_70 = "INFO-70"; // Cif không có maker
        public static final String INFO_71 = "INFO-71"; // Không cập nhật được thông tin hạn mức hiện tại
        public static final String INFO_72 = "INFO-72"; // Chức năng cài đặt hạn mức giao dịch chưa được sử dụng
        public static final String INFO_73 = "INFO-73"; // Không có hạn mức cấu hình hợp lệ
        public static final String INFO_74 = "INFO-74"; // User không có quyền từ chối giao dịch
        public static final String INFO_75 = "INFO-75"; // Tài khoản vay không hợp lệ
        public static final String INFO_76 = "INFO-76"; // Lỗi cutoff tra cho client thanh toán khoản vay
        public static final String INFO_77 = "INFO-77"; // Maker khong duoc tao tra soat cho giao dich khac
        public static final String INFO_78 = "INFO-78"; // Giao dich đã được khởi tạo tra soát
        public static final String INFO_79 = "INFO-79"; // Lỗi không trùng số tiền thanh toán khởi tạo khoản vay
        public static final String INFO_80 = "INFO-80"; // Lỗi nhập số tiền vượt dư nợ hiện tại
        public static final String INFO_81 = "INFO-81"; //  User không đc quyền tạo giao dịch tra soát với giao dịch cũ
        public static final String INFO_82 = "INFO-82"; //  data giao dịch không tồn tại
        public static final String INFO_111 = "INFO-111"; // Thông tin không hợp lệ
        public static final String INFO_120 = "INFO-120"; // Email không đúng định sạng
        public static final String INFO_121 = "INFO-121"; // Email hiện tại có thông tin không hợp lệ
        public static final String INFO_122 = "INFO-122"; // Email mới trùng email hiện tại
        public static final String INFO_123 = "INFO-123"; // Email mới đã tồn tại
        public static final String INFO_130 = "INFO-130"; // ma so thue ko ton tai - vi dien tu
        public static final String INFO_131 = "INFO-131";   // Gói tài khoản không hợp lệ
        public static final String INFO_132 = "INFO-132";   // Không phải KH SME (KH bán lẻ)

        public static final String TSOL_15 = "TSOL-15";    // Mã giao dịch đang được chờ xử lý tra soát
        public static final String TSOL_2 = "TSOL-2";    // Mã giao dịch không tồn tại tra soát

        public static final String USER_100 = "USER-100"; // Invalid user status
        public static final String USER_102 = "USER-102"; // Invalid user role
        public static final String USER_103 = "USER-103"; // Thông tin user không hợp lệ
        public static final String USER_104 = "USER-104"; // Trạng thái user inactive (Khóa từ kênh BE)
        public static final String USER_106 = "USER-106"; // Dữ liệu người dùng có thể đã bị chỉnh sửa
        public static final String USER_107 = "USER-107"; // Invalid user role forget pin
        public static final String USER_404 = "USER-404"; // User not found
        public static final String USER_406 = "USER-406"; // User maker status invalid

        public static final String ERROR_96 = "ERROR-96"; // Exception
        public static final String ERROR_112 = "NOT_BALANCE";
        public static final String ERROR_113 = "NOT_AVAILABLE";
        public static final String ERROR_114 = "CARD_FORM";
        public static final String ERROR_115 = "USER-NOT-FOUND";
        public static final String ERROR_116 = "BUSINESS-NO-NOT-FOUND";
        public static final String ERROR_117 = "MOBILE-NO-NOT-FOUND";
        public static final String ERROR_118 = "CUS-STATUS-INVALID";
        public static final String ERROR_119 = "EMAIL-NO-NOT-FOUND";
        public static final String ERROR_120 = "ACC-NO-NOT-FOUND";
        public static final String ERROR_121 = "CARD-NO-NOT-FOUND";
        public static final String ERROR_122 = "CLIENT-NOT-VALID";

        public static final String CAPTCHA_01 = "CAPTCHA-01"; // Thông tin Captcha không hợp lệ
        public static final String AUTHEN_METHOD_01 = "AUTHEN-METHOD-01";
        public static final String AUTHEN_METHOD_IB_02 = "AUTHEN-METHOD-IB-02";
        public static final String AUTHEN_METHOD_MB_02 = "AUTHEN-METHOD-MB-02";
        public static final String REMIND_NAME_EXIST =
                "REMIND-NAME-EXIST"; // tên gợi nhớ thụ hưởng đã tồn tại
        public static final String ACCOUNT_NO_EXIST =
                "ACCOUNT-NO-EXIST"; // số tài khoản thụ hưởng đã tồn tại
        public static final String CARD_NO_EXIST =
                "CARD-NO-EXIST"; // số tài khoản thụ hưởng đã tồn tại
        public static final String ID_NO_EXIST = "ID-NO-EXIST"; // số giấy tờ đã tồn tại
        public static final String INVOICE_NO_EXIST = "INVOICE-NO-EXIST"; // số giấy tờ đã tồn tại
        public static final String CMND_ISSUE_DATE_EXPIRE = "CMND-ISSUE-DATE-EXPIRE";
        public static final String HC_ISSUE_DATE_EXPIRE = "HC-ISSUE-DATE-EXPIRE";
        public static final String INVALID_DATA = "INVALID-DATA";
        public static final String TAX_CODE_EXIST = "TAX-CODE-EXIST";

        public static final String CANCEL_TRANS_SUCCESS = "CANCEL-TRANS-SUCCESS-00";
        public static final String REJECT_TRANS_SUCCESS = "REJECT-TRANS-SUCCESS-00";
        public static final String DELETE_TRANS_SUCCESS = "DELETE-TRANS-SUCCESS-00";
        public static final String DELETE_TRANS_FAIL = "DELETE-TRANS-FAIL-0201";
        public static final String REJECT_TRANS_FAIL = "REJECT-TRANS-FAIL-0202";
        public static final String CANCEL_TRANS_FAIL = "CANCEL-TRANS-FAIL-0203";
        public static final String CONFIRM_TRANS_FAIL = "CONFIRM-TRANS-FAIL-0204";
        public static final String LIMIT_TRANS_FAIL = "LIMIT-TRANS-FAIL-0205";
        public static final String TRANS_BATCH_SIZE_ZERO = "BATCH-EMPTY-0207";
        public static final String LIMIT_CARD_BATCH_FAIL = "LIMIT-CARD-BATCH-FAIL-0206";
        public static final String FAVOR_FUNC_INSERT = "FAVOR_FUNC_INSERT";
        public static final String REGIS_BILL_RECEI =
                "REGIS-BILL-RECEI-"; // -1  đăng ký nhận kết quả giao dịch  thành công -2 tai chinh -3 phi
        // taichinh
        public static final String CANC_BILL_RECEI =
                "CANC-BILL-RECEI-"; // -0 hủy đăng ký nhận kết quả giao dịch thành công -2 tai chinh -3 phi
        // taichinh
        public static final String EMAIL_SETTING_REGIST =
                "EMAIL-SETTING-REGIST"; //  dang ky email thanh cong
        public static final String EMAIL_SETTING_CHANGE =
                "EMAIL-SETTING-CHANGE"; //  thay doi email thanh cong

        public static final String ERROR_1001 = "ERROR-1001";
        public static final String ERROR_1002 = "ERROR-1002";
        public static final String ERROR_1003 = "ERROR-1003"; // ko có sub provider hợp lệ
        public static final String ERROR_1004 = "ERROR-1004"; // thiếu thông tin amount
        public static final String ERROR_1005 = "ERROR-1005"; // thiếu thông tin cusName and addInfo
        public static final String ERROR_1006 = "ERROR-1006"; // thiếu thông tin pcm bill info
        public static final String ERROR_1007 = "ERROR-1007"; // tài khoản nguồn không phải VNĐ

        public static final String ERROR_NOT_MB = "ERROR-NOT-MB"; // Môi trường nguồn ko phải MB

        public static final String UNLOCK_LOGIN_WEB = "UNLOCK-LOGIN-WEB"; //   Mở khóa đăng nhập web thành công
        public static final String UNLOCKED_LOGIN_WEB = "UNLOCKED-LOGIN-WEB"; //  Lỗi đã mở khóa đăng nhập web
        public static final String LOCKED_LOGIN_WEB = "LOCKED-LOGIN-WEB"; //  Khóa đăng nhập web thành công
        public static final String CANC_FUTUTRE_TRANS_1 = "CANC-FUTUTRE-TRANS-1"; //  Huy lenh tuong lai khong hop le
        public static final String CANC_FUTUTRE_TRANS_2 = "CANC-FUTUTRE-TRANS-2"; //  Huy lenh truoc hh:mm ngay hieu luc

        // SMS
        public static final String SMS_ACTIVE_USER_SUCC = "ACTIVE_USER_SUCC";                   // Kích hoạt lần đầu
        public static final String SMS_ACTIVE_OTHER_DEVICE_SUCC = "ACTIVE_OTHER_DEVICE_SUCC";   // Kích hoạt lại
        public static final String SMS_SET_PERMISSION_USER_MSG = "SET-PERMISSION-USER-MSG";     // Phân quyền truy vấn
        public static final String SMS_SET_PERMISSION_PAY_USER_MSG = "SET-PERMISSION-PAY-USER-MSG"; // Phân quyền giao dịch
        public static final String SMS_SET_ECOM_USER_MSG = "SET-ECOM-USER-MSG";                 // Phân quyền Ecom
        public static final String SMS_SET_DIRECT_USER_MSG = "SET-DIRECT-USER-MSG";             // Phân quyền TTTT
        public static final String UNLOCK_LOGIN_WEB_MSG = "UNLOCK_LOGIN_WEB_MSG"; //khoa dang nhap
        public static final String STOP_SMS_ACTIVE_MSG = "STOP-SMS-ACTIVE-MSG"; //ngung sms chủ động
        public static final String STOP_SMS_ACTIVE_ADMIN_MSG = "STOP-SMS-ACTIVE-AD-MSG";  // //ngung sms chủ động ma quan tri
        public static final String ACTIVATE_SMS_ACTIVE_MSG = "ACTIVATE-SMS-ACTIVE-MSG"; //kich hoat sms chủ động
        public static final String STOP_SMS_BANKING_MSG = "STOP-SMS-BANKING-MSG"; //ngung sms banking
        public static final String STOP_SMS_BANKING_ADMIN_MSG = "STOP-SMS-BANKING-ADMIN-MSG"; //ngung sms banking ma quan tri
        public static final String LIST_AUTO_DEBIT_EMPTY_MSG = "LIST-AUTO-DEBIT-EMPTY-MSG";

        //transfer247
        public static final String MSG_247_ERR = "FASTTRAN";
    }

    public static class MessageDefault {
        public static final String MESAGE_NOT_FOUND_VI =
                "Yêu cầu không thực hiện được trong lúc này. Quý khách vui lòng thực hiện lại sau.";
        public static final String MESAGE_NOT_FOUND_EN =
                "The request cannot be processed at this time. Please try again later.";
    }

    public static class RedisKey {
        public static final String KEY_LOGIN_SESSION = "login_session_";
        public static final String KEY_TEMP_PIN = "temp_pin_";
        public static final String KEY_SETTINGS_ADMIN_LIST_DDACCOUNT = "setttings_admin_list_ddacc";
        public static final String KEY_SETTINGS_ADMIN_LIST_DIRECT_TRANS = "setttings_admin_list_dtrans_";
        public static final String KEY_SETTINGS_ADMIN_LIST_DIRECT_ECOM = "setttings_admin_list_decom_";
        public static final String KEY_SETTINGS_ADMIN_USER_AUTHORITIES = "setttings_admin_user_authorities_";
        public static final String KEY_SETTINGS_ADMIN_CANCEL_DIRECT_TRANS = "setttings_admin_cancel_dtrans_";
        public static final String KEY_LIMIT_CHECKER = "limit_checker_lst";
        public static final String KEY_TXN = "txn_";
        public static final String KEY_SEC_TXN = "sec_txn_";
        public static final String KEY_SOTP_ID = "sotp_id";
        public static final String KEY_REGIST_EMAIL = "authen_regist_email_";
        public static final String KEY_REGIST_BILL_RECEIVE = "regist_code_billing_receive_";
        public static final String KEY_TAX_SO_THUE = "tax_domestic_so_thue";
        public static final String KEY_TAX_REGISTRATION = "tax_registration";
        public static final String KEY_TAX_CUSTOM = "tax_custom";
        public static final String KEY_BHXH_DATA = "bhxh_data";
        public static final String KEY_SEAPORT_DATA = "seaport_data";
        public static final String KEY_SEAPORT_DATA_HCM = "seaport_data_hcm";
        public static final String KEY_ACTION_SMS_BANKING = "action_sms_banking_";
        public static final String KEY_CARD_ACTIVATE = "card_activate_";
        public static final String KEY_CARD_CREDIT_PAYMENT = "card_credit_payment_";
        public static final String KEY_CHANGE_ACCOUNT_PKG = "change_account_pkg";
    }

    public static class SmeCheckType {
        public static final String PIN = "PIN";
        public static final String CHANGE_PASSWORD = "CHANGE_PASSWORD";
        public static final String CONFIRM_PASS_AUTHEN = "CONFIRM_PASS_AUTHEN";
        public static final String ACTIVATE_OTP = "ACTIVATE-OTP";
        public static final String RE_ACTIVATE_OTP = "RE-ACTIVATE-OTP";
        public static final String SMARTOTP_ACTIVE = "SMARTOTP-ACTIVE";
        public static final String CONFIRM_AUTHEN_PIN = "CONFIRM-AUTHEN-PIN";
        public static final String CONFIRM_DEFAULT_ACC_PIN = "CONFIRM_DEFAULT_ACC_PIN";
        public static final String MAX_WRONG_CHANGE_PASSWORD = "MAX_WRONG_CHANGE_PASSWORD";
        public static final String LOCK_LOGIN_WEB = "LOCK-LOGIN-WEB";
        public static final String REGIST_BILL_RECEIVE = "REGIST-BILL-RECEIVE";
        public static final String ACTION_SMS_BANKING = "ACTION-SMS-BANKING";
        public static final String REQUEST_CARD_ACTIVE = "REQUEST_CARD_ACTIVE";
        public static final String FIND_ACCOUNT_SELECT = "FIND-ACCOUNT-SELECT";
    }

    // Phương thức xác thực lúc đăng ký
    public static class AuthenMethod {
        public static final String HARD_TOKEN = "1";
        public static final String SMART_OTP = "2";
    }

    // Phương thức xác thực được sử dụng khi xác thực giao dịch
    public static class AuthenType {
        public static final String HARD_TOKEN = "1";
        public static final String SMART_OTP = "2";
        public static final String SMS = "3";
        public static final String TOUCH_ID = "4";
        public static final String PIN = "5";
    }

    public static class SmeOtpType {
        public static final String ACTIVATE = "ACTIVATE";
        public static final String RE_ACTIVATE = "RE-ACTIVATE";
    }

    public static class SmartOtpDefault {
        public static final String FROM_ACC = "FROM_ACC";
        public static final String TO_ACC = "TO_ACC";
        public static final String AMOUNT = "0";
        public static final Long TXN = 0l;
    }

    public static class ServiceCode {
        public static final String TRANS_IN_VIA_ACCNO_SAME_CIF = "1001"; // Chuyển tiền trong VCB_Cùng chủ
        public static final String TRANS_OUT_VIA_ACCNO = "1002"; // Chuyển tiền ngoài VCB qua STK hoặc Thẻ (Ngày hiện tại)
        public static final String FAST_TRANS_VIA_ACCNO = "1003"; // Chuyển tiền nhanh 24/7 qua STK
        public static final String FAST_TRANS_BILATERAL_ACCNO = "1011"; // Chuyển tiền nhanh 24/7 qua STK song phương
        public static final String CASH_TRANS = "1012"; // Chuyển tiền người hưởng bằng CMND
        public static final String TRANS_IN_VIA_ACCNO_DIFF_CIF = "1005"; // Chuyển tiền trong VCB_Khác chủ
        public static final String TRANS_IN_VIA_ACCNO_FUTURE = "1006"; // Chuyển tiền trong VCB_Tương lai
        public static final String TRANS_IN_VIA_ACCNO_SCHEDULED = "1007"; // Chuyển tiền trong VCB_Định kỳ
        public static final String FAST_TRANS_VIA_CARDNO = "1008"; // Chuyển tiền nhanh 247 qua thẻ
        public static final String TRANS_OUT_VIA_ACCNO_FUTURE = "1009"; // Chuyển tiền ngoài VCB qua STK_Tương lai
        public static final String TRANS_OUT_VIA_ACCNO_SCHEDULED = "1010"; // Chuyển tiền ngoài VCB qua STK_Định kỳ
        // public static final String = "1101";                             // Chuyển tiền theo bảng kê
        public static final String BILL_PAYMENT = "2003"; // Thanh toán hóa đơn
        public static final String AUTODEBIT = "2004"; // Thanh toán hóa đơn định kỳ
        public static final String TOPUP = "2101";                             // Nạp tiền đại lý
        // public static final String = "3001";                             // Quản lý trạng thái lệnh giao dịch
        // public static final String = "3002";                             // Danh sách lệnh bị từ chối
        // public static final String = "3003";                             // Báo cáo phí giao dịch
        // public static final String = "3004";                             // Giao dịch chờ duyệt
        // public static final String = "3005";                             // Bảng kê chuyền tiền chờ duyệt
        // public static final String = "4001";                             // Khóa thẻ
        // public static final String = "4002";                             // Mở khóa thẻ
        // public static final String = "4003";                             // Kích hoạt thẻ
        // public static final String = "4004";                             // Đăng ký thanh toán internet
        // public static final String = "4005";                             // Hủy đăng ký thanh toán trên internet
        // public static final String = "4006";                             // Thanh toán thẻ tín dụng
        public static final String SETTING_USER_AUTHORITY = "5001"; // Quản lý quyền truy cập
        public static final String SETTING_DIRECT_TRANS = "5002"; // Quản lý phân quyền thanh toán trực tiếp
        // public static final String = "5003";                             // Quản lý phương thức xác thực
        // public static final String = "5004";                             // Quản lý thông báo
        public static final String SOCIAL_INSURANCE = "6001";       // Bảo hiểm xã hội
        public static final String DOMESTIC_TAX_VCB = "7001";       // Nộp thuế nội địa trong VCB
        public static final String DOMESTIC_TAX = "7002";           // Nộp thuế nội địa ngoài VCB
        public static final String IMPORT_EXPORT_TAX_VCB = "7003";  // Nộp thuế xuất nhập khẩu trong VCB
        public static final String IMPORT_EXPORT_TAX = "7004";      // Nộp thuế xuất nhập khẩu ngoài VCB
        public static final String REGISTRATION_TAX_VCB = "7005";   // Nộp thuế trước bạ trong VCB
        public static final String REGISTRATION_TAX = "7006";       // Nộp thuế trước bạ ngoài VCB
        public static final String SEAPORT_PAYMENT_VCB = "7007";    // Nộp phí hạ tầng cảng biển trong VCB
        public static final String SEAPORT_PAYMENT = "7008";        // Nộp phí hạ tầng cảng biển ngoài VCB
        public static final String SEAPORT_PAYMENT_HCM = "7009";    // Nộp phí hạ tầng cảng biển HCM
        public static final String TRANS_BATCH_IN = "1101";        // Chuyển tiền theo bảng kê trong VCB
        public static final String TRANS_BATCH_OUT = "1102";        // Chuyển tiền theo bảng kê ngoài VCB
        public static final String TRANS_BATCH_CMND = "1103";        // Chuyển tiền theo bảng kê CMND
        public static final String SETTING_CHANGE_DEFAULT_ACC = "5003";        // Thay đổi tài khoản mặc định
        public static final String CARD_LOCK = "4001";              // Khóa thẻ
        public static final String CARD_UNLOCK = "4002";            // Mở khóa thẻ
        public static final String CARD_ACTIVATE = "4003";          // Kích hoạt thẻ
        public static final String CARD_ONLINE_REG = "4004";        // Đăng ký thanh toán internet
        public static final String CARD_ONLINE_CANCEL = "4005";     // Hủy đăng ký thanh toán trên internet
        public static final String CARD_CREDIT_PAYMENT = "4006";    // Thanh toán thẻ tín dụng
        public static final String AUTODEBIT_REG = "2201"; // Đăng ký dịch vụ VCB Auto Debit
        public static final String AUTODEBIT_CANC = "2202"; // Hủy đăng ký VCB auto Debit
        public static final String IMPORT_EXPORT_FEE_VCB = "7011";  // Nộp phí hải quan trong VCB
        public static final String IMPORT_EXPORT_FEE = "7012";      // Nộp phí hải quan ngoài VCB
        public static final String EWALLET_ACTIVE = "2301"; // Đăng ký liên kết ví điện tử
        public static final String EWALLET_CHANGE_INFO = "2302"; // Thay đổi thông tin liên kết ví điện tử
        public static final String EWALLET_DEACTIVE = "2303"; // Hủy đăng ký liên kết ví điện tử
        public static final String TRANSFER_WALLET = "2304"; // Nạp ví điện tử
        public static final String CHANGE_ACCOUNT_PKG = "8003";     // Thay đổi gói tài khoản
        public static final String CHANGE_ACCOUNT_PKG_INFO = "8004";    // Thay đổi thông tin gói tài khoản
        public static final String QUERY_AVARAGE_BALANCE = "8005";  // Tra cứu số dư bình quân
        public static final String OPEN_ACC_SELECT = "6801"; // Mở tài khoản số chọn
        public static final String REGIST_REMINDER_DEBIT = "8006";  // Đăng ký nhắc nợ
        public static final String STOP_REMINDER_DEBIT = "8007"; // Hủy đăng ký nhắc nợ
        public static final String LOAN_PAYMENT = "9001"; // Thanh toán khoản vay
        public static final String TRANS_CHARGEBACK = "8008"; // lập yêu cầu tra soát
    }

    public static class TransStatus {
        // Luồng GD thành công: 1 => 5 => 10 => 3
        public static final String MAKER_WAIT_CONFIRM = "1"; // Chờ xác nhận lập lệnh
        public static final String FAIL = "2"; // Giao dịch lỗi
        public static final String SUCCESS = "3"; // Thành công, Duyệt lệnh thành công
        public static final String TIMEOUT = "4"; // Giao dịch nghi vấn
        public static final String MAKER_SUCCESS = "5"; // Lập lệnh thành công, chờ duyệt lệnh
        public static final String APPROVE_FAIL = "6"; // Duyệt giao dịch không thành công
        public static final String CANCEL_SUCCESS = "7"; // Huỷ duyệt giao dịch thành công
        public static final String CANCEL_FAIL = "8"; // Huỷ duyệt giao dịch không thành công
        public static final String REJECT_SUCCESS = "9"; // Từ chối duyệt giao dịch thành công
        public static final String CHEKER_WAIT_CONFIRM = "10"; // Chờ xác nhận duyệt lệnh
        public static final String REJECT_FAIL = "11"; // Từ chối duyệt giao dịch thất bại
        public static final String TRANSFER_FAIL = "12"; // Trừ tiền billing lỗi
        public static final String TRANSFER_TIMEOUT = "13"; // Trừ tiền billing timeout
        public static final String REVERT_SUCCESS = "14"; // revert thành công
        public static final String REVERT_FAIL = "15"; // revert lỗi
        public static final String REVERT_TIMEOUT = "16"; // revert lỗi
        public static final String PAY_BILL_TIMEOUT = "17"; // gạch nợ timeout
        public static final String WAIT_CONFIRM = "18"; // duyệt thành công chờ xử lý
        public static final String VALID_FAIL = "20"; // valid đọc lô lỗi
        public static final String VALID_SUCCESS = "21"; // valid đọc lô thành công
    }

    public static class TransPhase {
        public static final int MAKER_INIT = 1;
        public static final int MAKER_CONFIRM = 2;
        public static final int CHECKER_INIT = 3;
        public static final int CHECKER_CONFIRM = 4;
    }

    public static class TransType {
        public static final String FUTUREDATE = "FUTUREDATE";
        public static final String RECURRING = "RECURRING";
        public static final String TRANSFER = "FT";
        public static final String REVERT = "RFT";
    }

    public static class BGBillType {
        public static final String BILLING = "BILLING";
        public static final String AUTODEBIT = "AUTODEBIT";
        public static final String TOPUP = "TOPUP";
    }

    public static class MBServiceType {
        public static final String BILLING = "20";      // Thanh toán hóa đơn
        public static final String TOPUP = "21";        // Nạp tiền điện tử
        public static final String AUTODEBIT = "22";    // THanh toán tự động
    }

    public static class SubServices {
        public static String[] TRANS_CURRENT_DATE = {"1001", "1005", "1002", "1003", "1008", "1004", "1012", "1011"};
        public static String[] TRANS_FUTURE_DATE = {"1007", "1006", "1010", "1009"};
        public static String[] PAYMENT_BILL = {"2003"};
        public static String[] TOP_UP = {"2101", "2304"};
        public static String[] PAYMENT_CREDIT_CARD = {"4006"};
        public static String[] PAYMENT_TAX = {"7001", "7002", "7003", "7004", "7005", "7006", "7011", "7012"};
        public static String[] SOCIAL_INSURANCE = {"6001"};
        public static String[] SEAPORT_FEE = {"7007", "7008", "7009"};
        public static String[] LOAN_PAYMENT = {"9001"};
        public static String[] TRANS_CHARGEBACK = {"8008"};
        public static String[] FAST_TRANS_OUT_247 = {"1003", "1011", "1008"};
    }

    public static class ExcelExportEndpoint {
        public static List<String> exportEndpoints = Arrays.asList("/nonbank-service/v1/export/excel",
                "/nonbank-service/v1/trans-management/fee/report/export-excel",
                "/nonbank-service/v1/export-page/excel",
                "/nonbank-service/v1/transbatch/excel/export-batchfile");
    }

    public static class Currency {
        public static final String VND = "VND";
        public static final String USD = "USD";
        public static final String EUR = "EUR";
    }

    public static class BatchStatus {
        public static final String WAITING_CHECK = "1";
        public static final String CANCEL = "2";
        public static final String CHECKING = "3";
        public static final String CHECK_FAIL = "4";
        public static final String CHECK_SUCCESS = "5";
        public static final String WAIT_MAKER_CONFIRM = "6";
        public static final String CHECKER_CANCEL = "7";
        public static final String CHECKER_REJECT = "8";
        public static final String PUSH_BANK_WAIT = "9";
        public static final String PUSH_BANK_FAIL = "10";
        public static final String PUSH_BANK_SUCCESS = "11";
        public static final String TRANSFER_BANK_SUCCESS = "12";
        public static final String TRANSFER_BANK_HAVE_ERROR = "13";
        public static final String TRANSFER_BANK_ALL_ERROR = "14";
        public static final String MAKER_CONFIRMED = "15";
        public static final String WAIT_CHECKER_CONFIRM = "16";
        public static final String CANCEL_FAIL = "17";
        public static final String TIMEOUT_BANK = "18";
    }

    public static class BatchItemStatus {
        public static final String PENDING_ACCOUNTING = "2"; //chờ hạch toán bên bank
        public static final String APPROVED_ACCOUNTING = "3"; // hạch toán thành công
        public static final String REJECT_ACCOUNTING = "4";//hạch toán thất bại
    }

    public static class EmailReceiveConfig {
        public static final String RECEIVE_NOTHING = "0";
        public static final String RECEIVE_ALL = "1";
        public static final String RECEIVE_FINANCE = "2";
        public static final String RECEIVE_NON_FINANCE = "3";
    }

    public static class PagePrintType {
        public static final String PENDING_TRANSFER_PRESENT = "1";
        public static final String PENDING_TRANSFER_FUTURE = "2";
        public static final String PENDING_BILLING = "3";
        public static final String PENDING_TOPUP = "4";
        public static final String PENDING_PAY_CARD = "5";
        public static final String PENDING_TAX = "6";
        public static final String PENDING_SEA_PORT = "7";
        public static final String PENDING_BHXH = "8";
        public static final String LIST_TRANSFER_PRESENT = "9";
        public static final String LIST_TRANSFER_FUTURE = "10";
        public static final String LIST_BILLING = "11";
        public static final String LIST_TOPUP = "12";
        public static final String LIST_PAY_CARD = "13";
        public static final String LIST_TAX = "14";
        public static final String LIST_SEA_PORT = "15";
        public static final String LIST_BHXH = "16";
        public static final String LIST_CHARGEBACK= "17";
        public static final String LIST_CHARGEBACK_PENDING= "18";
    }

    public static class PcmChannel {
        public static final String IB_CHANNEL = "IBB";
        public static final String MB_CHANNEL = "MBB";
    }

    /**
     * Danh sách trạng thái file chuyển tiền theo lô
     */
    public static class TransBatchStatus {
        public static final String WAITING_CHECK = "1";
        public static final String CANCEL = "2";
        public static final String CHECKING = "3";
        public static final String CHECK_FAIL = "4";
        public static final String CHECK_SUCCESS = "5";
    }

    public static class BatchTransferStatus {
        public static final String WAITING_CHECK = "1"; // cho kiem tra
        public static final String CANCEL = "2"; // da huy
        public static final String CHECKING = "3"; // dang kiem tra
        public static final String CHECK_FAIL = "4"; // da kiem tra - lenh loi
        public static final String CHECK_SUCCESS = "5"; // da kiem tra - lenh hop le
        public static final String WAIT_PROCESS = "6"; // cho xu ly
        public static final String PROCESS_SUCCESS = "7"; // da xu ly
        public static final String REJECT = "9"; // da tu choi
        public static final String MAKER_SUCCESS = "10"; // da xu ly - lap lenh thanh cong
        public static final String REJECT_APART = "12"; // da tu choi 1 phan
        public static final String PROCESS_APART = "13"; // da xu ly mot phan
    }
}
