package com.wai.whiteley.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.wai.whiteley.R;

public class HintAdapter<T> extends ArrayAdapter<T> {
    protected String mHint;
    protected int mTextColor;
    protected int mHintColor;
    protected LayoutInflater mLayoutInflater;
    protected int mResource;

    public HintAdapter(Context context, List<T> data, String hint) {
        super(context, 0, new ArrayList<>(data)); // copy array, to prevent adding "hint" several times later
        mLayoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mHint = hint;
        add(null); // add empty to show hint instead it
        setDropDownViewResource(R.layout.spinner_item_simple);

        mResource = R.layout.spinner_item_simple;
        mTextColor = getContext().getResources().getColor(R.color.font_color1);
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.textColorHint, typedValue, true);
        mHintColor = getContext().getResources().getColor(typedValue.resourceId);
    }

    @Override
    public int getCount() {
        return super.getCount() - 1; // hide hint at drop down view
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.textView = (TextView) convertView.findViewById(R.id.text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        T item = getItem(position);
        holder.textView.setText(item != null ? item.toString() : mHint);
        holder.textView.setTextColor(item != null ? mTextColor : mHintColor);
        return convertView;
    }

    private class ViewHolder {
        TextView textView;
    }
}