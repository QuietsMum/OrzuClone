package orzu.org.chat;

public interface message_interface {
    int TYPE_MY = 1;
    int TYPE_THEIR = 2;
    int getType();
    String getDate();
}