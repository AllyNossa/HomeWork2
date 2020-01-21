package com.aknosova.homework2;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    Disposable disposable;
    String currentPrice = "700000";
    TextView tWTitlePrice;
    Button btnStop;
    Button btnContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        loadData();

        btnStop.setOnClickListener(v -> {
            if (disposable == null) return;

            disposable.dispose();
            disposable = null;
            tWTitlePrice.setText("Опаньки... Что-то пошло не так");
        });

        btnContinue.setOnClickListener(v ->
                loadData());
    }

    private void initViews() {
        tWTitlePrice = findViewById(R.id.text_price);
        btnStop = findViewById(R.id.button1);
        btnContinue = findViewById(R.id.button2);
    }

    private void loadData() {
        disposable = Observable.fromCallable(() -> getTitlePrice())
                .subscribeOn(Schedulers.io())
                .startWith("")
                .observeOn(Schedulers.computation())
                .map(result -> {
                    if (result.equals("")) {
                        return result;
                    }

                    Thread.sleep(3000);
                    return String.format(result, currentPrice);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> handleResult(result),
                        error -> Log.e(MainActivity.class.getName(), "Ошибка загрузки данных c ошибкой", error),
                        () -> {
                            Log.d(MainActivity.class.getName(), "onComplete");
                            disposable = null;
                        }
                );
    }

    private String getTitlePrice() {
        String titlePrice = "Ваша цена выше рынка на %s руб.";
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return titlePrice;
    }

    private void handleResult(String result) {
        if (result.equals("")) {
            tWTitlePrice.setText("Идет загрузка данных...");
        } else {
            tWTitlePrice.setText(result);
        }
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
        super.onDestroy();
    }
}
