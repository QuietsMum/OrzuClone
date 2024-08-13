package orzu.org.chat;

public class their_message implements message_interface {
    String message,date;
    public their_message(String message, String date) {
        this.message = message;
        this.date = date;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    @Override
    public int getType() {
        return message_interface.TYPE_THEIR;
    }
}
