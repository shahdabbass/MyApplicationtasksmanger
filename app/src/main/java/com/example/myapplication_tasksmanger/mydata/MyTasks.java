package com.example.myapplication_tasksmanger.mydata;

public class MyTasks {
public String keyId;//رقم المهمه
public int importance;//درجه الاهميه 1-5
public String shortTitle;//عنوان قصير
public String text;//نص المهمه
public long time;//زمن بناء المهمه
public boolean isCompleted;//هل تمت المهمه
public String subjId;//رقم موضوع المهمه
public String userId;//رقم المستعمل الذي اضاف المهمه

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getKeyId() {
        return keyId;
    }

    public int getImportance() {
        return importance;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }

    public String getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getSubjId() {
        return subjId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setSubjId(String subjId) {
        this.subjId = subjId;
    }
    @Override
    public String toString() {
        return "MyTasks{" +
                "keyId=" + keyId +
                ", importance=" + importance +
                ", shortTitle='" + shortTitle + '\'' +
                ", text='" + text + '\'' +
                ", time=" + time +
                ", isCompleted=" + isCompleted +
                ", subjId=" + subjId +
                ", userId=" + userId +
                '}';
    }
}
