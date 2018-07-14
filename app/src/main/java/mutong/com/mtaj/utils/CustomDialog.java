package mutong.com.mtaj.utils;

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
    private UserCommonServiceSpi userCommonService;
    private String deviceNum;

    public CustomDialog(Context context,int resource,Handler handler,String deviceNum)
    {
        this.context = context;
        this.resource = resource;
        this.handler = handler;
        this.deviceNum = deviceNum;

        userCommonService = new UserCommonServiceSpi(context);

        initDialog();
    }
    public void showDialog()
    {
        if(resource == R.layout.dialog_normal)
        {
            TextView cancel = (TextView) view.findViewById(R.id.cancel);
            final TextView confirm = (TextView) view.findViewById(R.id.ok);
            final EditText dialogDeviceName = (EditText) view.findViewById(R.id.device_name);

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String deviceName = dialogDeviceName.getText().toString();
                    User user = userCommonService.getLoginUser();

                    Map<String, String> map = new HashMap<String, String>();
                    map.put("phoneNum",user.getPhoneNum());
                    map.put("token",user.getUserToken());
                    map.put("deviceNum",deviceNum);
                    map.put("deviceName",deviceName);

                    HttpUtil httpUtil = new HttpUtil(handler,context);
                    httpUtil.post(map,"/modify/modifyDeviceName");
                    dialog.dismiss();
                }
            });
        }

        if (resource == R.layout.dialog_adduser)
        {
            TextView cancel = (TextView) view.findViewById(R.id.cancel);
            TextView confirm = (TextView) view.findViewById(R.id.ok);
            final EditText phone = (EditText)view.findViewById(R.id.phone);
            final EditText validDate = (EditText)view.findViewById(R.id.valid_date);
            phone.addTextChangedListener(new SpaceTextWatcher(phone));

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phoneStr = phone.getText().toString().replace(" ","");
                    String validDateStr = validDate.getText().toString();

                    //校验输入的日期是否正确
                    if(!StringUtil.isEmpty(validDateStr) && !DateUtil.isDataFormat(validDateStr))
                    {
                        Toast.makeText(context,"有效期格式输入错误，正确的格式:2099.12.12",Toast.LENGTH_LONG).show();
                        return;
                    }
                    User user = userCommonService.getLoginUser();

                    Map<String, String> map = new HashMap<String, String>();
                    map.put("phoneNum",user.getPhoneNum());
                    map.put("token",user.getUserToken());
                    map.put("deviceNum",deviceNum);
                    map.put("bindPhoneNum",phoneStr);
                    map.put("validDate",validDateStr);

                    HttpUtil httpUtil = new HttpUtil(handler,context);
                    httpUtil.post(map,"/device/bindDevice4User");
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
