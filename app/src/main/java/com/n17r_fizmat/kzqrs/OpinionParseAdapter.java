package com.n17r_fizmat.kzqrs;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

/**
 * Created by Alisher on 7/14/2016.
 */
public class OpinionParseAdapter extends ParseQueryAdapter {
    ParseUser senderUser;
    public OpinionParseAdapter(Context context, final ParseUser receiverUser) {
        super(context, new ParseQueryAdapter.QueryFactory<ParseObject>() {
            public ParseQuery create() {
                ParseQuery query = new ParseQuery("Opinion");
                query.whereEqualTo("receiver", receiverUser);
                query.orderByDescending("createdAt");
                return query;
            }
        });
    }

    @Override
    public View getItemView(ParseObject object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.rowlayout, null);
        }
        super.getItemView(object, v, parent);

//        ParseImageView profilePic = (ParseImageView) v.findViewById(R.id.rowParseImage);
        final ImageView profileImage = (ImageView) v.findViewById(R.id.rowProfilePic);
        TextView username = (TextView) v.findViewById(R.id.rowUsername);
        TextView firstWord = (TextView) v.findViewById(R.id.rowFirstWord);
        TextView secondWord = (TextView) v.findViewById(R.id.rowSecondWord);
        TextView thirdWord = (TextView) v.findViewById(R.id.rowThirdWord);

        try {
            senderUser = (ParseUser) object.fetchIfNeeded().get("sender");
//            imageFile = senderUser.fetchIfNeeded().getParseFile("avatar");
            ParseFile avatar = (ParseFile) senderUser.fetchIfNeeded().get("avatar");
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
//            if (imageFile != null) {
//                profilePic.setParseFile(imageFile);
//                profilePic.loadInBackground();
//            }
            Object name = senderUser.fetchIfNeeded().get("name");
            Object f = object.fetchIfNeeded().get("firstWord");
            Object s = object.fetchIfNeeded().get("secondWord");
            Object t = object.fetchIfNeeded().get("thirdWord");
            if (name != null) {
                username.setText(name.toString());
            }
            if (f != null) {
                firstWord.setText(f.toString());
            }
            if (s != null) {
                secondWord.setText(s.toString());
            }
            if (t != null) {
                thirdWord.setText(t.toString());
            }
        } catch (ParseException e) {
            Log.v("Parse", e.toString());
            e.printStackTrace();
        }

//        profileImage.setOnItemClickListener(new AdapterView.OnItemClickListener());
        return v;
    }

//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.rowProfilePic:
//                Context c = getContext();
//                Intent profileIntent = new Intent(c, ProfileActivity.class);
//                Bundle b = new Bundle();
//                String id = senderUser.getObjectId();
//                Log.d("ParseUser", "senderUser: " + id);
//                b.putString("ParseUserId", id);
//                profileIntent.putExtras(b);
//                c.startActivity(profileIntent);
//        }
//    }
}
