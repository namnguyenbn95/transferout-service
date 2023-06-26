package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HardTokenAuthenMethodDTO {
    private String type;
    private String name;
    private String status;          // Trạng thái token (1: Checked, 2: Pending Activation, 3: Active, 4: Expired, 5: Lock Active, 6: Locked, 7: Auto Locked, 8: Unregistered, 9: Pending Verification)
    private String tokenDetail;     // Serial của token
}
