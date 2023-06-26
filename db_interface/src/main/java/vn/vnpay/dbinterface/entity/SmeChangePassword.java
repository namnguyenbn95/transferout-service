package vn.vnpay.dbinterface.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "SME_LOG_CHANGEPASSWORD")
public class SmeChangePassword {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "CHANGE_DATE")
    private LocalDateTime changeDate;

    @Column(name = "USER_NAME")
    private String username;

    @Column(name = "PIN")
    private String pin;
}
