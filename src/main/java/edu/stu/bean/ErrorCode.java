package edu.stu.bean;

public class ErrorCode {
    private Integer code;

    private boolean status;

    private String message;

    public ErrorCode(Integer code) {
        setCode(code);
        processCode(code);
    }

    public Integer getCode() {
        return code;
    }

    private void setCode(Integer code) {
        this.code = code;
    }

    public boolean isStatus() {
        return status;
    }

    private void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    private void setMessage(String message) {
        this.message = message;
    }

    private void processCode(Integer code) {
        switch (code) {
            case 0:
                setStatus(true);
                setMessage("query success");
                break;
            case 101:
                setStatus(false);
                setMessage("缺少必填的参数，出现这个情况还可能是et的值和实际加密方式不对应");
                break;
            case 102:
                setStatus(false);
                setMessage("不支持的语言类型");
                break;
            case 103:
                setStatus(false);
                setMessage("翻译文本过长");
                break;
            case 104:
                setStatus(false);
                setMessage("不支持的API类型");
                break;
            case 105:
                setStatus(false);
                setMessage("不支持的签名类型");
                break;
            case 106:
                setStatus(false);
                setMessage("不支持的响应类型");
                break;
            case 107:
                setStatus(false);
                setMessage("不支持的传输加密类型");
                break;
            case 108:
                setStatus(false);
                setMessage("appKey无效，请先注册账号，然后登录后台创建应用和实例并完成绑定， 可获得应用ID和密钥等信息，其中应用ID就是appKey（ 注意不是应用密钥）");
                break;
            case 109:
                setStatus(false);
                setMessage("batchLog格式不正确");
                break;
            case 110:
                setStatus(false);
                setMessage("无相关服务的有效实例");
                break;
            case 111:
                setStatus(false);
                setMessage("开发者账号无效");
                break;
            case 113:
                setStatus(false);
                setMessage("q不能为空");
                break;
            case 201:
                setStatus(false);
                setMessage("解密失败，可能为DES,BASE64,URLDecode的错误");
                break;
            case 202:
                setStatus(false);
                setMessage("签名检验失败");
                break;
            case 203:
                setStatus(false);
                setMessage("访问IP地址不在可访问IP列表");
                break;
            case 205:
                setStatus(false);
                setMessage("请求的接口与应用的平台类型不一致");
                break;
            case 301:
                setStatus(false);
                setMessage("辞典查询失败");
                break;
            case 302:
                setStatus(false);
                setMessage("翻译查询失败");
                break;
            case 303:
                setStatus(false);
                setMessage("服务端的其它异常");
                break;
            case 401:
                setStatus(false);
                setMessage("账户已经欠费");
                break;
            case 411:
                setStatus(false);
                setMessage("访问频率受限,请稍后访问");
                break;
            case 412:
                setStatus(false);
                setMessage("长请求过于频繁，请稍后访问");
                break;
            default:
                setStatus(false);
                setMessage("未知错误");
        }
    }
}
