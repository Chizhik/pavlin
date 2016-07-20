package com.n17r_fizmat.kzqrs;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private OpinionParseAdapter mainAdapter;
    private ListView listView;
    private ImageView profilePic;
    private TextView username;
    private Button saveButton;
    private EditText first, second, third;
    private ParseUser currentUser = ParseUser.getCurrentUser();
    // TODO hostUser != currentUser
    private ParseUser hostUser = currentUser;
    private Bitmap bm;
    private View header;
    private SwipeRefreshLayout swipeRefreshLayout;
// checking git
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            // TODO change hostUser
            String q = b.getString("ParseUserId");
            if (q != null && !q.matches("")) {
                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.whereEqualTo("objectId", q);
                query.getInBackground(q, new GetCallback<ParseUser>() {
                    public void done(final ParseUser user, ParseException e) {
                        if (e == null) {
                            listView = (ListView) findViewById(R.id.lvMain);
                            hostUser = user;
                            header = createHeader();
                            listView.addHeaderView(header);
                            mainAdapter = new OpinionParseAdapter(ProfileActivity.this, hostUser);
                            listView.setAdapter(mainAdapter);
                            Log.d("ParseUser", "user: " + user.getObjectId());
                            Log.d("ParseUser", "hostUser: " + hostUser.getObjectId());
                            Log.d("ParseUser", "currentUser: " + currentUser.getObjectId());
                        } else {
                            finish();
                            Log.d("ParseUser", "Couldn't find ParseUser");
                        }
                    }
                });
            }
        }

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_profile);
//        header = createHeader();
        swipeRefreshLayout.setOnRefreshListener(this);

    }


    private View createHeader() {
        View v = getLayoutInflater().inflate(R.layout.header, null);
        profilePic = (ImageView) v.findViewById(R.id.profileImageHome);
        username = (TextView) v.findViewById(R.id.usernameTextHome);
        saveButton = (Button) v.findViewById(R.id.saveButton);
        first = (EditText) v.findViewById(R.id.firstEditText);
        second = (EditText) v.findViewById(R.id.secondEditText);
        third = (EditText) v.findViewById(R.id.thirdEditText);
        if (hostUser.get("name") != null && hostUser.getParseFile("avatar") != null) {
            ParseFile avatar = (ParseFile) hostUser.get("avatar");
            avatar.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (e == null) {
                        bm = BitmapFactory.decodeByteArray(data, 0, data.length);
                        profilePic.setImageBitmap(bm);
                    } else {
                        Log.d("ParseException", e.toString());
                        Toast.makeText(ProfileActivity.this, "Something went wrong while downloading avatar", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            username.setText(hostUser.getString("name"));
            profilePic.setOnClickListener(this);
            saveButton.setOnClickListener(this);
        }
        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.saveButton:
                String f = first.getText().toString();
                String s = second.getText().toString();
                String t = third.getText().toString();
                if (f.matches("") || s.matches("") || t.matches("")) {
                    Toast.makeText(this, "Заполните все три поля", Toast.LENGTH_SHORT).show();
                } else {
                    ParseObject op = new ParseObject("Opinion");
                    op.put("sender", currentUser);
                    op.put("receiver", hostUser);
                    op.put("firstWord", f);
                    op.put("secondWord", s);
                    op.put("thirdWord", t);
                    op.saveInBackground();
                    first.setText("");
                    first.clearFocus();
                    second.setText("");
                    second.clearFocus();
                    third.setText("");
                    third.clearFocus();
                    mainAdapter = new OpinionParseAdapter(this, hostUser);
                    listView.setAdapter(mainAdapter);
                }
                break;
        }
    }

    @Override
    public void onRefresh() {
        mainAdapter = new OpinionParseAdapter(this, hostUser);
        listView.setAdapter(mainAdapter);
        swipeRefreshLayout.setRefreshing(false);
    }
}
