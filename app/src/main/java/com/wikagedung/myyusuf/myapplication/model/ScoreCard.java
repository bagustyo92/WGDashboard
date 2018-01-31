package com.wikagedung.myyusuf.myapplication.model;

/**
 * Created by myyusuf on 10/29/16.
 */
public class ScoreCard {

    private int rowType;
    private String label1;
    private String label2;
    private String label3;
    private String label4;
    private String label5;
    private String label6;
    private String label7;
    private String label8;
    private String statusImageName;

    public ScoreCard() {
        rowType = 0;
        label1 = "";
        label2 = "";
        label3 = "";
        label4 = "";
        label5 = "";
        label6 = "";
        label7 = "";
        label8 = "";
        statusImageName = "";
    }

    public int getRowType() {
        return rowType;
    }

    public void setRowType(int rowType) {
        this.rowType = rowType;
    }

    public String getLabel1() {
        return label1;
    }

    public void setLabel1(String label1) {
        this.label1 = label1;
    }

    public String getLabel2() {
        return label2;
    }

    public void setLabel2(String label2) {
        this.label2 = label2;
    }

    public String getLabel3() {
        return label3;
    }

    public void setLabel3(String label3) {
        this.label3 = label3;
    }

    public String getLabel4() {
        return label4;
    }

    public void setLabel4(String label4) {
        this.label4 = label4;
    }

    public String getLabel5() {
        return label5;
    }

    public void setLabel5(String label5) {
        this.label5 = label5;
    }

    public String getLabel6() {
        return label6;
    }

    public void setLabel6(String label6) {
        this.label6 = label6;
    }

    public String getLabel7() {
        return label7;
    }

    public void setLabel7(String label7) {
        this.label7 = label7;
    }

    public String getLabel8() {
        return label8;
    }

    public void setLabel8(String label8) {
        this.label8 = label8;
    }

    public String getStatusImageName() {
        return statusImageName;
    }

    public void setStatusImageName(String statusImageName) {
        this.statusImageName = statusImageName;
    }
}
