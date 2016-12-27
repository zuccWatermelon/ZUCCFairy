package com.eternallove.demo.zuccfairy.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.eternallove.demo.zuccfairy.R;
import com.eternallove.demo.zuccfairy.db.FairyDB;
import com.eternallove.demo.zuccfairy.modle.ReceivedBean;
import com.eternallove.demo.zuccfairy.modle.UserBean;
import com.eternallove.demo.zuccfairy.ui.adapters.ChatAdapter;

import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int NUM_BOT_DIR_1=1;//子目录中item的个数
    private static final int NUM_BOT_DIR_3=1;

    public static String User_id;
    private InputMethodManager imm;
    private View[] mViews = new View[2];
    private int mCurrent;
    private int mPopupHeight;
    private View mPopupView1;
    private View mPopupView3;
    private PopupWindow mPopupWindow; //弹窗
    private TextView mtvDailyPunch;
    private TextView mtvHomepage;
    private FairyDB fairyDB;
    private ChatAdapter adapter;
    private ReceivedBean receivedBean;
    private List<ReceivedBean> mPastList;
    private List<ReceivedBean> mNewList;

    @BindView(R.id.swipeRefreshLayout)    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recyclerView)          RecyclerView       mRecyclerView;
    @BindView(R.id.edt_bot_send)          EditText           mEditText;
    @BindView(R.id.btn_bot_send)          Button             mBtnSend;
    @BindView(R.id.bot_dir_1)             Button             mBtnBotDir1;
    @BindView(R.id.bot_dir_2)             Button             mBtnBotDir2;
    @BindView(R.id.bot_dir_3)             Button             mBtnBotDir3;
    public static void actionStart(Context context){
        Intent intent=new Intent();
        intent.setClass(context,MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mViews[0] = findViewById(R.id.ll_bot_dir);
        mViews[1] = findViewById(R.id.ll_bot_in);
        mCurrent = 0;
        mPopupHeight = (int ) this.getResources().getDimension(R.dimen.height_popupWindow);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        mPopupView1 = getLayoutInflater().inflate(R.layout.popup_window_dir_1,null);
        mPopupView3 = getLayoutInflater().inflate(R.layout.popup_window_dir_3,null);

        mtvDailyPunch = (TextView) mPopupView1.findViewById(R.id.tv_daily_punch);
        mtvHomepage = (TextView) mPopupView3.findViewById(R.id.tv_homepage);

        mPopupWindow = new PopupWindow(mPopupView1,WRAP_CONTENT, WRAP_CONTENT, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));

        mBtnSend.setOnClickListener(this);
        mBtnBotDir1.setOnClickListener(this);
        mBtnBotDir2.setOnClickListener(this);
        mBtnBotDir3.setOnClickListener(this);
        mtvDailyPunch.setOnClickListener(this);
        mtvHomepage.setOnClickListener(this);
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                new GetReceived().execute((Void)null);
            }
        });
        this.fairyDB = FairyDB.getInstance(this);
        User_id = PreferenceManager.getDefaultSharedPreferences(this).getString("user_id",null);
        mPastList = fairyDB.loadReceivedAll(User_id);
        mNewList = new ArrayList<>();
        adapter = new ChatAdapter(MainActivity.this, mPastList,mNewList);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(
                new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, true));
        mRecyclerView.setHasFixedSize(true);
        new GetReceived().execute((Void)null);
    }

    /**
     * 如其名
     * @param view
     */
    public void shift(View view) {

        mViews[mCurrent].animate().translationY(mViews[mCurrent].getHeight())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mViews[1 - mCurrent].animate().translationY(0)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        mCurrent = 1 - mCurrent;
                                    }
                                }).start();
                    }
                }).start();
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    /**
     * 如果点击的还是输入框就返回FALSE
     * @param v
     * @param event
     * @return
     */
    public  boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = { 0, 0 };
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bot_dir_1:
                mPopupWindow.setContentView(mPopupView1);
                mPopupWindow.showAsDropDown(mBtnBotDir1,0,-1*(mViews[0].getHeight()+NUM_BOT_DIR_1* mPopupHeight));
                break;
            case R.id.bot_dir_2:
                CalendarActivity.actionStart(this);
                break;
            case R.id.bot_dir_3:
                mPopupWindow.setContentView(mPopupView3);
                mPopupWindow.showAsDropDown(mBtnBotDir3,0,-1*(mViews[0].getHeight()+NUM_BOT_DIR_3* mPopupHeight));
                break;
            case R.id.tv_daily_punch:
                CardAcitvity.actionStart(MainActivity.this);
                mPopupWindow.dismiss();
                break;
            case R.id.tv_homepage:
                Toast.makeText(this, "你点击了“home”按键！", Toast.LENGTH_SHORT).show();
                mPopupWindow.dismiss();
                break;
            case R.id.btn_bot_send:
                String msg = mEditText.getText().toString();
                if(msg == null ||"".equals(msg))
                    break;
                receivedBean = new ReceivedBean(User_id,System.currentTimeMillis(),msg,null);
                mNewList.add(receivedBean);
                adapter.notifyDataSetChanged();
                fairyDB.saveReceived(receivedBean);
                mEditText.setText("");
                break;
            default:break;
        }
    }

    private class GetReceived extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mSwipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            //TODO : get date from ...then update mReceivedList
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            adapter.notifyDataSetChanged();
            mSwipeRefreshLayout.setRefreshing(false);
        }

    }
}