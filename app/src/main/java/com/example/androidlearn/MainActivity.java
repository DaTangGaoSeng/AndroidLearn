package com.example.androidlearn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.os.Handler;

import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    final static int UPTYPE = 1;
    final static int DOWNTYPE = 0;
    private List<Category> categories = new ArrayList<>();
    private MyAdapter myAdapter;
    private Handler handler = new Handler();
    private SwipeRefreshLayout swipeRefreshLayout;
    private Random random = new Random();
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(linearLayoutManager);
        swipeRefreshLayout = findViewById(R.id.refresh);
        myAdapter = new MyAdapter(categories, this);
        recyclerView.setAdapter(myAdapter);
        request(UPTYPE);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                request(UPTYPE);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        recyclerView.addOnScrollListener(new OnLastitemListener() {
            @Override
            void loadmore() {
                request(DOWNTYPE);
            }
        });
    }

    private void request(final int Type) {
        final StringBuilder stringBuilder = new StringBuilder();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //随机访问网页
                    int i = random.nextInt(20);
                    int j = random.nextInt(20);
                    URL url = new URL("http://gank.io/api/data/Android/" + i + "/" + j);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setConnectTimeout(8000);
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String tem;
                    while ((tem = bufferedReader.readLine()) != null)
                        stringBuilder.append(tem);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (Type == UPTYPE) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            translate(stringBuilder.toString(), UPTYPE);
                        }
                    });
                } else if (Type == DOWNTYPE) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            translate(stringBuilder.toString(), DOWNTYPE);
                        }
                    });
                }
            }
        }).start();

    }

    private void translate(String json, int Type) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            final JSONArray jsonArray = jsonObject.getJSONArray("results");
            final int length = jsonArray.length();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                Map<String, String> hashMap = new HashMap<>();
                String id = jsonObject1.getString("_id");
                String createdat = jsonObject1.getString("createdAt");
                String des = jsonObject1.optString("desc");
                String desc;
                if (des.length() > 35) {
                    des.substring(0, 35);
                    desc = des + "....";
                } else desc = des;
                String publishedAt = jsonObject1.getString("publishedAt").substring(0, 10);
                String source = jsonObject1.getString("source");
                String type = jsonObject1.getString("type");
                String url = jsonObject1.getString("url");
                String who = "by: " + jsonObject1.getString("who");
                hashMap.put("id", id);
                hashMap.put("createdAt", createdat);
                hashMap.put("desc", desc);
                hashMap.put("publishedAt", publishedAt);
                hashMap.put("source", source);
                hashMap.put("type", type);
                hashMap.put("url", url);
                hashMap.put("who", who);
                List<String> tem = new ArrayList<>();//存放图片网址的
                if (Type == UPTYPE) {
                    if (jsonObject1.has("images")) {
                        JSONArray jsonArray1 = jsonObject1.getJSONArray("images");
                        for (int j = 0; j < jsonArray1.length(); j++) {
                            tem.add(jsonArray1.get(j).toString());
                        }
                        categories.add(0, new Category(hashMap, tem));
                    } else
                        categories.add(0, new Category(hashMap));
                } else if (Type == DOWNTYPE) {
                    if (jsonObject1.has("images")) {
                        JSONArray jsonArray1 = jsonObject1.getJSONArray("images");
                        for (int j = 0; j < jsonArray1.length(); j++) {
                            tem.add(jsonArray1.get(j).toString());
                        }
                        categories.add(categories.size(), new Category(hashMap, tem));
                    } else
                        categories.add(categories.size(), new Category(hashMap));
                }
            }
            if (Type == UPTYPE) {

                    myAdapter.notifyItemRangeInserted(0, length);
                myAdapter.notifyItemRangeChanged(0, myAdapter.getItemCount());
                myAdapter.notifyDataSetChanged();

            } else {
                myAdapter.notifyItemRangeInserted(myAdapter.getItemCount() - length, myAdapter.getItemCount() - 1);
                myAdapter.notifyItemRangeChanged(myAdapter.getItemCount() - length, myAdapter.getItemCount());
                myAdapter.notifyDataSetChanged();
            }
            Toast.makeText(this,"更新了"+jsonArray.length()+"条信息",Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
