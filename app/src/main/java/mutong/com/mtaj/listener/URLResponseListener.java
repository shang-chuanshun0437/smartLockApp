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
        //JSONObject resultObject = response.getJSONObject("result");
        //String retCode = resultObject.getString("retcode");
        Message msg = new Message();
        msg.what = 1;
        msg.obj = response;
        handler.sendMessage(msg);
    }
}
