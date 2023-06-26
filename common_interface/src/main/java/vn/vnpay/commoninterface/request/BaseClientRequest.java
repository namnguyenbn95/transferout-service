package vn.vnpay.commoninterface.request;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class BaseClientRequest {

    /* Thông tin thiết bị */
    @SerializedName("E")
    String IMEI = "unknown";            // Số IMEI hoặc ID Thiết bị

    @SerializedName("DT")
    String OS = "unknown";              // Hệ điều hành IOS|ANDROID|WindowsPhone

    @SerializedName("PM")
    String PM = "unknown";              // Model thiết bị. Ví dụ: HTC One A9|iPhone 6S

    @SerializedName("OV")
    String OV = "unknown";              // Phiên bản hệ điều hành

    @SerializedName("PS")
    String PS = "unknown";              // Đã root hay chưa

    @SerializedName("ATS")
    String ATS = "unknown";             // Thời gian cài ứng dụng trên thiết bị

    String lang = "vi";                 // Mã ngôn ngữ
    String appVersion;                  // Phiên bản ứng dụng
    String sessionId;                   // ID phiên đăng nhập
    long clientId = 0;                  // ID của client
    long keyId = 0;                     // Key ID
    String clientIP = "unknown";        // IP thực hiện request
    String attachedRoot = "";           // Trạng thái root
    String attachedHook = "";           // Trạng thái hook

    @NotBlank(message = "source must not be blank")
    @Pattern(regexp = "^IB$|^MB$|^BANK-HUB$", flags = {Pattern.Flag.CASE_INSENSITIVE}, message = "source must be IB or MB or BANK-HUB")
    String source;// IB, MB, BANK-HUB

    @NotBlank(message = "user must not be blank")
    private String user;
}
