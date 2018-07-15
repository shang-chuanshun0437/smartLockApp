package mutong.com.mtaj.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import mutong.com.mtaj.repository.Device;
import mutong.com.mtaj.repository.Preference;
import mutong.com.mtaj.repository.User;
import mutong.com.mtaj.repository.UserSqlite;
import mutong.com.mtaj.utils.StringUtil;

public class UserCommonServiceSpi {
    private Context context;

    private final String DBNAME = "user.db";

    private UserSqlite userSqlite;

    public UserCommonServiceSpi(Context context) {
        this.context = context;
        this.userSqlite = new UserSqlite(context, DBNAME, null, Constant.DBVERSION);
    }

    /*
    * 获取用户的登录信息
    **/
    public User getLoginUser() {
        User user = null;
        SQLiteDatabase db = userSqlite.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from login_user", null);
        while (cursor.moveToNext()) {
            String username = cursor.getString(cursor.getColumnIndex("username"));
            String usertoken = cursor.getString(cursor.getColumnIndex("usertoken"));
            String refreshtoken = cursor.getString(cursor.getColumnIndex("refreshtoken"));
            String password = cursor.getString(cursor.getColumnIndex("password"));
            String phonenum = cursor.getString(cursor.getColumnIndex("phonenum"));

            user = new User();
            user.setUserName(username);
            user.setUserToken(usertoken);
            user.setRefreshToken(refreshtoken);
            user.setPassword(password);
            user.setPhoneNum(phonenum);
        }
        db.close();
        return user;
    }

    /*
    * 存储用户的登录信息
    */
    public void insertUser(User user) {
        //先清空login_user，只能有一个登录用户
        deleteDataFromSqlite(Constant.LOGIN_USER_TABLE, null);

        SQLiteDatabase db = userSqlite.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("username", user.getUserName());
        values.put("password", user.getPassword());
        values.put("usertoken", user.getUserToken());
        values.put("refreshtoken", user.getRefreshToken());
        values.put("phonenum", user.getPhoneNum());

        db.insert(Constant.LOGIN_USER_TABLE, null, values);

        db.close();
    }

    public void insertDevice(Device device) {
        SQLiteDatabase db = userSqlite.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("username", device.getUserName());
        values.put("phonenum", device.getPhoneNum());
        values.put("devicenum", device.getDeviceNum());
        values.put("devicename", device.getDeviceName());
        values.put("role", device.getRole());
        values.put("version", device.getDeviceVersion());
        values.put("adminUser", device.getAdminName());
        values.put("attachedtime", device.getAttachedTime());
        values.put("bloothmac", device.getBloothMac());
        values.put("validdate", device.getValidDate());

        db.insert(Constant.DEVICE_USER_TABLE, null, values);

        db.close();
    }

    public void deleteDevice(String phoneNum, String deviceNum) {
        SQLiteDatabase db = userSqlite.getWritableDatabase();
        db.delete("device_user", "phonenum=? and devicenum=?", new String[]{phoneNum, deviceNum});

        db.close();
    }

    public Device[] queryDevice() {
        List<Device> devices = new ArrayList<Device>();

        SQLiteDatabase db = userSqlite.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from device_user", null);

        while (cursor.moveToNext()) {
            Device device = new Device();

            device.setUserName(cursor.getString(cursor.getColumnIndex("username")));
            device.setPhoneNum(cursor.getString(cursor.getColumnIndex("phonenum")));
            device.setDeviceNum(cursor.getString(cursor.getColumnIndex("devicenum")));
            device.setDeviceName(cursor.getString(cursor.getColumnIndex("devicename")));
            device.setDeviceVersion(cursor.getString(cursor.getColumnIndex("version")));
            device.setAdminName(cursor.getString(cursor.getColumnIndex("adminUser")));
            device.setBloothMac(cursor.getString(cursor.getColumnIndex("bloothmac")));
            device.setRole(cursor.getString(cursor.getColumnIndex("role")));
            device.setAttachedTime(cursor.getString(cursor.getColumnIndex("attachedtime")));
            device.setValidDate(cursor.getString(cursor.getColumnIndex("validdate")));

            devices.add(device);
        }
        db.close();

        return devices.toArray(new Device[devices.size()]);
    }

    public Device[] queryByDeviceNum(String deviceNum) {
        List<Device> devices = new ArrayList<Device>();

        SQLiteDatabase db = userSqlite.getReadableDatabase();

        Cursor cursor = db.query(Constant.DEVICE_USER_TABLE, null, "devicenum = ?",
                new String[]{deviceNum}, null, null, null);

        while (cursor.moveToNext()) {
            Device device = new Device();

            device.setUserName(cursor.getString(cursor.getColumnIndex("username")));
            device.setPhoneNum(cursor.getString(cursor.getColumnIndex("phonenum")));
            device.setDeviceNum(cursor.getString(cursor.getColumnIndex("devicenum")));
            device.setDeviceName(cursor.getString(cursor.getColumnIndex("devicename")));
            device.setDeviceVersion(cursor.getString(cursor.getColumnIndex("version")));
            device.setAdminName(cursor.getString(cursor.getColumnIndex("adminUser")));
            device.setBloothMac(cursor.getString(cursor.getColumnIndex("bloothmac")));
            device.setRole(cursor.getString(cursor.getColumnIndex("role")));
            device.setAttachedTime(cursor.getString(cursor.getColumnIndex("attachedtime")));
            device.setValidDate(cursor.getString(cursor.getColumnIndex("validdate")));

            devices.add(device);
        }
        db.close();

        return devices.toArray(new Device[devices.size()]);
    }

    public Device queryByDeviceName(String deviceName) {
        Device device = new Device();

        SQLiteDatabase db = userSqlite.getReadableDatabase();

        Cursor cursor = db.query(Constant.DEVICE_USER_TABLE, null, "devicename = ?",
                new String[]{deviceName}, null, null, null);

        while (cursor.moveToNext()) {
            device.setUserName(cursor.getString(cursor.getColumnIndex("username")));
            device.setPhoneNum(cursor.getString(cursor.getColumnIndex("phonenum")));
            device.setDeviceNum(cursor.getString(cursor.getColumnIndex("devicenum")));
            device.setDeviceName(cursor.getString(cursor.getColumnIndex("devicename")));
            device.setDeviceVersion(cursor.getString(cursor.getColumnIndex("version")));
            device.setAdminName(cursor.getString(cursor.getColumnIndex("adminUser")));
            device.setBloothMac(cursor.getString(cursor.getColumnIndex("bloothmac")));
            device.setRole(cursor.getString(cursor.getColumnIndex("role")));
            device.setAttachedTime(cursor.getString(cursor.getColumnIndex("attachedtime")));
            device.setValidDate(cursor.getString(cursor.getColumnIndex("validdate")));
        }
        db.close();

        return device;
    }

    //sql为空，则清空表数据
    public void deleteDataFromSqlite(String table, String sql) {
        SQLiteDatabase db = userSqlite.getWritableDatabase();
        if (StringUtil.isEmpty(sql)) {
            db.delete(table, null, null);
        } else {
            db.execSQL(sql);
        }
        db.close();
    }

    /*
    * 获取用户的设置信息
    **/
    public Preference getPreference(String phonenum) {
        Preference preference = null;

        SQLiteDatabase db = userSqlite.getReadableDatabase();

        Cursor cursor = db.query(Constant.PREFERENCE, null, "phonenum = ?",
                new String[]{phonenum}, null, null, null);
        while (cursor.moveToNext()) {
            preference = new Preference();

            String phonenumDB = cursor.getString(cursor.getColumnIndex("phonenum"));
            String headportrait = cursor.getString(cursor.getColumnIndex("headportrait"));

            preference.setPhoneNum(phonenumDB);
            preference.setHeadPortraitPath(headportrait);

        }
        db.close();
        return preference;
    }

    /*
    * 存储用户的设置信息
    */
    public void insertPreference(Preference preference) {
        SQLiteDatabase db = userSqlite.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("phonenum", preference.getPhoneNum());
        values.put("headportrait", preference.getHeadPortraitPath());

        db.insert(Constant.PREFERENCE, null, values);

        db.close();
    }

    /*
   * 更新用户的设置信息
   */
    public void updatePreference(Preference preference) {
        SQLiteDatabase db = userSqlite.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("phonenum", preference.getPhoneNum());
        values.put("headportrait", preference.getHeadPortraitPath());

        db.update(Constant.PREFERENCE, values, "username = ?", new String[]{preference.getPhoneNum()});
        //update(String table,ContentValues values,String whereClause, String[] whereArgs)：
        db.close();
    }

    /*
  * 更新用户的设置信息
  */
    public void updateDevice(Device device)
    {
        SQLiteDatabase db = userSqlite.getReadableDatabase();

        String deviceName = "'" + device.getDeviceName() + "'";
        String deviceNum = "'" + device.getDeviceNum() + "'";

        String sql = String.format("UPDATE device_user set devicename=%s where devicenum=%s", deviceName,deviceNum);

        db.execSQL(sql);
        db.close();
    }
}


