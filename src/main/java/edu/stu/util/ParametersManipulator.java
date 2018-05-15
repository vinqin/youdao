package edu.stu.util;

import edu.stu.bean.Parameters;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class ParametersManipulator {
    public static Parameters parameters = null;

    /**
     * 命令行参数定义规则：
     * Java参数:shell变量
     * args[0]:src_type                    # 需要翻译的源语言类型(String)
     * args[1]:dest_type                   # 需要翻译的目标语言类型(String)
     * args[2]:filepath                    # 要翻译内容所在的文件的路径(String)
     * args[3]:web                         # 开启web翻译的判断标志(Boolean)
     * args[4]:basic_pronunciation         # 要播放的基本音标发音的类型，英式音标（uk）或美式音标（us）二选一，如果basic_pronunciation的值为null代表不发音  *
     * ====================================# (String)
     * args[5]:voice                       # 要播放的声音类型，女声（0），男声（1），默认值为0(Character)
     * args[6]:source_speech               # 用sox播放所查询的源内容的判断标志(Boolean)
     * args[7]:dest_speech                 # 用sox播放翻译后的内容的判断标志(Boolean)
     * args[8]:mp3_dir                     # 从有道API下载的基本音标的音频文件(.mp3格式)需要存放目录
     * args[9+]:$*                         # 需要翻译的内容(String)
     */
    public ParametersManipulator(String[] args) {
        if (args.length < 9) {
            System.out.println("The translate tool has a bug, please update your tool's version. Or contact with the " +
                    "author of this tool Vin(qfuqin@163.com).");
            System.exit(-1);//程序内部bug，可能的原因是shell脚本的参数传递错误
        }

        String srcType = checkType(args[0]);
        String destType = checkType(args[1]);
        String filepath = args[2];
        Boolean webInformation = Boolean.parseBoolean(args[3]);
        String basicPronunciation = args[4];
        Character voice = args[5].charAt(0);
        Boolean sourceSpeech = Boolean.parseBoolean(args[6]);
        Boolean destSpeech = Boolean.parseBoolean(args[7]);
        String mp3FilePath = args[8];
        String query;
        if (args.length > 9) {
            //需要翻译的内容来自于命令行参数
            query = queryFromArgument(args);
        } else if (filepath.equals("null")) {
            //需要翻译的内容来源于管道
            query = queryFromPipe();
        } else {
            //需要翻译的内容来自于文件
            query = queryFromFile(filepath, destType);
        }

        if (query.trim().equals("")) {
            query = defaultQuery(destType);
        }

        parameters = new Parameters();
        parameters.setSrcType(srcType);
        parameters.setDestType(destType);
        parameters.setFilepath(filepath);
        parameters.setWebInformation(webInformation);
        parameters.setBasicPronunciation(basicPronunciation);
        parameters.setVoice(voice);
        parameters.setSourceSpeech(sourceSpeech);
        parameters.setDestSpeech(destSpeech);
        parameters.setMp3FilePath(mp3FilePath);
        parameters.setQuery(query);

    }

    /**
     * 需要翻译的内容来自于终端命令行参数，命令行参数args的索引从9开始，所有字符串都是需要翻译的内容
     *
     * @param args 命令行参数
     * @return 需要翻译的内容
     **/
    private String queryFromArgument(String[] args) {
        StringBuilder sb = new StringBuilder();
        for (int i = 9; i < args.length; i++) {
            sb.append(args[i].trim());
            sb.append(" ");
        }
        return sb.toString().trim();
    }

    /**
     * 需要翻译的内容来自于文件
     *
     * @param filepath 文件路径
     * @param type     需要翻译的目标语言类型
     * @return 需要翻译的内容
     */
    private String queryFromFile(String filepath, String type) {
        StringBuilder sb = new StringBuilder();
        try {
            FileInputStream fInStream = new FileInputStream(filepath);
            InputStreamReader inStreamReader = new InputStreamReader(fInStream);
            BufferedReader bufReader = new BufferedReader(inStreamReader);
            String buffer;
            while ((buffer = bufReader.readLine()) != null) {
                sb.append(buffer.trim());
                sb.append(" ");
            }

            bufReader.close();
            inStreamReader.close();
            fInStream.close();

        } catch (IOException e) {
            //从文件中读取发生异常时，将需要翻译的内容统一为"请您输入需要翻译的内容。"
            sb.append(defaultQuery(type).trim());
        }
        return sb.toString().trim();
    }

    /**
     * 需要翻译的内容来自于管道（终端重定向）
     *
     * @return 需要翻译的内容
     */
    private String queryFromPipe() {
        StringBuilder sb = new StringBuilder();
        Scanner stdin = new Scanner(System.in);

        while (stdin.hasNextLine()) {
            String line = stdin.nextLine();
            sb.append(line.trim());
            sb.append(" ");
        }

        return sb.toString().trim();
    }

    private String checkType(String type) {
        //检查语言类型
        String language;
        switch (type.toLowerCase()) {
            case "zh-chs":
                language = "zh-CHS";
                break;
            case "ja":
                language = "ja";
                break;
            case "en":
                language = "EN";
                break;
            case "ko":
                language = "ko";
                break;
            case "fr":
                language = "fr";
                break;
            case "ru":
                language = "ru";
                break;
            case "pt":
                language = "pt";
                break;
            case "es":
                language = "es";
                break;
            case "vi":
                language = "vi";
                break;
            default:
                language = "auto";
        }

        return language;

    }

    private String defaultQuery(String type) {
        String query;
        switch (type.toLowerCase()) {
            case "zh-chs":
                query = "请您输入需要翻译的内容。";
                break;
            case "ja":
                query = "翻訳の内容を入力して下さい。";
                break;
            case "en":
                query = "Please type something to translate.";
                break;
            case "ko":
                query = "번역 된 내용을 입력 해 주세요.";
                break;
            case "fr":
                query = "Voulez-vous traduire dans son contenu.";
                break;
            case "ru":
                query = "Пожалуйста, вам нужен перевод ввода контент.";
                break;
            case "pt":
                query = "Por favor, digite precisa traduzir o conteúdo.";
                break;
            case "es":
                query = "Por favor ingrese la necesidad de traducción de contenido.";
                break;
            case "vi":
                query = "Xin vui lòng nhập nội dung cần dịch.";
                break;
            default:
                query = "Please type something to translate.";
        }

        return query;
    }

}
