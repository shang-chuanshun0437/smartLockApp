package mutong.com.mtaj.repository;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import mutong.com.mtaj.common.Constant;

public class UserSqlite extends SQLiteOpenHelper
{

    public UserSqlite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
    }

    // 当第一次创建数据库的时候，调用该方法
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String usersql = "create table login_user(username varchar(11),usertoken varchar(32),refreshtoken varchar(32))";
        db.execSQL(usersql);

        String devicesql = "create table device_user(username varchar(11),devicenum varchar(11),devicename varchar(128),role int)";
        db.execSQL(devicesql);

        onUpgrade(db,1, Constant.DBVERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        System.out.println("oldVersion:" + oldVersion);
        System.out.println("newVersion:" + newVersion);

        for(int i = oldVersion + 1;i <= newVersion;i++)
        {
            switch (i)
            {
                case 2:
                    String sql = "alter table login_user add password varchar(128)";
                    db.execSQL(sql);
                    break;
                case 3:
                    String sqlDeviceVersion = "alter table device_user add version varchar(36)";
                    db.execSQL(sqlDeviceVersion);

                    String sqlAdminUser = "alter table device_user add adminUser varchar(11)";
                    db.execSQL(sqlAdminUser);
                    break;
                case 4:
                    String sqlAttachedTime = "alter table device_user add attachedtime varchar(20)";
                    db.execSQL(sqlAttachedTime);

                    String sqlBloothMac = "alter table device_user add bloothmac bloothmac(36)";
                    db.execSQL(sqlBloothMac);
                    break;
                case 5:
                    String nickname = "alter table login_user add nickname varchar(48)";
                    db.execSQL(nickname);
                    break;
                case 6:
                    String head = "alter table login_user add headportrait varchar(256)";
                    db.execSQL(head);
                    break;
                case 7:
                    String preference = "create table preference(username varchar(11) PRIMARY KEY,headportrait varchar(256),nickname varchar(32))";
                    db.execSQL(preference);
                    break;
            }
        }
    }
}
