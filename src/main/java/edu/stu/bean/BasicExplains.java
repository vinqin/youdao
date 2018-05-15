package edu.stu.bean;

public class BasicExplains {
    private Integer id;
    private String explains;
    private Integer bId;

    public BasicExplains() {

    }

    public BasicExplains(String explains, Integer bId) {
        this.explains = explains;
        this.bId = bId;
    }

    public String getExplains() {
        return explains;
    }

    public void setExplains(String explains) {
        this.explains = explains;
    }

    public Integer getbId() {
        return bId;
    }

    public void setbId(Integer bId) {
        this.bId = bId;
    }
}
