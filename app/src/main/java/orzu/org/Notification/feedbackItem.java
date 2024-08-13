package orzu.org.Notification;

public class feedbackItem implements Literature {
    private String name, count, feed_desc, money, id, type_of_task;

    public feedbackItem(String name, String count, String feed_desc, String money, String id, String typs_of_task) {
        this.name = name;
        this.count = count;
        this.feed_desc = feed_desc;
        this.money = money;
        this.id = id;
        this.type_of_task = typs_of_task;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getFeed_desc() {
        return feed_desc;
    }

    public void setFeed_desc(String feed_desc) {
        this.feed_desc = feed_desc;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType_of_task() {
        return type_of_task;
    }

    public void setType_of_task(String type_of_task) {
        this.type_of_task = type_of_task;
    }

    @Override
    public int getType() {
        return Literature.TYPE_MAGAZINE;
    }
}
