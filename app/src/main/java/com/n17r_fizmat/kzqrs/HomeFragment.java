package com.n17r_fizmat.kzqrs;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {
    private OpinionParseAdapter mainAdapter;
    private ListView listView;
    private ImageView profilePic;
    private EditText first, second, third;
    private ParseUser currentUser = ParseUser.getCurrentUser();
    private Bitmap bm;
    private View header;
    private SwipeRefreshLayout swipeRefreshLayout;

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
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        listView = (ListView) v.findViewById(R.id.lvMain);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        header = createHeader(savedInstanceState);
        listView.addHeaderView(header);
        mainAdapter = new OpinionParseAdapter(getContext(), currentUser);
        listView.setAdapter(mainAdapter);
        listView.setOnItemClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);
//        swipeRefreshLayout.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        swipeRefreshLayout.setRefreshing(true);
//
//                                        onPause();
//                                        onResume();
//                                    }
//                                }
//        );
        return v;
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
            ParseFile avatar = (ParseFile) currentUser.get("avatar");
            avatar.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (e == null) {
                        bm = BitmapFactory.decodeByteArray(data, 0, data.length);
                        profilePic.setImageBitmap(bm);
                    } else {
                        Log.d("ParseException", e.toString());
                        Toast.makeText(getContext(), "Ошибка при загрузке аватара", Toast.LENGTH_SHORT).show();
                    }
                }
            });
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

//         Inflate the layout for this fragment
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
                    mainAdapter = new OpinionParseAdapter(getContext(), currentUser);
                    listView.setAdapter(mainAdapter);
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
        listView.removeHeaderView(header);
        header = createHeader(null);
        listView.addHeaderView(header);
        mainAdapter = new OpinionParseAdapter(getContext(), currentUser);
        listView.setAdapter(mainAdapter);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        ParseObject object = (ParseObject) listView.getItemAtPosition(i);
        try {
            Context c = getContext();
            ParseUser senderUser = (ParseUser) object.fetchIfNeeded().get("sender");
            Intent profileIntent = new Intent(c, ProfileActivity.class);
            Bundle b = new Bundle();
            String id = senderUser.getObjectId();
            Log.d("ParseUser", "senderUser: " + id);
            b.putString("ParseUserId", id);
            profileIntent.putExtras(b);
            c.startActivity(profileIntent);
        } catch (ParseException e) {
            Log.v("Parse", e.toString());
            e.printStackTrace();
        }
    }
}
