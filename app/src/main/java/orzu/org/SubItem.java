package orzu.org;

public class SubItem {
    String name,parent_id,id;
    Boolean check;
    public SubItem(String name, String parent_id, String id) {
        this.name = name;
        this.parent_id = parent_id;
        this.id = id;
        this.check = false;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Boolean getCheck() {
        return check;
    }
    public void setCheck(Boolean check) {
        this.check = check;
    }
    public String getParent_id() {
        return parent_id;
    }
    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
}
