package edu.stu.util;

import edu.stu.bean.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;

public class ResultManipulator {
    public static ResultSet resultSet = null;
    public static StringBuilder resultPrint = null;

    public ResultManipulator(String resultJsonString, String type) {
        jsonSplit(resultJsonString, type);
    }

    private void jsonSplit(String resultJsonString, String type) {
        JSONObject resultJson = new JSONObject(resultJsonString);
        ErrorCode errorCode = new ErrorCode(Integer.parseInt(resultJson.getString("errorCode")));//错误返回码，一定存在
        if (!errorCode.isStatus()) {
            switch (type.toLowerCase()) {
                case "zh-chs":
                    resultPrint = new StringBuilder(PrintCN.failure + "\n\t" + errorCode.getCode() + errorCode
                            .getMessage());
                    break;
                default:
                    resultPrint = new StringBuilder(PrintEN.failure + " " + errorCode.getCode());
            }
            return;//查询失败，直接返回
        }

        resultSet = new ResultSet();
        resultSet.setErrorCode(errorCode);
        if (!setQuery(resultJson)) {
            if (type.toLowerCase().equals("zh-chs")) {
                resultPrint = new StringBuilder(PrintCN.noResult);
            } else {
                resultPrint = new StringBuilder(PrintEN.noResult);
            }
            return;//查询失败，直接返回
        }

        //以下步骤才是真正的查询成功
        setLang(resultJson);
        setTranslation(resultJson);
        setBasicTranslation(resultJson);
        setUrl(resultJson);
        type = resultSet.getLang().equals("") ? "zh-chs" : resultSet.getLang().substring(resultSet.getLang()
                .lastIndexOf("2") + 1);

        resultPrint = new StringBuilder();
        if (type.toLowerCase().equals("zh-chs")) {
            getResultCN(resultJson);
        } else {
            getResultEN(resultJson);
        }

    }

    private boolean setQuery(JSONObject resultJson) {
        try {
            String query = resultJson.getString("query");//需要翻译的内容，查询正确时，一定存在
            resultSet.setQuery(query);
        } catch (JSONException jsonException) {
            resultSet.setQuery("");
            return false;
        }
        return true;
    }


    private void setLang(JSONObject resultJson) {
        try {
            String lang = resultJson.getString("l");//源语言和目标语言（比如EN2zh-CHS），查询正确时一定存在
            resultSet.setLang(lang);
        } catch (JSONException jsonException) {
            resultSet.setLang("");
        }
    }

    private void setTranslation(JSONObject resultJson) {
        try {
            JSONArray translation = resultJson.getJSONArray("translation");//一般性翻译结果，查询正确时一定存在
            resultSet.setTranslation((List) translation.toList());
        } catch (JSONException jsonException) {
            resultSet.setTranslation(new ArrayList<>());
        }
    }

    private void setBasicTranslation(JSONObject resultJson) {
        try {
            JSONObject basicJSON = resultJson.getJSONObject("basic");// 有道词典-基本词典,查词时才有
            BasicTranslation basicTranslation = new BasicTranslation();
            String phoneticOfBasic = "";
            String ukPhonetic = "";
            String usPhonetic = "";
            String ukSpeechUrl = "";
            String usSpeechUrl = "";
            try {
                phoneticOfBasic = basicJSON.getString("phonetic");
                ukPhonetic = basicJSON.getString("uk-phonetic");
                usPhonetic = basicJSON.getString("us-phonetic");
                ukSpeechUrl = basicJSON.getString("uk-speech");//注意如果指定的key没有map到value，则会抛异常
                usSpeechUrl = basicJSON.getString("us-speech");
            } catch (JSONException jsonException) {

            }
            JSONArray explainsOfBasic = basicJSON.getJSONArray("explains");//一定存在
            basicTranslation.setPhonetic(phoneticOfBasic);
            basicTranslation.setUkPhonetic(ukPhonetic);
            basicTranslation.setUsPhonetic(usPhonetic);
            basicTranslation.setUkSpeechUrl(ukSpeechUrl);
            basicTranslation.setUsSpeechUrl(usSpeechUrl);

            List<String> strings = (List) explainsOfBasic.toList();
            List<BasicExplains> basicExp = new ArrayList<>();

            for (String str : strings) {
                BasicExplains exp = new BasicExplains();
                exp.setExplains(str.trim());
                basicExp.add(exp);
            }
            basicTranslation.setExplainsList(basicExp);

            List<WebTranslation> webs = getWebTranslations(resultJson);
            basicTranslation.setWebTranslationList(webs);
            basicTranslation.setDate(new Date());
            basicTranslation.setCount(1);//初次查询该单词
            basicTranslation.setLang(resultSet.getLang());
            List<String> detailTranslations = resultSet.getTranslation();
            StringBuilder detailTranslation = new StringBuilder();
            for (String str : detailTranslations) {
                detailTranslation.append(str);
                detailTranslation.append(" ");
            }
            basicTranslation.setDetailTranslation(detailTranslation.toString().trim());

            /*
            这里千万注意：数据库中的query字段必须和用户查询内容一样，而不应当和有道API返回的query字段一样。
            原因是：既然有道API有返回结果了，则说明该单词或者句子翻译成功。但是有道API会对用户提交的query数据稍作修改，这样返回的query与用户原始输入的query就不一致了，
            则最终会导致本地数据库无法同步。
            例如，用户输入的query为：你好吗？
            而有道API返回的是：你好吗
            它去掉了用户输入的“？”这个字符，这样数据库中保存的记录的query就是”你好吗“，没有“？”，用户下次再查询”你好吗？“的时候，数据库中找不到”你好吗？“，
            因此程序再次向有道API发起查询，也会往数据库中再一次添加一条”你好吗“不带”？“的记录
            basicTranslation.setQuery(resultSet.getQuery());这条语句是错误的
            */
            basicTranslation.setQuery(ParametersManipulator.parameters.getQuery());

            //有道API注释：voice 没有男声的，会输出女声。即英文查词时反正会有声音
            basicTranslation.setUkSpeechActualUrl(ParametersManipulator.parameters.getMp3FilePath() + "uk" +
                    resultSet.getQuery() + ParametersManipulator.parameters.getVoice() + ".mp3");
            basicTranslation.setUsSpeechActualUrl(ParametersManipulator.parameters.getMp3FilePath() + "us" +
                    resultSet.getQuery() + ParametersManipulator.parameters.getVoice() + ".mp3");

            resultSet.setBasicTranslation(basicTranslation);
        } catch (JSONException jsonException) {
            resultSet.setBasicTranslation(null);
        }


    }

    private void setUrl(JSONObject resultJson) {
        try {
            String speakUrl = resultJson.getString("speakUrl");//源语言发音地址，翻译成功一定存在
            String tSpeakUrl = resultJson.getString("tSpeakUrl");//翻译结果发音地址，翻译成功一定存在

            if (resultSet.getBasicTranslation() != null && resultSet.getBasicTranslation().getUkSpeechUrl().equals
                    ("")) {
                resultSet.getBasicTranslation().setUkSpeechUrl(tSpeakUrl);
                resultSet.getBasicTranslation().setUsSpeechUrl(tSpeakUrl);
            }

            resultSet.setSpeakUrl(speakUrl);
            resultSet.settSpeakUrl(tSpeakUrl);
        } catch (JSONException jsonException) {
            resultSet.setSpeakUrl("");
            resultSet.settSpeakUrl("");
        }
    }


    public static List<WebTranslation> getWebTranslations(JSONObject resultJson) {
        List<WebTranslation> webs = null;
        try {
            JSONArray webOfDetail = resultJson.getJSONArray("web"); // 有道词典-网络释义，该结果不一定存在
            List<Object> jsonObjectList = (List) webOfDetail.toList();//webOfDetail.toList()中的元素已经被硬编码为HashMap
            webs = new ArrayList<>();
            for (Object obj : jsonObjectList) {
                Map<Object, Object> map1 = (Map) obj;
                String webKey = (String) map1.get("key");
                String webValue = "";
                ArrayList<String> aList = (ArrayList) map1.get("value");
                for (String v : aList) {
                    webValue = webValue + " " + v;
                }
                webKey = webKey.trim();
                webValue = webValue.trim();
                webs.add(new WebTranslation(webKey, webValue));
            }

        } catch (JSONException jsonException) {
            return new ArrayList<>();
        }

        return webs;
    }

    private void getResultCN(JSONObject resultJson) {
        resultPrint.append(PrintCN.show);
        resultPrint.append(resultSet.getQuery());
        resultPrint.append("\n");
        resultPrint.append(PrintCN.dest);
        resultPrint.append(langCN(resultSet.getLang()));
        resultPrint.append("\n");

        if (resultSet.getBasicTranslation() != null) {
            resultPrint.append(PrintCN.phonetic);
            resultPrint.append("\n\t" + PrintCN.ukPhonetic + " ");
            resultPrint.append(resultSet.getBasicTranslation().getUkPhonetic());
            resultPrint.append("\n\t" + PrintCN.usPhonetic + " ");
            resultPrint.append(resultSet.getBasicTranslation().getUsPhonetic());
            resultPrint.append("\n");
            resultPrint.append(PrintCN.basicTranslation);
            List<BasicExplains> explains = resultSet.getBasicTranslation().getExplainsList();
            for (BasicExplains exp : explains) {
                resultPrint.append("\n\t\"" + exp.getExplains() + "\"  ");
            }
        }
        resultPrint.append("\n");
        resultPrint.append(PrintCN.detailTranslation);
        resultPrint.append("\n");
        for (String str : resultSet.getTranslation()) {
            resultPrint.append("\t\"" + str + "\"  ");
        }
        resultPrint.append("\n");

        //如果用户请求web翻译
        if (ParametersManipulator.parameters.getWebInformation()) {
            List<WebTranslation> webs = getWebTranslations(resultJson);
            if (webs != null) {
                resultPrint.append(PrintCN.webTranslation);
                resultPrint.append("\n");
                resultApp(webs);
            }
        }

        resultPrint.append("\n");

        resultPrint.append(PrintCN.searchCounts);
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (resultSet.getBasicTranslation() != null) {
            resultPrint.append(resultSet.getBasicTranslation().getCount());
            resultPrint.append(PrintCN.searchHistory);
            resultPrint.append(" ");
            resultPrint.append(PrintCN.recentlySearchDate);
            resultPrint.append(time.format(resultSet.getBasicTranslation().getDate()));
        } else {
            resultPrint.append(0);
            resultPrint.append(PrintCN.searchHistory);
            resultPrint.append(" ");
            resultPrint.append(PrintCN.recentlySearchDate);
            resultPrint.append(time.format(new Date()));
        }


    }


    private void getResultEN(JSONObject resultJson) {
        resultPrint.append(PrintEN.show);
        resultPrint.append(resultSet.getQuery());
        resultPrint.append("\n");
        resultPrint.append(PrintEN.dest);
        resultPrint.append(langEN(resultSet.getLang()));
        resultPrint.append("\n");
        if (resultSet.getBasicTranslation() != null) {
            resultPrint.append(PrintEN.phonetic);
            resultPrint.append("\n\t" + PrintEN.ukPhonetic + " ");
            resultPrint.append(resultSet.getBasicTranslation().getUkPhonetic());
            resultPrint.append("\n\t" + PrintEN.usPhonetic + " ");
            resultPrint.append(resultSet.getBasicTranslation().getUsPhonetic());
            resultPrint.append("\n");
            resultPrint.append(PrintEN.basicTranslation);
            for (BasicExplains exp : resultSet.getBasicTranslation().getExplainsList()) {
                resultPrint.append("\n\t\"" + exp.getExplains() + "\"  ");
            }
        }
        resultPrint.append("\n");
        resultPrint.append(PrintEN.detailTranslation);
        resultPrint.append("\n");
        for (String str : resultSet.getTranslation()) {
            resultPrint.append("\t\"" + str + "\"  ");
        }
        resultPrint.append("\n");

        //如果用户请求web翻译
        if (ParametersManipulator.parameters.getWebInformation()) {
            List<WebTranslation> webs = getWebTranslations(resultJson);
            if (webs != null) {
                resultPrint.append(PrintEN.webTranslation);
                resultPrint.append("\n");
                resultApp(webs);
            }
        }
        resultPrint.append("\n");

        SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss yyyy-MM-dd");
        resultPrint.append(PrintEN.searchCounts);
        if (resultSet.getBasicTranslation() != null) {
            resultPrint.append(resultSet.getBasicTranslation().getCount());
            resultPrint.append(PrintEN.searchHistory);
            resultPrint.append(" ");
            resultPrint.append(PrintEN.recentlySearchDate);
            resultPrint.append(time.format(resultSet.getBasicTranslation().getDate()));
        } else {
            resultPrint.append(0);
            resultPrint.append(PrintEN.searchHistory);
            resultPrint.append(" ");
            resultPrint.append(PrintEN.recentlySearchDate);
            resultPrint.append(time.format(new Date()));
        }


    }

    public static void resultApp(List<WebTranslation> webs) {
        for (WebTranslation web : webs) {
            resultPrint.append("\t");
            resultPrint.append(web.getWebKey());
            resultPrint.append("\n\t\"");
            resultPrint.append(web.getWebValue());
            resultPrint.append("\"\n");
        }


    }


    public static String langCN(String lang) {
        for (int i = 0;
             i < 81;
             i++) {
            if (lang.equals(lang1[i])) {
                return lang2[i];
            }
        }
        return "英文翻中文";
    }

    public static String langEN(String lang) {
        for (int i = 0;
             i < 81;
             i++) {
            if (lang.equals(lang1[i])) {
                return lang3[i];
            }
        }
        return "Chinese translate English";
    }

    private static String[] lang1 = new String[81];
    private static String[] lang2 = new String[81];//CN
    private static String[] lang3 = new String[81];//EN

    static {
        Map<String, String> map = new TreeMap<>();//TreeMap<>在map集合中插入新元素时，会将该新元素有序地插入
        map.put("zh-CHS", "中文");
        map.put("ja", "日文");
        map.put("EN", "英文");
        map.put("ko", "韩文");
        map.put("fr", "法文");
        map.put("ru", "俄文");
        map.put("pt", "葡萄牙文");
        map.put("es", "西班牙文");
        map.put("vi", "越南文");

        int k = 0;
        for (Map.Entry<String, String> entry1 : map.entrySet()) {
            for (Map.Entry<String, String> entry2 : map.entrySet()) {
                lang1[k] = entry1.getKey() + "2" + entry2.getKey();
                lang2[k] = entry1.getValue() + "转" + entry2.getValue();
                String from = goal(entry1.getKey());
                String to = goal(entry2.getKey());
                lang3[k] = from + " to " + to;
                //System.out.println(lang3[k]);
                ++k;
            }
        }
    }

    public static String goal(String lang) {
        switch (lang) {
            case "zh-CHS":
                return "Chinese";
            case "EN":
                return "English";
            case "ko":
                return "Korean";
            case "ja":
                return "Japanese";
            case "fr":
                return "French";
            case "ru":
                return "Russian";
            case "pt":
                return "Portuguese";
            case "es":
                return "Spanish";
            case "vi":
                return "Vietnamese";
            default:
                return "English";
        }

    }

}
