package com.wai.whiteley.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

public class DrakeCircusMapView extends SubsamplingScaleImageView {

    private OnExtraDrawListener extraDrawListener;
    
    public DrakeCircusMapView(Context context) {
        this(context, null);
    }

    public DrakeCircusMapView(Context context, AttributeSet attr) {
        super(context, attr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Don't draw pin before image is ready so it doesn't move around during setup.
        if (!isReady()) {
            return;
        }

        if(extraDrawListener != null) {
        	extraDrawListener.onExtraDraw(canvas);
        }
    }

    public void setOnExtraDrawListener(OnExtraDrawListener listener) {
    	this.extraDrawListener = listener;
    }
    
    public interface OnExtraDrawListener {
    	public void onExtraDraw(Canvas canvas);
    }
    
}
