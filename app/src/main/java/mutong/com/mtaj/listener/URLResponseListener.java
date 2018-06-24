package mutong.com.mtaj.listener;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import mutong.com.mtaj.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import mutong.com.mtaj.common.ErrorCode;

public class URLResponseListener implements Response.Listener<JSONObject>
{
    private Context context;
    private Handler handler;

    public URLResponseListener(Context context,Handler handler)
    {
        this.context = context;
        this.handler = handler;
    }

    @Override
    public void onResponse(JSONObject response)
    {
        try
        {
            JSONObject resultObject = response.getJSONObject("result");
            String retCode = resultObject.getString("retcode");

            switch (retCode)
            {
                case ErrorCode.SUCEESS:
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = response;
                    handler.sendMessage(msg);
                    break;
                case ErrorCode.USERNAME_EXIST:
                    Toast.makeText(context,"用户名已注册，请重新输入用户名....", Toast.LENGTH_LONG).show();
                    break;
                case ErrorCode.USERNAME_NOT_EXIST:
                    Toast.makeText(context,"用户名不存在，请注册后再登录....", Toast.LENGTH_LONG).show();
                    break;
                default:
                    System.out.println(retCode);
                    break;
            }
        }
        catch (JSONException e)
        {
            System.out.println(e.getMessage());
        }
    }
}
