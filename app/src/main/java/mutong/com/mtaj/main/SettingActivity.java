package mutong.com.mtaj.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import mutong.com.mtaj.R;
import mutong.com.mtaj.common.UserCommonServiceSpi;
import mutong.com.mtaj.repository.User;
import mutong.com.mtaj.utils.StringUtil;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener
{
    private EditText modifyNickName;
    private TextView modifySave;
    private ImageView back;

    private User user;
    private UserCommonServiceSpi userCommonService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_nickname);

    }

    @Override
    protected void onResume()
    {
        super.onResume();

    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {

        }
    }
}
