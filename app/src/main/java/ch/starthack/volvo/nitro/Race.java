package ch.starthack.volvo.nitro;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class Race extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race);

        RelativeLayout rl = findViewById(R.id.game_layout);

        ImageView iv = new ImageView(this);
        iv.setBackgroundColor(Color.RED);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(30, 40);
        params.leftMargin = 50;
        params.topMargin = 60;
        rl.addView(iv, params);
    }
}
