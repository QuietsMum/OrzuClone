package orzu.org;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        // конструктор суперкласса
        super(context, "orzuDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // создаем таблицу с полями
        db.execSQL("create table orzutable ("
                + "id,"
                + "token,"
                + "name,"
                + "pass" + ");");

        db.execSQL("create table orzunotif ("
                + "type,"
                + "id,"
                + "title,"
                + "city,"
                + "created_at,"
                + "work_with" + ");");

        db.execSQL("create table orzuchat ("
                + "id,"
                + "name,"
                + "date,"
                + "their_text,"
                + "my_text" + ");");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}