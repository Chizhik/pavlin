package com.n17r_fizmat.kzqrs;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFragmentNew extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private final static int LIMIT = 10;
    private ListView news_list;
    private NewsAdapter mainAdapter;
    private Context context;
    private ProgressDialog mProgressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Opinion> newsList = null;
    private Button btnLoadMore;
    private Date lastDate;
    private Date firstDate;

    public NewsFragmentNew() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        View v = inflater.inflate(R.layout.fragment_news, container, false);
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setTitle("Загрузка");
        mProgressDialog.setMessage("Пожалуйста подождите");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.show();
        newsList = new ArrayList<Opinion>();
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Opinion");
        query.orderByDescending("createdAt");
        query.setLimit(LIMIT);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                new Work(objects).execute();
            }
        });
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.news_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        return v;
    }

    private class Work extends AsyncTask<Void, Void, Void> {
        private List<ParseObject> objects;
        public Work(List<ParseObject> ob) {
            this.objects = ob;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (objects != null) {
                for (int i = 0; i < objects.size(); i++) {
                    ParseObject object = objects.get(i);
                    newsList.add(opinionFromParseObject(object));
                }
            }
            mProgressDialog.dismiss();
            return null;
        }
        @Override
        protected void onPostExecute(Void args) {
            try {
                View v = getView();
                news_list = (ListView) v.findViewById(R.id.news_list);
                if (objects != null && !objects.isEmpty() && objects.size() == LIMIT) {
                    btnLoadMore = new Button(context);
                    btnLoadMore.setText("Загрузить еще");
                    news_list.addFooterView(btnLoadMore);
                }
                mainAdapter = new NewsAdapter(context, newsList);
                news_list.setAdapter(mainAdapter);
                btnLoadMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loadMoreListView();
                    }
                });
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }
    }

    private void loadMoreListView() {
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setTitle("Загрузка мнений");
        mProgressDialog.setMessage("Пожалуйста подождите");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.show();
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Opinion");
        query.orderByDescending("createdAt");
        query.setLimit(LIMIT);
        query.whereLessThan("createdAt", lastDate);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                new WorkLoadMore(objects).execute();
            }
        });
    }
    private class WorkLoadMore extends AsyncTask<Void, Void, Void> {
        private List<ParseObject> objects;
        public WorkLoadMore(List<ParseObject> ob) {
            this.objects = ob;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            for (int i = 0; i < objects.size(); i++) {
                ParseObject object = objects.get(i);
                newsList.add(opinionFromParseObject(object));
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void args) {
            if (objects.isEmpty() || objects.size() < LIMIT) {
                news_list.removeFooterView(btnLoadMore);
            }
            mainAdapter.notifyDataSetChanged();
            mProgressDialog.dismiss();
        }

    }

    @Override
    public void onRefresh() {
        newsList.clear();
        mainAdapter.notifyDataSetChanged();
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Opinion");
        query.orderByDescending("createdAt");
        query.setLimit(LIMIT);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (objects != null) {
                    new WorkRefresh(objects).execute();
                }
            }
        });
    }
    private class WorkRefresh extends AsyncTask<Void, Void, Void> {
        private List<ParseObject> objects;
        public WorkRefresh(List<ParseObject> ob) {
            this.objects = ob;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            for (int i = 0; i < objects.size(); i++) {
                ParseObject object = objects.get(i);
                newsList.add(opinionFromParseObject(object));
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void args) {
            if (objects.isEmpty() || objects.size() < LIMIT) {
                news_list.removeFooterView(btnLoadMore);
            }
            mainAdapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        }

    }

    private class NewsAdapter extends BaseAdapter {
        Context context;
        LayoutInflater inflater;
        private List<Opinion> news_l = null;
        public NewsAdapter(Context context, List<Opinion> list) {
            this.context = context;
            this.news_l = list;
            inflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return news_l.size();
        }

        @Override
        public Object getItem(int i) {
            return news_l.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            NewsHolder holder;
            Opinion op = news_l.get(i);
            if (view == null) {
                holder = new NewsHolder();
                view = inflater.inflate(R.layout.row_news, null);
                holder.profileSender = (ImageView) view.findViewById(R.id.rowSenderProfilePic);
                holder.profileReceiver = (ImageView) view.findViewById(R.id.rowReceiverProfilePic);
                holder.time = (TextView) view.findViewById(R.id.news_time_text);
                holder.usernameSender = (TextView) view.findViewById(R.id.rowSenderUsername);
                holder.usernameReceiver = (TextView) view.findViewById(R.id.rowReceiverUsername);
                holder.firstWord = (TextView) view.findViewById(R.id.rowFirstWord);
                holder.secondWord = (TextView) view.findViewById(R.id.rowSecondWord);
                holder.thirdWord = (TextView) view.findViewById(R.id.rowThirdWord);
                view.setTag(holder);
            } else {
                holder = (NewsHolder) view.getTag();
            }
            if (op.getSender() == null) {
                holder.usernameSender.setText("Аноним");
                Glide
                        .with(context)
                        .load(R.drawable.profile_pic)
                        .into(holder.profileSender);
            } else {
                holder.usernameSender.setText(op.getSender().getUsername());
                Glide
                        .with(context)
                        .load(op.getSender().getAvatar())
                        .into(holder.profileSender);
            }
            holder.usernameReceiver.setText(op.getReceiver().getUsername());
            Glide
                    .with(context)
                    .load(op.getReceiver().getAvatar())
                    .into(holder.profileReceiver);
            holder.firstWord.setText(op.getFirstWord());
            holder.secondWord.setText(op.getSecondWord());
            holder.thirdWord.setText(op.getThirdWord());
            holder.time.setText(op.getDate());
            holder.profileSender.setOnClickListener(senderClickListener);
            holder.profileReceiver.setOnClickListener(receiverClickListener);
            return view;
        }
    }
    private View.OnClickListener senderClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final int position = news_list.getPositionForView(view);
            if (position != ListView.INVALID_POSITION) {
                Opinion op = (Opinion) news_list.getItemAtPosition(position);
                Context c = getContext();
                if (op.getSender() != null) {
                    Intent profileIntent = new Intent(c, ProfileActivity.class);
                    Bundle b = new Bundle();
                    String id = op.getSender().getUserId();
                    b.putString("ParseUserId", id);
                    profileIntent.putExtras(b);
                    c.startActivity(profileIntent);
                }
            }
        }
    };
    private View.OnClickListener receiverClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final int position = news_list.getPositionForView(view);
            if (position != ListView.INVALID_POSITION) {
                Opinion op = (Opinion) news_list.getItemAtPosition(position);
                Context c = getContext();
                Intent profileIntent = new Intent(c, ProfileActivity.class);
                Bundle b = new Bundle();
                String id = op.getReceiver().getUserId();
                b.putString("ParseUserId", id);
                profileIntent.putExtras(b);
                c.startActivity(profileIntent);
            }
        }
    };

    private Opinion opinionFromParseObject(ParseObject object) {
        ParseUser sender;
        User userSender;
        Object temp = object.get("sender");
        if (temp == JSONObject.NULL) {
            userSender = null;
        } else {
            sender = (ParseUser) temp;
            try {
                sender.fetchIfNeeded();
            } catch (ParseException pe) {
                pe.printStackTrace();
            }
            String senderId = sender.getObjectId();
            String usernameSender = sender.getUsername();
            String avatarSender = ((ParseFile)sender.get("avatar_small")).getUrl();
            userSender = new User(usernameSender, avatarSender, senderId);
        }
        ParseUser receiver = (ParseUser)object.get("receiver");
        try {
            receiver.fetchIfNeeded();
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        String first = object.get("firstWord").toString();
        String second = object.get("secondWord").toString();
        String third = object.get("thirdWord").toString();
        String receiverId = receiver.getObjectId();
        String usernameReceiver = receiver.getUsername();
        String avatarReceiver = ((ParseFile)receiver.get("avatar")).getUrl();
        User userReceiver = new User(usernameReceiver, avatarReceiver, receiverId);
        Date date_s = object.getCreatedAt();
        lastDate = date_s;
        String date = (String) DateUtils.getRelativeDateTimeString(getContext(), date_s.getTime(), DateUtils.MINUTE_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0);
        return new Opinion(userSender, userReceiver, first, second, third, date);
    }
}
