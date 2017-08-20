package com.example.erasyl.coap.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.erasyl.coap.AddCarActivity;
import com.example.erasyl.coap.Car;
import com.example.erasyl.coap.EditCarActivity;
import com.example.erasyl.coap.LoadFinesActivity;
import com.example.erasyl.coap.R;
import com.example.erasyl.coap.SharedPrefer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link FragmentMyCars#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentMyCars extends Fragment implements View.OnClickListener{

    private String SAVED_TEXT = "gos_nums";
    Button btnAddNewCar;
    private ArrayList<Car> arrayCars;
    private ListView lvCars;
    private File sdCard = Environment.getExternalStorageDirectory();
    private File directory = new File (sdCard.getAbsolutePath() + "/ЦОАП");


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public FragmentMyCars() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentMyCars.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentMyCars newInstance(String param1, String param2) {
        FragmentMyCars fragment = new FragmentMyCars();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Мой автомобили");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mycars, container, false);

        lvCars = (ListView) view.findViewById(R.id.lvMyCars);
        arrayCars = new ArrayList<>();
        setCar();
        btnAddNewCar = (Button) view.findViewById(R.id.btnAddNewCar);
        btnAddNewCar.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddNewCar: {
                Intent intent = new Intent(getActivity(), AddCarActivity.class);
                startActivity(intent);
                break;
            }
        }
    }



    private void setCar() {

        SharedPrefer sharedPrefer = new SharedPrefer();
        String carsInds = "" + sharedPrefer.getPref("carsInds", getActivity().getApplicationContext());

        if (carsInds.length() > 0 && carsInds.charAt(0) == '$') {
            carsInds = carsInds.substring(1);
        }
        if (!carsInds.equals("null")) {
            String carsArr[] = carsInds.split("[$]");
            for (String tmpCarInd : carsArr) {
                String tmpGn = "" + sharedPrefer.getPref("gn_" + tmpCarInd, getActivity().getApplicationContext());
                if (!tmpGn.equals("null")) {
                    String date = "" + sharedPrefer.getPref("FineCheckedDate_" + tmpCarInd, getActivity().getApplicationContext());
                    arrayCars.add(new Car(
                            tmpCarInd,
                            "http://stonebridge-autoelectrics.co.uk/communities/2/000/001/523/882//images/6851173_533x304.png",
                            date,
                            tmpGn,
                            "***"
                    ));
                }
            }
        }
        CustomAdapter adapter = new CustomAdapter(
                getActivity().getApplicationContext(), R.layout.custom_cars_layout, arrayCars
        );
        lvCars.setAdapter(adapter);
    }

    public class CustomAdapter extends ArrayAdapter<Car> {

        private ArrayList<Car> cars;
        private Context context;
        private int resource;


        public CustomAdapter(Context context, int resource, ArrayList<Car> cars) {
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
                convertView = layoutInflater.inflate(R.layout.custom_cars_with_rem_layout, null, true);
            }
            Car product = getItem(position);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.ivCar);
            TextView tvFineData = (TextView) convertView.findViewById(R.id.tvFineData);
            TextView tvGnFine = (TextView) convertView.findViewById(R.id.tvGnFine);
            Button btnShowCar = (Button) convertView.findViewById(R.id.btnShowTmpCar);
            Button btnRemove = (Button) convertView.findViewById(R.id.btnRemove);
            Button btnEdit = (Button) convertView.findViewById(R.id.btnEdit);
            final TextView tvTmpCarId = (TextView) convertView.findViewById(R.id.tvTmpCarId);

            tvFineData.setText(product.getDate());
            tvGnFine.setText(product.getGn());
            tvTmpCarId.setText(product.getId());

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

            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EditCarActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    SharedPrefer sharedPrefer = new SharedPrefer();
                    String tmpCarInd = tvTmpCarId.getText().toString();
                    String userName = "" + sharedPrefer.getPref("name_" + tmpCarInd, getContext().getApplicationContext());
                    String userEmail = "" + sharedPrefer.getPref("email_" + tmpCarInd, getContext().getApplicationContext());
                    String userPhone = "" + sharedPrefer.getPref("phone_" + tmpCarInd, getContext().getApplicationContext());
                    String userGn = "" + sharedPrefer.getPref("gn_" + tmpCarInd, getContext().getApplicationContext());
                    String userPn = "" + sharedPrefer.getPref("pn_" + tmpCarInd, getContext().getApplicationContext());

                    intent.putExtra("name", userName);
                    intent.putExtra("email", userEmail);
                    intent.putExtra("phone", userPhone);
                    intent.putExtra("gn", userGn);
                    intent.putExtra("pn", userPn);
                    intent.putExtra("id", tmpCarInd);
                    context.startActivity(intent);
                }
            });

            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SharedPrefer shared = new SharedPrefer();
                    String ind = tvTmpCarId.getText().toString();
                    String userGn = "" + shared.getPref("gn_" + ind, getContext().getApplicationContext());

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(FragmentMyCars.this.getActivity());
                    builder1.setTitle("Подтверждение удаления");
                    builder1.setMessage("Вы уверены что вы хотите удалить машину с Гос. номером: \"" + userGn + "\" ?");
                    builder1.setCancelable(true);
                    builder1.setNeutralButton("Удалить",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    SharedPrefer sharedPrefer = new SharedPrefer();
                                    String tmpCarInd = tvTmpCarId.getText().toString();

                                    File fdelete = new File(sdCard.getAbsolutePath() + "/ЦОАП/image_" + tmpCarInd +".jpg");
                                    if (fdelete.exists()) {
                                        if (fdelete.delete()) {
                                            Toast.makeText(FragmentMyCars.this.getActivity(), "deleted image", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(FragmentMyCars.this.getActivity(), "not deleted iamge", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    sharedPrefer.removePref("email_" + tmpCarInd, getContext().getApplicationContext());
                                    sharedPrefer.removePref("phone_" + tmpCarInd, getContext().getApplicationContext());
                                    sharedPrefer.removePref("gn_" + tmpCarInd, getContext().getApplicationContext());
                                    sharedPrefer.removePref("pn_" + tmpCarInd, getContext().getApplicationContext());

                                    String carsInds = "" + sharedPrefer.getPref("carsInds", getActivity().getApplicationContext());
                                    carsInds = carsInds.replace("$" + tmpCarInd, "");
                                    sharedPrefer.putPref(getActivity().getApplicationContext(), "carsInds", carsInds);

                                    if (carsInds.trim().length() == 0) {
                                        sharedPrefer.putPref(getActivity().getApplicationContext(), "lastCarInd", "null");
                                    }

                                    FragmentMyCars myCarsFragment = new FragmentMyCars();
                                    FragmentManager manager = getActivity().getSupportFragmentManager();
                                    manager.beginTransaction().replace(
                                            R.id.relativelayout_for_fragment,
                                            myCarsFragment,
                                            myCarsFragment.getTag()
                                    ).commit();
                                    dialog.cancel();

                                }
                            });
                    builder1.setPositiveButton("Отмена",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();



                }
            });

//        Picasso.with(context).load(product.getImage()).into(imageView);

            return convertView;
        }
    }
}
