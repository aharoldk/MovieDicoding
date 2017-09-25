package com.example.aharoldk.moviedicoding;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aharoldk.moviedicoding.pojo.pojodetail.Detail;
import com.example.aharoldk.moviedicoding.utils.APIClient;
import com.example.aharoldk.moviedicoding.utils.APIInterface;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {
    public static final String ID_MOVIE = "idMovie";

    @BindView(R.id.ivPoster) ImageView ivPoster;
    @BindView(R.id.tvTitle) TextView tvTitle;
    @BindView(R.id.tvDate) TextView tvDate;
    @BindView(R.id.tvDesk) TextView tvDesk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        // remove the following flag for version < API 19
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
        );
        setContentView(R.layout.activity_detail);


        String idMovie = getIntent().getStringExtra(ID_MOVIE);

        ButterKnife.bind(this);
        
        retrofit(idMovie);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    private void retrofit(String idMovie) {
        APIInterface apiInterface = APIClient.getApiClient().create(APIInterface.class);

        Call<Detail> call = apiInterface.getDetailMovie(idMovie, BuildConfig.API_KEY, BuildConfig.LANG);

        call.enqueue(new Callback<Detail>() {
            @Override
            public void onResponse(@NonNull Call<Detail> call, @NonNull Response<Detail> response) {
                Log.i("response", ""+response.body());
                int code = response.code();

                if (code >= 200 && code < 300) {

                    Picasso.with(getApplicationContext())
                            .load("https://image.tmdb.org/t/p/w185"+response.body().getPosterPath())
                            .into(ivPoster);

                    tvTitle.setText(String.valueOf(response.body().getTitle()));
                    tvDate.setText(String.valueOf(response.body().getReleaseDate()));
                    tvDesk.setText(String.valueOf(response.body().getOverview()));

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
            public void onFailure(@NonNull Call<Detail> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
