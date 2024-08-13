package orzu.org;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class category_model  {
    @SerializedName("id")
    @Expose
    String id;
    @SerializedName("name")
    @Expose
    String name;
    @SerializedName("parent_id")
    @Expose
    private String parent_id;
    String color = "1";

    category_model(String id, String name, String parent_id) {
        this.id = id;
        this.name = name;
        this.parent_id = parent_id;
    }

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

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
