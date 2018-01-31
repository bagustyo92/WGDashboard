package com.wikagedung.myyusuf.myapplication.model;

/**
 * Created by myyusuf on 10/10/16.
 */
public class NetProfit {

    private int rowType;
    private String label1;
    private String label2;
    private String label3;
    private String label2TextColor;

    public NetProfit() {
        rowType = 0;
        label1 = "";
        label2 = "";
        label3 = "";
        label2TextColor = "#000000";
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

    public String getLabel2TextColor() {
        return label2TextColor;
    }

    public void setLabel2TextColor(String label2TextColor) {
        this.label2TextColor = label2TextColor;
    }
}
