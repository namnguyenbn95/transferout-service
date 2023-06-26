package vn.vnpay.dbinterface.common;

import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;

@Slf4j
public class CommonUtils {

    public static String getSessionId() {
        return UUID.randomUUID().toString().replaceAll("-", "");
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
                return "";
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

    private static int pcmIndex = 1;

    public static int getPcmIndex() {
        pcmIndex = (pcmIndex++ == 10000) ? 1 : pcmIndex;
        return pcmIndex;
    }
}
