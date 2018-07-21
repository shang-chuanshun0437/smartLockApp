package mutong.com.mtaj.common;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.File;
import java.lang.ref.WeakReference;

import mutong.com.mtaj.main.AndroidOPermissionActivity;
import mutong.com.mtaj.utils.PermissionUtils;

public class AppDownloadManager
{
    public static final String TAG = "AppDownloadManager";
    private WeakReference<Activity> weakReference;
    private DownloadManager mDownloadManager;
    private DownloadChangeObserver mDownLoadChangeObserver;
    private DownloadReceiver mDownloadReceiver;
    private long mReqId;
    private OnUpdateListener mUpdateListener;
    private Context context;
    //apk下载文件的路径
    private String downloadApkPath;

    public AppDownloadManager(Context context)
    {
        weakReference = new WeakReference<Activity>((Activity)context);
        mDownloadManager = (DownloadManager) weakReference.get().getSystemService(Context.DOWNLOAD_SERVICE);
        mDownLoadChangeObserver = new DownloadChangeObserver(new Handler());
        mDownloadReceiver = new DownloadReceiver();
        this.context = context;
    }

    public void setUpdateListener(OnUpdateListener mUpdateListener) {
        this.mUpdateListener = mUpdateListener;
    }

    public void downloadApk(String apkUrl, String title, String desc)
    {
        // fix bug : 装不了新版本，在下载之前应该删除已有文件
        //确定apk下载的绝对路径
        String dirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();

        dirPath = dirPath.endsWith(File.separator) ? dirPath : dirPath + File.separator;
        downloadApkPath = dirPath + "mutong.apk";

        File apkFile = new File(downloadApkPath);
        if (apkFile != null && apkFile.exists()) {
            apkFile.delete();
        }

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl));

        request.setTitle(title);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        request.setAllowedOverRoaming(true);
        request.setMimeType("application/vnd.android.package-archive");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        //创建目录
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdir() ;

        //设置文件存放路径
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS ,"mutong.apk" ) ;
        //
        mReqId = mDownloadManager.enqueue(request);

    }

    /**
     * 取消下载
     */
    public void cancel() {
        mDownloadManager.remove(mReqId);
    }

    /**
     * 对应 {@link Activity }
     */
    public void resume() {
        //设置监听Uri.parse("content://downloads/my_downloads")
        weakReference.get().getContentResolver().registerContentObserver(Uri.parse("content://downloads/my_downloads"), true,
                mDownLoadChangeObserver);
        // 注册广播，监听APK是否下载完成
        weakReference.get().registerReceiver(mDownloadReceiver,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    /**
     * 对应{@link Activity#onPause()} ()}
     */
    public void onPause() {
        weakReference.get().getContentResolver().unregisterContentObserver(mDownLoadChangeObserver);
        weakReference.get().unregisterReceiver(mDownloadReceiver);
    }

    private void updateView() {
        int[] bytesAndStatus = new int[]{0, 0, 0};
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(mReqId);
        Cursor c = null;
        try {
            c = mDownloadManager.query(query);
            if (c != null && c.moveToFirst()) {
                //已经下载的字节数
                bytesAndStatus[0] = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                //总需下载的字节数
                bytesAndStatus[1] = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                //状态所在的列索引
                bytesAndStatus[2] = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }

        if (mUpdateListener != null) {
            mUpdateListener.update(bytesAndStatus[0], bytesAndStatus[1]);
        }
        System.out.println("下载进度：" + bytesAndStatus[0] + "/" + bytesAndStatus[1] + "");
    }

    class DownloadChangeObserver extends ContentObserver {

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public DownloadChangeObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            updateView();
        }
    }

    class DownloadReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent)
        {
            try
            {
                boolean haveInstallPermission;
                // 兼容Android 8.0
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                {
                    //先获取是否有安装未知来源应用的权限
                    haveInstallPermission = context.getPackageManager().canRequestPackageInstalls();
                    if (!haveInstallPermission)
                    {
                        //没有权限，并去设置页面授权
                        PermissionUtils.requestPermission(context,Manifest.permission.REQUEST_INSTALL_PACKAGES);
                        haveInstallPermission = context.getPackageManager().canRequestPackageInstalls();
                        if(!haveInstallPermission)
                        {
                            Toast.makeText(context,"您未授权，升级失败",Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            installApk(context, intent);
                        }

                    } else {
                        installApk(context, intent);
                    }
                } else {
                    installApk(context, intent);
                }
            }
           catch (Exception e)
           {
               Toast.makeText(context,"安装出问题:" + e.getMessage(),Toast.LENGTH_LONG).show();
           }
        }
    }

    /**
     * @param context
     * @param intent
     */
    private void installApk(Context context, Intent intent)
    {
        System.out.println("inter installApk");
        long completeDownLoadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

        Uri uri;
        Intent intentInstall = new Intent();
        intentInstall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentInstall.setAction(Intent.ACTION_VIEW);

        if (completeDownLoadId == mReqId) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) { // 6.0以下
                uri = mDownloadManager.getUriForDownloadedFile(completeDownLoadId);
            } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) { // 6.0 - 7.0
                File apkFile = queryDownloadedApk(context, completeDownLoadId);
                uri = Uri.fromFile(apkFile);
            } else { // Android 7.0 以上
                File apkFile = new File(Uri.parse(downloadApkPath).getPath());
                uri = FileProvider.getUriForFile(context, "mutong.com.mtaj.main.fileProvider", apkFile);
                intentInstall.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            intentInstall.setDataAndType(uri, "application/vnd.android.package-archive");
            context.startActivity(intentInstall);
        }
    }

    //通过downLoadId查询下载的apk，解决6.0以后安装的问题
    public static File queryDownloadedApk(Context context, long downloadId) {
        File targetApkFile = null;
        DownloadManager downloader = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        if (downloadId != -1) {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadId);
            query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
            Cursor cur = downloader.query(query);
            if (cur != null) {
                if (cur.moveToFirst()) {
                    String uriString = cur.getString(cur.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    if (!TextUtils.isEmpty(uriString)) {
                        targetApkFile = new File(Uri.parse(uriString).getPath());
                    }
                }
                cur.close();
            }
        }
        return targetApkFile;
    }

    public interface OnUpdateListener {
        void update(int currentByte, int totalByte);
    }

    public interface AndroidOInstallPermissionListener {
        void permissionSuccess();

        void permissionFail();
    }

}

