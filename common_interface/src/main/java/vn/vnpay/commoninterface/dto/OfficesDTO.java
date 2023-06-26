package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OfficesDTO {
    private long id;
    private String title;
    private String address;
    private double longitude;
    private double latitude;
    private String ccName;
    private String costCenter;

    public OfficesDTO() {

    }

    public OfficesDTO(long id, String title, String address, double longitude, double latitude) {
        this.id = id;
        this.title = title;
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
    }
}

