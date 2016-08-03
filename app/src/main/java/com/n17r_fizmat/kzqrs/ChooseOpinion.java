package com.n17r_fizmat.kzqrs;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class ChooseOpinion extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ListView list_share;
    private OpinionParseAdapter mainAdapter;
    private ParseUser user = ParseUser.getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_opinion);

        list_share = (ListView) findViewById(R.id.list_share);
        mainAdapter = new OpinionParseAdapter(ChooseOpinion.this, user);
        list_share.setAdapter(mainAdapter);
        list_share.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        ParseObject object = (ParseObject) list_share.getItemAtPosition(i);
        try {
            String objectID = object.fetchIfNeeded().getObjectId();
            Intent intent = new Intent(ChooseOpinion.this, ShareActivity.class);
            Bundle b = new Bundle();
            b.putString("OpinionId", objectID);
            intent.putExtras(b);
            startActivity(intent);
        } catch (ParseException e) {
            Log.v("Parse", e.toString());
            e.printStackTrace();
        }
    }
}
