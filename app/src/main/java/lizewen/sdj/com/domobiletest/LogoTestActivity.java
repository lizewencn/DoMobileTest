package lizewen.sdj.com.domobiletest;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogoTestActivity extends AppCompatActivity {

    ImageView logo;


    private final String REG = "<link\\s+rel=\"apple\\-touch\\-icon([^>]+)\">";

    private final int SUCC = 101;


    private final int FAIL = 102;


    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case SUCC:

                    String url = (String) msg.obj;
                    if (TextUtils.isEmpty(url)) return;
                    if (!url.startsWith("http")) {
                        url = "http:" + url;
                    }
                    Glide.with(LogoTestActivity.this)
                            .load(url)

                            .into(logo);
                    break;
                case FAIL:
                    Toast.makeText(LogoTestActivity.this, (String) msg.obj, 1).show();
                    break;
            }


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo_test);

        final EditText website = findViewById(R.id.editText);

        Button btn = findViewById(R.id.button);


        logo = findViewById(R.id.imageView);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String websiteStr = website.getText().toString().trim();
                checkWebSite(websiteStr);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
//                            URL url = new URL(websiteStr);
//                            URL url = new URL("https://m.taobao.com");
                            URL url = new URL("https://tieba.baidu.com");
//                            URL url = new URL("https://m.weibo.com");
                            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                            urlConnection.setRequestMethod("POST");
                            urlConnection.setDoInput(true);
                            urlConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 10_3 like Mac OS X) AppleWebKit/603.1.30 (KHTML, like Gecko) Version/10.3 Mobile/14E277 Safari/603.1.30");


                            urlConnection.connect();
                            int responseCode = urlConnection.getResponseCode();
                            if (200 == responseCode) {
                                long startMill = System.currentTimeMillis();
                                Log.e("time", "start = " + startMill);
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

                                String result = stringBuilder.toString();
                                inputStream.close();

                                String label = getLabel(result);

                                String imgUrl = getUrl(label);

                                long endMill = System.currentTimeMillis();
                                Log.e("time", "end = " + endMill);
                                Log.e("time", "inteal = " + (endMill - startMill));
                                handler.obtainMessage(SUCC, imgUrl).sendToTarget();
                            } else {
                                handler.obtainMessage(FAIL, "" + responseCode).sendToTarget();
                            }

                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                            //todo 非法地址
                            handler.obtainMessage(FAIL, "请输入正确的网址").sendToTarget();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                }).start();
            }
        });
    }

    private void checkWebSite(String websiteStr) {

    }

    /**
     * 提取图标所在标签
     *
     * @param result
     * @return
     */
    private String getLabel(String result) {
        try {
            if (TextUtils.isEmpty(result)) {
                return null;
            }
            Pattern pattern = Pattern.compile(REG);
            String headResult = result.split("</head>")[0];
            Matcher matcher = pattern.matcher(headResult);
            matcher.find();
            String group = matcher.group();
            return group;
        } catch (Exception e) {
            handler.obtainMessage(FAIL, "好像没有获取到图标呢~").sendToTarget();
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从标签中提取图片地址
     *
     * @param labelStr
     * @return
     */
    private String getUrl(String labelStr) {

        try {
            String href = labelStr.split("href=\"")[1];
            String substring = href.substring(0, href.length() - 2);
            return substring;
        } catch (Exception e) {
            handler.obtainMessage(FAIL, "好像没有获取到图标呢~").sendToTarget();
            e.printStackTrace();
        }
        return null;
    }
}
