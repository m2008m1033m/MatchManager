package com.dxbcom.matchmanager.customViews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;

/**
 * Created by mohammed on 6/29/16.
 */
public class TableLayoutWithRadioButton extends TableLayout implements View.OnClickListener {

    private RadioButton mActiveRadioButton;

    public TableLayoutWithRadioButton(Context context) {
        super(context);
    }

    public TableLayoutWithRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onClick(View v) {
        final RadioButton rb = (RadioButton) v;
        if (mActiveRadioButton != null) {
            mActiveRadioButton.setChecked(false);
        }
        rb.setChecked(true);
        mActiveRadioButton = rb;
    }

    public int getSelectedRow() {
        return mActiveRadioButton == null ? -1 : ((int) mActiveRadioButton.getTag());
    }

    /* (non-Javadoc)
            * @see android.widget.TableLayout#addView(android.view.View, int, android.view.ViewGroup.LayoutParams)
    */
    @Override
    public void addView(View child, int index,
                        android.view.ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        setChildrenOnClickListener((TableRow) child);
    }


    /* (non-Javadoc)
     * @see android.widget.TableLayout#addView(android.view.View, android.view.ViewGroup.LayoutParams)
     */
    @Override
    public void addView(View child, android.view.ViewGroup.LayoutParams params) {
        super.addView(child, params);
        setChildrenOnClickListener((TableRow) child);
    }

    private void setChildrenOnClickListener(TableRow tr) {
        final int c = tr.getChildCount();
        final View v = tr.getChildAt(c - 1);
        if (v instanceof RadioButton) {
            v.setOnClickListener(this);
            v.setTag(getChildCount() - 1); // store the position of this radio button
        }
    }
}
