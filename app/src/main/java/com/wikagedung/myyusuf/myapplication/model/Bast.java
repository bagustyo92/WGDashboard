package com.wikagedung.myyusuf.myapplication.model;

import java.io.Serializable;

/**
 * Created by myyusuf on 11/4/16.
 */
public class Bast implements Serializable {

    private String name;
    private String bastDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBastDate() {
        return bastDate;
    }

    public void setBastDate(String bastDate) {
        this.bastDate = bastDate;
    }
}
