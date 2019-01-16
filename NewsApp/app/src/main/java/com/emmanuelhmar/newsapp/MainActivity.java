package com.emmanuelhmar.newsapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();
    private final String API_KEY = "4e2c9e0c-e5ed-4047-b898-d8bf5733034b";
    private NewsAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

//        Call<List<NewsContent>> call = service.contributors("square", "retrofit");
        Call<com.emmanuelhmar.newsapp.Response> call = service.getAllContent();

        Log.i(TAG, "onCreate: " + call.request().url());
        Log.i(TAG, "onCreate: CALL" + call.isExecuted());

//        List<NewsContent> contributors = null;
//        try {
//            contributors = call.execute().body();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        for (NewsContent contributor : contributors) {
//            System.out.println(contributor.getPillarName() + " (" + contributor.getSectionName() + ")");
//        }

        call.enqueue(new Callback<com.emmanuelhmar.newsapp.Response>() {
            @Override
            public void onResponse(Call<com.emmanuelhmar.newsapp.Response> call, Response<com.emmanuelhmar.newsapp.Response> response) {

                News news = response.body().getNews();
                List<NewsContent> contributors = news.getResults();
                //                    contributors = call.execute().body();
                for (NewsContent contributor : contributors) {
                    System.out.println(contributor.getPillarName() + " (" + contributor.getSectionName() + ")");
                }
                generateDataList(contributors);
            }

            @Override
            public void onFailure(Call<com.emmanuelhmar.newsapp.Response> call, Throwable t) {

                Log.d(TAG, "onFailure: " + call.request().url());
                t.printStackTrace();
                Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }
//        @Override
//            public void onResponse(Call<List<NewsContent>> call, Response<List<NewsContent>> response) {
//                Log.d(TAG, "onResponse: CALL " + call);
//
//                Log.d(TAG, "onResponse: Response " + response.code());
//
//                Log.d(TAG, "onResponse: Body " + response.body());
////                Log.d(TAG, "onResponse: " + jsonResponse.newsContents);
////                JSONResponse jsonResponse = JSONResponse.parseJSON(String.valueOf(response.body()));
//
//                List<NewsContent> contributors = null;
//                //                    contributors = call.execute().body();
//                contributors = response.body();
//                for (NewsContent contributor : contributors) {
//                    System.out.println(contributor.getPillarName() + " (" + contributor.getSectionName() + ")");
//                }
//                generateDataList(response.body());
//            }
//
//            @Override
//            public void onFailure(Call<List<NewsContent>> call, Throwable t) {
//                Log.d(TAG, "onFailure: " + call.request().url());
//                t.printStackTrace();
//                Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
//            }

        );
    }

    private void generateDataList(List<NewsContent> news) {
        recyclerView = findViewById(R.id.recycler_view);
        adapter = new NewsAdapter(this, news);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);
    }
}
