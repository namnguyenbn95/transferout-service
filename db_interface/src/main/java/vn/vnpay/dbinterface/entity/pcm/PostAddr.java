package vn.vnpay.dbinterface.entity.pcm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostAddr {
    String addr1;
    String addr2;
    String addr3;
    String addr4;
    CountryCode countryCode;
    String city;
    String stateProv;
    String postalCode;
}
