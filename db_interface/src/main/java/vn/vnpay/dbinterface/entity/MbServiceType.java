package vn.vnpay.dbinterface.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "mb_service_type")
public class MbServiceType {

    @Id
    @Column(name = "servicetype_code")
    private String servicetypeCode;

    @Column(name = "servicetype_name")
    private String servicetypeName;

    @Column(name = "status")
    private String status;

    @Column(name = "pcm_code")
    private String pcmCode;

    @Column(name = "is_financial")
    private String isFinancial;

    @Column(name = "servicetype_name_en")
    private String servicetypeNameEn;
}
