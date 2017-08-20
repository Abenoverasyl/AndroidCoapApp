package com.example.erasyl.coap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class EditCarActivity extends Activity implements View.OnClickListener{

    private TextView tvUserName;
    private TextView tvUserTelNum;
    private TextView tvUserEmail;
    private TextView tvUserGosNum;
    private TextView tvUserTexPas;

    private Button btnSave;
    private Button btnBackFines;
    private Button btnAddPhoto;

    private String userName;
    private String userEmail;
    private String userPhone;
    private String userPn;
    private String userGn;
    private String id;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private ImageView ivImageCar;
    private String userChoosenTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_car);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String email = intent.getStringExtra("email");
        String phone = intent.getStringExtra("phone");
        String gn = intent.getStringExtra("gn");
        String pn = intent.getStringExtra("pn");
        id = intent.getStringExtra("id");

        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvUserTelNum = (TextView) findViewById(R.id.tvUserTelNum);
        tvUserEmail = (TextView) findViewById(R.id.tvUserEmail);
        tvUserGosNum = (TextView) findViewById(R.id.tvUserGosNum);
        tvUserTexPas = (TextView) findViewById(R.id.tvUserTexPas);

        ivImageCar = (ImageView) findViewById(R.id.ivImageCar);
        setImageCar(id);

        tvUserName.setText(name);
        tvUserTelNum.setText(phone);
        tvUserEmail.setText(email);
        tvUserGosNum.setText(gn);
        tvUserTexPas.setText(pn);

        btnSave = (Button) findViewById(R.id.btnSave);
        btnBackFines = (Button) findViewById(R.id.btnBackFines);
        btnAddPhoto = (Button) findViewById(R.id.btnAddPhoto);

        btnSave.setOnClickListener(this);
        btnBackFines.setOnClickListener(this);
        btnAddPhoto.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSave: {

                userName = tvUserName.getText().toString();
                userEmail = tvUserEmail.getText().toString();
                userPhone = tvUserTelNum.getText().toString();
                userGn = tvUserGosNum.getText().toString();
                userPn = tvUserTexPas.getText().toString();

                if (validate(userName, userEmail, userPhone, userGn, userPn)) {

                    SharedPrefer sharedPrefer = new SharedPrefer();
                    sharedPrefer.putPref(getApplicationContext(), "name_" + id, userName);
                    sharedPrefer.putPref(getApplicationContext(), "email_" + id, userEmail);
                    sharedPrefer.putPref(getApplicationContext(), "phone_" + id, userPhone);
                    sharedPrefer.putPref(getApplicationContext(), "gn_" + id, userGn);
                    sharedPrefer.putPref(getApplicationContext(), "pn_" + id, userPn);

                    ivImageCar.buildDrawingCache();
                    Bitmap bMap = ivImageCar.getDrawingCache();
                    createDirectoryAndSaveFile(bMap, "image_" + id + ".jpg");

                    Toast.makeText(this, "Изменения сохранены", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("showMyCars", "showMyCars");
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(this, "Проверьте данные!", Toast.LENGTH_LONG).show();
                }

                break;
            }

            case R.id.btnBackFines: {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("showMyCars", "showMyCars");
                startActivity(intent);
                finish();
                break;
            }
            case R.id.btnAddPhoto: {
                selectImage();
                break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Сделать снимок"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Загрузить из галереи"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = { "Сделать снимок", "Загрузить из галереи",
                "Отмена" };

        AlertDialog.Builder builder = new AlertDialog.Builder(EditCarActivity.this);
        builder.setTitle("Фотография");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(EditCarActivity.this);

                if (items[item].equals("Сделать снимок")) {
                    userChoosenTask ="Сделать снимок";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Загрузить из галереи")) {
                    userChoosenTask ="Загрузить из галереи";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals("Отмена")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap myBmp = (Bitmap) data.getExtras().get("data");
        ivImageCar.setImageBitmap(myBmp);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ivImageCar.setImageBitmap(bm);
    }

    private void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {

        File direct = new File(Environment.getExternalStorageDirectory() + "/ЦОАП");

        if (!direct.exists()) {
            File wallpaperDirectory = new File("/sdcard/ЦОАП/");
            wallpaperDirectory.mkdirs();
        }

        File file = new File(new File("/sdcard/ЦОАП/"), fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setImageCar(String id) {
        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File (sdCard.getAbsolutePath() + "/ЦОАП");
        File file = new File(directory, "image_" + id +".jpg"); //or any other format supported
        if(file.exists()) {
            FileInputStream streamIn = null;
            try {
                streamIn = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = BitmapFactory.decodeStream(streamIn); //This gets the image
            if (bitmap.getByteCount() > 5) {
                ivImageCar.setImageBitmap(bitmap);
            }
            try {
                streamIn.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            ivImageCar.setImageDrawable(getResources().getDrawable(R.drawable.caricon));
        }
    }

    private boolean validate(String name, String email, String phone, String gn, String pn) {
        if (name.length() > 0 && email.length() > 0 && phone.length() > 0 &&
                gn.length() > 0 && pn.length() > 0 && isNumb(phone)) {
            return true;
        }
        return false;
    }

    private boolean isNumb(String phone) {
        for (int i = 1; i < phone.length(); i++) {
            if (phone.charAt(i) > '9' || phone.charAt(i) < '0') {
                return false;
            }
        }
        return true;
    }
}