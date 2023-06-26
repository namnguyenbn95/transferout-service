package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.CardTDQTDTO;
import vn.vnpay.commoninterface.dto.VipAccDTO;

import java.util.List;

@Getter
@Setter
public class GetFeeComboBankResponse extends BaseBankResponse {
    private String cif;

    // Tên khách hàng
    private String cusName;

    // Mã gói phí
    private String feeCode;

    // Số tài khoản thu phí
    private String account;

    // Ngày hiệu lực định dạng: YYYY-MM-DD
    private String effectiveDate;

    // Người đăng ký
    private String creator;

    // Định dạng: YYYY-MM-DD HH:MM:SS
    private String creationTime;

    // Người phê duyệt
    private String approver;

    // Định dạng: YYYY-MM-DD HH:MM:SS
    private String approveTime;

    // Mã cost center đăng ký
    private String brn;

    // Trạng thái gói phí A : Active I : Inactive
    private String status;

    // Số lượng tài khoản số đẹp đã đăng ký
    private int vipAccTotalActual;

    private List<VipAccDTO> vipAccList;

    private int cardTDQTTotalActual;

    private List<CardTDQTDTO> cardTDQTList;

    private int cardGNQTotalActual;

    private List<CardTDQTDTO> cardGNQTList;

    // Tình trạng thu phí gói 4: Miễn phí- đáp ứng điều kiện số dư(áp dụng với Tình trạng sử dụng gói 1 và 3) 5- Thu phí- ko đáp ứng điều kiện số dư. (áp dụng với Tình trạng sử dụng gói 1 và 3) 6- Nợ phí
    private String debitFeeStatus;
}
