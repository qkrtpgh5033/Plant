package com.example.plant;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;

import lib.kingja.switchbutton.SwitchMultiButton;
import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.plant.NoteAdapter;
import com.example.plant.OnTabItemSelectedListener;
import com.example.plant.Plant;
import com.example.plant.R;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DataSnapshot;


import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class PlantFragment extends Fragment {
    private static final String TAG = "PlantFragment";

    RecyclerView recyclerView;
    NoteAdapter adapter;

    Context context;
    OnTabItemSelectedListener listener;

    String URL;
    Data data;

    TextView temp;
    TextView humi;
    TextView soil_humi;
    ImageView tomato_img;


    private FirebaseDatabase database = FirebaseDatabase.getInstance(); // 파이어베이스 객체
    private DatabaseReference databaseReference = database.getReference();  // 데이터베이스를 참조할 수 있는 객체
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;

        if (context instanceof OnTabItemSelectedListener) {
            listener = (OnTabItemSelectedListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (context != null) {
            context = null;
            listener = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.activity_plant, container, false);

        initUI(rootView);

        databaseReference.child("plant").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Plant plant = snapshot.getValue(Plant.class);
                temp.setText(plant.getTemp()+"℃");      // 온도
                humi.setText(plant.getHumi()+"%");      // 습도
                soil_humi.setText(plant.getSoil_humi()+"%");    //토양습도

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return rootView;
    }


    private void initUI(ViewGroup rootView) {

        temp = (TextView)rootView.findViewById(R.id.temp);
        humi = (TextView)rootView.findViewById(R.id.humi);
        soil_humi = (TextView)rootView.findViewById(R.id.soil_humi);

        tomato_img = (ImageView)rootView.findViewById(R.id.tomato_img);
        Glide.with(this).load(R.drawable.tomato_move).into(tomato_img);

        URL = "https://search.naver.com/search.naver?where=nexearch&sm=top_hty&fbm=0&ie=utf8&query=%EC%B2%9C%EC%95%88+%EB%82%A0%EC%94%A8";
        weather(rootView);

    }


    private void weather(ViewGroup rootView)
    {
        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg)
            {
                Bundle bundle = msg.getData();
                TextView temp = (TextView)rootView.findViewById(R.id.weather_temp);
                temp.setText(bundle.getString("temp"));

                ImageView weather_img = (ImageView)rootView.findViewById(R.id.weather_state);
                TextView weather_state_txt = (TextView)rootView.findViewById(R.id.weather_state_txt);
                String weather_state = bundle.getString("weather_state");
                weather_state_txt.setText(weather_state);

                if(weather_state.equals("맑음"))
                    weather_img.setImageResource(R.drawable.sunny);
                else if(weather_state.equals("흐림"))
                    weather_img.setImageResource(R.drawable.cloud_many);
                else if(weather_state.contains("구름"))
                    weather_img.setImageResource(R.drawable.cloud);
                else if(weather_state.contains("비"))
                {
                    weather_img.setImageResource(R.drawable.rain);
                    Glide.with(rootView).load(R.drawable.rain_gif).into(weather_img);
                }
                else if(weather_state.contains("눈"))
                    weather_img.setImageResource(R.drawable.snow);
                else
                    weather_img.setImageResource(R.drawable.cloud);

                ImageView[] uv_dust_img = new ImageView[2];
                TextView uv_state_txt = (TextView)rootView.findViewById(R.id.uv_state_txt);
                TextView dust_state_txt = (TextView)rootView.findViewById(R.id.dust_state_txt);
                uv_dust_img[0] = (ImageView)rootView.findViewById(R.id.uv_state);
                uv_dust_img[1] = (ImageView)rootView.findViewById(R.id.dust_state);

                String[] uv_dust = bundle.getStringArray("uv_dust");
                uv_state_txt.setText(uv_dust[0]);
                dust_state_txt.setText(uv_dust[1]);

                if(uv_dust[0].contains("좋음"))
                    uv_dust_img[0].setImageResource(R.drawable.good);
                else if(uv_dust[0].equals("보통"))
                    uv_dust_img[0].setImageResource(R.drawable.soso);
                else if(uv_dust[0].equals("나쁨"))
                    uv_dust_img[0].setImageResource(R.drawable.bad);
                else if(uv_dust[0].equals("매우 나쁨"))
                    uv_dust_img[0].setImageResource(R.drawable.real_bad);
                else
                    uv_dust_img[0].setImageResource(R.drawable.soso);

                if(uv_dust[1].contains("좋음"))
                    uv_dust_img[1].setImageResource(R.drawable.good);
                else if(uv_dust[1].equals("보통"))
                    uv_dust_img[1].setImageResource(R.drawable.soso);
                else if(uv_dust[1].equals("나쁨"))
                    uv_dust_img[1].setImageResource(R.drawable.bad);
                else if(uv_dust[1].equals("매우 나쁨"))
                    uv_dust_img[1].setImageResource(R.drawable.real_bad);
                else
                    uv_dust_img[1].setImageResource(R.drawable.soso);


                //밤,낮 구분
                ImageView allday = (ImageView)rootView.findViewById(R.id.allday);
                LinearLayout weather_background = (LinearLayout)rootView.findViewById(R.id.weather_background);
                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat hourFormat = new SimpleDateFormat("hh", Locale.getDefault());
                SimpleDateFormat Format= new SimpleDateFormat("a", Locale.getDefault()); // 오전오후
                int hour = Integer.parseInt(hourFormat.format(currentTime));
                String morning_afternoon = Format.format(currentTime);
                System.out.println(morning_afternoon);

                System.out.println("오전 or 오후 : " + morning_afternoon);
                System.out.println("시간 : " + hour);


                if( (((hour >= 6 && hour <= 12) && (morning_afternoon.equals("오후")))  || (((hour >= 0 && hour <= 6) || hour == 12)) && ((morning_afternoon.equals("오전")))) )
                {
                    allday.setImageResource(R.drawable.afternoon);
                    weather_background.setBackgroundResource(R.drawable.border_strong_blue);
                }
                else if( (morning_afternoon.equals("오후")) && (hour>=4 && hour <= 6))
                {
                    allday.setImageResource(R.drawable.afternoon);
                    weather_background.setBackgroundResource(R.drawable.border_afternoon);
                }
                else
                {
                    allday.setImageResource(R.drawable.morning);
                    weather_background.setBackgroundResource(R.drawable.border_light_blue);
                }



            }
        };


        new Thread()
        {
            @Override
            public void run()
            {
                Document document = null;

                try {
                    document = Jsoup.connect(URL).get();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                try{
                    Elements element = document.select(".temperature_text").select("strong");
                    System.out.println("@@@@@@@@@@@@@@@");
                    System.out.println(element.text());
                    String weather_temp = element.text().split("°")[0].substring(5)+"°";

                    element = document.select(".temperature_info").select("p span.weather.before_slash");
                    String weather_state = element.text();
                    System.out.println("@@@@@@@@@@@@@@@");
                    System.out.println(weather_state);


                    element = document.select(".report_card_wrap").select("span.txt");
                    String dust = element.get(0).text();
                    System.out.println(dust);

                    element =  document.select(".report_card_wrap").select("span.txt");
                    String uv = element.get(2).text();
                    System.out.println(uv);

                    String[] uv_dust = new String[2];
                    uv_dust[0] = uv;
                    uv_dust[1] = dust;

                    Bundle bundle = new Bundle();

                    Message msg = handler.obtainMessage();
                    bundle.putString("temp", weather_temp);
                    msg.setData(bundle);
                    handler.sendMessage(msg);

                    msg = handler.obtainMessage();
                    bundle.putString("weather_state", weather_state);
                    msg.setData(bundle);
                    handler.sendMessage(msg);

                    msg = handler.obtainMessage();
                    bundle.putStringArray("uv_dust", uv_dust);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }catch (Exception e){
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run()
                        {
                            Toast.makeText(getActivity(), "웹 html 구조가 변경 되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }, 0);
                }


            }

        }.start();




    }


}
