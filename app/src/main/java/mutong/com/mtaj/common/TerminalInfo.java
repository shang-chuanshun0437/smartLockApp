package mutong.com.mtaj.common;

import android.content.Context;
import android.telephony.TelephonyManager;

public class TerminalInfo
{
    private Context context;
    private TelephonyManager telephonyManager;

    public TerminalInfo(Context context)
    {
        this.context = context;
        telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
    }

   public String getTerminalId()
   {
       try
       {
           return telephonyManager.getDeviceSoftwareVersion();
       }
       catch (SecurityException e)
       {
            System.out.println("没有获取设备id的权限");
       }
       return Constant.DEFALUT_TERMINALID;
   }
}
