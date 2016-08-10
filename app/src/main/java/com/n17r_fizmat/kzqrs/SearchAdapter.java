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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by Alisher on 7/15/2016.
 */
public class SearchAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    private List<User> user_list = null;
    private ArrayList<User> arrayList;
    public SearchAdapter(Context context, List<User> userList) {
        this.context = context;
        this.user_list = userList;
        this.arrayList = new ArrayList<User>();
        this.arrayList.addAll(userList);
        inflater = LayoutInflater.from(context);
    }

//    public View getItemView(ParseObject object, View v, ViewGroup parent) {
//        Context c = getContext();
//        if (v == null) {
//            v = View.inflate(getContext(), R.layout.row_search_result, null);
//        }
//        super.getItemView(object, v, parent);
//
//        final ImageView profileImage = (ImageView) v.findViewById(R.id.resultImage);
//        TextView username = (TextView) v.findViewById(R.id.resultText);
//        try {
//            user = (ParseUser) object;
//            String avatarURL = ((ParseFile) user.fetchIfNeeded().get("avatar_small")).getUrl();
//            Glide
//                    .with(c)
//                    .load(avatarURL)
//                    .into(profileImage);
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
//            Object name = user.fetchIfNeeded().getUsername();
//            if (name != null) {
//                username.setText(name.toString());
//            }
//        } catch (ParseException e) {
//            Log.v("Parse", e.toString());
//            e.printStackTrace();
//        }
//        card.setOnClickListener(this);

//        return v;
//    }

    public class ViewHolder {
        TextView username;
        ImageView profileImage;
    }

    @Override
    public int getCount() {
        return user_list.size();
    }

    @Override
    public Object getItem(int i) {
        return user_list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.row_search_result, null);
            holder.username = (TextView) view.findViewById(R.id.resultText);
            holder.profileImage = (ImageView) view.findViewById(R.id.resultImage);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.username.setText(user_list.get(i).getUsername());
        Glide
                .with(context)
                .load(user_list.get(i).getAvatar())
                .into(holder.profileImage);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(context, ProfileActivity.class);
                Bundle b = new Bundle();
                b.putString("ParseUserId", user_list.get(i).getUserId());
                profileIntent.putExtras(b);
                context.startActivity(profileIntent);
            }
        });
        return view;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        user_list.clear();
        if (charText.length() == 0) {
            user_list.addAll(arrayList);
        } else {
            for (User u : arrayList) {
                if (u.getUsername().toLowerCase(Locale.getDefault()).startsWith(charText)) {
                    Log.d("USERNAME", u.getUsername());
                    user_list.add(u);
                }
            }
        }
        notifyDataSetChanged();
    }

}
