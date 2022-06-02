package com.example.plant;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class DietFragment extends Fragment {
    private static final String TAG = "PlantFragment";

    RecyclerView recyclerView;
    NoteAdapter adapter;

    Context context;
    OnTabItemSelectedListener listener;

    String URL;
    ArrayList<DietTable> DietArray;
    TextView [] day;
    TextView [] week_day;
    TextView [] lunch;
    TextView [] dinner;
    ProgressDialog customProgressDialog;
    int index = 0;
    boolean flag = false;


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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.diet, container, false);

        initUI(rootView);

        DietArray = new ArrayList<DietTable>();

        URL = "http://dormi.kongju.ac.kr/sub.php?code=041303";
        TextView test2 = (TextView)rootView.findViewById(R.id.lunch);
        TextView test = (TextView)rootView.findViewById(R.id.day1_lunch);


        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg)
            {
                Bundle bundle = msg.getData();

                for(int i = 0 ; i < 5 ; i ++)
                {
                    day[i].setText(DietArray.get(i).getDate());

                    String lunch_str = DietArray.get(i).getLunch();
                    lunch_str = lunch_str.replace(" ", "\n");
                    lunch[i].setText(lunch_str);

                    String dinner_str = DietArray.get(i).getDinner();
                    dinner_str = dinner_str.replace(" ", "\n");
                    dinner[i].setText(dinner_str);

                    if(i == index && flag)
                    {
                        day[i].setBackgroundResource(R.drawable.border_top_blue);
                        day[i].setTextColor(Color.WHITE);
                        week_day[i].setBackgroundResource(R.drawable.border_top_blue);
                        week_day[i].setTextColor(Color.WHITE);
                        lunch[i].setBackgroundResource(R.drawable.border_top_light_blue);
                        dinner[i].setBackgroundResource(R.drawable.border_down_light_blue);
                    }
                }


            }
        };


        new Thread()
        {
            @Override
            public void run(){

                Document document = null;
                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
                SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.getDefault());

                String month_str = monthFormat.format(currentTime)+"월";
                String day_str = dayFormat.format(currentTime)+"일";
                String now_day = month_str + " " + day_str;



                try {
                    document = Jsoup.connect(URL).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                // 웹에서 요일 -> 일 -> 월 이렇게 시작하므로 2번째 인덱스 부터 시작
                for(int i = 2 ;i < 7; i++)
                {
                    Element element = document.select("tr").get(i);
                    String contents = "";

                    for(int j = 0 ; j < 5; j++) // 요일 -> 날짜 -> 아침 -> 점심 -> 저녁
                    {
                        contents += element.select("td").get(j).text()+",";
                    }
                    System.out.println(contents);
                    String [] split = contents.split(",");

                    String weekday = split[0];

                    String date = split[1];
                    if(date.equals(now_day))
                    {
                        index = i-2;
                        flag = true;
                    }

                    String breakfast = split[2];
                    String lunch = split[3];
                    String dinner = split[4];

                    DietArray.add(new DietTable(date, lunch, dinner));
                }
                System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                System.out.println(index);

                Bundle bundle = new Bundle();

                Message msg = handler.obtainMessage();
                bundle.putString("o", "o");
                msg.setData(bundle);
                handler.sendMessage(msg);


            }
        }.start();


        return rootView;
    }


    private void initUI(ViewGroup rootView) {

        day = new TextView[5];
        week_day = new TextView[5];
        lunch = new TextView[5];
        dinner = new TextView[5];


        day[0] = (TextView) rootView.findViewById(R.id.day1);
        day[1] = (TextView) rootView.findViewById(R.id.day2);
        day[2] = (TextView) rootView.findViewById(R.id.day3);
        day[3] = (TextView) rootView.findViewById(R.id.day4);
        day[4] = (TextView) rootView.findViewById(R.id.day5);

        week_day[0] =(TextView) rootView.findViewById(R.id.weekday1);
        week_day[1] =(TextView) rootView.findViewById(R.id.weekday2);
        week_day[2] =(TextView) rootView.findViewById(R.id.weekday3);
        week_day[3] =(TextView) rootView.findViewById(R.id.weekday4);
        week_day[4] =(TextView) rootView.findViewById(R.id.weekday5);

        lunch[0] = (TextView) rootView.findViewById(R.id.day1_lunch);
        lunch[1] = (TextView) rootView.findViewById(R.id.day2_lunch);
        lunch[2] = (TextView) rootView.findViewById(R.id.day3_lunch);
        lunch[3] = (TextView) rootView.findViewById(R.id.day4_lunch);
        lunch[4] = (TextView) rootView.findViewById(R.id.day5_lunch);

        dinner[0] = (TextView) rootView.findViewById(R.id.day1_dinner);
        dinner[1] = (TextView) rootView.findViewById(R.id.day2_dinner);
        dinner[2] = (TextView) rootView.findViewById(R.id.day3_dinner);
        dinner[3] = (TextView) rootView.findViewById(R.id.day4_dinner);
        dinner[4] = (TextView) rootView.findViewById(R.id.day5_dinner);

    }










}
