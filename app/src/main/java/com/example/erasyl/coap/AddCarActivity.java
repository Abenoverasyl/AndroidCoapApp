package com.example.erasyl.coap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class AddCarActivity extends Activity implements View.OnClickListener{

    private TextView tvUserName;
    private TextView tvUserTelNum;
    private TextView tvUserEmail;
    private TextView tvUserGosNum;
    private TextView tvUserTexPas;

    private Button btnCheckFine;
    private Button btnBackFines;
    private Button btnAddPhoto;

    private String userName;
    private String userEmail;
    private String userPhone;
    private String userPn;
    private String userGn;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private ImageView ivImageCar;
    private String userChoosenTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);

        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvUserTelNum = (TextView) findViewById(R.id.tvUserTelNum);
        tvUserEmail = (TextView) findViewById(R.id.tvUserEmail);
        tvUserGosNum = (TextView) findViewById(R.id.tvUserGosNum);
        tvUserTexPas = (TextView) findViewById(R.id.tvUserTexPas);

        btnCheckFine = (Button) findViewById(R.id.btnCheckFine);
        btnBackFines = (Button) findViewById(R.id.btnBackFines);
        btnAddPhoto = (Button) findViewById(R.id.btnAddPhoto);

        ivImageCar = (ImageView) findViewById(R.id.ivImageCar);
        ivImageCar.setImageDrawable(getResources().getDrawable(R.drawable.caricon));

        btnCheckFine.setOnClickListener(this);
        btnBackFines.setOnClickListener(this);
        btnAddPhoto.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCheckFine: {

                userName = tvUserName.getText().toString();
                userEmail = tvUserEmail.getText().toString();
                userPhone = tvUserTelNum.getText().toString();
                userGn = tvUserGosNum.getText().toString();
                userPn = tvUserTexPas.getText().toString();

                if (validate(userName, userEmail, userPhone, userGn, userPn)) {
                    Intent intent = new Intent(this, LoadFinesActivity.class);
                    intent.putExtra("name", userName);
                    intent.putExtra("email", userEmail);
                    intent.putExtra("phone", userPhone);
                    intent.putExtra("gn", userGn);
                    intent.putExtra("pn", userPn);
                    intent.putExtra("save", "save");
                    startActivity(intent);

                    ivImageCar.buildDrawingCache();
                    Bitmap bMap = ivImageCar.getDrawingCache();

                    SharedPrefer sharedPrefer = new SharedPrefer();
                    String lastCarInd = "" + sharedPrefer.getPref("lastCarInd", getApplicationContext());
                    int tmpCarInd = 1;
                    if (!lastCarInd.equals("null")) {
                        tmpCarInd = Integer.parseInt(lastCarInd) + 1;
                    }

                    createDirectoryAndSaveFile(bMap, "image_" + tmpCarInd + ".jpg");

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

        AlertDialog.Builder builder = new AlertDialog.Builder(AddCarActivity.this);
        builder.setTitle("Фотография");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(AddCarActivity.this);

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
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ivImageCar.setImageBitmap(thumbnail);
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