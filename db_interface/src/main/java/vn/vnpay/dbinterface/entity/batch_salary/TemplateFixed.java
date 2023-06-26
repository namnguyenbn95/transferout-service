package vn.vnpay.dbinterface.entity.batch_salary;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Document(collection = "batch-template-fixed")
@Builder
public class TemplateFixed {
    @Id
    String id;
    String cif;
    String beneName;
    String beneAcctNo;
    String beneBankOld;
    String beneBankNew;
    String createdUser;
    LocalDateTime createdTime;
}
