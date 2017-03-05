package io.github.kfaryarok.kfaryarokapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;

import io.github.kfaryarok.kfaryarokapp.updates.ClassesAffected;
import io.github.kfaryarok.kfaryarokapp.updates.GlobalAffected;
import io.github.kfaryarok.kfaryarokapp.updates.Update;
import io.github.kfaryarok.kfaryarokapp.updates.UpdateAdapter;
import io.github.kfaryarok.kfaryarokapp.updates.UpdateParser;
import io.github.kfaryarok.kfaryarokapp.util.TestUtil;

public class MainActivity extends AppCompatActivity implements UpdateAdapter.UpdateAdapterOnClickHandler {

    private RecyclerView mUpdatesRecyclerView;
    private UpdateAdapter mUpdateAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO fetch updates, filter unwanted ones and save into array
        // temp until i get a json serving server up
        String response = TestUtil.getTestJsonString(); // UpdateFetcher.fetchUpdates();

        Update[] updates = new Update[0];
        try {
            updates = UpdateParser.parseUpdates(response);
        } catch (JSONException e) {
            // just in case something errors, exit
            e.printStackTrace();
            Toast.makeText(this, "כישלון בעיבוד נתונים.", Toast.LENGTH_LONG).show();
            finish();
        }

        mUpdatesRecyclerView = (RecyclerView) findViewById(R.id.rv_updates);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mUpdatesRecyclerView.setLayoutManager(layoutManager);
        mUpdatesRecyclerView.setHasFixedSize(true);

        mUpdateAdapter = new UpdateAdapter(updates, this);
        mUpdatesRecyclerView.setAdapter(mUpdateAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                // TODO settings menu
                break;
            case R.id.menu_advanced:
                item.setChecked(!item.isChecked());
                // TODO save checked data and show advanced options
                break;
            case R.id.menu_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClickCard(Update update) {
        Intent intent = new Intent(this, UpdateDetailsActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, update.getLongText());
        if (update.getAffected() instanceof GlobalAffected)
            intent.putExtra(Intent.EXTRA_SUBJECT, new String[] { "global" });
        else if (update.getAffected() instanceof ClassesAffected)
            intent.putExtra(Intent.EXTRA_SUBJECT, ((ClassesAffected) update.getAffected()).getClassesAffected());
        startActivity(intent);
    }

    @Override
    public void onClickOptions(Update update) {
        // TODO show options, but i don't know what options
    }

}