package mutong.com.mtaj.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import mutong.com.mtaj.R;
import mutong.com.mtaj.common.Constant;
import mutong.com.mtaj.common.UserCommonServiceSpi;
import mutong.com.mtaj.repository.User;

/**
 * 自定义对话框
 */
public class CustomDialog
{
    private Dialog dialog;
    private Context context;
    private int resource;
    private Handler handler;
    private View view;
    private String msg;

    public CustomDialog(Context context,int resource,Handler handler,String msg)
    {
        this.context = context;
        this.resource = resource;
        this.handler = handler;
        this.msg = msg;

        initDialog();
    }
    public void showDialog()
    {
        if(resource == R.layout.dialog_normal)
        {
            TextView cancel = (TextView) view.findViewById(R.id.cancel);
            final TextView confirm = (TextView) view.findViewById(R.id.ok);
            final TextView versionDes = (TextView) view.findViewById(R.id.versiondes);

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handler.sendEmptyMessage(Constant.DOWNLOAD_APK);
                    dialog.dismiss();
                }
            });
        }

        dialog.show();
    }

    private void initDialog()
    {
        dialog = new Dialog(this.context, R.style.NormalDialogStyle);

        view = View.inflate(context, resource, null);
        dialog.setContentView(view);
        //使得点击对话框外部不消失对话框
        dialog.setCanceledOnTouchOutside(false);
        //设置对话框的大小
        view.setMinimumHeight((int) (ScreenSizeUtil.getInstance(context).getScreenHeight() * 0.30f));
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtil.getInstance(context).getScreenWidth() * 0.75f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);
    }
}
