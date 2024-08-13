package orzu.org.Notification;

public class ChooseYouItem implements Literature {
    private String header, desc, price, usluga, data,id,type_task;
    public ChooseYouItem(String header, String desc, String price, String usluga, String data,String id,String type_task) {
        this.header = header;
        this.desc = desc;
        this.price = price;
        this.usluga = usluga;
        this.data = data;
        this.id = id;
        this.type_task =type_task;
    }
    public String getHeader() {
        return header;
    }
    public void setHeader(String header) {
        this.header = header;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getPrice() {
        return price;
    }
    public void setPrice(String price) {
        this.price = price;
    }
    public String getUsluga() {
        return usluga;
    }
    public void setUsluga(String usluga) {
        this.usluga = usluga;
    }
    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
    }
    @Override
    public int getType() {
        return Literature.TYPE_BOOK;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType_task() {
        return type_task;
    }

    public void setType_task(String type_task) {
        this.type_task = type_task;
    }
}

