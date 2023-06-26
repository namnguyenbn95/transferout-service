package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class HardTokenUsersDTO {
    private int cif;
    private String fullname;
    private String mobileNo;
    private String username;
    private List<HardTokenAuthenMethodDTO> authenMethods;
}
