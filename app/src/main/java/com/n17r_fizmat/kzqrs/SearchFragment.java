package com.n17r_fizmat.kzqrs;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements SearchView.OnQueryTextListener, AdapterView.OnItemClickListener {

    private ListView listView;
    SearchView searchView;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search, container, false);

        searchView = (SearchView) v.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(this);

        listView = (ListView) v.findViewById(R.id.searchListView);
        listView.setOnItemClickListener(this);
        return v;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        ProgressDialog pd = new ProgressDialog(getContext());
        pd.setTitle("Идет поиск");
        pd.setMessage("Пожалуйста подождите");
        pd.show();
        SearchParseAdapter mainAdapter = new SearchParseAdapter(getContext(), query);
        listView.setAdapter(mainAdapter);
        pd.hide();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        ParseObject object = (ParseObject) listView.getItemAtPosition(i);
        ParseUser user = (ParseUser) object;
        Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
        Bundle b = new Bundle();
        String id = user.getObjectId();
        Log.d("ParseUser", "senderUser: " + id);
        b.putString("ParseUserId", id);
        profileIntent.putExtras(b);
        startActivity(profileIntent);
    }
}
