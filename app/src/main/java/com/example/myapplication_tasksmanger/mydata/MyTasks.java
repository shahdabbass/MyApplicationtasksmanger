package com.example.myapplication_tasksmanger.mydata;

import java.io.Serializable;

public class MyTasks implements Serializable {
public String id;//رقم المهمه
public int importance;//درجه الاهميه 1-5
public String shortTitle;//عنوان قصير
public String text;//نص المهمه
public long time;//زمن بناء المهمه
public boolean isCompleted;//هل تمت المهمه
public String subjId;//رقم موضوع المهمه
public String userId;//رقم المستعمل الذي اضاف المهمه
    public String img;
    public boolean isStar;

    public void setStar(boolean star) {
        isStar = star;
    }

    public boolean isStar() {
        return isStar;
    }


    public void setImg(String img) {
        this.img = img;
    }

    public String getImg() {
        return img;
    }

    public void setId(String keyId) {
        this.id = keyId;
    }

    public String getId() {
        return id;
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
                "keyId=" + id +
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
