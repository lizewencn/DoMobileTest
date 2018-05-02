package lizewen.sdj.com.domobiletest;

import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class RateActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    private MyHandle handle = new MyHandle(this);
    RateAdapter rateAdapter;

    private List<RateBean> rateBeanList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        initView();
        rateBeanList = new ArrayList<>();
        initData();
    }

    private void initView() {

        setTitle("汇率助手");
        recyclerView = findViewById(R.id.recycle_view);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.top = 1;
            }
        });

    }

    private void initData() {
        if (rateBeanList != null || !rateBeanList.isEmpty()) {
            rateBeanList.clear();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                //https://api.fixer.io/latest

                URL url = null;
                try {
                    url = new URL("https://api.fixer.io/latest");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
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
//                        Bean bean = JSON.parseObject(stringBuilder.toString(), Bean.class);
                        handle.obtainMessage(MyHandle.SUCC, stringBuilder.toString()).sendToTarget();
                    } else {
                        handle.obtainMessage(MyHandle.FAIL, String.valueOf(responseCode)).sendToTarget();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    handle.obtainMessage(MyHandle.FAIL, "非法地址").sendToTarget();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                    handle.obtainMessage(MyHandle.FAIL, "未知错误").sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                    handle.obtainMessage(MyHandle.FAIL, "未知错误").sendToTarget();
                }
//
            }
        }).start();
    }


    private void onSucc(String result) {
        if (TextUtils.isEmpty(result)) {
            Toast.makeText(this, "获取数据失败", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject jsonObject = JSON.parseObject(result);

        JSONObject rates = jsonObject.getJSONObject("rates");

        Map<String, Object> innerMap = rates.getInnerMap();
        TreeMap<String, Object> newMap = sortMap(innerMap);
        Log.e("aaaaaaaaaa:", "" + innerMap.toString());

        if (innerMap.isEmpty()) return;
        //获取美元汇率
        BigDecimal usd = (BigDecimal) newMap.get("USD");
        Set<Map.Entry<String, Object>> entries = newMap.entrySet();
        Iterator<Map.Entry<String, Object>> iterator = entries.iterator();


        rateBeanList = new ArrayList<>(newMap.size());
        RateBean rateBean = null;

        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            rateBean = new RateBean();
            String key = entry.getKey();
            BigDecimal value = (BigDecimal) entry.getValue();
            rateBean.setCurrencyName(key);
            rateBean.setRate((BigDecimal) value);
            //计算以1美元为基准的金额
            if("USD".equals(key)){
                rateBean.setAmount(new BigDecimal("1"));
            }else{
                BigDecimal multiply = new BigDecimal("1").divide(usd, 4, BigDecimal.ROUND_HALF_UP).multiply( value);
                rateBean.setAmount(multiply);
            }
            rateBeanList.add(rateBean);

        }
        /**
         * 重现用户的自定义排序
         */
        Collections.sort(rateBeanList,new MyComparator(this));


        rateAdapter = new RateAdapter(rateBeanList, this);
        recyclerView.setAdapter(rateAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SimpleItemTouchHelperCallback(rateAdapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }


    /**
     * 将取回的数据按字母顺序排序
     *
     * @param map
     */
    private TreeMap<String, Object> sortMap(Map<String, Object> map) {

        TreeMap<String, Object> treeMap = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        treeMap.putAll(map);

        return treeMap;
    }

    public void onFail(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
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
                    reference.get().onSucc((String) msg.obj);
                    break;
                case FAIL:
                    reference.get().onFail((String) msg.obj);
                    break;
            }
        }
    }

}
