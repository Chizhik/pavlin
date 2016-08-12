package com.n17r_fizmat.kzqrs;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {
    private OpinionAdapter mainAdapter;
    private Context context;
    private List<Opinion> opList;
    private Date lastDate;
    private Button btnLoadMore;
    private ListView listView;
    private ImageView profilePic;
    private EditText first, second, third;
    private ParseUser currentUser;
    private View header;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog mProgressDialog;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance(ParseUser host) {
        HomeFragment newFragment = new HomeFragment();
        String id = host.getObjectId();
        Bundle args = new Bundle();
        args.putString("ParseUserId", id);
        newFragment.setArguments(args);
        return newFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        context = getContext();
        currentUser = ParseUser.getCurrentUser();
        opList = new ArrayList<Opinion>();
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Opinion");
        query.whereEqualTo("receiver", currentUser);
        query.orderByDescending("createdAt");
        query.setLimit(10);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (objects != null) {
                    for (int i = 0; i < objects.size(); i++) {
                        ParseObject object = objects.get(i);
                        opList.add(opinionFromParseObject(object));
                    }
                }
                try {
                    View v = getView();
                    listView = (ListView) v.findViewById(R.id.lvMain);
                    header = createHeader(savedInstanceState);
                    listView.addHeaderView(header);
                    btnLoadMore = new Button(context);
                    btnLoadMore.setText("Загрузить еще");
                    listView.addFooterView(btnLoadMore);
                    mainAdapter = new OpinionAdapter(getContext(), opList);
                    listView.setAdapter(mainAdapter);
                    listView.setOnItemClickListener(HomeFragment.this);
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
        });
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        return v;
    }

    private void loadMoreListView() {
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setTitle("Загрузка мнений");
        mProgressDialog.setMessage("Пожалуйста подождите");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.show();
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Opinion");
        query.orderByDescending("createdAt");
        query.setLimit(10);
        query.whereLessThan("createdAt", lastDate);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (objects != null) {
                    for (int i = 0; i < objects.size(); i++) {
                        ParseObject object = objects.get(i);
                        opList.add(opinionFromParseObject(object));
                    }
                    mainAdapter.notifyDataSetChanged();
                }
                mProgressDialog.dismiss();
            }
        });
    }


    private View createHeader(Bundle savedInstanceState) {
        View v = getLayoutInflater(savedInstanceState).inflate(R.layout.header_new, null);
        TextView username = (TextView) v.findViewById(R.id.usernameTextHome);
        Button saveButton = (Button) v.findViewById(R.id.saveButton);
        ImageView shareButton = (ImageView) v.findViewById(R.id.share_button);
        ImageView settingsButton = (ImageView) v.findViewById(R.id.settings_button);
        shareButton.setVisibility(View.VISIBLE);
        settingsButton.setVisibility(View.VISIBLE);
        profilePic = (ImageView) v.findViewById(R.id.profileImageHome);
        first = (EditText) v.findViewById(R.id.firstEditText);
        second = (EditText) v.findViewById(R.id.secondEditText);
        third = (EditText) v.findViewById(R.id.thirdEditText);
        if (currentUser.getParseFile("avatar") != null) {
            String avatarURL = ((ParseFile)currentUser.get("avatar")).getUrl();
            Glide
                    .with(this)
                    .load(avatarURL)
                    .into(profilePic);
            username.setText(currentUser.getUsername());
            saveButton.setOnClickListener(this);
            shareButton.setOnClickListener(this);
            settingsButton.setOnClickListener(this);
        } else {
            Intent registerIntent = new Intent(getContext(), SettingsActivity.class);
            registerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(registerIntent);
            getActivity().finish();
        }
        return v;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.settings_button:
                Intent intentHome = new Intent(getContext(), SettingsActivity.class);
                startActivity(intentHome);
                break;
            case R.id.saveButton:
                String f = first.getText().toString().trim();
                String s = second.getText().toString().trim();
                String t = third.getText().toString().trim();
                if (f.matches("") || s.matches("") || t.matches("")) {
                    Toast.makeText(getContext(), "Заполните все три поля", Toast.LENGTH_SHORT).show();
                } else if (f.contains(" ") || s.contains(" ") || t.contains(" ")) {
                    Toast.makeText(getContext(), "Введите по одному слову в каждое поле", Toast.LENGTH_SHORT).show();
                } else {
                    ParseObject op = new ParseObject("Opinion");
                    op.put("sender", currentUser);
                    op.put("receiver", currentUser);
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
                    Toast.makeText(getContext(), "Сохранено!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.share_button:
                Intent intent = new Intent(getContext(), ShareActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onRefresh() {
        // TODO Improve!

        opList.clear();
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Opinion");
        query.orderByDescending("createdAt");
        query.setLimit(10);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (objects != null) {
                    for (int i = 0; i < objects.size(); i++) {
                        ParseObject object = objects.get(i);
                        opList.add(opinionFromParseObject(object));
                    }
                    listView.removeHeaderView(header);
                    header = createHeader(null);
                    listView.addHeaderView(header);
                    mainAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Opinion opn = (Opinion) listView.getItemAtPosition(i);
        Context c = getContext();
        User temp = opn.getSender();
        if (temp != null) {
            Intent profileIntent = new Intent(c, ProfileActivity.class);
            Bundle b = new Bundle();
            String id = temp.getUserId();
            Log.d("ParseUser", "senderUser: " + id);
            b.putString("ParseUserId", id);
            profileIntent.putExtras(b);
            c.startActivity(profileIntent);
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
        String date = (String) DateUtils.getRelativeDateTimeString(getContext(), date_s.getTime(), DateUtils.MINUTE_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0);
        return new Opinion(userSender, null, first, second, third, date);
    }
}
