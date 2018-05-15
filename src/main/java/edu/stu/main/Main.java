package edu.stu.main;

import edu.stu.bean.BasicTranslation;
import edu.stu.bean.Parameters;
import edu.stu.bean.ResultSet;
import edu.stu.dao.DBManipulator;
import edu.stu.util.*;

public class Main {

    public static void main(String[] args) {
        final ParametersManipulator paramManipulator = new ParametersManipulator(args);//必须保证ParametersManipulator最开始被实例化一次
        final Parameters PARAM = paramManipulator.parameters;
        BasicTranslation basic = DBManipulator.getBasicTranslation(PARAM.getQuery());
        //String lang = PARAM.getSrcType() + "2" + PARAM.getDestType();
        //if (basic != null && basic.getLang().toLowerCase().equals(lang.toLowerCase())) {
        if (basic != null) {
            if (PARAM.getDestType().toLowerCase().equals("zh-chs") || PARAM.getDestType().toLowerCase().equals
                    ("auto")) {
                TOOL.getResultCN(basic);

            } else {
                TOOL.getResultEN(basic);
            }
            //根据用户需求下载并播放mp3文件
            TOOL.mp3(basic);
            System.out.println(ResultManipulator.resultPrint);
            return;//如果从本地数据库中查找到了翻译内容，则不调用有道API
        }

        try {

            String resultJson = TOOL.requestForHttp();//从有道API处获得JSON字符串结果
            final ResultManipulator resultManipulator = new ResultManipulator(resultJson, PARAM.getDestType());
            final ResultSet RESULTSET = resultManipulator.resultSet;

            System.out.println(resultManipulator.resultPrint);

            if (RESULTSET.getBasicTranslation() != null) {
                DBManipulator.addBasicTranslation(RESULTSET.getBasicTranslation());//往数据库中添加记录
            }

            //根据用户需求下载并播放mp3文件
            TOOL.mp3(RESULTSET.getBasicTranslation());

        } catch (Exception e) {
            e.printStackTrace();
            if (paramManipulator.parameters.getDestType().toLowerCase().equals("zh-chs")) {
                System.out.println(PrintCN.internetError);
            } else {
                System.out.println(PrintEN.internetError);
            }
        }


    }
}
