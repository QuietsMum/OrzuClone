package orzu.org.chat;

import android.graphics.Bitmap;
public class chatItems {
    String Name,chat,time,notification,id, chat_id, mes_delivered, mes_saw;
    String img;
    public chatItems() {    }
    public chatItems(String name, String id, String chat_id,  String mes_delivered, String mes_saw, String notification, String time, String img) {
        Name = name;
        this.id = id;
        this.chat_id = chat_id;
        this.mes_delivered = mes_delivered;
        this.mes_saw = mes_saw;
        this.time = time;
        this.notification = notification;
        this.img = img;
    }

    public String getName() {
        return Name;
    }
    public void setName(String name) {
        Name = name;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getchat_id() {
        return chat_id;
    }
    public void setchat_id(String chat_id) {
        this.id = chat_id;
    }
    public String getDel() {
        return mes_delivered;
    }
    public void setDel(String chat) {
        this.mes_delivered = mes_delivered;
    }
    public String getSaw() {
        return mes_saw;
    }
    public void setSaw(String chat) {
        this.mes_saw = mes_saw;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getNotification() {
        return notification;
    }
    public void setNotification(String notification) {
        this.notification = notification;
    }
    public String getImg() {
        return img;
    }
    public void setImg(String img) {
        this.img = img;
    }
}
