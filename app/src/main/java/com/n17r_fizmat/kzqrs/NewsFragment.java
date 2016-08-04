package com.n17r_fizmat.kzqrs;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFragment extends Fragment {
    ListView news_list;
    NewsParseAdapter mainAdapter;


    public NewsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_news, container, false);
        news_list = (ListView) v.findViewById(R.id.news_list);
        mainAdapter = new NewsParseAdapter(getContext());
        news_list.setAdapter(mainAdapter);
        return v;
    }

}
