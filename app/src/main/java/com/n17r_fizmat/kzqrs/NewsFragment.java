package com.n17r_fizmat.kzqrs;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
 * A simple {@link Fragment} subclass.
 */
public class NewsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    ListView news_list;
    NewsParseAdapter mainAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;


    public NewsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_news, container, false);
        news_list = (ListView) v.findViewById(R.id.news_list);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.news_swipe_refresh_layout);
        mainAdapter = new NewsParseAdapter(getContext());
        news_list.setAdapter(mainAdapter);
        swipeRefreshLayout.setOnRefreshListener(this);
        return v;
    }

    @Override
    public void onRefresh() {
        mainAdapter = new NewsParseAdapter(getContext());
        news_list.setAdapter(mainAdapter);
        swipeRefreshLayout.setRefreshing(false);
    }

    private class NewsParseAdapter extends ParseQueryAdapter {
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
            NewsHolder holder = null;

            if (v == null) {
                v = View.inflate(getContext(), R.layout.news_rowlayout, null);
                super.getItemView(object, v, parent);
                holder = new NewsHolder();
                holder.profileSender = (ImageView) v.findViewById(R.id.rowSenderProfilePic);
                holder.profileReceiver = (ImageView) v.findViewById(R.id.rowReceiverProfilePic);
                holder.senderClick = (LinearLayout) v.findViewById(R.id.senderClick);
                holder.receiverClick = (LinearLayout) v.findViewById(R.id.receiverClick);
                holder.time = (TextView) v.findViewById(R.id.news_time_text);
                holder.usernameSender = (TextView) v.findViewById(R.id.rowSenderUsername);
                holder.usernameReceiver = (TextView) v.findViewById(R.id.rowReceiverUsername);
                holder.firstWord = (TextView) v.findViewById(R.id.rowFirstWord);
                holder.secondWord = (TextView) v.findViewById(R.id.rowSecondWord);
                holder.thirdWord = (TextView) v.findViewById(R.id.rowThirdWord);
                holder.senderClick.setOnClickListener(senderClickListener);
                holder.receiverClick.setOnClickListener(receiverClickListener);
                v.setTag(holder);
            } else {
                holder = (NewsHolder) v.getTag();
            }

            try {
                senderUser = (ParseUser) object.fetchIfNeeded().get("sender");
                receiverUser = (ParseUser) object.fetchIfNeeded().get("receiver");
                ParseFile avatarSender = (ParseFile) senderUser.fetchIfNeeded().get("avatar_small");
                final ParseFile avatarReceiver = (ParseFile) receiverUser.fetchIfNeeded().get("avatar_small");
                final NewsHolder finalHolder = holder;
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
                                        finalHolder.profileSender.setImageBitmap(bmSender);
                                        finalHolder.profileReceiver.setImageBitmap(bmReceiver);
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
                Date time_s = object.fetchIfNeeded().getCreatedAt();
                Object nameSender = senderUser.fetchIfNeeded().getUsername();
                Object nameReceiver = receiverUser.fetchIfNeeded().getUsername();
                Object f = object.fetchIfNeeded().get("firstWord");
                Object s = object.fetchIfNeeded().get("secondWord");
                Object t = object.fetchIfNeeded().get("thirdWord");
                if (time_s != null) {
                    String str = (String) DateUtils.getRelativeDateTimeString(getContext(), time_s.getTime(), DateUtils.MINUTE_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0);
                    holder.time.setText(str);
                }
                if (nameSender != null) {
                    holder.usernameSender.setText(nameSender.toString());
                }
                if (nameReceiver != null) {

                    holder.usernameReceiver.setText(nameReceiver.toString());
                }
                if (f != null) {
                    holder.firstWord.setText(f.toString());
                }
                if (s != null) {
                    holder.secondWord.setText(s.toString());
                }
                if (t != null) {
                    holder.thirdWord.setText(t.toString());
                }
            } catch (ParseException e) {
                Log.v("Parse", e.toString());
                e.printStackTrace();
            }

            return v;
        }

    }
    private View.OnClickListener senderClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final int position = news_list.getPositionForView(view);
            if (position != ListView.INVALID_POSITION) {
                ParseObject object = (ParseObject) news_list.getItemAtPosition(position);
                try {
                    Context c = getContext();
                    ParseUser senderUser = (ParseUser) object.fetchIfNeeded().get("sender");
                    Intent profileIntent = new Intent(c, ProfileActivity.class);
                    Bundle b = new Bundle();
                    String id = senderUser.getObjectId();
                    b.putString("ParseUserId", id);
                    profileIntent.putExtras(b);
                    c.startActivity(profileIntent);
                } catch (ParseException e) {
                    Log.v("Parse", e.toString());
                    e.printStackTrace();
                }
            }
        }
    };

    private View.OnClickListener receiverClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final int position = news_list.getPositionForView(view);
            if (position != ListView.INVALID_POSITION) {
                ParseObject object = (ParseObject) news_list.getItemAtPosition(position);
                try {
                    Context c = getContext();
                    ParseUser senderUser = (ParseUser) object.fetchIfNeeded().get("receiver");
                    Intent profileIntent = new Intent(c, ProfileActivity.class);
                    Bundle b = new Bundle();
                    String id = senderUser.getObjectId();
                    b.putString("ParseUserId", id);
                    profileIntent.putExtras(b);
                    c.startActivity(profileIntent);
                } catch (ParseException e) {
                    Log.v("Parse", e.toString());
                    e.printStackTrace();
                }
            }
        }
    };
}
