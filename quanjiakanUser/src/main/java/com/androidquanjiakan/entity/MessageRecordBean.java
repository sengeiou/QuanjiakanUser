package com.androidquanjiakan.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 作者：Administrator on 2017/3/16 10:54
 * <p>
 * 邮箱：liuzj@hi-board.com
 */

@Entity
public class MessageRecordBean {
    @Id(autoincrement = true)
    private Long id;

    private String Message;
    private String Time;
    private String Title;
    @Generated(hash = 396724350)
    public MessageRecordBean(Long id, String Message, String Time, String Title) {
        this.id = id;
        this.Message = Message;
        this.Time = Time;
        this.Title = Title;
    }
    @Generated(hash = 744517954)
    public MessageRecordBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getMessage() {
        return this.Message;
    }
    public void setMessage(String Message) {
        this.Message = Message;
    }
    public String getTime() {
        return this.Time;
    }
    public void setTime(String Time) {
        this.Time = Time;
    }
    public String getTitle() {
        return this.Title;
    }
    public void setTitle(String Title) {
        this.Title = Title;
    }
    
}
