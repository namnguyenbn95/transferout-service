package vn.vnpay.commoninterface.ott;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SendOTT {
    @Autowired
    private Environment env;

    public boolean sendNotification(String mobile, String content, String type) {
        try {
            Entity entity = new Entity();
            entity.setAesKey(env.getProperty("ott.aesKey"));
            entity.setBankCode(env.getProperty("ott.bankCode"));
            entity.setCiff("");
            entity.setContent(content);
            entity.setMacKey(env.getProperty("ott.macKey"));
            entity.setMutils("");
            entity.setPhone(mobile);
            entity.setTitle("");
            entity.setType(type);
            entity.setUrl(env.getProperty("ott.url.single"));
            return NoticeClient.getInstance().sendMessage(entity, "");
        } catch (Exception ex) {
            log.info("sendNotification...ex: " + ex.toString());
        }
        return false;
    }

    public boolean sendNotification(String mobile, String content, String type, String urlImg, String rawData) {
        try {
            Entity entity = new Entity();
            entity.setAesKey(env.getProperty("ott.aesKey"));
            entity.setBankCode(env.getProperty("ott.bankCode"));
            entity.setCiff("");
            entity.setContent(content);
            entity.setMacKey(env.getProperty("ott.macKey"));
            entity.setMutils("");
            entity.setPhone(mobile);
            entity.setTitle("");
            entity.setType(type);
            entity.setUrl(env.getProperty("ott.url.single"));
            entity.setUrlImg(urlImg);
            return NoticeClient.getInstance().sendMessage(entity, rawData);
        } catch (Exception ex) {
            log.info("sendNotification...ex: " + ex.toString());
        }
        return false;
    }
}
