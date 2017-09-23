package com.example.aharoldk.moviedicoding;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.aharoldk.moviedicoding.adapter.MovieAdapter;
import com.example.aharoldk.moviedicoding.detailclicklistener.DetailClickListener;
import com.example.aharoldk.moviedicoding.pojo.Movie;
import com.example.aharoldk.moviedicoding.pojo.ResultsItem;
import com.example.aharoldk.moviedicoding.utils.APIClient;
import com.example.aharoldk.moviedicoding.utils.APIInterface;
import com.example.aharoldk.moviedicoding.utils.alarm.AlarmPreference;
import com.example.aharoldk.moviedicoding.utils.alarm.AlarmReceiver;
import com.example.aharoldk.moviedicoding.utils.gcmplayservices.SchedulerTask;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, DetailClickListener {

    @BindView(R.id.etSearch) EditText etSearch;
    @BindView(R.id.btnCari) Button btnSearch;
    @BindView(R.id.rvMain) RecyclerView rvMain;

    static final String API_KEYS = "3ee47da55c8dae070eb764306712efc3";
    static final String LANG = "en-US";

    private List<ResultsItem> list = new ArrayList<>();
    private MovieAdapter mAdapter;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        declarate();
        setAlarm();
    }

    private void declarate() {
        ButterKnife.bind(this);

        mAdapter = new MovieAdapter(list);

        SchedulerTask mSchedulerTask = new SchedulerTask(this);
        mSchedulerTask.createPeriodicTask();

        btnSearch.setOnClickListener(this);

        rvMain.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rvMain.setHasFixedSize(true);
        rvMain.setNestedScrollingEnabled(false);
        rvMain.setAdapter(mAdapter);


        mAdapter.setItemClickListener(this);
    }


    private void setAlarm() {
        Calendar calRepeatTimeTime = Calendar.getInstance();

        AlarmPreference alarmPreference = new AlarmPreference(this);
        AlarmReceiver alarmReceiver = new AlarmReceiver();

        calRepeatTimeTime.set(Calendar.HOUR_OF_DAY, 9);
        calRepeatTimeTime.set(Calendar.MINUTE, 15);

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        String repeatTimeTime = timeFormat.format(calRepeatTimeTime.getTime());

        alarmPreference.setRepeatingTime(repeatTimeTime);

        Log.i("repeatTimeTime", ""+repeatTimeTime);
        alarmReceiver.setRepeatingAlarm(this, alarmPreference.getRepeatingTime());

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCari:
                checkSearchEditText();
                break;

        }
    }

    private void checkSearchEditText() {
        String search = etSearch.getText().toString();

        if(!TextUtils.isEmpty(search)){
            parteeeehRetrofit(search);
        } else {
            Toast.makeText(this, "Please Fill Search", Toast.LENGTH_SHORT).show();
        }
    }

    private void parteeeehRetrofit(String search) {
        APIInterface apiInterface = APIClient.getApiClient().create(APIInterface.class);

        Call<Movie> call = apiInterface.getSearchMovie(API_KEYS, LANG, search);

        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(@NonNull Call<Movie> call, @NonNull Response<Movie> response) {
                Log.i("response", ""+response.body());
                int code = response.code();

                if (code >= 200 && code < 300) {

                    list = response.body().getResults();
                    mAdapter.setData(list);

                } else if (code == 401) {
                    Toast.makeText(getApplicationContext(), "Error "+code+" : Response Unauthenticated", Toast.LENGTH_SHORT).show();
                } else if (code >= 400 && code < 500) {
                    Toast.makeText(getApplicationContext(), "Error "+code+" : Response Client Error", Toast.LENGTH_SHORT).show();
                } else if (code >= 500 && code < 600) {
                    Toast.makeText(getApplicationContext(), "Error "+code+" : Response Server Error", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Error "+code+" : Unexpected Response", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Movie> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }


    @Override
    public void onItemDetailClicked(String idMovie) {
        Intent intentDetailActivity = new Intent(this, DetailActivity.class);
        intentDetailActivity.putExtra(DetailActivity.ID_MOVIE, idMovie);

        startActivity(intentDetailActivity);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
