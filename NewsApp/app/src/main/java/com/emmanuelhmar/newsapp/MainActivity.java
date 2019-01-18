package com.emmanuelhmar.newsapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NewsAdapter.onNoteListener {
    private static final String TAG = MainActivity.class.getName();
    private final String API_KEY = "4e2c9e0c-e5ed-4047-b898-d8bf5733034b";
    private NewsAdapter adapter;
    private RecyclerView recyclerView;
    private List<NewsContent> newsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

//        Call the values without overriding the current saved settings
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

//        Get the boolean value from the Settings Activity
        Boolean switchPref = sharedPreferences.getBoolean(SettingsActivity.KEY_PREF_SWITCH, false);

        Log.d(TAG, "MakeText switchpref: ");
        Toast.makeText(this, switchPref.toString(), Toast.LENGTH_LONG).show();


        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

//        Call<List<NewsContent>> call = service.contributors("square", "retrofit");
        Call<com.emmanuelhmar.newsapp.Response> call = service.getAllContent();

        Log.i(TAG, "onCreate: " + call.request().url());
        Log.i(TAG, "onCreate: CALL" + call.isExecuted());

//        Call retrofit async
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
        );
    }

    private void generateDataList(List<NewsContent> news) {
        this.newsList = news;
        recyclerView = findViewById(R.id.recycler_view);
        adapter = new NewsAdapter(this, news, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewClick(int position) {
        Log.d(TAG, "onViewClick: " + position);
        NewsContent content = newsList.get(position);

        Log.d(TAG, "URI " + content.getWebURL());

        Uri uri = Uri.parse(content.getWebURL());

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);

        startActivity(intent);
    }
}
