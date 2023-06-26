package vn.vnpay.commoninterface.common;

import com.google.common.hash.Hashing;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import vn.vnpay.commoninterface.config.BeanUtils;
import vn.vnpay.commoninterface.service.CommonService;
import vn.vnpay.commons.security.CustomerUtils;
import vn.vnpay.commons.security.exception.ValidationException;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.sql.Timestamp;
import java.text.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class CommonUtils {
    // Mảng các ký tự có dấu
    private static char[] SOURCE_CHARACTERS = {'À', 'Á', 'Â', 'Ã', 'È', 'É',
            'Ê', 'Ì', 'Í', 'Ò', 'Ó', 'Ô', 'Õ', 'Ù', 'Ú', 'Ý', 'à', 'á', 'â',
            'ã', 'è', 'é', 'ê', 'ì', 'í', 'ò', 'ó', 'ô', 'õ', 'ù', 'ú', 'ý',
            'Ă', 'ă', 'Đ', 'đ', 'Ĩ', 'ĩ', 'Ũ', 'ũ', 'Ơ', 'ơ', 'Ư', 'ư', 'Ạ',
            'ạ', 'Ả', 'ả', 'Ấ', 'ấ', 'Ầ', 'ầ', 'Ẩ', 'ẩ', 'Ẫ', 'ẫ', 'Ậ', 'ậ',
            'Ắ', 'ắ', 'Ằ', 'ằ', 'Ẳ', 'ẳ', 'Ẵ', 'ẵ', 'Ặ', 'ặ', 'Ẹ', 'ẹ', 'Ẻ',
            'ẻ', 'Ẽ', 'ẽ', 'Ế', 'ế', 'Ề', 'ề', 'Ể', 'ể', 'Ễ', 'ễ', 'Ệ', 'ệ',
            'Ỉ', 'ỉ', 'Ị', 'ị', 'Ọ', 'ọ', 'Ỏ', 'ỏ', 'Ố', 'ố', 'Ồ', 'ồ', 'Ổ',
            'ổ', 'Ỗ', 'ỗ', 'Ộ', 'ộ', 'Ớ', 'ớ', 'Ờ', 'ờ', 'Ở', 'ở', 'Ỡ', 'ỡ',
            'Ợ', 'ợ', 'Ụ', 'ụ', 'Ủ', 'ủ', 'Ứ', 'ứ', 'Ừ', 'ừ', 'Ử', 'ử', 'Ữ',
            'ữ', 'Ự', 'ự',};

    // Mảng các ký tự không dấu
    private static char[] DESTINATION_CHARACTERS = {'A', 'A', 'A', 'A', 'E',
            'E', 'E', 'I', 'I', 'O', 'O', 'O', 'O', 'U', 'U', 'Y', 'a', 'a',
            'a', 'a', 'e', 'e', 'e', 'i', 'i', 'o', 'o', 'o', 'o', 'u', 'u',
            'y', 'A', 'a', 'D', 'd', 'I', 'i', 'U', 'u', 'O', 'o', 'U', 'u',
            'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A',
            'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'E', 'e',
            'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E',
            'e', 'I', 'i', 'I', 'i', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o',
            'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O',
            'o', 'O', 'o', 'U', 'u', 'U', 'u', 'U', 'u', 'U', 'u', 'U', 'u',
            'U', 'u', 'U', 'u',};

    /**
     * Bỏ dấu 1 ký tự
     *
     * @param ch
     * @return
     */
    public static char removeAccent(char ch) {
        int index = Arrays.binarySearch(SOURCE_CHARACTERS, ch);
        if (index >= 0) {
            ch = DESTINATION_CHARACTERS[index];
        }
        return ch;
    }

    private static final String SPECIAL_CHARS_REMARK = StringEscapeUtils.unescapeJava("\\u0022\\u0023\\u0027\\u0060\\u00A0\\u201C\\u201D");

    /**
     * Bỏ dấu 1 chuỗi
     *
     * @param s
     * @return
     */
    public static String removeAccent(String s) {
        StringBuilder sb = new StringBuilder(s);
        for (int i = 0; i < sb.length(); i++) {
            sb.setCharAt(i, removeAccent(sb.charAt(i)));
        }
        return sb.toString();
    }

    public static String getSessionId() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String genOtp(boolean isTestEnv) {
        try {
            if (isTestEnv) {
                return "666888";
            }
            SecureRandom secureRandomGenerator = SecureRandom.getInstance("SHA1PRNG", "SUN");
            int m = secureRandomGenerator.nextInt(999999);
            return String.format("%06d", m);
        } catch (Exception e) {
        }
        return StringUtils.EMPTY;
    }

    public static class Pin {
        private static String AES_IV = "iU8hw2kJhHH9zboj";
        public static String AES_SALT = "g:oU4&Sw6G>@?>U@";

        private boolean verifyRegistCode(
                String plain, String encryptedRegistCode, String cif, String username, String mobileOtp)
                throws InvalidAlgorithmParameterException, NoSuchPaddingException,
                IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException,
                InvalidKeyException, InvalidKeySpecException {
            String password = cif + StringUtils.reverse(username) + mobileOtp + AES_SALT;
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            SecretKey secret = new SecretKeySpec(md.digest(), "AES");

            String cipher = encryptRegistCode(plain, secret);
            if (cipher.equals(encryptedRegistCode)) {
                return true;
            }
            return false;
        }

        public static String encryptRegistCode(String plain, SecretKey key)
                throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
                InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(
                    Cipher.ENCRYPT_MODE, key, new IvParameterSpec(AES_IV.getBytes(StandardCharsets.UTF_8)));
            byte[] cipherText = cipher.doFinal(plain.getBytes());
            return Base64.getEncoder().encodeToString(cipherText);
        }

        public static String decryptRegistCode(String decrypted, SecretKey key)
                throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
                InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(
                    Cipher.DECRYPT_MODE, key, new IvParameterSpec(AES_IV.getBytes(StandardCharsets.UTF_8)));
            byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(decrypted));
            return new String(plainText);
        }

        public static String hash(String cusUserId, String cif, String plainPin) {
            String reverse = new StringBuffer(cusUserId).reverse().toString();
            String cifPad = (cif.length() < 3) ? StringUtils.leftPad(cif, 3, "0") : cif;
            String checkPin = plainPin + reverse + cifPad;
            return Hashing.sha256().hashString(checkPin, Charset.forName("UTF-8")).toString();
        }

        public static boolean verify(String cusUserId, String cif, String plainPin, String hashedPin) {
            String hash = hash(cusUserId, cif, plainPin);
            if (hash.equalsIgnoreCase(hashedPin)) {
                log.info("Pin.verify = {}", true);
                return true;
            }
            log.info("Pin.verify = {}", false);
            return false;
        }

        public static int validateNewPin(
                String cusUserId, String cif, String hashedPin, String newPin) {
            CommonService commonService = BeanUtils.getApplicationContext().getBean(CommonService.class);
            StringBuilder regex = new StringBuilder();
            regex.append("^(?=.*[a-z])");

            String hasUpperChar = commonService.getConfig("PASS_HAS_UPPER_CHAR", "1");
            String hasNumber = commonService.getConfig("PASS_HAS_NUMBER", "1");
            String hasSpecialChar = commonService.getConfig("PASS_HAS_SPECIAL_CHAR", "1");
            String length = commonService.getConfig("PASS_LENGTH", "{8,20}");
            if (hasNumber.equals("1")) {
                regex.append("(?=.*[0-9])");
            }
            if (hasUpperChar.equals("1")) {
                regex.append("(?=.*[A-Z])");
            }
            if (hasSpecialChar.equals("1")) {
                regex.append("(?=.*[~!@#$%^&*()_{}<>?])");
            }
            regex.append("(?=\\S+$).");
            regex.append(length);
            regex.append("$");

            if (verify(cusUserId, cif, newPin, hashedPin)) {
                return 1;
            }
            if (newPin.matches(regex.toString())) {
                return 0;
            }
            return -1;
        }
    }

    public static String toString(Object data) {
        return String.valueOf(data == null ? "" : data).trim();
    }

    public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        return sdf.format(date);
    }

    public static String formatLocalDateTime(LocalDateTime localDateTime) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
        return localDateTime.format(dtf);
    }

    public static String format(String outPattern, Date data) {
        DateFormat df = new SimpleDateFormat(outPattern);
        return df.format(data);
    }

    public static String formatAmount(String amount, String ccy) {
        if (StringUtils.isBlank(amount)) {
            return "0";
        }
        String pattern = ccy.contentEquals("VND") ? "###,###" : "###,###.##";
        DecimalFormat df = new DecimalFormat(pattern);
        if (!ccy.contentEquals("VND")) {
            df.setMinimumFractionDigits(2);
        }
        String toShow = df.format(Double.valueOf(amount));
        return toShow;
    }

    public static String formatAmount(double amount, String ccy) {
        try {
            String pattern = ccy.contentEquals("VND") ? "###,###,###,###,###" : "###,###,###,###,###.##";
            DecimalFormat df = new DecimalFormat(pattern);
            if (!ccy.contentEquals("VND")) {
                df.setMinimumFractionDigits(2);
            }
            String toShow = df.format(Double.valueOf(amount));
            return toShow;
        } catch (Exception e) {

        }
        return "";
    }

    // <editor-fold defaultstate="collapsed" desc="Các phương thức liên quan đến thời gian">
    public static class TimeUtils {

        /**
         * Tra ra date fromdate mac dinh
         *
         * @return
         */
        public static String fromDefaultDateHis() {
            SimpleDateFormat dt22 = new SimpleDateFormat("ddMMyy");
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.YEAR, -1);
            return dt22.format(cal.getTime());
        }

        /**
         * Kiểm tra tính hợp lệ của ngày tháng
         *
         * @param pattern Mẫu dữ liệu
         * @param data    Ngày tháng/thời gian
         * @return
         */
        public static boolean isValid(String pattern, String data) {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            sdf.setLenient(false);
            try {
                Date date = sdf.parse(data);
                return true;
            } catch (ParseException ex) {
                return false;
            }
        }

        /**
         * Định dạng lại thời gian
         *
         * @param inPattern  Mẫu dữ liệu đầu vào. Ví dụ: yyyMMdd
         * @param outPattern Mẫu dữ liệu đầu vào. Ví dụ: dd/MM/yyyy
         * @param data       Dữ liệu đầu vào (sẽ theo inPattern). Ví dụ: 20160615
         * @return Dữ liệu trả ra: 15/06/2016
         */
        public static String format(String inPattern, String outPattern, String data) {
            try {
                DateFormat df1 = new SimpleDateFormat(inPattern);
                DateFormat df2 = new SimpleDateFormat(outPattern);
                Date date = df1.parse(data);
                return df2.format(date);
            } catch (Exception ex) {
                log.error("An error occurred while formatting '%s': %s", data, ex.getMessage());
                return data;
            }
        }

        /**
         * Định dạng lại thời gian
         *
         * @param outPattern Mẫu dữ liệu đầu vào. Ví dụ: dd/MM/yyyy
         * @param data       Dữ liệu đầu vào (sẽ theo inPattern). Ví dụ: 20160615
         * @return Dữ liệu trả ra: 15/06/2016
         */
        public static String format(String outPattern, Date data) {
            DateFormat df = new SimpleDateFormat(outPattern);
            return df.format(data);
        }

        /**
         * Chuyển đổi dữ liệu thời gian ra dạng timestamp
         *
         * @param pattern  Mẫu dữ liệu đầu vào
         * @param dateTime Dữ liệu đầu vào
         * @return Dữ liệu trả ra có dạng yyyy-MM-dd HH:mm:ss
         */
        public static Timestamp toTimestamp(String pattern, String dateTime) {
            try {
                DateFormat df = new SimpleDateFormat(pattern);
                Date date = (Date) df.parse(dateTime);
                Timestamp timeStampDate = new Timestamp(date.getTime());
                return timeStampDate;
            } catch (Exception ex) {
                log.error(
                        "An error occurred while trying to convert '%s' to datetime: %s",
                        dateTime, ex.getMessage());
                return null;
            }
        }

        /**
         * Lấy thời gian hiện tại theo dạng timestamp
         *
         * @return Dữ liệu trả ra có dạng: 2016-06-16 10:32:57.051
         */
        public static Timestamp getNow() {
            return getNow(false);
        }

        /**
         * Lấy ngày hiện tại theo dạng timestamp
         *
         * @param onlyDate true: Chỉ lấy date, thời gian tự set về 00:00:00.000
         * @return Dữ liệu trả ra có dạng: 2016-06-16 00:00:00.000
         */
        public static Timestamp getNow(boolean onlyDate) {
            if (onlyDate) {
                Calendar c = Calendar.getInstance();
                c.set(Calendar.HOUR, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);
                return new Timestamp(c.getTime().getTime());
            } else {
                return new Timestamp(new Date().getTime());
            }
        }

        /**
         * Lấy thời gian hiện tại trả ra chuỗi
         *
         * @param outPattern Định dạng ngày tháng muốn trả ra
         * @return Chuỗi thời gian hiện tại
         */
        public static String getNow(String outPattern) {
            DateFormat df = new SimpleDateFormat(outPattern);
            Date date = new Date();
            return df.format(date);
        }

        /**
         * Thêm/bớt thời gian
         *
         * @param inPattern   Định dạng thời gian truyền vào
         * @param outPattern  Định dạng thời gian cần trả ra
         * @param dateTime    Thời gian
         * @param miliseconds Số giây cần thay đổi (số dương: tăng thời gian, số âm: lùi thời gian). Nếu
         *                    muốn tiến thêm 1 ngày có thể truyền vào: 1*24*60*60*1000
         * @return Chuỗi thời gian sau khi thêm/bớt
         */
        public static String shiftTime(
                String inPattern, String outPattern, String dateTime, long miliseconds) {
            try {
                DateFormat df1 = new SimpleDateFormat(inPattern);
                DateFormat df2 = new SimpleDateFormat(outPattern);
                Date date = (Date) df1.parse(dateTime);
                return df2.format(new Date(date.getTime() + miliseconds));
            } catch (Exception ex) {
                log.error("An error occurred while shifting time '%s': %s", dateTime, ex.getMessage());
                return "";
            }
        }

        /**
         * Tính số ngày giữa 2 thời gian
         *
         * @param date1 Ngày thứ nhất
         * @param date2 Ngày thư hai
         * @return Số ngày giữa 2 thời gian truyền vào
         */
        public static long differTimes(Date date1, Date date2) {

            System.out.println(date1);
            System.out.println(date2);
            return date2.getTime() - date1.getTime();
        }

        /**
         * Tính chênh lệch giữa thời gian truyền vào với hiện tại
         *
         * @param date Ngày cần tính toán
         * @return Số ngày giữa thời gian truyền vào với hiện tại
         */
        public static long differTimes(Date date) {
            return differTimes(getNow(), date);
        }

        /**
         * Tính số ngày giữa 2 thời gian
         *
         * @param pattern1 Định dạng thời gian 1
         * @param date1    Ngày thứ nhất
         * @param pattern2 Định dạng thời gian của ngày thứ 2
         * @param date2    Ngày thư hai
         * @return Số ngày giữa 2 thời gian truyền vào
         */
        public static long differTimes(String pattern1, String date1, String pattern2, String date2) {
            return differTimes(toTimestamp(pattern1, date1), toTimestamp(pattern2, date2));
        }

        /**
         * Tính thời gian chênh lệch
         *
         * @param pattern Định dạng thời gian truyền vào
         * @param date    Ngày cần so sánh với hiện tại
         * @param hasTime Có thời gian vào không. Nếu không thì thời gian được set về: 00:00:00.000
         * @return Số ngày giữa 2 thời gian truyền vào
         */
        public static long differTimes(String pattern, String date, boolean hasTime) {
            if (hasTime) {
                return differTimes(getNow(), toTimestamp(pattern, date));
            } else {
                return differTimes(getNow(true), toTimestamp(pattern, date));
            }
        }

        public static Integer getDayOfDate(Date date) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return cal.get(Calendar.DAY_OF_MONTH);
        }

        public static Integer getHourFromDate(Date date) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return cal.get(Calendar.HOUR_OF_DAY);
        }

        public static Integer getMonthFromDate(Date date) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return cal.get(Calendar.MONTH);
        }

        public static Date stringToDate(String dateStr, String format) {
            DateFormat df = new SimpleDateFormat(format);
            try {
                Date date = df.parse(dateStr);
                return date;
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
        }

        public static String shiftFromNow(
                String outPattern, int timeUnit, int shiftTime) {
            try {
                DateFormat df = new SimpleDateFormat(outPattern);
                Calendar cal = Calendar.getInstance();
                cal.add(timeUnit, shiftTime);
                return df.format(cal.getTime());
            } catch (Exception ex) {
                log.error("An error occurred while shifting time", ex.getMessage());
                return "";
            }
        }
    }

    // </editor-fold>

    public static class StringUtil {

        // Loại bỏ L thường, I hoa, số 0, o thường, O hoa để tránh nhầm lẫn
        private static final String ALPHA_CAPS = "ABCDEFGHJKLMNPQRSTUVWXYZ";
        private static final String ALPHA = "abcdefghijkmnpqrstuvwxyz";
        private static final String NUM = "123456789";
        private static final String SPL_CHARS = "!@#$%^&*_=/";

        public static String random(
                int minLen, int maxLen, int noOfCAPSAlpha, int noOfDigits, int noOfSplChars) {
            if (minLen > maxLen) {
                throw new IllegalArgumentException("Min. Length > Max. Length!");
            }
            if ((noOfCAPSAlpha + noOfDigits + noOfSplChars) > minLen) {
                throw new IllegalArgumentException(
                        "Min. Length should be atleast sum of (CAPS, DIGITS, SPL CHARS) Length!");
            }
            Random rnd = new Random();
            int len = rnd.nextInt(maxLen - minLen + 1) + minLen;
            char[] pswd = new char[len];
            int index = 0;
            for (int i = 0; i < noOfCAPSAlpha; i++) {
                index = getNextIndex(rnd, len, pswd);
                pswd[index] = ALPHA_CAPS.charAt(rnd.nextInt(ALPHA_CAPS.length()));
            }
            for (int i = 0; i < noOfDigits; i++) {
                index = getNextIndex(rnd, len, pswd);
                pswd[index] = NUM.charAt(rnd.nextInt(NUM.length()));
            }
            for (int i = 0; i < noOfSplChars; i++) {
                index = getNextIndex(rnd, len, pswd);
                pswd[index] = SPL_CHARS.charAt(rnd.nextInt(SPL_CHARS.length()));
            }
            for (int i = 0; i < len; i++) {
                if (pswd[i] == 0) {
                    pswd[i] = ALPHA.charAt(rnd.nextInt(ALPHA.length()));
                }
            }
            return String.valueOf(pswd);
        }

        public static String random(
                int minLen,
                int maxLen,
                int noOfCAPSAlpha,
                int noOfDigits,
                String charConfig,
                String NUM) {
            if (minLen > maxLen) {
                throw new IllegalArgumentException("Min. Length > Max. Length!");
            }
            if ((noOfCAPSAlpha + noOfDigits) > minLen) {
                throw new IllegalArgumentException(
                        "Min. Length should be atleast sum of (CAPS, DIGITS, SPL CHARS) Length!");
            }
            String ALPHA_CAPS = charConfig.toUpperCase();
            String ALPHA = charConfig.toLowerCase();
            Random rnd = new Random();
            int len = rnd.nextInt(maxLen - minLen + 1) + minLen;
            char[] pswd = new char[len];
            int index = 0;
            for (int i = 0; i < noOfCAPSAlpha; i++) {
                index = getNextIndex(rnd, len, pswd);
                pswd[index] = ALPHA_CAPS.charAt(rnd.nextInt(ALPHA_CAPS.length()));
            }
            for (int i = 0; i < noOfDigits; i++) {
                index = getNextIndex(rnd, len, pswd);
                pswd[index] = NUM.charAt(rnd.nextInt(NUM.length()));
            }
            for (int i = 0; i < len; i++) {
                if (pswd[i] == 0) {
                    pswd[i] = ALPHA.charAt(rnd.nextInt(ALPHA.length()));
                }
            }
            return String.valueOf(pswd);
        }

        @SuppressWarnings("empty-statement")
        private static int getNextIndex(Random rnd, int len, char[] pswd) {
            int index = rnd.nextInt(len);
            while (pswd[index = rnd.nextInt(len)] != 0)
                ;
            return index;
        }

        /**
         * Loại bỏ dấu trong chuỗi
         *
         * @param input Chuỗi đầu vào. Ví dụ: Dữ liệu test
         * @return Dữ liệu trả ra: Du lieu test
         */
        public static String removeUnicode(String input) {
            String temp = Normalizer.normalize(input, Normalizer.Form.NFD);
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            return pattern.matcher(temp).replaceAll("").replace("đ", "d").replace("Đ", "D");
        }
    }

    public static String maskCard(String cardNo) {
        if (!StringUtils.isEmpty(cardNo)) {
            int length = cardNo.length();
            return cardNo.substring(0, 4) + "*****" + cardNo.substring(length - 4, length);
        }
        return null;
    }

    public static String getDate(String patten) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat f = new SimpleDateFormat(patten);
        return f.format(cal.getTime());
    }

    public static class Checksum {
        private static final String FIRST_SALT = "$:PsT&6^$(HGw%@@";
        private static final String LAST_SALT = "5[1j5hALT:v{Qz7@";

        public static String generate(
                long cusId, String cif, String username, String mobileOtp, String defaultAcc)
                throws NoSuchAlgorithmException, ValidationException {
            return CustomerUtils.makeChecksum(
                    cusId, cif, username, mobileOtp, defaultAcc, FIRST_SALT, LAST_SALT);
        }

        public static boolean verify(
                long cusId,
                String cif,
                String username,
                String mobileOtp,
                String defaultAcc,
                String signData)
                throws NoSuchAlgorithmException, ValidationException {
            return CustomerUtils.verifyChecksum(
                    cusId, cif, username, mobileOtp, defaultAcc, FIRST_SALT, LAST_SALT, signData);
        }
    }

    //    public static void main(String[] args) throws NoSuchAlgorithmException, ValidationException
    // {
    //      long cusUserId = 725;
    //      String cif = "0020000490";
    //      String username = "20000490T505";
    //      String mobile = "0978478498";
    //      String defaultAcc = "1000000222";
    //      System.out.println(Checksum.generate(cusUserId, cif, username, mobile, defaultAcc));
    //      System.out.println(
    //          Checksum.verify(
    //              cusUserId,
    //              cif,
    //              username,
    //              mobile,
    //              defaultAcc,
    //              "f6ca2fe5b8b02806a65fc55111b7b06483f3111b651db8a290df9345ac59c21b"));
    //    }

    //  public static void main(String[] args) throws NoSuchAlgorithmException,
    // InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
    // BadPaddingException, InvalidKeyException {
    //    String password = "0008363359" + StringUtils.reverse("8363359L506") + "0978484545" +
    // Pin.AES_SALT;
    //    MessageDigest md = MessageDigest.getInstance("MD5");
    //    md.update(password.getBytes());
    //    SecretKey secret = new SecretKeySpec(md.digest(), "AES");
    //    String registCode = Pin.decryptRegistCode("PugKTGlNM59Cqbpn7DIg4w==", secret);
    //    System.out.println(registCode);
    //
    //    String pin = CommonUtils.Pin.hash("869", "0008363359", registCode);
    //    System.out.println(pin);
    //  }

    private static int pcmIndex = 1;

    public static int getPcmIndex() {
        pcmIndex = (pcmIndex++ == 10000) ? 1 : pcmIndex;
        return pcmIndex;
    }

    /**
     * verify string only contains digit
     *
     * @param str
     * @return
     */
    public static boolean onlyDigits(String str) {
        // Regex to check string
        // contains only digits
        String regex = "[0-9]+";

        // Compile the ReGex
        Pattern p = Pattern.compile(regex);

        // If the string is empty
        // return false
        if (str == null) {
            return false;
        }

        // Find match between given string
        // and regular expression
        // using Pattern.matcher()
        Matcher m = p.matcher(str);

        // Return if the string
        // matched the ReGex
        return m.matches();
    }

    public static boolean isRemarkContainsSpecialChars(final String remark) {
        if (StringUtils.isEmpty(remark)) {
            return false;
        }
        return StringUtils.containsAny(remark, SPECIAL_CHARS_REMARK);
    }
}
