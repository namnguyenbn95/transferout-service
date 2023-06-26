package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.CardTDQTDTO;
import vn.vnpay.commoninterface.dto.VipAccDTO;

import java.util.List;

@Getter
@Setter
@Builder
public class RegisterFeeComboBankRequest extends BaseBankRequest {
    // CIF khách hàng
    private String cif;

    // Tên khách hàng
    private String cusName;

    // Mã gói phí
    private String feeCode;

    // Số tài khoản thu phí
    private String account;

    // Nếu DigiBiz thì truyền DIGIBIZ Nếu quầy thì truyền user maker
    private String creator;

    // Định dạng: YYYY-MM-DD HH:MM:SS
    private String creationTime;

    // Nếu DigiBiz thì truyền DIGIBIZ Nếu quầy thì truyền user checker
    private String approver;

    // Định dạng: YYYY-MM-DD HH:MM:SS
    private String approveTime;

    // Nếu DigiBiz thì truyền 068 Nếu quầy thì truyền theo cost center
    private String brn;

    // Nếu đăng ký / thay đổi gói thì truyền A : Active Nếu hủy đăng ký thì truyền I : Inactive
    private String status;

    // Nếu không truyền thì mặc định là không đăng ký tài khoản số đẹp hoặc không thay đổi số tài khoản số đẹp
    private List<VipAccDTO> vipAccList;

    // Nếu không truyền thì mặc định là không đăng ký hoặc không thay đổi thẻ
    private List<CardTDQTDTO> cardTDQTList;

    // Nếu không truyền thì mặc định là không đăng ký hoặc không thay đổi thẻ
    private List<CardTDQTDTO> cardGNQTList;
}
