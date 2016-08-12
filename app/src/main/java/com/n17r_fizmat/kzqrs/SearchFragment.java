package com.n17r_fizmat.kzqrs;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {
    private EditText editSearch;
    private ListView listView;
    private SearchAdapter adapter;
    private ProgressDialog mProgressDialog;
    private List<User> userList = null;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search, container, false);
//        mProgressDialog = new ProgressDialog(getContext());
//        mProgressDialog.setTitle("Поиск пользователей");
//        mProgressDialog.setMessage("Загрузка...");
//        mProgressDialog.setIndeterminate(false);
//        mProgressDialog.show();
        userList = new ArrayList<User>();
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> userObjects, ParseException error) {
                if (userObjects != null) {
                    for (int i = 0; i < userObjects.size(); i++) {
                        String username = userObjects.get(i).getUsername();
                        String id = userObjects.get(i).getObjectId();
                        String avatar = ((ParseFile)userObjects.get(i).get("avatar_small")).getUrl();
                        User user = new User(username, avatar, id);
                        userList.add(user);
                    }
                }
                try {
                    View v = getView();
                    listView = (ListView) v.findViewById(R.id.searchListView);
                    editSearch = (EditText) v.findViewById(R.id.search_EditText);
                    adapter = new SearchAdapter(getContext(), userList);
                    listView.setAdapter(adapter);
//                    mProgressDialog.dismiss();
                    editSearch.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            String text = editSearch.getText().toString().toLowerCase(Locale.getDefault());
                            adapter.filter(text);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        return v;
    }
}
