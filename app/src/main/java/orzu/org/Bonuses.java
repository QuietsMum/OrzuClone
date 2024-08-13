package orzu.org;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Bonuses {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("userid")
    @Expose
    private String userid;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("discription")
    @Expose
    private String discription;
    @SerializedName("images")
    @Expose
    private String images;
    @SerializedName("logo")
    @Expose
    private String logo;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("catid")
    @Expose
    private String catid;
    @SerializedName("percent")
    @Expose
    private String percent ="0";
    @SerializedName("date")
    @Expose
    private String date;

    public Bonuses() {
    }

    Bonuses(String id, String name, String percent, String logo, String discription) {
        this.name = name;
        this.id = id;
        if (logo.equals("null")) {
            this.logo = "images/logoNew.png";
        } else {
            this.logo = logo;
        }
        this.percent = percent;
        this.discription = discription;
    }

    Bonuses(String name, String percentage) {
        this.name = name;
        this.percent = percentage;
    }


    // Getter Methods

    public String getId() {
        return id;
    }

    public String getUserid() {
        return userid;
    }

    public String getName() {
        return name;
    }

    public String getDiscription() {
        return discription;
    }

    public String getImages() {
        return images;
    }

    public String getLogo() {
        return logo;
    }

    public String getCity() {
        return city;
    }

    public String getCatid() {
        return catid;
    }

    public String getPercent() {
        return percent;
    }

    public String getDate() {
        return date;
    }

    // Setter Methods

    public void setId(String id) {
        this.id = id;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCatid(String catid) {
        this.catid = catid;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

    public void setDate(String date) {
        this.date = date;
    }


}
