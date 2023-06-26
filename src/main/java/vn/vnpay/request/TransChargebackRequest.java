package vn.vnpay.request;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.request.BaseClientRequest;

@Getter
@Setter
public class TransChargebackRequest extends BaseClientRequest {
    private String fromAcc;             //số tk chịu phí tra soát
    private String serviceCode;
    private String requestTSID;         //mã yêu cầu tra soát
    private String requestTSName;       //tên yêu cầu
    private String requestTSNameEN;     //tên yêu cầu tiếng anh
    private String reasonID;            //mã lí do tra soát
    private String reasonName;          //tên lí do
    private String reasonNameEN;        //tên lí do tiếng anh
    private String departmentID;        //mã phòng ban
    private double feeTSOL;             // phí bank trả cho giao dịch tra soát
    private String creditAcc;           //số tk hưởng có thể thay đổi
    private String creditName;          //tên tk hưởng có thể thay đổi
    private String content;             //nội dung có thể thay đổi
    private String idNo;                //số giấy tờ tùy thân có thể thay đổi
    private String issueDate;           //ngày cấp có thể thay đổi
    private String issuePlace;          //nơi cấp có thể thay đổi
    private String teller;              //trả về từ api get detail
    private int sequence;               //trả về từ api get detail
    private String hostdate;            //trả về từ api get detail
    private String pcTime;              //trả về từ api get detail
    private String isByPassNotBalance;
}
