

package com.androidproject.sudiet.tools;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.KeyEvent;


public class NotDismissableEditText extends AppCompatEditText {

    public NotDismissableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        return true;
        //return super.onKeyPreIme(keyCode, event);
    }
}
