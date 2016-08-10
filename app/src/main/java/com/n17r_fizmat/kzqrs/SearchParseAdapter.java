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

import com.bumptech.glide.Glide;
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
public class SearchParseAdapter extends ParseQueryAdapter {
    ParseUser user;
    public SearchParseAdapter(Context context, final String searchQuery) {
        super(context, new ParseQueryAdapter.QueryFactory<ParseUser>() {
            public ParseQuery<ParseUser> create() {
                ParseQuery<ParseUser> query = ParseUser.getQuery();
                // TODO sort by popularity
                query.whereStartsWith("username", searchQuery);
                return query;
            }
        });
    }

    @Override
    public View getItemView(ParseObject object, View v, ViewGroup parent) {
        Context c = getContext();
        if (v == null) {
            v = View.inflate(getContext(), R.layout.row_search_result, null);
        }
        super.getItemView(object, v, parent);

        final ImageView profileImage = (ImageView) v.findViewById(R.id.resultImage);
        TextView username = (TextView) v.findViewById(R.id.resultText);
        try {
            user = (ParseUser) object;
            String avatarURL = ((ParseFile) user.fetchIfNeeded().get("avatar_small")).getUrl();
            Glide
                    .with(c)
                    .load(avatarURL)
                    .into(profileImage);
//            ParseFile avatar = (ParseFile) user.fetchIfNeeded().get("avatar_small");
//            avatar.getDataInBackground(new GetDataCallback() {
//                @Override
//                public void done(byte[] data, ParseException e) {
//                    if (e == null) {
//                        Bitmap bm = BitmapFactory.decodeByteArray(data , 0, data .length);
//                        profileImage.setImageBitmap(bm);
//                    } else {
//                        Log.d("ParseException", e.toString());
//                        Toast.makeText(getContext(), "Something went wrong while downloading avatar", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
            Object name = user.fetchIfNeeded().getUsername();
            if (name != null) {
                username.setText(name.toString());
            }
        } catch (ParseException e) {
            Log.v("Parse", e.toString());
            e.printStackTrace();
        }
//        card.setOnClickListener(this);

        return v;
    }

//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.resultCard:
//                Context c = getContext();
//                Intent profileIntent = new Intent(c, ProfileActivity.class);
//                Bundle b = new Bundle();
//                String id = user.getObjectId();
//                b.putString("ParseUserId", id);
//                profileIntent.putExtras(b);
//                c.startActivity(profileIntent);
//
//                break;
//        }
//    }
}
