package com.pnf.penequillaunch.others;

public class Page {
    public String diagnose ;
    public String page ;
    public String timestamp ;

    public Page(){
        diagnose = "";
        page= "";
        timestamp = "" ;
    }

    public String getDiagnose() {
        return diagnose;
    }

    public void setDiagnose(String diagnose) {
        this.diagnose = diagnose;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
