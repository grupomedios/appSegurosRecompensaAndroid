
package com.grupomedios.dclub.segurosrecompensa.notifications.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Notification {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("successful")
    @Expose
    private Integer successful;
    @SerializedName("failed")
    @Expose
    private Integer failed;
    @SerializedName("converted")
    @Expose
    private Integer converted;
    @SerializedName("remaining")
    @Expose
    private Integer remaining;
    @SerializedName("queued_at")
    @Expose
    private Integer queuedAt;
    @SerializedName("send_after")
    @Expose
    private Integer sendAfter;
    @SerializedName("canceled")
    @Expose
    private Boolean canceled;
    @SerializedName("url")
    @Expose
    private Object url;
    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("headings")
    @Expose
    private Headings headings;
    @SerializedName("contents")
    @Expose
    private Contents contents;
    @SerializedName("isAndroid")
    @Expose
    private boolean isAndroid;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getSuccessful() {
        return successful;
    }

    public void setSuccessful(Integer successful) {
        this.successful = successful;
    }

    public Integer getFailed() {
        return failed;
    }

    public void setFailed(Integer failed) {
        this.failed = failed;
    }

    public Integer getConverted() {
        return converted;
    }

    public void setConverted(Integer converted) {
        this.converted = converted;
    }

    public Integer getRemaining() {
        return remaining;
    }

    public void setRemaining(Integer remaining) {
        this.remaining = remaining;
    }

    public Integer getQueuedAt() {
        return queuedAt;
    }

    public void setQueuedAt(Integer queuedAt) {
        this.queuedAt = queuedAt;
    }

    public Integer getSendAfter() {
        return sendAfter;
    }

    public void setSendAfter(Integer sendAfter) {
        this.sendAfter = sendAfter;
    }

    public Boolean getCanceled() {
        return canceled;
    }

    public void setCanceled(Boolean canceled) {
        this.canceled = canceled;
    }

    public Object getUrl() {
        return url;
    }

    public void setUrl(Object url) {
        this.url = url;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Headings getHeadings() {
        return headings;
    }

    public void setHeadings(Headings headings) {
        this.headings = headings;
    }

    public Contents getContents() {
        return contents;
    }

    public void setContents(Contents contents) {
        this.contents = contents;
    }

    public boolean isAndroid() {
        return isAndroid;
    }


}
