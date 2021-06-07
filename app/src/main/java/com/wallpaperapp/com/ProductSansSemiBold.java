package com.wallpaperapp.com;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class ProductSansSemiBold extends androidx.appcompat.widget.AppCompatTextView {

    public ProductSansSemiBold(Context context) {
        super(context);
        initTypeFace(context);
    }

    public ProductSansSemiBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTypeFace(context);
    }

    public ProductSansSemiBold(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTypeFace(context);
    }

    private void initTypeFace(Context context){
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/ProductSansSemiBold.ttf");
        this.setTypeface(typeface);
    }
}
