package com.example.aharoldk.moviedicoding;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

    static final String API_KEYS = "3ee47da55c8dae070eb764306712efc3";
    static final String LANG = "en-US";

    @BindView(R.id.ivPoster) ImageView ivPoster;
    @BindView(R.id.tvTitle) TextView tvTitle;
    @BindView(R.id.tvDate) TextView tvDate;
    @BindView(R.id.tvDesk) TextView tvDesk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        String idMovie = getIntent().getStringExtra(ID_MOVIE);

        Toast.makeText(this, ""+idMovie, Toast.LENGTH_SHORT).show();
        ButterKnife.bind(this);
        
        retrofit(idMovie);
    }

    private void retrofit(String idMovie) {
        APIInterface apiInterface = APIClient.getApiClient().create(APIInterface.class);

        Call<Detail> call = apiInterface.getDetailMovie(idMovie, API_KEYS, LANG);

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
