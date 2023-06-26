package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ATMsDTO {
    private String title;
    private String address;
    private String location;
    private double longitude;
    private double latitude;
    private long quantity;
    private String serialNo;
    private String servingTime;

    public ATMsDTO(String title, String address, String location, double longitude, double latitude) {
        this.title = title;
        this.address = address;
        this.location = location;
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
