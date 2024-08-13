package orzu.org;

public class BonusList {
    String idUser, date, value, reason,plmn;

    public BonusList(String idUser, String date, String value, String reason,String plmn) {
        this.idUser = idUser;
        this.date = date;
        this.value = value;
        this.reason = reason;
        this.plmn = plmn;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getPlmn() {
        return plmn;
    }

    public void setPlmn(String plmn) {
        this.plmn = plmn;
    }
}
