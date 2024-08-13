package orzu.org;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import java.util.HashMap;
import java.util.Map;

public class Common {
    static String URL = "https://orzu.org/api?appid=$2y$12$esyosghhXSh6LxcX17N/suiqeJGJq/VQ9QkbqvImtE4JMWxz7WqYS&opt=view_task&tasks=all&requests=no&page=";
    public static String userId = "";
    public static String user_to = "";
    static Boolean allCity = false;
    static String taskId = "";
    public static Bitmap bitmap;
    static Boolean fragmentshimmer = false;
    public static int drawable = R.drawable.ic_person_72pt_3x;
    static Drawable d;
    public static String city = "";
    public static String utoken = "";
    public static String name;
    static String name_only;
    static String birth;
    static String about;
    static String sex;
    static String wallet;
    static String nameOfChat;
    public static String chatId;
    static String taskName;
    @SuppressLint("UseSparseArrays")
    static HashMap<Integer,String> values = new HashMap<>();
    static HashMap<String, Map<Integer,Integer>> subFilter = new HashMap<>();
    static String city1 = "";
    static String referrer = "";
    static String sad = "";
    static String neutral = "";
    static String happy = "";
    static String countryCode="";
}
