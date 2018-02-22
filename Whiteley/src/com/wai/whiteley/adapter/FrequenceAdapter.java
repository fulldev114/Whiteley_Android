package com.wai.whiteley.adapter;

import java.util.List;

import android.content.Context;

import com.wai.whiteley.R;

public class FrequenceAdapter extends HintAdapter<String> {

    public FrequenceAdapter(Context context, List <String> freqArray) {
        super(context, freqArray, context.getString(R.string.str_fb_freq_value));
    }
}
