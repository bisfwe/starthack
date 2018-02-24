package ch.starthack.volvo.nitro;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class Race extends AppCompatActivity {

    private RelativeLayout rl;
    private Car playerCar, opponentCar;
    private static final int FRAME_RATE = 30;
    private boolean raceOver = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race);

        rl = findViewById(R.id.game_layout);

        playerCar = new Car(new ImageView(this), 100);
        playerCar.image.setBackgroundColor(Color.GREEN);

        RelativeLayout.LayoutParams playerParams = new RelativeLayout.LayoutParams(30, 40);
        playerParams.leftMargin = 50;
        playerParams.topMargin = (int) playerCar.position;
        rl.addView(playerCar.image, playerParams);

        opponentCar = new Car(new ImageView(this), 105);
        opponentCar.image.setBackgroundColor(Color.RED);

        RelativeLayout.LayoutParams opponentParams = new RelativeLayout.LayoutParams(30, 40);
        opponentParams.leftMargin = 100;
        opponentParams.topMargin = 60;
        rl.addView(opponentCar.image, opponentParams);

        new GameThread().start();
    }

    private class GameThread extends Thread {

        @Override
        public void run() {
            final long tickDuration = 1000/FRAME_RATE;
            while (!raceOver) {
                Race.this.update(tickDuration);
                runOnUiThread(Race.this::draw);
                try {
                    sleep(tickDuration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void draw() {
        rl.removeView(playerCar.image);
        RelativeLayout.LayoutParams playerParams = new RelativeLayout.LayoutParams(30, 40);
        playerParams.leftMargin = 50;
        playerParams.topMargin = (int) playerCar.position;
        rl.addView(playerCar.image, playerParams);
    }

    private void update(long elapsedMillis) {
        playerCar.position += playerCar.speed * (elapsedMillis/1000.0);
    }
}
