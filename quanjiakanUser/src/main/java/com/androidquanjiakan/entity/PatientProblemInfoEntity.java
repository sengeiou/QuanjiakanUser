package com.androidquanjiakan.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/7/12 0012.
 */
public class PatientProblemInfoEntity implements Serializable {
    private String user_id;
    private String name;
    private String sex;
    private String age;

    private String id;
    private String target_id;
    private String title;
    private String date;
    private String clinic;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTarget_id() {
        return target_id;
    }

    public void setTarget_id(String target_id) {
        this.target_id = target_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getClinic() {
        return clinic;
    }

    public void setClinic(String clinic) {
        this.clinic = clinic;
    }

    @Override
    public String toString() {
        return "PatientProblemInfoEntity{" +
                "user_id='" + user_id + '\'' +
                ", name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", age='" + age + '\'' +
                ", id='" + id + '\'' +
                ", target_id='" + target_id + '\'' +
                ", title='" + title + '\'' +
                ", date='" + date + '\'' +
                ", clinic='" + clinic + '\'' +
                '}';
    }
}
