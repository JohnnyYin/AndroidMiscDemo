
package com.example.widgetlib;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class ScoreBoardViewActivity extends Activity {

    private ScoreBoardView mScoreBoardView;
    private ScoreBoardView mScoreBoardView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_board_view);

        mScoreBoardView = (ScoreBoardView) findViewById(R.id.score_board);
        mScoreBoardView2 = (ScoreBoardView) findViewById(R.id.score_board2);

        mScoreBoardView.setNum(9);
        mScoreBoardView2.setNum(5);
        mScoreBoardView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mScoreBoardView.startAnim();
                mScoreBoardView2.startAnim();
            }
        });
    }

}
