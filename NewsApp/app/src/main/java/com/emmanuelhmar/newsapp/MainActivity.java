package com.emmanuelhmar.newsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NewsAdapter.onNoteListener {
    private static final String TAG = MainActivity.class.getName();
    private final String API_KEY = "4e2c9e0c-e5ed-4047-b898-d8bf5733034b";
    private NewsAdapter adapter;
    private RecyclerView recyclerView;
    private List<NewsContent> newsList;
    private LinearLayoutManager layoutManager;
    private Parcelable state;
    private final static String BUNDLE_LAYOUT = "recycler_layout";
    private boolean isConnected;
    @BindView(R.id.search_button)
    Button button;
    @BindView(R.id.search_bar)
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

//        Call the values without overriding the current saved settings
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

//        Check if the device is connected to the internet
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

//        True if connected, False if not
        isConnected = activeNetwork != null && activeNetwork.isConnected();

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

        String orderBySetting = getPrefSettings(sharedPreferences, "orderBy");
        String sectionSetting = getPrefSettings(sharedPreferences, "section");

        runTheNetwork(service, null, orderBySetting, sectionSetting);

//        Run this when button is clicked
        button.setOnClickListener(view -> {
            String search = editText.getText().toString();

            if (search.isEmpty()) {
                search = null;
            }

            runTheNetwork(service, search, orderBySetting, sectionSetting);
        });
    }

    private void runTheNetwork(GetDataService service, String search, String orderBySetting, String sectionSetting) {

//        Internet is Connected then proceed
        if (isConnected) {
            Call<com.emmanuelhmar.newsapp.Response> call = service.getAllContent(search, orderBySetting, sectionSetting, API_KEY);

            Log.i(TAG, "onCreate: " + call.request().url());

//        Call retrofit async
            call.enqueue(new Callback<com.emmanuelhmar.newsapp.Response>() {
                             @Override
                             public void onFailure(Call<com.emmanuelhmar.newsapp.Response> call, Throwable t) {

                                 Log.d(TAG, "onFailure: " + call.request().url());
                                 t.printStackTrace();
                                 Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                             }

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
                         }
            );
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show();
        }

    }

    private void generateDataList(List<NewsContent> news) {
        this.newsList = news;
        recyclerView = findViewById(R.id.recycler_view);
        adapter = new NewsAdapter(this, news, this);
        layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);
        layoutManager.onRestoreInstanceState(state);
    }

    //    Create the Options Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //    When the menu options are selected
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

    private String getPrefSettings(SharedPreferences sharedPreferences, String setting) {

        switch (setting) {
            case "section":
                String sectionSetting = sharedPreferences.getString(getString(R.string.settings_section_key),
                        getString(R.string.settings_section_default));
                assert sectionSetting != null;
                if (sectionSetting.isEmpty()) {
                    sectionSetting = null;
                }
                return sectionSetting;
            case "orderBy":
//        Get the value of the listPreference section from the setting page
                String orderBySetting = sharedPreferences.getString(getString(R.string.settings_order_by_key),
                        getString(R.string.settings_order_by_default));

//        If orderBySetting if empty, pass null to not pass the orderBySetting query
                assert orderBySetting != null;
                if (orderBySetting.isEmpty()) {
                    orderBySetting = null;
                }
                return orderBySetting;
            default:
                break;

        }

        return null;
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: ");
        super.onPause();

    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_LAYOUT,
                layoutManager.onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            state = savedInstanceState.getParcelable(BUNDLE_LAYOUT);
        }
    }

    //    Implemented a View Clicklistener for RecyclerView
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
