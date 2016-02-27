package com.johnnyyin.retrofitdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.retrofit.SimpleService;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.result)
    TextView mResult;
    @Bind(R.id.test)
    Button mTestBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mTestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(SimpleService.API_URL)
                        .callFactory(NetUtils.okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                // Create an instance of our GitHub API interface.
                SimpleService.GitHub github = retrofit.create(SimpleService.GitHub.class);

                // Create a call instance for looking up Retrofit contributors.
                Call<List<SimpleService.Contributor>> call = github.contributors("square", "retrofit");

                // Fetch and print a list of the contributors to the library.
                call.enqueue(new Callback<List<SimpleService.Contributor>>() {
                    @Override
                    public void onResponse(Call<List<SimpleService.Contributor>> call, Response<List<SimpleService.Contributor>> response) {
                        List<SimpleService.Contributor> body = response.body();
                        if (body == null || body.isEmpty()) {
                            return;
                        }
                        StringBuilder sb = new StringBuilder();
                        for (SimpleService.Contributor contributor : body) {
                            sb.append(contributor.login);
                            sb.append("[");
                            sb.append(contributor.contributions);
                            sb.append("]\n");
                        }
                        if (sb.length() > 0) {
                            sb.deleteCharAt(sb.length() - 1);
                        }
                        mResult.setText(sb.toString());
                    }

                    @Override
                    public void onFailure(Call<List<SimpleService.Contributor>> call, Throwable t) {
                        mResult.setText("fail: " + call.request());
                    }
                });
            }
        });
    }
}
