package lizewen.sdj.com.domobiletest;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;

import com.alibaba.fastjson.JSON;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class RateActivity extends AppCompatActivity {

    SwipeMenuRecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        initView();

        initData();
    }

    private void initView() {

        recyclerView = findViewById(R.id.recycle_view);
        recyclerView.setLongPressDragEnabled(true);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void initData() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                //https://api.fixer.io/latest

                URL url = null;
                try {
                    url = new URL("https://api.fixer.io/latest");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoInput(true);


                    urlConnection.connect();
                    int responseCode = urlConnection.getResponseCode();
                    if (200 == responseCode) {
                        InputStream inputStream = urlConnection.getInputStream();
                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        StringBuilder stringBuilder = new StringBuilder();
                        while (true) {
                            String s = bufferedReader.readLine();
                            if (s == null) {
                                break;
                            } else {
                                stringBuilder.append(s);

                            }
                        }
                        Bean bean = JSON.parseObject(stringBuilder.toString(), Bean.class);
                        new MyHandle(RateActivity.this).obtainMessage(MyHandle.SUCC, bean).sendToTarget();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    new MyHandle(RateActivity.this).obtainMessage(MyHandle.FAIL, "非法地址").sendToTarget();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                    new MyHandle(RateActivity.this).obtainMessage(MyHandle.FAIL, "未知错误").sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                    new MyHandle(RateActivity.this).obtainMessage(MyHandle.FAIL, "未知错误").sendToTarget();
                }
//
            }
        }).start();
    }


    private void onSucc(Bean bean) {

    }

    public void onFail(String msg) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.rete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        initData();
        return true;
    }

    class MyHandle extends Handler {
        private static final int SUCC = 101;


        private static final int FAIL = 102;

        WeakReference<RateActivity> reference;

        MyHandle(RateActivity activity) {

            reference = new WeakReference<>(activity);

        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case SUCC:
                    reference.get().onSucc((Bean) msg.obj);
                    break;
                case FAIL:
                    reference.get().onFail((String) msg.obj);
                    break;
            }
        }
    }
}
