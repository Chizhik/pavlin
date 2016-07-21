package com.n17r_fizmat.kzqrs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

/**
 * Created by Alisher on 7/15/2016.
 */
public class SearchParseAdapter extends ParseQueryAdapter implements View.OnClickListener {
    ParseUser user;
    public SearchParseAdapter(Context context, final String searchQuery) {
        super(context, new ParseQueryAdapter.QueryFactory<ParseObject>() {
            public ParseQuery create() {
                ParseQuery<ParseUser> query = new ParseUser().getQuery();
                // TODO sort by popularity
                query.whereContains("name", searchQuery);
                return query;
            }
        });
    }

    @Override
    public View getItemView(ParseObject object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.row_search_result, null);
        }
        super.getItemView(object, v, parent);

        final ImageView profileImage = (ImageView) v.findViewById(R.id.resultImage);
        CardView card = (CardView) v.findViewById(R.id.resultCard);
        TextView username = (TextView) v.findViewById(R.id.resultText);
        try {
            user = (ParseUser) object;
            ParseFile avatar = (ParseFile) user.fetchIfNeeded().get("avatar_small");
            avatar.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (e == null) {
                        Bitmap bm = BitmapFactory.decodeByteArray(data , 0, data .length);
                        profileImage.setImageBitmap(bm);
                    } else {
                        Log.d("ParseException", e.toString());
                        Toast.makeText(getContext(), "Something went wrong while downloading avatar", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            Object name = user.fetchIfNeeded().get("name");
            if (name != null) {
                username.setText(name.toString());
            }
        } catch (ParseException e) {
            Log.v("Parse", e.toString());
            e.printStackTrace();
        }
        card.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.resultCard:
                // TODO new fragment
                Context c = getContext();
                Intent profileIntent = new Intent(c, ProfileActivity.class);
                Bundle b = new Bundle();
                String id = user.getObjectId();
                b.putString("ParseUserId", id);
                profileIntent.putExtras(b);
                c.startActivity(profileIntent);
//                // Create new fragment and transaction
//                Fragment newFragment = HomeFragment.newInstance(user);
//                // consider using Java coding conventions (upper first char class names!!!)
//                FragmentTransaction transaction = ((MainActivity)getContext()).getSupportFragmentManager().beginTransaction();
//
//                // Replace whatever is in the fragment_container view with this fragment,
//                // and add the transaction to the back stack
//                transaction.replace(android.R.id.content, newFragment);
//                transaction.addToBackStack(null);
//
//                // Commit the transaction
//                transaction.commit();
                break;
        }
    }
}
