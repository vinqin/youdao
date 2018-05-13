package edu.stu.bean;

import java.util.Date;
import java.util.List;

public class BasicTranslation {

    private Integer id;
    private String query;
    private String detailTranslation;
    private String lang;
    private Integer count;
    private Date date;
    private String phonetic;
    private String ukPhonetic;
    private String usPhonetic;
    private String ukSpeechUrl;
    private String usSpeechUrl;
    private String ukSpeechActualUrl;
    private String usSpeechActualUrl;
    private List<BasicExplains> explainsList;
    private List<WebTranslation> webTranslationList;

    public BasicTranslation() {

    }

    public BasicTranslation(String query, Integer count, Date date, String phonetic, String ukPhonetic, String
            usPhonetic, String ukSpeechUrl, String usSpeechUrl, String ukSpeechActualUrl, String usSpeechActualUrl) {
        this.query = query;
        this.count = count;
        this.date = date;
        this.phonetic = phonetic;
        this.ukPhonetic = ukPhonetic;
        this.usPhonetic = usPhonetic;
        this.ukSpeechUrl = ukSpeechUrl;
        this.usSpeechUrl = usSpeechUrl;
        this.ukSpeechActualUrl = ukSpeechActualUrl;
        this.usSpeechActualUrl = usSpeechActualUrl;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getDetailTranslation() {
        return detailTranslation;
    }

    public void setDetailTranslation(String detailTranslation) {
        this.detailTranslation = detailTranslation;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPhonetic() {
        return phonetic;
    }

    public void setPhonetic(String phonetic) {
        this.phonetic = phonetic;
    }

    public String getUkPhonetic() {
        return ukPhonetic;
    }

    public void setUkPhonetic(String ukPhonetic) {
        this.ukPhonetic = ukPhonetic;
    }

    public String getUsPhonetic() {
        return usPhonetic;
    }

    public void setUsPhonetic(String usPhonetic) {
        this.usPhonetic = usPhonetic;
    }

    public String getUkSpeechUrl() {
        return ukSpeechUrl;
    }

    public void setUkSpeechUrl(String ukSpeechUrl) {
        this.ukSpeechUrl = ukSpeechUrl;
    }

    public String getUsSpeechUrl() {
        return usSpeechUrl;
    }

    public void setUsSpeechUrl(String usSpeechUrl) {
        this.usSpeechUrl = usSpeechUrl;
    }

    public String getUkSpeechActualUrl() {
        return ukSpeechActualUrl;
    }

    public void setUkSpeechActualUrl(String ukSpeechActualUrl) {
        this.ukSpeechActualUrl = ukSpeechActualUrl;
    }

    public String getUsSpeechActualUrl() {
        return usSpeechActualUrl;
    }

    public void setUsSpeechActualUrl(String usSpeechActualUrl) {
        this.usSpeechActualUrl = usSpeechActualUrl;
    }

    public List<BasicExplains> getExplainsList() {
        return explainsList;
    }

    public void setExplainsList(List<BasicExplains> explainsList) {
        this.explainsList = explainsList;
    }

    public List<WebTranslation> getWebTranslationList() {
        return webTranslationList;
    }

    public void setWebTranslationList(List<WebTranslation> webTranslationList) {
        this.webTranslationList = webTranslationList;
    }
}
