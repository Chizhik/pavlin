package com.n17r_fizmat.kzqrs;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.format.DateUtils;
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

import java.util.Date;

/**
 * Created by Alisher on 7/14/2016.
 */
public class OpinionParseAdapter extends ParseQueryAdapter {
    ParseUser senderUser;
    public OpinionParseAdapter(Context context, final ParseUser receiverUser) {
        super(context, new ParseQueryAdapter.QueryFactory<ParseObject>() {
            public ParseQuery create() {
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Opinion");
                query.whereEqualTo("receiver", receiverUser);
                query.orderByDescending("createdAt");
                return query;
            }
        });
    }

    @Override
    public View getItemView(ParseObject object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.row_opinion, null);
        }
        super.getItemView(object, v, parent);

//        ParseImageView profilePic = (ParseImageView) v.findViewById(R.id.rowParseImage);
        final ImageView profileImage = (ImageView) v.findViewById(R.id.rowProfilePic);
        TextView time = (TextView) v.findViewById(R.id.time_text);
        TextView username = (TextView) v.findViewById(R.id.rowUsername);
        TextView firstWord = (TextView) v.findViewById(R.id.rowFirstWord);
        TextView secondWord = (TextView) v.findViewById(R.id.rowSecondWord);
        TextView thirdWord = (TextView) v.findViewById(R.id.rowThirdWord);

        try {
            senderUser = (ParseUser) object.fetchIfNeeded().get("sender");
//            imageFile = senderUser.fetchIfNeeded().getParseFile("avatar");
            ParseFile avatar = (ParseFile) senderUser.fetchIfNeeded().get("avatar_small");
            avatar.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (e == null) {
                        Bitmap bm = BitmapFactory.decodeByteArray(data , 0, data .length);
                        profileImage.setImageBitmap(bm);
                    } else {
                        Log.d("ParseException", e.toString());
                        Toast.makeText(getContext(), "Ошибка при загрузке аватара", Toast.LENGTH_SHORT).show();
                    }
                }
            });
//            if (imageFile != null) {
//                profilePic.setParseFile(imageFile);
//                profilePic.loadInBackground();
//            }
            Date time_s = object.fetchIfNeeded().getCreatedAt();
            Object name = senderUser.fetchIfNeeded().getUsername();
            Object f = object.fetchIfNeeded().get("firstWord");
            Object s = object.fetchIfNeeded().get("secondWord");
            Object t = object.fetchIfNeeded().get("thirdWord");
            if (time_s != null) {
                String str = (String) DateUtils.getRelativeDateTimeString(getContext(), time_s.getTime(), DateUtils.MINUTE_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0);
                time.setText(str);
            }
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
        return v;
    }


}
