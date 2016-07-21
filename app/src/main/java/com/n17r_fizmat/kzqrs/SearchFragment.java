package com.n17r_fizmat.kzqrs;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements SearchView.OnQueryTextListener {

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
        return v;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        ProgressDialog pd = new ProgressDialog(getContext());
        pd.setTitle("Мдет поиск");
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

}
