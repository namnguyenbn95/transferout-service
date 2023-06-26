package vn.vnpay.commoninterface.ott;

public class Entity {
    private String url;
    private String aesKey;
    private String macKey;
    private String bankCode;
    private String ciff;
    private String phone;
    private String content;
    private String title;
    private String mutils;
    private String type;
    private String urlImg;

    public String getUrlImg() {
        return urlImg;
    }

    public void setUrlImg(String urlImg) {
        this.urlImg = urlImg;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the mutils
     */
    public String getMutils() {
        return mutils;
    }

    /**
     * @param mutils the mutils to set
     */
    public void setMutils(String mutils) {
        this.mutils = mutils;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the aesKey
     */
    public String getAesKey() {
        return aesKey;
    }

    /**
     * @param aesKey the aesKey to set
     */
    public void setAesKey(String aesKey) {
        this.aesKey = aesKey;
    }

    /**
     * @return the macKey
     */
    public String getMacKey() {
        return macKey;
    }

    /**
     * @param macKey the macKey to set
     */
    public void setMacKey(String macKey) {
        this.macKey = macKey;
    }

    /**
     * @return the bankCode
     */
    public String getBankCode() {
        return bankCode;
    }

    /**
     * @param bankCode the bankCode to set
     */
    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    /**
     * @return the ciff
     */
    public String getCiff() {
        return ciff;
    }

    /**
     * @param ciff the ciff to set
     */
    public void setCiff(String ciff) {
        this.ciff = ciff;
    }

    /**
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone the phone to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
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
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }


}
