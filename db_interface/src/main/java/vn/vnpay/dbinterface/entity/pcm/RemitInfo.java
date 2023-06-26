package vn.vnpay.dbinterface.entity.pcm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RemitInfo {
    HostInfo hostInfo;
    PmtInstruction pmtInstruction;
}
