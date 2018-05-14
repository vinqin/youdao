package edu.stu.main;

import edu.stu.util.*;

public class Main {

    public static void main(String[] args) {
        new ParametersManipulator(args);//必须保证ParametersManipulator最开始被实例化一次

        if (TOOL.getResultFromDB()) {
            return;//如果从本地数据库中查找到了翻译内容，则不调用有道API
        }

        try {
            String resultJsonString = TOOL.requestForHttp();//从有道API处获得JSON字符串结果
            new ResultManipulator(resultJsonString, ParametersManipulator.parameters.getDestType());//必须保证ResultManipulator最开始被实例化一次
            TOOL.getResultFromAPI();

        } catch (Exception e) {
            //网络异常
            String type = ParametersManipulator.parameters.getDestType();
            if (type.toLowerCase().equals("zh-chs")) {
                System.out.println(PrintCN.INTERNET_ERROR);
            } else {
                System.out.println(PrintEN.INTERNET_ERROR);
            }
            e.printStackTrace();
        }
    }
}
