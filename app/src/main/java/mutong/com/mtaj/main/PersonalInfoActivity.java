package mutong.com.mtaj.main;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

import mutong.com.mtaj.BuildConfig;
import mutong.com.mtaj.R;
import mutong.com.mtaj.common.CircleImageView;
import mutong.com.mtaj.common.PhotoPopupWindow;
import mutong.com.mtaj.common.UserCommonServiceSpi;
import mutong.com.mtaj.repository.Preference;
import mutong.com.mtaj.repository.User;
import mutong.com.mtaj.utils.PermissionUtils;
import mutong.com.mtaj.utils.StringUtil;

public class PersonalInfoActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final int REQUEST_IMAGE_GET = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_SMALL_IMAGE_CUTTING = 2;
    private static final int REQUEST_BIG_IMAGE_CUTTING = 3;
    private static final String IMAGE_FILE_NAME = "icon.jpg";

    private TextView personalAccountEdit;
    private TextView personalAccount;
    private TextView personalNickNameEdit;
    private TextView personalNickName;
    private TextView headPoatrity;
    private CircleImageView headImage;
    private TextView personalText;
    private ImageView back;

    private UserCommonServiceSpi userCommonService;
    //更换头像
    private PhotoPopupWindow mPhotoPopupWindow;
    private Uri mImageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_info);

        userCommonService = new UserCommonServiceSpi(this);

        personalAccountEdit = (TextView)findViewById(R.id.personal_account_edit);
        personalNickNameEdit = (TextView)findViewById(R.id.personal_nickname_edit);
        personalNickName = (TextView)findViewById(R.id.personal_nickname);
        headPoatrity = (TextView)findViewById(R.id.personal_head);
        headImage = (CircleImageView)findViewById(R.id.circleImageView);
        personalText = (TextView)findViewById(R.id.personal_info_text);
        back = (ImageView)findViewById(R.id.personal_back);

        personalNickNameEdit.setOnClickListener(this);
        personalNickName.setOnClickListener(this);
        headImage.setOnClickListener(this);
        headPoatrity.setOnClickListener(this);
        personalText.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        User user = userCommonService.getLoginUser();
        if (user != null)
        {
            personalAccountEdit.setText(user.getPhoneNum());
            personalNickNameEdit.setText(user.getUserName());
            Preference preference = userCommonService.getPreference(user.getPhoneNum());
            if (preference != null)
            {
                if (preference.getHeadPortraitPath() != null)
                {
                    Bitmap bitmap = BitmapFactory.decodeFile(preference.getHeadPortraitPath());
                    headImage.setImageBitmap(bitmap);
                }
            }
        }
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.personal_nickname_edit:
            case R.id.personal_nickname:
                Intent intent = new Intent(this, ModifyNickNameActivity.class);
                startActivity(intent);
                break;

            case R.id.personal_info_text:
            case R.id.personal_back:
                finish();
                break;
            case R.id.personal_head:
            case R.id.circleImageView:
                mPhotoPopupWindow = new PhotoPopupWindow(PersonalInfoActivity.this,
                        new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 权限申请
                        if (ContextCompat.checkSelfPermission(PersonalInfoActivity.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED)
                        {
                            //权限还没有授予，需要在这里写申请权限的代码
                            PermissionUtils.requestPermission(PersonalInfoActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        } else {
                            // 如果权限已经申请过，直接进行图片选择
                            mPhotoPopupWindow.dismiss();
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            // 判断系统中是否有处理该 Intent 的 Activity
                            if (intent.resolveActivity(getPackageManager()) != null) {
                                startActivityForResult(intent, REQUEST_IMAGE_GET);
                            } else {
                                Toast.makeText(PersonalInfoActivity.this, "未找到图片查看器", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                            // 权限申请
                            if (ContextCompat.checkSelfPermission(PersonalInfoActivity.this,
                                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                                    || ContextCompat.checkSelfPermission(PersonalInfoActivity.this,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    != PackageManager.PERMISSION_GRANTED)
                            {
                                // 权限还没有授予，需要在这里写申请权限的代码
                                PermissionUtils.requestPermission(PersonalInfoActivity.this,Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                            } else {
                                // 权限已经申请，直接拍照
                                mPhotoPopupWindow.dismiss();
                                imageCapture();
                            }
                    }
                });
                View rootView = LayoutInflater.from(PersonalInfoActivity.this)
                        .inflate(R.layout.activity_main, null);
                mPhotoPopupWindow.showAtLocation(rootView,
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
        }
    }

    /**
     * 判断系统及拍照
     */
    private void imageCapture()
    {
        Intent intent;
        Uri pictureUri = null;
        File pictureFile = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME);
        // 判断当前系统
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            pictureUri = FileProvider.getUriForFile(this,"mutong.com.mtaj.main.fileProvider", pictureFile);

        } else {
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            pictureUri = Uri.fromFile(pictureFile);
        }
        // 去拍照

        intent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    /**
     * 处理回调结果
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 回调成功
        if (resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                // 大图切割
                case REQUEST_BIG_IMAGE_CUTTING:
                    if(mImageUri == null)
                    {
                        Toast.makeText(this,"无法获取图片",Toast.LENGTH_LONG).show();
                        return;
                    }
                    //将头像数路径存入sqlite  preference
                    User user = userCommonService.getLoginUser();
                    if (user != null)
                    {
                        Preference preference = userCommonService.getPreference(user.getPhoneNum());

                        if (preference == null)
                        {
                            preference = new Preference();
                            preference.setPhoneNum(user.getPhoneNum());
                            preference.setHeadPortraitPath(mImageUri.getEncodedPath());
                            userCommonService.insertPreference(preference);
                        }
                        else
                        {
                            preference.setHeadPortraitPath(mImageUri.getEncodedPath());
                            userCommonService.updatePreference(preference);
                        }

                    }

                    Bitmap bitmap = BitmapFactory.decodeFile(mImageUri.getEncodedPath());
                    headImage.setImageBitmap(bitmap);
                    break;
                // 相册选取
                case REQUEST_IMAGE_GET:
                    try
                    {
                        startBigPhotoZoom(data.getData());
                    }
                    catch (NullPointerException e)
                    {
                        e.printStackTrace();
                    }
                    break;
                // 拍照
                case REQUEST_IMAGE_CAPTURE:
                    File temp = new File(Environment.getExternalStorageDirectory() + "/" + IMAGE_FILE_NAME);
                    startBigPhotoZoom(temp);
            }
        }
    }

    public Uri getImageContentUri(Context context, File imageFile)
    {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    public void startBigPhotoZoom(Uri uri)
    {
        User user = userCommonService.getLoginUser();
        if (user == null)
        {
            finish();
        }
        // 创建大图文件夹
        Uri imageUri = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            String storage = Environment.getExternalStorageDirectory().getPath();
            File dirFile = new File(storage + "/weasy");
            if (!dirFile.exists())
            {
                if (!dirFile.mkdirs())
                {
                    Toast.makeText(this,"创建文件夹失败，请稍后重试",Toast.LENGTH_LONG).show();
                    return;
                }
            }
            File file = new File(dirFile,   user.getPhoneNum() + ".jpg");
            System.out.println("file:::" + file.getAbsolutePath());
            imageUri = Uri.fromFile(file);
            mImageUri = imageUri; // 将 uri 传出，方便设置到视图中
        }

        // 开始切割
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1); // 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 600); // 输出图片大小
        intent.putExtra("outputY", 600);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false); // 不直接返回数据
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); // 返回一个文件
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, REQUEST_BIG_IMAGE_CUTTING);
    }

    /**
     * 大图模式切割图片
     * 直接创建一个文件将切割后的图片写入
     */
    public void startBigPhotoZoom(File inputFile)
    {
        User user = userCommonService.getLoginUser();
        if(user == null)
        {
            finish();
        }
        // 创建大图文件夹
        Uri imageUri = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String storage = Environment.getExternalStorageDirectory().getPath();
            File dirFile = new File(storage + "/bigIcon");
            if (!dirFile.exists()) {
                if (!dirFile.mkdirs())
                {
                    Toast.makeText(this,"创建文件夹失败，请稍后重试",Toast.LENGTH_LONG).show();
                    return;
                }
            }
            File file = new File(dirFile, user.getPhoneNum() + ".jpg");
            System.out.println(file.getAbsolutePath());
            try
            {
                System.out.println(file.getCanonicalPath());
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            imageUri = Uri.fromFile(file);
            mImageUri = imageUri; // 将 uri 传出，方便设置到视图中
        }

        // 开始切割
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(getImageContentUri(PersonalInfoActivity.this, inputFile), "image/*");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1); // 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 600); // 输出图片大小
        intent.putExtra("outputY", 600);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false); // 不直接返回数据
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); // 返回一个文件
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        // intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, REQUEST_BIG_IMAGE_CUTTING);
    }
}
