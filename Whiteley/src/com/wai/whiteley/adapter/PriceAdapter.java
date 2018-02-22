package com.wai.whiteley.adapter;

import java.util.List;

import android.content.Context;

import com.wai.whiteley.R;

public class PriceAdapter extends HintAdapter<String> {

    public PriceAdapter(Context context, List <String> priceArray) {
        super(context, priceArray, context.getString(R.string.str_fb_price_value));
    }
}
