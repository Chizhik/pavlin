package com.n17r_fizmat.kzqrs;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {
    private OpinionAdapter mainAdapter;
    private final static int LIMIT = 7;
    private Date lastDate;
    private Button btnLoadMore;
    private List<Opinion> opList;
    private ProgressDialog mProgressDialog;
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
                mProgressDialog = new ProgressDialog(ProfileActivity.this);
                mProgressDialog.setTitle("Загрузка");
                mProgressDialog.setMessage("Пожалуйста подождите");
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.show();
                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.whereEqualTo("objectId", q);
                query.getInBackground(q, new GetCallback<ParseUser>() {
                    public void done(final ParseUser user, ParseException e) {
                        if (e == null) {
                            listView = (ListView) findViewById(R.id.lvMain_profile);
                            hostUser = user;
                            opList = new ArrayList<Opinion>();
                            ParseQuery<ParseObject> opQuery = new ParseQuery<ParseObject>("Opinion");
                            opQuery.whereEqualTo("receiver", hostUser);
                            opQuery.orderByDescending("createdAt");
                            opQuery.setLimit(LIMIT);
                            opQuery.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {
                                    new Work(objects).execute();
                                }
                            });

                        } else {
                            finish();
                            Log.d("ParseUser", "Couldn't find ParseUser");
                        }
                    }
                });

            }

        }
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_profile);
        swipeRefreshLayout.setOnRefreshListener(this);

    }

    private class Work extends AsyncTask<Void, Void, Void> {
        private List<ParseObject> objects;

        public Work(List<ParseObject> ob) {
            this.objects = ob;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (objects != null) {
                for (int i = 0; i < objects.size(); i++) {
                    ParseObject object = objects.get(i);
                    opList.add(opinionFromParseObject(object));
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            try {
                header = createHeader();
                listView.addHeaderView(header);
                if (objects != null && !objects.isEmpty() && objects.size() == LIMIT) {
                    btnLoadMore = new Button(ProfileActivity.this);
                    btnLoadMore.setText("Загрузить еще");
                    listView.addFooterView(btnLoadMore);
                }
                mainAdapter = new OpinionAdapter(ProfileActivity.this, opList);
                listView.setAdapter(mainAdapter);
                listView.setOnItemClickListener(ProfileActivity.this);
                mProgressDialog.dismiss();
                btnLoadMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loadMoreListView();
                    }
                });

            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }
    }


    private View createHeader() {
        View v = getLayoutInflater().inflate(R.layout.header_new, null);
        profilePic = (ImageView) v.findViewById(R.id.profileImageHome);
        username = (TextView) v.findViewById(R.id.usernameTextHome);
        saveButton = (Button) v.findViewById(R.id.saveButton);
        first = (EditText) v.findViewById(R.id.firstEditText);
        second = (EditText) v.findViewById(R.id.secondEditText);
        third = (EditText) v.findViewById(R.id.thirdEditText);
        if (hostUser != null && hostUser.getParseFile("avatar") != null) {
            String avatarURL = ((ParseFile) hostUser.get("avatar")).getUrl();
            Glide
                    .with(this)
                    .load(avatarURL)
                    .into(profilePic);
            username.setText(hostUser.getUsername());
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
                if (f.trim().matches("") || s.trim().matches("") || t.trim().matches("")) {
                    Toast.makeText(this, "Заполните все три поля", Toast.LENGTH_SHORT).show();
                } else {
                    ParseObject op = new ParseObject("Opinion");
                    if (currentUser == null)  {
                        op.put("sender", JSONObject.NULL);
                    } else {
                        op.put("sender", currentUser);
                    }
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
                    Toast.makeText(this, "Сохранено!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onRefresh() {
        opList.clear();
        mainAdapter.notifyDataSetChanged();
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Opinion");
        query.whereEqualTo("receiver", hostUser);
        query.orderByDescending("createdAt");
        query.setLimit(LIMIT);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                new WorkRefresh(objects).execute();
            }
        });
    }
    private class WorkRefresh extends AsyncTask<Void, Void, Void> {
        private List<ParseObject> objects;
        public WorkRefresh(List<ParseObject> ob) {
            this.objects = ob;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            for (int i = 0; i < objects.size(); i++) {
                ParseObject object = objects.get(i);
                opList.add(opinionFromParseObject(object));
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void args) {
            if (objects.isEmpty() || objects.size() < LIMIT) {
                listView.removeFooterView(btnLoadMore);
            }
            mainAdapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        }

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Opinion opn = (Opinion) listView.getItemAtPosition(i);
        User temp = opn.getSender();
        if (temp != null) {
            Intent profileIntent = new Intent(this, ProfileActivity.class);
            Bundle b = new Bundle();
            String id = temp.getUserId();
            Log.d("ParseUser", "senderUser: " + id);
            b.putString("ParseUserId", id);
            profileIntent.putExtras(b);
            startActivity(profileIntent);
        }
    }
    private Opinion opinionFromParseObject(ParseObject object) {
        ParseUser sender;
        User userSender;
        Object temp = object.get("sender");
        if (temp == JSONObject.NULL) {
            userSender = null;
        } else {
            sender = (ParseUser) temp;
            try {
                sender.fetchIfNeeded();
            } catch (ParseException pe) {
                pe.printStackTrace();
            }
            String senderId = sender.getObjectId();
            String usernameSender = sender.getUsername();
            String avatarSender = ((ParseFile)sender.get("avatar_small")).getUrl();
            userSender = new User(usernameSender, avatarSender, senderId);
        }

        String first = object.get("firstWord").toString();
        String second = object.get("secondWord").toString();
        String third = object.get("thirdWord").toString();
        Date date_s = object.getCreatedAt();
        lastDate = date_s;
        String date = (String) DateUtils.getRelativeDateTimeString(this, date_s.getTime(), DateUtils.MINUTE_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0);
        return new Opinion(userSender, null, first, second, third, date);
    }

    private void loadMoreListView() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Загрузка мнений");
        mProgressDialog.setMessage("Пожалуйста подождите");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.show();
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Opinion");
        query.whereEqualTo("receiver", hostUser);
        query.orderByDescending("createdAt");
        query.setLimit(LIMIT);
        query.whereLessThan("createdAt", lastDate);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                new WorkLoadMore(objects).execute();
            }
        });
    }
    private class WorkLoadMore extends AsyncTask<Void, Void, Void> {
        private List<ParseObject> objects;
        public WorkLoadMore(List<ParseObject> ob) {
            this.objects = ob;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            for (int i = 0; i < objects.size(); i++) {
                ParseObject object = objects.get(i);
                opList.add(opinionFromParseObject(object));
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void args) {
            if (objects.isEmpty() || objects.size() < LIMIT) {
                listView.removeFooterView(btnLoadMore);
            }
            mainAdapter.notifyDataSetChanged();
            mProgressDialog.dismiss();
        }

    }
}
