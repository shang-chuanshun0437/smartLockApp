package mutong.com.mtaj.listener;


import android.content.Context;
import android.os.Handler;

import mutong.com.mtaj.volley.Response;
import mutong.com.mtaj.volley.VolleyError;

import mutong.com.mtaj.common.Constant;

public class URLErrorResponseListener implements Response.ErrorListener
{
    private Context context;
    private Handler handler;

    public URLErrorResponseListener(Context context,Handler handler)
    {
        this.context = context;
        this.handler = handler;
    }

    @Override
    public void onErrorResponse(VolleyError error)
    {
        handler.sendEmptyMessage(Constant.CONSLE_FAIL);
    }
}
