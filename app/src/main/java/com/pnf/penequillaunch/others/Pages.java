package com.pnf.penequillaunch.others;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.pnf.penequillaunch.equilsdk.Const;

import java.io.File;

public class Pages {

    private final static char dc = '"';
    public String firstName;
    public String lastName;
    public String mobile;
    public String date;
    public String age;
    public String gender;
    public String followUp;
    public String diagnose;
    public String prescription;
    public String fullpage;
    public String nextPage;
    public String UGCId;
    int pageID = 0;
    private Context context;

    public Pages(String nextPage, String lastName, int pageID, String firstName, String mobile, String date, String age, String gender, String prescription, String fullpage, String diagnose, String followUp , String UGCId) {
        this.firstName = firstName;
        this.mobile = mobile;
        this.date = date;
        this.age = age;
        this.gender = gender;
        this.nextPage = nextPage;
        this.prescription = prescription;
        this.fullpage = fullpage;
        this.pageID = pageID;
        this.lastName = lastName;
        this.followUp = followUp;
        this.diagnose = diagnose;
        this.UGCId=UGCId;
    }
    public Pages(String firstName , String lastName , int pageId ,String mobile , String age , String date , String  gender , String followUp , String diagnose ,  String ugcid){
        this.firstName =  firstName ;
        this.lastName = lastName ;
        this.pageID = pageId ;
        this.mobile = mobile ;
        this.age = age ;
        this.date = date ;
        this.gender = gender  ;
        this.followUp = followUp ;
        this.diagnose = diagnose ;
        this.UGCId = ugcid;
    }

    public Pages() {
        firstName = "";
        lastName = "";
        mobile = "";
        nextPage = "";
        date = "";
        age = "";
        gender = "";
        prescription = "";
        followUp = "";
        diagnose = "";
        fullpage = "";
        UGCId = "";
    }

    public Pages(int pageID) {
        this.pageID = pageID;
        firstName = "";
        lastName = "";
        mobile = "";
        nextPage = "";
        date = "";
        age = "";
        gender = "";
        prescription = "";
        followUp = "";
        diagnose = "";
        fullpage = "";
        UGCId = "";
    }
    public Pages(String pageID){

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

    public String getNextPage() {
        return nextPage;
    }

    public void setNextPage(String nextPage) {
        this.nextPage = nextPage;
    }

    public int getPageID() {
        return pageID;
    }

    public void setPageID(int pageID) {
        this.pageID = pageID;
    }

    public String getName() {
        return firstName;
    }

    public void setName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    public String getFullpage() {
        return fullpage;
    }

    public void setFullpage(String fullpage) {
        this.fullpage = fullpage;
    }


    public String getUGCId() {
        return UGCId;
    }

    public void SETUGCId(String fullpage) {
        this.UGCId = UGCId;
    }

    public void doAdjustments(Context context) {
        this.context = context;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        float multiplier = preferences.getFloat("paper_scale", 10f);
        //float multiplier = 4.6f;
        int height,width;
        height= (int)((Const.Coordinates.defaultNameEndY-Const.Coordinates.defaultNameStartY)*multiplier);
        width= (int)((Const.Coordinates.defaultNameEndX-Const.Coordinates.defaultNameStartX)*multiplier);
        firstName = addPrefixAndSuffix(firstName, height, width);

        height= (int)((Const.Coordinates.defaultMobileEndY-Const.Coordinates.defaultMobileStartY)*multiplier);
        width= (int)((Const.Coordinates.defaultMobileEndX-Const.Coordinates.defaultMobileStartX)*multiplier);
        mobile = addPrefixAndSuffix(mobile, height, width);

        height= (int)((Const.Coordinates.defaultDiagnoseEndY-Const.Coordinates.defaultDiagnoseStartY)*multiplier);
        width= (int)((Const.Coordinates.defaultDiagnoseEndX-Const.Coordinates.defaultDiagnoseStartX)*multiplier);
        diagnose = addPrefixAndSuffix(diagnose, height, width);

        height= (int)((Const.Coordinates.defaultNextPageEndY-Const.Coordinates.defaultNextPageStartY)*multiplier);
        width= (int)((Const.Coordinates.defaultNextPageEndX-Const.Coordinates.defaultNextPageStartX)*multiplier);
        nextPage = addPrefixAndSuffix(nextPage, height, width);

        height= (int)((Const.Coordinates.defaultFollowUPEndY-Const.Coordinates.defaultFollowUPStartY)*multiplier);
        width= (int)((Const.Coordinates.defaultFollowUPEndX-Const.Coordinates.defaultFollowUPStartX)*multiplier);
        followUp = addPrefixAndSuffix(followUp, height, width);
/*
        height= (int)((Const.Coordinates.defaultDateEndY-Const.Coordinates.defaultDateStartY)*multiplier);
        width= (int)((Const.Coordinates.defaultDateEndX-Const.Coordinates.defaultDateStartY)*multiplier);
        date = addPrefixAndSuffix(date, height, width);
        */
        height= (int)((Const.Coordinates.defaultAgeEndY-Const.Coordinates.defaultAgeStartY)*multiplier);
        width= (int)((Const.Coordinates.defaultAgeEndX-Const.Coordinates.defaultAgeStartX)*multiplier);
        age = addPrefixAndSuffix(age, height, width);

        height= (int)((Const.Coordinates.defaultGenderEndY-Const.Coordinates.defaultGenderStartY)*multiplier);
        width= (int)((Const.Coordinates.defaultGenderEndX-Const.Coordinates.defaultGenderStartX)*multiplier);
        gender = addPrefixAndSuffix(gender, height, width);

        prescription = addPrefixAndSuffix(prescription, (int) (Const.Coordinates.pageVerticalPixels * multiplier), (int) (Const.Coordinates.pageHorizontalPixels * multiplier));
        //fullpage = addPrefixAndSuffix(fullpage, (int)(Resources.getSystem().getDisplayMetrics().heightPixels*(1.06)),(int)(Resources.getSystem().getDisplayMetrics().widthPixels*(1.35)));
        /*Log.d("Pages height",""+(int)(Resources.getSystem().getDisplayMetrics().heightPixels));*/
        fullpage = addPrefixAndSuffix(fullpage, (int)(Const.Coordinates.pageVerticalPixels * multiplier), (int) (Const.Coordinates.pageHorizontalPixels * multiplier) );
        Log.d("Pages height",""+ (int)(Const.Coordinates.pageVerticalPixels * multiplier));

    }

    private String addPrefixAndSuffix(String field, int height, int width) {
        File localfile = context.getFileStreamPath("prescription.png");

        String prefix = "<html>\n" +
                "<style>\n" +
                "    div{\n" +
                "     background-image: url(\"file://"+localfile.getPath()+"\");\n" +
                "     background-repeat: no-repeat;\n" +
                "     background-size: " + width + "px " + height + "px;\n" +
                "     height: " + height + "px;\n" +
                "     width: " + width + "px;\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "</style>\n" +
                "<body >\n" +
                "<center><div><svg height=" + dc + height + dc + " width=" + dc + width + dc + " >" +
                "\n<path d=" + dc + "M";
        String suffix = "" + dc + " stroke=" + dc + "blue" + dc + " stroke-width=" + dc + "2.5" + dc + "/>\n</svg></div></center></body></html>";

        if (field.length() > 1)
            return prefix + field.trim().substring(1) +suffix ;
        else return field;
    }
}