package com.honeywell.honeywellproject.BaseActivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import butterknife.ButterKnife;

/**
 * The type Super activity.
 * @author QHT
 */
public abstract class AbstractSuperActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getContentViewId());
        ButterKnife.bind(this);
    }

    /**
     * Gets content view id.
     *
     * @return the content view id
     */
    public abstract int getContentViewId();


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
