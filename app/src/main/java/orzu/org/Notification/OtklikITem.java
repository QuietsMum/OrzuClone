package orzu.org.Notification;

public class OtklikITem implements Literature {
    private String kondic, descrip_kondic;
    public OtklikITem(String kondic, String descrip_kondic) {
        this.kondic = kondic;
        this.descrip_kondic = descrip_kondic;
    }
    public String getKondic() {
        return kondic;
    }
    public void setKondic(String kondic) {
        this.kondic = kondic;
    }
    public String getDescrip_kondic() {
        return descrip_kondic;
    }
    public void setDescrip_kondic(String descrip_kondic) {
        this.descrip_kondic = descrip_kondic;
    }
    @Override
    public int getType() {
        return Literature.TYPE_NEWSPAPER;
    }
}
