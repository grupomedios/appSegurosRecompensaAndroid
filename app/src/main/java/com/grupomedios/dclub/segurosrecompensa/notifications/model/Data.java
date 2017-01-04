
package com.grupomedios.dclub.segurosrecompensa.notifications.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("foo")
    @Expose
    private String foo;
    @SerializedName("your")
    @Expose
    private String your;

    public String getFoo() {
        return foo;
    }

    public void setFoo(String foo) {
        this.foo = foo;
    }

    public String getYour() {
        return your;
    }

    public void setYour(String your) {
        this.your = your;
    }

}
