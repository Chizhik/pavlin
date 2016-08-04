package com.n17r_fizmat.kzqrs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
 * Created by Alisher on 8/4/2016.
 */
public class NewsParseAdapter extends ParseQueryAdapter {
    private ParseUser senderUser;
    private ParseUser receiverUser;
    public NewsParseAdapter(Context context) {
        super(context, new ParseQueryAdapter.QueryFactory<ParseObject>() {
            public ParseQuery create() {
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Opinion");
                query.orderByDescending("createdAt");
                return query;
            }
        });
    }

    @Override
    public View getItemView(ParseObject object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.news_rowlayout, null);
        }
        super.getItemView(object, v, parent);

//        ParseImageView profilePic = (ParseImageView) v.findViewById(R.id.rowParseImage);
        final ImageView profileSender = (ImageView) v.findViewById(R.id.rowSenderProfilePic);
        final ImageView profileReceiver = (ImageView) v.findViewById(R.id.rowReceiverProfilePic);
        TextView usernameSender = (TextView) v.findViewById(R.id.rowSenderUsername);
        TextView usernameReceiver = (TextView) v.findViewById(R.id.rowReceiverUsername);
        TextView firstWord = (TextView) v.findViewById(R.id.rowFirstWord);
        TextView secondWord = (TextView) v.findViewById(R.id.rowSecondWord);
        TextView thirdWord = (TextView) v.findViewById(R.id.rowThirdWord);

        try {
            senderUser = (ParseUser) object.fetchIfNeeded().get("sender");
            receiverUser = (ParseUser) object.fetchIfNeeded().get("receiver");
            ParseFile avatarSender = (ParseFile) senderUser.fetchIfNeeded().get("avatar_small");
            final ParseFile avatarReceiver = (ParseFile) receiverUser.fetchIfNeeded().get("avatar_small");
            avatarSender.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(final byte[] dataSender, ParseException e) {
                    if (e == null) {
                        avatarReceiver.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] dataReceiver, ParseException e) {
                                if (e == null) {
                                    Bitmap bmSender = BitmapFactory.decodeByteArray(dataSender , 0, dataSender.length);
                                    Bitmap bmReceiver = BitmapFactory.decodeByteArray(dataReceiver , 0, dataReceiver.length);
                                    profileSender.setImageBitmap(bmSender);
                                    profileReceiver.setImageBitmap(bmReceiver);
                                } else {
                                    Log.d("ParseException", e.toString());
                                    Toast.makeText(getContext(), "Ошибка при загрузке аватара", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Log.d("ParseException", e.toString());
                        Toast.makeText(getContext(), "Ошибка при загрузке аватара", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            Object nameSender = senderUser.fetchIfNeeded().getUsername();
            Object nameReceiver = receiverUser.fetchIfNeeded().getUsername();
            Object f = object.fetchIfNeeded().get("firstWord");
            Object s = object.fetchIfNeeded().get("secondWord");
            Object t = object.fetchIfNeeded().get("thirdWord");
            if (nameSender != null) {
                usernameSender.setText(nameSender.toString());
            }
            if (nameReceiver != null) {
                usernameReceiver.setText(nameReceiver.toString());
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
