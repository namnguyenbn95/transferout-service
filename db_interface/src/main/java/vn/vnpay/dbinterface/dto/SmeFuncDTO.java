package vn.vnpay.dbinterface.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class SmeFuncDTO {
    public SmeFuncDTO() {

    }

    public SmeFuncDTO(String type, int order) {
        this.type = type;
        this.order = order;
    }

    private int displayHomeId;
    private Long id;
    private String userName;
    private String serviceCode;
    private int status;
    private String channel;
    private Date updateDate;
    private int countAct;
    private String serviceGroup;
    private int roleType;
    private String confirmType;
    private String serviceName;
    private String serviceTypeCode;
    private String serviceTypeName;
    private String type; //1. Gợi ý, 2 gần đây, 3 yêu thích
    private String billServiceCode;
    private String billServiceName;
    private String srvGroupName;
    private int order;
    private String isFinancial;
    private String isTrans;
    private int typeService;
    private String urlIcon;
    private String position;

}
