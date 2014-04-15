package co.nz.splashYay.cagefight.scenes;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewOpenSans extends TextView {
	public TextViewOpenSans(Context context) {
        super(context);
        setTypeface(OpenSans.getInstance(context).getTypeFace());
    }

    public TextViewOpenSans(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(OpenSans.getInstance(context).getTypeFace());
    }

    public TextViewOpenSans(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
        setTypeface(OpenSans.getInstance(context).getTypeFace());
        
    }

}
