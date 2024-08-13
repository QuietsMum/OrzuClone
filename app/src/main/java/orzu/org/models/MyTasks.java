package orzu.org.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MyTasks {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("task")
    @Expose
    private String task;
    @SerializedName("sub_cat_id")
    @Expose
    private String sub_cat_id;
    @SerializedName("sub_cat_name")
    @Expose
    private String sub_cat_name;
    @SerializedName("cat_id")
    @Expose
    private String cat_id;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("icurrentd")
    @Expose
    private String current;
    @SerializedName("cdate")
    @Expose
    private String cdate;
    @SerializedName("edate")
    @Expose
    private String edate;
    @SerializedName("cdate_l")
    @Expose
    private String cdate_l;
    @SerializedName("level_l")
    @Expose
    private String level_l;
    @SerializedName("work_with")
    @Expose
    private String work_with;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("narrative")
    @Expose
    private String narrative;


// Getter Methods

    public String getId() {
        return id;
    }

    public String getTask() {
        return task;
    }

    public String getSub_cat_id() {
        return sub_cat_id;
    }

    public String getSub_cat_name() {
        return sub_cat_name;
    }

    public String getCat_id() {
        return cat_id;
    }

    public String getAmount() {
        return amount;
    }

    public String getCurrent() {
        return current;
    }

    public String getCdate() {
        return cdate;
    }

    public String getEdate() {
        return edate;
    }

    public String getCdate_l() {
        return cdate_l;
    }

    public String getLevel_l() {
        return level_l;
    }

    public String getWork_with() {
        return work_with;
    }

    public String getCity() {
        return city;
    }

    public String getNarrative() {
        return narrative;
    }

// Setter Methods

    public void setId(String id) {
        this.id = id;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public void setSub_cat_id(String sub_cat_id) {
        this.sub_cat_id = sub_cat_id;
    }

    public void setSub_cat_name(String sub_cat_name) {
        this.sub_cat_name = sub_cat_name;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setCurrent(String current) {
        this.current = current;
    }

    public void setCdate(String cdate) {
        this.cdate = cdate;
    }

    public void setEdate(String edate) {
        this.edate = edate;
    }

    public void setCdate_l(String cdate_l) {
        this.cdate_l = cdate_l;
    }

    public void setLevel_l(String level_l) {
        this.level_l = level_l;
    }

    public void setWork_with(String work_with) {
        this.work_with = work_with;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setNarrative(String narrative) {
        this.narrative = narrative;
    }
}