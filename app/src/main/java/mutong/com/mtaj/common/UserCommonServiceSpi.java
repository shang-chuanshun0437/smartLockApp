package mutong.com.mtaj.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import mutong.com.mtaj.repository.Device;
import mutong.com.mtaj.repository.User;
import mutong.com.mtaj.repository.UserSqlite;
import mutong.com.mtaj.utils.StringUtil;

public class UserCommonServiceSpi
{
    private Context context;

    private final String DBNAME = "user.db";

    private UserSqlite userSqlite;

    public UserCommonServiceSpi(Context context)
    {
        this.context = context;
        this.userSqlite = new UserSqlite(context,DBNAME,null,Constant.DBVERSION);
    }
    public User getLoginUser()
    {
        User user = null;
        SQLiteDatabase db = userSqlite.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from login_user",null);
        while(cursor.moveToNext())
        {
            String username = cursor.getString(cursor.getColumnIndex("username"));
            String usertoken = cursor.getString(cursor.getColumnIndex("usertoken"));
            String refreshtoken = cursor.getString(cursor.getColumnIndex("refreshtoken"));
            String password = cursor.getString(cursor.getColumnIndex("password"));
            String nickName = cursor.getString(cursor.getColumnIndex("nickname"));

            user = new User();
            user.setUserName(username);
            user.setUserToken(usertoken);
            user.setRefreshToken(refreshtoken);
            user.setPassword(password);
            user.setNickName(nickName);
        }
        db.close();
        return user;
    }

    public void insertUser(User user)
    {
        SQLiteDatabase db = userSqlite.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("username",user.getUserName());
        values.put("password",user.getPassword());
        values.put("usertoken",user.getUserToken());
        values.put("refreshtoken",user.getRefreshToken());
        values.put("nickname",user.getNickName());

        db.insert("login_user",null,values);

        db.close();
    }

    public void insertDevice(Device device)
    {
        SQLiteDatabase db = userSqlite.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("username",device.getUserName());
        values.put("devicenum",device.getDeviceNum());
        values.put("devicename",device.getDeviceName());
        values.put("role",device.getRole());
        values.put("version",device.getDeviceVersion());
        values.put("adminUser",device.getAdminName());
        values.put("attachedtime",device.getAttachedTime());
        values.put("bloothmac",device.getBloothMac());

        db.insert("device_user",null,values);

        db.close();
    }

    public void deleteDevice(String userName,String deviceNum)
    {
        SQLiteDatabase db = userSqlite.getWritableDatabase();
        db.delete("device_user","username=? and devicenum=?",new String[]{userName,deviceNum});

        db.close();
    }

    public Device[] queryDevice()
    {
        List<Device> devices = new ArrayList<Device>();

        SQLiteDatabase db = userSqlite.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from device_user",null);

        while(cursor.moveToNext())
        {
            Device device = new Device();

            device.setUserName(cursor.getString(cursor.getColumnIndex("username")));
            device.setDeviceNum(cursor.getString(cursor.getColumnIndex("devicenum")));
            device.setDeviceName(cursor.getString(cursor.getColumnIndex("devicename")));
            device.setDeviceVersion(cursor.getString(cursor.getColumnIndex("version")));
            device.setAdminName(cursor.getString(cursor.getColumnIndex("adminUser")));
            device.setBloothMac(cursor.getString(cursor.getColumnIndex("bloothmac")));
            try
            {
                device.setRole(Integer.valueOf(cursor.getString(cursor.getColumnIndex("role"))));
            }
            catch (Exception e)
            {
            }
            device.setAttachedTime(cursor.getString(cursor.getColumnIndex("attachedtime")));

            devices.add(device);
        }
        db.close();

        return devices.toArray(new Device[devices.size()]);
    }

    public boolean isLogin()
    {
        User user = getLoginUser();
        if(user != null)
        {
            System.out.println(user.toString());
            if (user.getUserToken() != null)
            {
                return false;
            }
        }
        return false;
    }

    //sql为空，则清空表数据
    public void deleteDataFromSqlite(String table,String sql)
    {
        SQLiteDatabase db = userSqlite.getWritableDatabase();
        if (StringUtil.isEmpty(sql))
        {
            db.delete(table,null,null);
        }
        else
        {
            db.execSQL(sql);
        }
        db.close();
    }
}
