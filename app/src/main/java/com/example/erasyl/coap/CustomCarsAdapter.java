package com.example.erasyl.coap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.erasyl.coap.fragments.FragmentMainNews;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Erasyl on 28.03.2017.
 */

public class CustomCarsAdapter extends ArrayAdapter<Car> {

    private ArrayList<Car> cars;
    private Context context;
    private int resource;
    private File sdCard = Environment.getExternalStorageDirectory();
    private File directory = new File (sdCard.getAbsolutePath() + "/ЦОАП");


    public CustomCarsAdapter(Context context, int resource, ArrayList<Car> cars) {
        super(context, resource, cars);
        this.cars = cars;
        this.context = context;
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext()
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.custom_cars_layout, null, true);
        }
        Car product = getItem(position);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.ivCar);


        File file = new File(directory, "image_" + product.getId() +".jpg"); //or any other format supported
        if(file.exists()) {
            FileInputStream streamIn = null;
            try {
                streamIn = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = BitmapFactory.decodeStream(streamIn); //This gets the image
            if (bitmap.getByteCount() > 5) {
                imageView.setImageBitmap(bitmap);
            }
            try {
                streamIn.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            imageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.caricon));
        }


        TextView tvFineData = (TextView) convertView.findViewById(R.id.tvFineData);
        TextView tvGnFine = (TextView) convertView.findViewById(R.id.tvGnFine);
        Button btnShowCar = (Button) convertView.findViewById(R.id.btnShowTmpCar);
        final TextView tvTmpCarId = (TextView) convertView.findViewById(R.id.tvTmpCarId);

        tvFineData.setText(product.getDate());
        tvGnFine.setText(product.getGn());
        tvTmpCarId.setText(product.getId());

        btnShowCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LoadFinesActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                SharedPrefer sharedPrefer = new SharedPrefer();
                String tmpCarInd = tvTmpCarId.getText().toString();
                String userEmail = "" + sharedPrefer.getPref("email_" + tmpCarInd, getContext().getApplicationContext());
                String userPhone = "" + sharedPrefer.getPref("phone_" + tmpCarInd, getContext().getApplicationContext());
                String userGn = "" + sharedPrefer.getPref("gn_" + tmpCarInd, getContext().getApplicationContext());
                String userPn = "" + sharedPrefer.getPref("pn_" + tmpCarInd, getContext().getApplicationContext());

                intent.putExtra("name", "Пользователь");
                intent.putExtra("email", userEmail);
                intent.putExtra("phone", userPhone);
                intent.putExtra("gn", userGn);
                intent.putExtra("pn", userPn);
                intent.putExtra("id", tmpCarInd);
                context.startActivity(intent);
            }
        });

//        Picasso.with(context).load(product.getImage()).into(imageView);

        return convertView;
    }

    private void getImage() throws IOException {

    }
}
