package com.androidquanjiakan.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/7 0007.
 */
public class PhoneDoctorRecordEntity implements Serializable {
    /**
     "member_id": "10000",
     "name": "电话医生",
     "service_type": "2",
     "gender": "f",
     "age": "50",
     "icon": "http://picture.quanjiakan.com:9080/quanjiakan/resources/doctor/20161109205701_zdspkq.jpg",
     "mobile": "01059131209",
     "title": "专家",
     "position_id": "1",
     "doctor_grade_id": "1",
     "clinic_name_id": "21",
     "hospital_id": "0",
     "hospital_name": "在线咨询",
     "good_at": "手术,眼科杂症",
     "recommend_rate": "5",
     "description": "医生介绍",
     "creator": "1",
     "createtime": "2016-11-26 09:40:06.0",
     "status": "2",
     "province": "广东",
     "city": "广州",
     "dist": "荔湾区",
     "certification": null,
     "clinic_phone": null,
     "verify_user_name": "caiwen",
     "is_delete": "1",
     "type": "5",
     "user_id": "11108",
     "total_price": "0.01",
     "starttime": "1480149384250",
     "finishtime": "1511685384250",
     "clinic_name": "电话医生"
     */
    private String member_id;
    private String name;
    private String gender;
    private String age;
    private String icon;
    private String mobile;
    private String title;
    private String clinic_name_id;
    private String hospital_id;
    private String hospital_name;
    private String good_at;
    private String recommend_rate;
    private String description;
    private String creator;
    private String createtime;

    private String status;
    private String province;
    private String city;
    private String dist;
    private String certification;
    private String clinic_phone;
    private String verify_user_name;
    private String service_type;
    private String user_id;
    private String total;
    private String starttime;
    private String finishtime;
    private String clinic_name;
    private String total_price;

    public String getTotal_price() {
        return total_price;
    }

    public void setTotal_price(String total_price) {
        this.total_price = total_price;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getClinic_name_id() {
        return clinic_name_id;
    }

    public void setClinic_name_id(String clinic_name_id) {
        this.clinic_name_id = clinic_name_id;
    }

    public String getHospital_id() {
        return hospital_id;
    }

    public void setHospital_id(String hospital_id) {
        this.hospital_id = hospital_id;
    }

    public String getHospital_name() {
        return hospital_name;
    }

    public void setHospital_name(String hospital_name) {
        this.hospital_name = hospital_name;
    }

    public String getGood_at() {
        return good_at;
    }

    public void setGood_at(String good_at) {
        this.good_at = good_at;
    }

    public String getRecommend_rate() {
        return recommend_rate;
    }

    public void setRecommend_rate(String recommend_rate) {
        this.recommend_rate = recommend_rate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDist() {
        return dist;
    }

    public void setDist(String dist) {
        this.dist = dist;
    }

    public String getCertification() {
        return certification;
    }

    public void setCertification(String certification) {
        this.certification = certification;
    }

    public String getClinic_phone() {
        return clinic_phone;
    }

    public void setClinic_phone(String clinic_phone) {
        this.clinic_phone = clinic_phone;
    }

    public String getVerify_user_name() {
        return verify_user_name;
    }

    public void setVerify_user_name(String verify_user_name) {
        this.verify_user_name = verify_user_name;
    }

    public String getService_type() {
        return service_type;
    }

    public void setService_type(String service_type) {
        this.service_type = service_type;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getFinishtime() {
        return finishtime;
    }

    public void setFinishtime(String finishtime) {
        this.finishtime = finishtime;
    }

    public String getClinic_name() {
        return clinic_name;
    }

    public void setClinic_name(String clinic_name) {
        this.clinic_name = clinic_name;
    }

    @Override
    public String toString() {
        return "PhoneDoctorRecordEntity{" +
                "member_id='" + member_id + '\'' +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", age='" + age + '\'' +
                ", icon='" + icon + '\'' +
                ", mobile='" + mobile + '\'' +
                ", title='" + title + '\'' +
                ", clinic_name_id='" + clinic_name_id + '\'' +
                ", hospital_id='" + hospital_id + '\'' +
                ", hospital_name='" + hospital_name + '\'' +
                ", good_at='" + good_at + '\'' +
                ", recommend_rate='" + recommend_rate + '\'' +
                ", description='" + description + '\'' +
                ", creator='" + creator + '\'' +
                ", createtime='" + createtime + '\'' +
                ", status='" + status + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", dist='" + dist + '\'' +
                ", certification='" + certification + '\'' +
                ", clinic_phone='" + clinic_phone + '\'' +
                ", verify_user_name='" + verify_user_name + '\'' +
                ", service_type='" + service_type + '\'' +
                ", user_id='" + user_id + '\'' +
                ", total='" + total + '\'' +
                ", clinic_name='" + clinic_name + '\'' +
                '}';
    }
}
