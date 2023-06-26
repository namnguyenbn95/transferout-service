package vn.vnpay.dbinterface.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@Document(collection = "sme_ott")
public class MongoSmeOTTLogEntity {
    @Id
    private String id;
    private String cif;
    private String username;
    private String mobile;
    private String messageType;
    private String content;
    private Date timeBankToSme;
    private Date timeSmeToOtt;
    private Date timeOttToClient;
    private Date timeClientConfirm; // time client send về ott thông báo đã nhận được notify
    private Date timeClientRead;
    private Date timeClientDelete;
    private String statusSmeToOtt; // 0-gửi thất bại; 1-gửi thành công
    private String statusOttToClient; // -1:chưa gửi; 0:đã gửi; 1:đã nhận(confirm); 2: đã đọc(read); 3: đã xóa(delete)
    private String device;
}
