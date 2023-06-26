package vn.vnpay.dbinterface.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "MB_MESSAGE")
public class MbMessage implements Serializable {

    private static final long serialVersionUID = -1413665731475064444L;

    @Id
    @Column(name = "MSG_CODE")
    private String code;

    @Column(name = "MSG_TEMPLATE")
    private String template;

    @Column(name = "MSG_TEMPLATE_EN")
    private String templateEn;

    @Column(name = "STATUS")
    private String status;
}
