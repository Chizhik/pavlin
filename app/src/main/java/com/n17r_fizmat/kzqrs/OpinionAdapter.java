package com.n17r_fizmat.kzqrs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by Alisher on 8/12/2016.
 */
public class OpinionAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<Opinion> op_l = null;
    public OpinionAdapter(Context context, List<Opinion> list) {
        this.context = context;
        this.op_l = list;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return op_l.size();
    }

    @Override
    public Object getItem(int i) {
        return op_l.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        OpinionHolder holder;
        final Opinion op = op_l.get(i);
        if (view == null) {
            holder = new OpinionHolder();
            view = inflater.inflate(R.layout.row_opinion, null);
            holder.profilePic = (ImageView) view.findViewById(R.id.rowProfilePic);
            holder.time = (TextView) view.findViewById(R.id.time_text);
            holder.username = (TextView) view.findViewById(R.id.rowUsername);
            holder.firstWord = (TextView) view.findViewById(R.id.rowFirstWord);
            holder.secondWord = (TextView) view.findViewById(R.id.rowSecondWord);
            holder.thirdWord = (TextView) view.findViewById(R.id.rowThirdWord);
            view.setTag(holder);
        } else {
            holder = (OpinionHolder) view.getTag();
        }
        if (op.getSender() == null) {
            holder.username.setText("Аноним");
            Glide
                    .with(context)
                    .load(R.drawable.profile_pic)
                    .into(holder.profilePic);
        } else {
            holder.username.setText(op.getSender().getUsername());
            Glide
                    .with(context)
                    .load(op.getSender().getAvatar())
                    .into(holder.profilePic);
        }
        holder.firstWord.setText(op.getFirstWord());
        holder.secondWord.setText(op.getSecondWord());
        holder.thirdWord.setText(op.getThirdWord());
        holder.time.setText(op.getDate());
        return view;
    }
}
