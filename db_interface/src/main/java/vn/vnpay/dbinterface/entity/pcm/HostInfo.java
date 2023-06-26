package vn.vnpay.dbinterface.entity.pcm;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.logging.log4j.util.Strings;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HostInfo {
    String tellerId;
    String seqNo;
    String hostDt;
    String pcTime;
    String branchNo;

    public String toString() {
        return this.tellerId
                + "-"
                + this.seqNo
                + "-"
                + (Strings.isBlank(this.hostDt) ?
                "" : hostDt.replace("-", ""))
                + "-"
                + this.pcTime
                + "-"
                + this.branchNo;
    }

    public HostInfo(String internalRefNo) {
        String[] tmps = internalRefNo.split("-");
        if (tmps.length == 5) {
            tellerId = tmps[0];
            seqNo = tmps[1];
            hostDt = tmps[2].replace("-", "");
            pcTime = tmps[3];
            branchNo = tmps[4];
        }
    }
}
