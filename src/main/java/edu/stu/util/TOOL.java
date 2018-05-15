package edu.stu.util;

import edu.stu.bean.BasicExplains;
import edu.stu.bean.BasicTranslation;
import edu.stu.bean.Parameters;
import edu.stu.bean.WebTranslation;
import edu.stu.dao.DBManipulator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
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

    /**
     * 准备发送到有道API的HTTP POST参数
     *
     * @return HTTP POST表单所需要填入的参数
     */
    public static String requestForHttp() throws Exception {
        String result = null;

        Parameters parameters = ParametersManipulator.parameters;
        Map<String, String> param = parameters.getParam();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        /*HttpPost*/
        HttpPost httpPost = new HttpPost(parameters.url);
        //System.out.println("请求的参数：" + new JSONObject(information.getRequestParams()).toString());
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
        } finally {
            try {
                if (httpResponse != null) {
                    httpResponse.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //System.out.println("返回的参数：" + result);
        return result;
    }


    private static boolean storeMp3File(String filePath, String url) {
        try {
            CloseableHttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(url);
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


    public static void mp3(BasicTranslation basic) {

        final Parameters PARAM = ParametersManipulator.parameters;

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
                        //如果文件下载失败则将数据库中mp3文件实际保存路径标记为失效
                        String tag = "UK-" + basic.getQuery() + PARAM.getVoice() + ".mp3 is invalid.";
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
                        //如果文件下载失败则将数据库中该字段标记为失败
                        String tag = "US-" + basic.getQuery() + PARAM.getVoice() + ".mp3 is invalid.";
                        basic.setUsSpeechActualUrl(tag);
                        DBManipulator.updateBasicTranslationById(basic);
                    }
                }
            }

        }

        if (PARAM.getSourceSpeech() && ResultManipulator.resultSet != null) {
            //用户如果要求播放源内容的mp3文件  ==>  注意，该文件无论basic是否为空，都一定存在
            String srcContentFileName = ParametersManipulator.parameters.getMp3FilePath() + "src.mp3";
            String srcContentUrl = ResultManipulator.resultSet.getSpeakUrl();
            storeMp3File(srcContentFileName, srcContentUrl);
        }

        if (PARAM.getDestSpeech() && ResultManipulator.resultSet != null) {
            //用户如果要求播放翻译结果的mp3文件  ==>  注意，该文件无论basic是否为空，都一定存在
            String targetContentFileName = ParametersManipulator.parameters.getMp3FilePath() + "target.mp3";
            String targetContentUrl = ResultManipulator.resultSet.gettSpeakUrl();
            storeMp3File(targetContentFileName, targetContentUrl);
        }

    }


    public static void getResultCN(BasicTranslation basic) {
        StringBuilder resultPrint = new StringBuilder();
        ResultManipulator.resultPrint = resultPrint;

        resultPrint.append(PrintCN.show);
        resultPrint.append(basic.getQuery());
        resultPrint.append("\n");
        resultPrint.append(PrintCN.dest);
        resultPrint.append(ResultManipulator.langCN(basic.getLang()));
        resultPrint.append("\n");

        resultPrint.append(PrintCN.phonetic);
        resultPrint.append("\n\t" + PrintCN.ukPhonetic + " ");
        resultPrint.append(basic.getUkPhonetic());
        resultPrint.append("\n\t" + PrintCN.usPhonetic + " ");
        resultPrint.append(basic.getUsPhonetic());
        resultPrint.append("\n");
        resultPrint.append(PrintCN.basicTranslation);
        List<BasicExplains> explains = basic.getExplainsList();
        for (BasicExplains exp : explains) {
            resultPrint.append("\n\t\"" + exp.getExplains() + "\"  ");
        }
        resultPrint.append("\n");

        //如果用户请求web翻译
        if (ParametersManipulator.parameters.getWebInformation()) {
            List<WebTranslation> webs = basic.getWebTranslationList();
            if (webs != null) {
                resultPrint.append(PrintCN.webTranslation);
                resultPrint.append("\n");
                ResultManipulator.resultApp(webs);
            }
        }
        resultPrint.append("\n");
        resultPrint.append(PrintCN.searchCounts);
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        resultPrint.append(basic.getCount());
        resultPrint.append(PrintCN.searchHistory);
        resultPrint.append(" ");
        resultPrint.append(PrintCN.recentlySearchDate);
        resultPrint.append(time.format(basic.getDate()));


    }

    public static void getResultEN(BasicTranslation basic) {
        StringBuilder resultPrint = new StringBuilder();
        ResultManipulator.resultPrint = resultPrint;

        resultPrint.append(PrintEN.show);
        resultPrint.append(basic.getQuery());
        resultPrint.append("\n");
        resultPrint.append(PrintEN.dest);
        resultPrint.append(ResultManipulator.langEN(basic.getLang()));

        resultPrint.append("\n");
        resultPrint.append(PrintEN.phonetic);

        resultPrint.append("\n\t" + PrintEN.ukPhonetic + " ");
        resultPrint.append(basic.getUkPhonetic());
        resultPrint.append("\n\t" + PrintEN.usPhonetic + " ");
        resultPrint.append(basic.getUsPhonetic());
        resultPrint.append("\n");
        resultPrint.append(PrintEN.basicTranslation);
        List<BasicExplains> explains = basic.getExplainsList();
        for (BasicExplains exp : explains) {
            resultPrint.append("\n\t\"" + exp.getExplains() + "\"  ");
        }
        resultPrint.append("\n");

        //如果用户请求web翻译
        if (ParametersManipulator.parameters.getWebInformation()) {
            List<WebTranslation> webs = basic.getWebTranslationList();
            if (webs != null) {
                resultPrint.append(PrintEN.webTranslation);
                resultPrint.append("\n");
                ResultManipulator.resultApp(webs);
            }
        }
        resultPrint.append("\n");
        resultPrint.append(PrintEN.searchCounts);
        SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss yyyy-MM-dd");
        resultPrint.append(basic.getCount());
        resultPrint.append(PrintEN.searchHistory);
        resultPrint.append(" ");
        resultPrint.append(PrintEN.recentlySearchDate);
        resultPrint.append(time.format(basic.getDate()));


    }

}
