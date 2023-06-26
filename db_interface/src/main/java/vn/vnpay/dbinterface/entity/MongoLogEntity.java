package vn.vnpay.dbinterface.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import vn.vnpay.dbinterface.common.Constants;

import java.time.LocalDateTime;

@Getter
@Setter
@Document(collection = Constants.MONGO_LOG_COLL)
public class MongoLogEntity {
    private String user = "";
    private String ipServer = "";
    private String endpoint = "";
    private LocalDateTime receivedTime;
    private LocalDateTime sentTime;
    private String OV = "";
    private String OS = "";
    private String PM = "";
    private String PS = "";
    private String IMEI = "";
    private String endpointDesc = "";
    private String resCode = "";
    private String resMsg = "";
    private String source;
    private String traceId = "";
    private String appVersion = "";
    private String clientIp = "";
}
