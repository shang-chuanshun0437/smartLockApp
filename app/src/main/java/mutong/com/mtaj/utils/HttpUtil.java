package mutong.com.mtaj.utils;

import android.content.Context;
import android.os.Handler;

import mutong.com.mtaj.volley.Request;
import mutong.com.mtaj.volley.RequestQueue;
import mutong.com.mtaj.volley.toolbox.JsonObjectRequest;
import mutong.com.mtaj.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Map;

import mutong.com.mtaj.common.Constant;
import mutong.com.mtaj.listener.URLErrorResponseListener;
import mutong.com.mtaj.listener.URLResponseListener;

public class HttpUtil
{
    private Handler handler;
    private Context context;
    private static RequestQueue queue = null;

    public HttpUtil(Handler handler,Context context)
    {
        this.context = context;
        this.handler = handler;
        setQueue();
    }

    //post 请求
    public void post(Map<String, String> map,String url)
    {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                Constant.URL_PREFIX + url,
                new JSONObject(map),
                new URLResponseListener(context,handler), new URLErrorResponseListener(context,handler));

        queue.add(jsonObjectRequest);
    }

    private void setQueue()
    {
        if (queue == null)
        {
            synchronized (this)
            {
                if(queue == null)
                {
                    queue = Volley.newRequestQueue(context);
                }
            }
        }
    }
}
