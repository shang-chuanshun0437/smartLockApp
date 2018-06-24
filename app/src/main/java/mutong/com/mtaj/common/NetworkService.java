package mutong.com.mtaj.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkService
{
    //判断网络连接是否可用
    public static boolean isNetworkOpen(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null)
        {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null)
            {
                System.out.println(networkInfo.getType());
                return true;
            }
        }
        Toast.makeText(context,"请打开网络连接....", Toast.LENGTH_LONG).show();
        return false;
    }
}
