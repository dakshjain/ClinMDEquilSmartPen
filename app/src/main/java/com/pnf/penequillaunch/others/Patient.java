package com.pnf.penequillaunch.others;


public class Patient {
    private String firstName ;
    private String lastName ;
    private String age ;
    private String mobile ;
    private String gender ;
    private String followUp ;
    private String ugcID ;
    private String Date ;
    private String ID ;
    public Patient(){
        firstName = "";
        lastName = "";
        age = "";
        mobile = "";
        gender = "";
        followUp = "";
        ugcID = "";
        ID = "";
        Date = "";

    }
    public Patient(String firstName, String age, String mobile, String ID , String lastName , String gender , String ugcID , String Date , String followUp) {
        this.firstName = firstName;
        this.age = age;
        this.mobile = mobile;
        this.ID = ID;
        this.lastName = lastName;
        this.gender = gender ;
        this.ugcID = ugcID ;
        this.Date = Date ;
        this.followUp = followUp;
    }
    public Patient(String firstName  , String lastName , String date ,  String mobile , String gender , String ugcid){
        this.firstName  =firstName;
        this. lastName   =lastName ;
        this.Date = date ;
        this.mobile = mobile ;
        this.gender = gender ;
        this.ugcID = ugcid ;
    }

    public Patient(String firstName  , String lastName , String date ,  String mobile , String gender , String ugcid , String patientid){
        this.firstName  =firstName;
        this. lastName   =lastName ;
        this.Date = date ;
        this.mobile = mobile ;
        this.gender = gender ;
        this.ugcID = ugcid ;
        this.ID = patientid;
    }


    public String getfirstName() {
        return firstName;
    }

    public void setfirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getlastName() {
        return lastName;
    }

    public void setlastName(String lastName) {
        this.lastName = lastName;
    }

    public String getmobile() {
        return mobile;
    }

    public void setmobile(String mobile) {
        this.mobile = mobile;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getfollowUp() {
        return followUp;
    }

    public void setfollowUp(String followUp) {
        this.followUp = followUp;
    }

    public String getUgcID() {
        return ugcID;
    }

    public void setUgcID(String ugcID) {
        this.ugcID = ugcID;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String Date) {
        this.Date = Date;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
