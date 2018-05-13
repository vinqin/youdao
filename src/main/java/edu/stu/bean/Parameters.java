package edu.stu.bean;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class Parameters {
    //需要翻译的源语言类型
    private String srcType;
    //需要翻译的目标语言类型
    private String destType;
    //翻译内容所在的文件的路径
    private String filepath;
    //详细翻译判断标志
    private Boolean webInformation;
    //要播放的基本音标发音的格式，英式音标（uk）或美式音标（us）二选一，该值为null代表不发音
    private String basicPronunciation;
    //要播放的声音类型，女声（0），男声（1）
    private Character voice;
    //播放源内容的判断标志
    private Boolean sourceSpeech;
    //播放翻译后的内容的判断标志
    private Boolean destSpeech;
    //从有道API下载的基本音标的音频文件(.mp3格式)需要存放目录
    private String mp3FilePath;
    //需要翻译的内容
    private String query;
    //需要提交的参数
    private Map<String, String> param;

    public static final String appKey = "1a11210f12961f21";
    public static final String pwd = "Nq9Yl6ZQtzxk6mRVov9fxNrUvPVE1fwB";
    public static final String url = "http://openapi.youdao.com/api";

    public String getSrcType() {
        return srcType;
    }

    public void setSrcType(String srcType) {
        this.srcType = srcType;
    }

    public String getDestType() {
        return destType;
    }

    public void setDestType(String destType) {
        this.destType = destType;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public Boolean getWebInformation() {
        return webInformation;
    }

    public void setWebInformation(Boolean webInformation) {
        this.webInformation = webInformation;
    }

    public String getBasicPronunciation() {
        return basicPronunciation;
    }

    public void setBasicPronunciation(String basicPronunciation) {
        this.basicPronunciation = basicPronunciation;
    }

    public Character getVoice() {
        return voice;
    }

    public void setVoice(Character voice) {
        this.voice = voice;
    }

    public Boolean getSourceSpeech() {
        return sourceSpeech;
    }

    public void setSourceSpeech(Boolean sourceSpeech) {
        this.sourceSpeech = sourceSpeech;
    }

    public Boolean getDestSpeech() {
        return destSpeech;
    }

    public void setDestSpeech(Boolean destSpeech) {
        this.destSpeech = destSpeech;
    }

    public String getMp3FilePath() {
        return mp3FilePath;
    }

    public void setMp3FilePath(String mp3FilePath) {
        this.mp3FilePath = mp3FilePath;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Map<String, String> getParam() {
        param = new HashMap<>();
        param.put("q", getQuery());
        param.put("from", getSrcType());
        param.put("to", getDestType());
        param.put("voice", getVoice() + "");

        String salt = String.valueOf(System.currentTimeMillis());
        param.put("salt", salt);
        param.put("appKey", appKey);
        param.put("sign", md5(appKey + getQuery() + salt + pwd));

        return param;
    }

    public void setParam(Map<String, String> param) {
        this.param = param;
    }

    /**
     * 生成32位MD5摘要
     *
     * @param string appKey+q+salt+密钥
     * @return md5(appKey + q + salt + 密钥)
     */
    public String md5(String string) {
        if (string == null) {
            return null;
        }
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F'};

        try {
            byte[] btInput = string.getBytes("utf-8");
            /* 获得MD5摘要算法的 MessageDigest 对象 */
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            /* 使用指定的字节更新摘要 */
            mdInst.update(btInput);
            /* 获得密文 */
            byte[] md = mdInst.digest();
            /* 把密文转换成十六进制的字符串形式 */
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            return null;
        }
    }
}
