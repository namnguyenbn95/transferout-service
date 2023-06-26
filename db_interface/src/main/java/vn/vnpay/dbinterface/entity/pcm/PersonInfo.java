package vn.vnpay.dbinterface.entity.pcm;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PersonInfo {
    String fullName;
    List<Contact> contact;
}
