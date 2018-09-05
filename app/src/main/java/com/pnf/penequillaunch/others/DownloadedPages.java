package com.pnf.penequillaunch.others;

import android.database.Cursor;

public class DownloadedPages {

    private String firstName;
    private String lastName;
    private String mobile;
    private String date;
    private String age;
    private String gender;
    private String nextPage;
    private String doctor;
    private String page;
    private String followUp;
    private String diagnose;
    private String ugcid;


    public DownloadedPages() {
        lastName = "";
        firstName = "";
        mobile = "";
        age = "";
        gender = "";
        nextPage = "";
        doctor = new Doctor().getRegistrationID();
        page = "";
        date = "";
        followUp = "";
        diagnose = "";
        ugcid = "";

    }

    public DownloadedPages(String UGCId,String firstName, String lastName, String mobile, String date, String age, String gender, String nextPage, String doctor, String page, String followUp, String diagnose) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobile = mobile;
        this.date = date;
        this.age = age;
        this.gender = gender;
        this.nextPage = nextPage;
        this.doctor = doctor;
        this.page = page;
        this.followUp = followUp;
        this.diagnose = diagnose;
        this.ugcid = UGCId;
    }
    public DownloadedPages(String pageId){
        Cursor c = PatientDetailsDatabaseHelper.getdatabasehelper().returnPageData(pageId);
        if(c.moveToNext()){
            try{
                firstName = c.getString(0);
                lastName = c.getString(1);
                page = c.getString(2);
                mobile = c.getString(3);
                age = string_age(c.getString(4));
                date = c.getString(5);
                gender = c.getString(6);
                followUp =c.getString(7);
                diagnose = c.getString(8);
                nextPage = c.getString(9);
                ugcid = c.getString(10);
                doctor = new Doctor().getRegistrationID();

            }catch (Exception e){e.printStackTrace();}
        }
    }

    private String string_age(String string) {
        if(string.equalsIgnoreCase("0")) return "NA";
        else return string;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNextPage() {
        return nextPage;
    }

    public void setNextPage(String nextPage) {
        this.nextPage = nextPage;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFollowUp() {
        return followUp;
    }

    public void setFollowUp(String followUp) {
        this.followUp = followUp;
    }

    public String getDiagnose() {
        return diagnose;
    }

    public void setDiagnose(String diagnose) {
        this.diagnose = diagnose;
    }


    public String getUgcid() {
        return ugcid;
    }

    public void setUgcid(String ugcid) {
        this.ugcid = ugcid;
    }
}