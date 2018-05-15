package edu.stu.bean;

public class WebTranslation {
    private Integer id;
    private String webKey;
    private String webValue;
    private Integer bId;

    public WebTranslation() {

    }

    public WebTranslation(String webKey, String webValue) {
        this.webKey = webKey;
        this.webValue = webValue;
    }

    public WebTranslation(String webKey, String webValue, Integer bId) {
        this.webKey = webKey;
        this.webValue = webValue;
        this.bId = bId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWebKey() {
        return webKey;
    }

    public void setWebKey(String webKey) {
        this.webKey = webKey;
    }

    public String getWebValue() {
        return webValue;
    }

    public void setWebValue(String webValue) {
        this.webValue = webValue;
    }

    public Integer getbId() {
        return bId;
    }

    public void setbId(Integer bId) {
        this.bId = bId;
    }
}
