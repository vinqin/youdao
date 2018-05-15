package edu.stu.bean;

import java.util.List;

public class ResultSet {
    //错误返回码，一定存在
    private ErrorCode errorCode;
    //查询的内容，查询正确时，一定存在
    private String query;
    //一般性的翻译结果，查询正确时一定存在
    private List<String> translation;
    //词义，基本词典,查词时才有
    private BasicTranslation basicTranslation;
    //源语言发音地址，翻译成功一定存在
    private String speakUrl;
    //翻译结果发音地址，翻译成功一定存在
    private String tSpeakUrl;
    //源语言和目标语言，一定存在
    private String lang;

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<String> getTranslation() {
        return translation;
    }

    public void setTranslation(List<String> translation) {
        this.translation = translation;
    }

    public BasicTranslation getBasicTranslation() {
        return basicTranslation;
    }

    public void setBasicTranslation(BasicTranslation basicTranslation) {
        this.basicTranslation = basicTranslation;
    }

    public String getSpeakUrl() {
        return speakUrl;
    }

    public void setSpeakUrl(String speakUrl) {
        this.speakUrl = speakUrl;
    }

    public String gettSpeakUrl() {
        return tSpeakUrl;
    }

    public void settSpeakUrl(String tSpeakUrl) {
        this.tSpeakUrl = tSpeakUrl;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
