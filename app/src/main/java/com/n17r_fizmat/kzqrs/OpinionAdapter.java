package com.n17r_fizmat.kzqrs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Alisher on 7/13/2016.
 */
public class OpinionAdapter extends ArrayAdapter<Opinion> {
    private Context context;
    private Opinion[] values;

    public OpinionAdapter(Context context, Opinion[] values) {
        super(context, R.layout.rowlayout, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
        ImageView profilePic = (ImageView) rowView.findViewById(R.id.rowProfilePic);
        TextView username = (TextView) rowView.findViewById(R.id.rowUsername);
        TextView firstWord = (TextView) rowView.findViewById(R.id.rowFirstWord);
        TextView secondWord = (TextView) rowView.findViewById(R.id.rowSecondWord);
        TextView thirdWord = (TextView) rowView.findViewById(R.id.rowThirdWord);
        if (values[position].getProfilePic() != null) {
            profilePic.setImageBitmap(values[position].getProfilePic());
        }
        username.setText(values[position].getUsername());
        firstWord.setText(values[position].getFirstWord());
        secondWord.setText(values[position].getSecondWord());
        thirdWord.setText(values[position].getThirdWord());
        return rowView;
    }
}
