package com.eternallove.demo.zuccfairy.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.eternallove.demo.zuccfairy.DateHelper;
import com.eternallove.demo.zuccfairy.R;
import com.eternallove.demo.zuccfairy.util.RoundImageView;

import org.w3c.dom.Text;

import java.util.Date;

/**
 * Created by angelzouxin on 2016/12/20.
 */

public class CardAcitvity extends AppCompatActivity {
    private static String DATE_FORMAT = "yyyy年MM月dd日 HH:mm";
    private static final int TOP_Time = 5*60;
    private static final int BUTTON_Time = 10*60;

    //5:00~10:00打卡，在生成card前请先调用该函数判断时间
    public static int checkTime(){
        String datestr[] = DateHelper.dateToString(new Date(System.currentTimeMillis()), "HH:mm").split(":");
        int hour = Integer.valueOf(datestr[0]), min = Integer.valueOf(datestr[1]);
        if(hour*60+min < TOP_Time) return 0;
        else if(hour*60+min <= BUTTON_Time) return 1;
        else return 2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_punch_card);
        init();
    }

    void init() {
        RoundImageView card_pic_me = (RoundImageView) findViewById(R.id.card_pic_me); //我的头像
        TextView card_time = (TextView) findViewById(R.id.card_time); //HH:mm
        TextView card_ymd = (TextView) findViewById(R.id.card_ymd); //yyyy年MM月dd日
        TextView card_punch_time = (TextView) findViewById(R.id.card_punch_time); //打卡持续天数
        TextView card_text_me = (TextView) findViewById(R.id.card_text_me); //xxxx人正在参与，比xx%的人起的早
        TextView card_word = (TextView) findViewById(R.id.card_word); //行动比语言更有说服力
        ImageView card_QR = (ImageView) findViewById(R.id.card_QR); //二维码

        String datestr = DateHelper.dateToString(new Date(System.currentTimeMillis()), "yyyy年MM月dd日 HH:mm");
        String timestr[] = datestr.split(" ");
        card_ymd.setText(timestr[0]);
        card_time.setText(timestr[1]);

        /*TODO:从userbean中获取头像和打卡持续天数
        * card_pic_me.setImageResource(); ||  card_pic_me.setImageBitmap();
        * card_punch_time.setText();
        */

        card_text_me.setText("57891人正在参与，比80%的人起的早");
        card_word.setText("行动比语言更有说服力");

        //TODO:设置QR图片
    }
}