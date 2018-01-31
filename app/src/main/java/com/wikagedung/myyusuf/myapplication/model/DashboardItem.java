package com.wikagedung.myyusuf.myapplication.model;

import android.widget.ImageView;

import java.math.BigDecimal;

/**
 * Created by myyusuf on 9/22/16.
 */
public class DashboardItem {

    private String image1;
    private String label1;
    private String label2;
    private String label3;
    private String label4;

    private BigDecimal value1 = new BigDecimal("0");
    private BigDecimal value2 = new BigDecimal("0");

    private String statusImageName = "equal_trend";
    private String percentageLabelColor = "blue";

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getLabel4() {
        return label4;
    }

    public void setLabel4(String label4) {
        this.label4 = label4;
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

    public BigDecimal getValue2() {
        return value2;
    }

    public void setValue2(BigDecimal value2) {
        this.value2 = value2;
    }

    public BigDecimal getValue1() {
        return value1;
    }

    public void setValue1(BigDecimal value1) {
        this.value1 = value1;
    }

    public String getStatusImageName() {
        return statusImageName;
    }

    public void setStatusImageName(String statusImageName) {
        this.statusImageName = statusImageName;
    }

    public String getPercentageLabelColor() {
        return percentageLabelColor;
    }

    public void setPercentageLabelColor(String percentageLabelColor) {
        this.percentageLabelColor = percentageLabelColor;
    }
}
