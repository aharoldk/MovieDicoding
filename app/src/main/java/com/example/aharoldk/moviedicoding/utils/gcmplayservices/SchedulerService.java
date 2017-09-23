package com.example.aharoldk.moviedicoding.utils.gcmplayservices;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.aharoldk.moviedicoding.DetailActivity;
import com.example.aharoldk.moviedicoding.R;
import com.example.aharoldk.moviedicoding.pojo.Movie;
import com.example.aharoldk.moviedicoding.pojo.ResultsItem;
import com.example.aharoldk.moviedicoding.utils.APIClient;
import com.example.aharoldk.moviedicoding.utils.APIInterface;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SchedulerService extends GcmTaskService {
    static final String API_KEYS = "3ee47da55c8dae070eb764306712efc3";
    static final String LANG = "en-US";

    private List<ResultsItem> list = new ArrayList<>();

    public static String TAG_TASK_UPCOMING = "UpcomingTask";

    @Override
    public int onRunTask(TaskParams taskParams) {
        int result = 0;
        if (taskParams.getTag().equals(TAG_TASK_UPCOMING)){
            getCurrentWeather();
            result = GcmNetworkManager.RESULT_SUCCESS;
        }
        return result;
    }

    private void getCurrentWeather(){
        APIInterface apiInterface = APIClient.getApiClient().create(APIInterface.class);

        Call<Movie> call = apiInterface.getUpcomingMovie(API_KEYS, LANG);

        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(@NonNull Call<Movie> call, @NonNull Response<Movie> response) {
                Log.i("upcoming", ""+response.body());
                int code = response.code();

                if (code >= 200 && code < 300) {
                    list = response.body().getResults();



                    showNotification(getApplicationContext(), String.valueOf(list.get(0).getId()),list.get(0).getTitle(), list.get(0).getOverview(), 100);


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
    public void onInitializeTasks() {
        super.onInitializeTasks();
        SchedulerTask mSchedulerTask = new SchedulerTask(this);
        mSchedulerTask.createPeriodicTask();
    }

    private void showNotification(Context context, String s, String title, String message, int notifId) {
        NotificationManager notificationManagerCompat = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_alarm)
                .setContentText(message)
                .setColor(ContextCompat.getColor(context, android.R.color.black))
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setSound(alarmSound);

        Intent intentDetailActivity = new Intent(this, DetailActivity.class);
        intentDetailActivity.putExtra(DetailActivity.ID_MOVIE, s);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intentDetailActivity, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(contentIntent);

        notificationManagerCompat.notify(notifId, builder.build());

    }
}
