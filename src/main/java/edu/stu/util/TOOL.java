package edu.stu.util;

import edu.stu.bean.*;
import edu.stu.dao.DBManipulator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class TOOL {


    private static final Parameters PARAM = ParametersManipulator.parameters;

    /**
     * 准备发送到有道API的HTTP POST参数
     *
     * @return HTTP POST表单所需要填入的参数
     */
    public static String requestForHttp() throws Exception {
        String result = "";
        Map<String, String> param = PARAM.getParam();
        //System.out.println("请求的参数：" + param.toString());

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(Parameters.url);
        List<BasicNameValuePair> params = new ArrayList<>();
        Iterator<Map.Entry<String, String>> it = param.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> en = it.next();
            String key = en.getKey();
            String value = en.getValue();
            if (value != null) {
                params.add(new BasicNameValuePair(key, value));
            }
        }
        httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

        /*HttpResponse*/
        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
        try {
            HttpEntity httpEntity = httpResponse.getEntity();
            result = EntityUtils.toString(httpEntity, "utf-8");

            EntityUtils.consume(httpEntity);//释放资源
            httpClient.close();
            httpResponse.close();
        } catch (IOException e) {

        }
        //System.out.println("返回的参数：" + result);
        return result;
    }


    private static boolean storeMp3File(String filePath, String url) {
        try {
            CloseableHttpClient client = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost(url);
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();

            InputStream is = entity.getContent();
            FileOutputStream fos = new FileOutputStream(new File(filePath));
            BufferedOutputStream bufOutStream = new BufferedOutputStream(fos);

            int inByte;
            while ((inByte = is.read()) != -1) {
                bufOutStream.write(inByte);
            }

            bufOutStream.flush();
            is.close();
            bufOutStream.close();
            fos.close();

            client.close();
        } catch (Exception e) {
            return false;
        }

        return true;
    }


    private static void mp3(BasicTranslation basic) {


        if (basic != null) {

            File ukSpeechActualFile = new File(basic.getUkSpeechActualUrl());

            //用户如果想要播放英式（uk）发音，而且本地不存在该mp3文件
            if (PARAM.getBasicPronunciation().equals("uk") && !ukSpeechActualFile.exists()) {
                //下载英式（uk）发音的mp3文件
                String uKFileName = basic.getUkSpeechActualUrl();
                String ukUrl = basic.getUkSpeechUrl();
                if (!ukUrl.equals("")) {
                    boolean flag = storeMp3File(uKFileName, ukUrl);
                    if (!flag) {
                        //如果文件下载失败则将数据库中mp3文件实际保存路径标记为空字符串，将mp3所在的URL也标记为空字符串
                        //String tag = "UK-" + basic.getQuery() + PARAM.getVoice() + ".mp3 is invalid.";
                        String tag = "";
                        basic.setUkSpeechUrl(tag);
                        basic.setUkSpeechActualUrl(tag);
                        DBManipulator.updateBasicTranslationById(basic);
                    }
                }
            }

            File usSpeechActualFile = new File(basic.getUsSpeechActualUrl());

            //用户如果想要播放美式（us）发音，而且本地不存在该mp3文件
            if (PARAM.getBasicPronunciation().equals("us") && !usSpeechActualFile.exists()) {
                //下载美式（us）发音的mp3文件
                String uSFileName = basic.getUsSpeechActualUrl();
                String uSUrl = basic.getUsSpeechUrl();
                if (!uSUrl.equals("")) {
                    boolean flag = storeMp3File(uSFileName, uSUrl);
                    if (!flag) {
                        //如果文件下载失败则将数据库中mp3文件实际保存路径标记为空字符串，将mp3所在的URL也标记为空字符串
                        //String tag = "US-" + basic.getQuery() + PARAM.getVoice() + ".mp3 is invalid.";
                        String tag = "";
                        basic.setUsSpeechUrl(tag);
                        basic.setUsSpeechActualUrl(tag);
                        DBManipulator.updateBasicTranslationById(basic);
                    }
                }
            }

        }

        if (PARAM.getSourceSpeech() && ResultManipulator.resultSet.getSpeakUrl() != null) {
            //用户如果要求播放源内容的mp3文件  ==>  注意，该文件无论basic是否为空，只要API翻译成功都一定存在
            String srcContentFileName = PARAM.getMp3FilePath() + "src.mp3";
            String srcContentUrl = ResultManipulator.resultSet.getSpeakUrl();
            storeMp3File(srcContentFileName, srcContentUrl);
        }

        if (PARAM.getDestSpeech() && ResultManipulator.resultSet.gettSpeakUrl() != null) {
            //用户如果要求播放翻译结果的mp3文件  ==>  注意，该文件无论basic是否为空，只要API翻译成功都一定存在
            String targetContentFileName = PARAM.getMp3FilePath() + "target.mp3";
            String targetContentUrl = ResultManipulator.resultSet.gettSpeakUrl();
            storeMp3File(targetContentFileName, targetContentUrl);
        }

    }


    //从数据库中获取翻译内容
    public static boolean getResultFromDB() {

        BasicTranslation basic = DBManipulator.getBasicTranslation(PARAM.getQuery());
        if (basic == null) {
            //数据库中找不到所需要翻译的内容
            return false;
        }


        if (PARAM.getDestType().toLowerCase().equals("zh-chs") || PARAM.getDestType().toLowerCase().equals
                ("auto")) {
            TOOL.getResultCN(basic);

        } else {
            TOOL.getResultEN(basic);
        }
        //根据用户需求处理mp3文件
        TOOL.mp3(basic);
        System.out.println(ResultManipulator.resultPrint);//打印结果
        DBManipulator.updateBasicTranslation(basic);//更新数据库中的记录

        return true;
    }

    //从有道API处获取翻译内容
    public static void getResultFromAPI() {
        System.out.println(ResultManipulator.resultPrint);//打印结果
        final ResultSet RESULTSET = ResultManipulator.resultSet;

        if (RESULTSET.getBasicTranslation() != null) {
            DBManipulator.addBasicTranslation(RESULTSET.getBasicTranslation());//往数据库中添加记录
        }

        //根据用户需求处理mp3文件
        TOOL.mp3(RESULTSET.getBasicTranslation());
    }


    public static void getResultCN(BasicTranslation basic) {
        StringBuilder resultPrint = new StringBuilder();
        ResultManipulator.resultPrint = resultPrint;

        resultPrint.append(PrintCN.SHOW);
        resultPrint.append(basic.getQuery());
        resultPrint.append("\n");
        resultPrint.append(PrintCN.DEST);
        resultPrint.append(ResultManipulator.langCN(basic.getLang()));
        resultPrint.append("\n");

        resultPrint.append(PrintCN.PHONETIC);
        resultPrint.append("\n\t" + PrintCN.UK_PHONETIC + " ");
        resultPrint.append(basic.getUkPhonetic());
        resultPrint.append("\n\t" + PrintCN.US_PHONETIC + " ");
        resultPrint.append(basic.getUsPhonetic());
        resultPrint.append("\n");
        resultPrint.append(PrintCN.BASIC_TRANSLATION);
        List<BasicExplains> explains = basic.getExplainsList();
        for (BasicExplains exp : explains) {
            resultPrint.append("\n\t\"" + exp.getExplains() + "\"  ");
        }
        resultPrint.append("\n");

        //打印一般性的翻译内容
        resultPrint.append(PrintCN.DETAIL_TRANSLATION);
        resultPrint.append("\n\t\"");
        resultPrint.append(basic.getDetailTranslation());
        resultPrint.append("\"\n");

        //如果用户请求web翻译
        if (PARAM.getWebInformation()) {
            List<WebTranslation> webs = basic.getWebTranslationList();
            if (webs != null) {
                resultPrint.append(PrintCN.WEB_TRANSLATION);
                resultPrint.append("\n");
                ResultManipulator.resultApp(webs);
            }
        }
        resultPrint.append("\n");
        resultPrint.append(PrintCN.SEARCH_COUNTS);
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        resultPrint.append(basic.getCount() + 1);//历史查询次数加一（即加上当前查询的这一次）为总共查询次数
        resultPrint.append(PrintCN.SEARCH_HISTORY);
        resultPrint.append(" ");
        resultPrint.append(PrintCN.RECENTLY_SEARCH_DATE);
        resultPrint.append(time.format(basic.getDate()));


    }

    public static void getResultEN(BasicTranslation basic) {
        StringBuilder resultPrint = new StringBuilder();
        ResultManipulator.resultPrint = resultPrint;

        resultPrint.append(PrintEN.SHOW);
        resultPrint.append(basic.getQuery());
        resultPrint.append("\n");
        resultPrint.append(PrintEN.DEST);
        resultPrint.append(ResultManipulator.langEN(basic.getLang()));

        resultPrint.append("\n");
        resultPrint.append(PrintEN.PHONETIC);

        resultPrint.append("\n\t" + PrintEN.UK_PHONETIC + " ");
        resultPrint.append(basic.getUkPhonetic());
        resultPrint.append("\n\t" + PrintEN.US_PHONETIC + " ");
        resultPrint.append(basic.getUsPhonetic());
        resultPrint.append("\n");
        resultPrint.append(PrintEN.BASIC_TRANSLATION);
        List<BasicExplains> explains = basic.getExplainsList();
        for (BasicExplains exp : explains) {
            resultPrint.append("\n\t\"" + exp.getExplains() + "\"  ");
        }
        resultPrint.append("\n");

        //打印一般性的翻译内容
        resultPrint.append(PrintEN.DETAIL_TRANSLATION);
        resultPrint.append("\n\t\"");
        resultPrint.append(basic.getDetailTranslation());
        resultPrint.append("\"\n");

        //如果用户请求web翻译
        if (PARAM.getWebInformation()) {
            List<WebTranslation> webs = basic.getWebTranslationList();
            if (webs != null) {
                resultPrint.append(PrintEN.WEB_TRANSLATION);
                resultPrint.append("\n");
                ResultManipulator.resultApp(webs);
            }
        }
        resultPrint.append("\n");
        resultPrint.append(PrintEN.SEARCH_COUNTS);
        SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss yyyy-MM-dd");
        resultPrint.append(basic.getCount() + 1);
        resultPrint.append(PrintEN.SEARCH_HISTORY);
        resultPrint.append(" ");
        resultPrint.append(PrintEN.RECENTLY_SEARCH_DATE);
        resultPrint.append(time.format(basic.getDate()));

    }

}
