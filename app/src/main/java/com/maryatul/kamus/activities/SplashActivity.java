package com.maryatul.kamus.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import com.maryatul.kamus.R;
import com.maryatul.kamus.database.KamusHelper;
import com.maryatul.kamus.model.ModelKamus;
import com.maryatul.kamus.utils.PreferencesManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SplashActivity extends AppCompatActivity {

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ButterKnife.bind(this);

        new LoadData().execute();
    }

    private class LoadData extends AsyncTask<Void, Integer, Void> {

        KamusHelper kamusHelper;
        PreferencesManager preferencesManager;

        double progress;
        double maxprogress = 100;

        @Override
        protected void onPreExecute() {
            kamusHelper = new KamusHelper(getApplicationContext());
            preferencesManager = new PreferencesManager(getApplicationContext());
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Boolean firstRun = preferencesManager.getFirstTimeLoad();
            if (firstRun) {
                ArrayList<ModelKamus> kamusMelayu = preLoadRaw(R.raw.melayu_indonesia);
                ArrayList<ModelKamus> kamusIndonesia = preLoadRaw(R.raw.indonesia_melayu);

                publishProgress((int) progress);

                try {
                    kamusHelper.open();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }

                double progressMaxInsert = 100.0;
                Double progressDiff = (progressMaxInsert - progress) / (kamusMelayu.size() + kamusIndonesia.size());

                kamusHelper.insertTransaction(kamusMelayu, true);
                progress += progressDiff;
                publishProgress((int) progress);

                kamusHelper.insertTransaction(kamusIndonesia, false);
                progress += progressDiff;
                publishProgress((int) progress);

                kamusHelper.close();
                preferencesManager.setFirstTimeLoad(false);

                publishProgress((int) maxprogress);

            }
            else {
                try {
                    synchronized (this) {
                        this.wait(1000);
                        publishProgress(50);

                        this.wait(300);
                        publishProgress((int) maxprogress);
                    }
                }
                catch (Exception e) {

                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void result) {
            Intent i = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }

    public ArrayList<ModelKamus> preLoadRaw(int data) {
        ArrayList<ModelKamus> listKamus = new ArrayList<>();
        BufferedReader reader;
        try {
            Resources res = getResources();
            InputStream raw_dict = res.openRawResource(data);

            reader = new BufferedReader(new InputStreamReader(raw_dict));
            String line = null;
            do {
                line = reader.readLine();
                String[] splitstr = line.split("\t");
                ModelKamus kamus;
                kamus = new ModelKamus(splitstr[0], splitstr[1]);
                listKamus.add(kamus);
            } while (line != null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return listKamus;
    }
}