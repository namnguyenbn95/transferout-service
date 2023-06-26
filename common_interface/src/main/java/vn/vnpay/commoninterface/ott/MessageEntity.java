/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.vnpay.commoninterface.ott;

/**
 * @author nam
 */
public class MessageEntity {
    private String content;
    private int type;
    private String mobile;
    private String cif;
    private String messageId;
    private int priority;
    private String urlImg;
    private String expireTime;
    private String messageTime;
    private int isEncrypt;
    private String rawData;

    public int getIsEncrypt() {
        return isEncrypt;
    }

    public void setIsEncrypt(int isEncrypt) {
        this.isEncrypt = isEncrypt;
    }

    /**
     * @return the rawData
     */
    public String getRawData() {
        return rawData;
    }

    /**
     * @param rawData the rawData to set
     */
    public void setRawData(String rawData) {
        this.rawData = rawData;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * @return the mobile
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * @param mobile the mobile to set
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     * @return the cif
     */
    public String getCif() {
        return cif;
    }

    /**
     * @param cif the cif to set
     */
    public void setCif(String cif) {
        this.cif = cif;
    }

    /**
     * @return the messageId
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * @param messageId the messageId to set
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    /**
     * @return the priority
     */
    public int getPriority() {
        return priority;
    }

    /**
     * @param priority the priority to set
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * @return the urlImg
     */
    public String getUrlImg() {
        return urlImg;
    }

    /**
     * @param urlImg the urlImg to set
     */
    public void setUrlImg(String urlImg) {
        this.urlImg = urlImg;
    }

    /**
     * @return the expireTime
     */
    public String getExpireTime() {
        return expireTime;
    }

    /**
     * @param expireTime the expireTime to set
     */
    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    /**
     * @return the messageTime
     */
    public String getMessageTime() {
        return messageTime;
    }

    /**
     * @param messageTime the messageTime to set
     */
    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

}
