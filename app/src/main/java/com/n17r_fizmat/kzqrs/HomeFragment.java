package com.n17r_fizmat.kzqrs;


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
import android.widget.Button;
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
public class HomeFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
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
//        ListView lvMain = (ListView) v.findViewById(R.id.lvMain);
        listView = (ListView) v.findViewById(R.id.lvMain);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        header = createHeader(savedInstanceState);
//        lvMain.addHeaderView(header);
        listView.addHeaderView(header);
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

    @Override
    public void onResume() {
        super.onResume();
        mainAdapter = new OpinionParseAdapter(getContext(), hostUser);
//        OpinionAdapter adapter = new OpinionAdapter(getContext(), values);


//        lvMain.setAdapter(adapter);
        listView.setAdapter(mainAdapter);
    }

    View createHeader(Bundle savedInstanceState) {
        View v = getLayoutInflater(savedInstanceState).inflate(R.layout.header, null);
        profilePic = (ImageView) v.findViewById(R.id.profileImageHome);
        username = (TextView) v.findViewById(R.id.usernameTextHome);
        saveButton = (Button) v.findViewById(R.id.saveButton);
        first = (EditText) v.findViewById(R.id.firstEditText);
        second = (EditText) v.findViewById(R.id.secondEditText);
        third = (EditText) v.findViewById(R.id.thirdEditText);
        if (currentUser.get("name") != null && currentUser.getParseFile("avatar") != null) {
            ParseFile avatar = (ParseFile) currentUser.get("avatar");
            avatar.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (e == null) {
                        bm = BitmapFactory.decodeByteArray(data, 0, data.length);
                        profilePic.setImageBitmap(bm);
                    } else {
                        Log.d("ParseException", e.toString());
                        Toast.makeText(getContext(), "Something went wrong while downloading avatar", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            username.setText(currentUser.getString("name"));
            profilePic.setOnClickListener(this);
            saveButton.setOnClickListener(this);
        }
        return v;
    }

//         Inflate the layout for this fragment
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.profileImageHome:
                Intent intentHome = new Intent(getContext(), SettingsActivity.class);
                startActivity(intentHome);
                break;
            case R.id.saveButton:
                String f = first.getText().toString();
                String s = second.getText().toString();
                String t = third.getText().toString();
                if (f.matches("") || s.matches("") || t.matches("")) {
                    Toast.makeText(getContext(), "Заполните все три поля", Toast.LENGTH_SHORT).show();
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
                    onPause();
                    onResume();
                }
                break;
        }
    }

    @Override
    public void onRefresh() {
        // TODO Improve!
        onPause();
        onResume();
        swipeRefreshLayout.setRefreshing(false);
    }
}
