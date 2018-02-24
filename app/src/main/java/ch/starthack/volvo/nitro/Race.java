package ch.starthack.volvo.nitro;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class Race extends AppCompatActivity {

    private RelativeLayout rl;
    private Car playerCar, opponentCar;
    private static final int FRAME_RATE = 15;
    private static final int RACE_LENGTH = 3500;
    private boolean raceOver = false;
    private boolean playerWon = false;
    private List<Obstacle> props = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race);

        rl = findViewById(R.id.game_layout);

        playerCar = new Car(new ImageView(this), 200);
        playerCar.image.setBackgroundColor(Color.GREEN);

        opponentCar = new Car(new ImageView(this), 225);
        opponentCar.image.setBackgroundColor(Color.RED);

        RelativeLayout.LayoutParams boostParams = new RelativeLayout.LayoutParams(100, 100);
        boostParams.leftMargin = 10;
        boostParams.rightMargin = 10;
        ImageButton boostButton = new ImageButton(this);
        boostButton.setBackgroundColor(Color.YELLOW);
        boostButton.setOnClickListener(view -> {
            playerCar.boostStrength = 1.5;
            playerCar.boostTime = 800;
        });
        rl.addView(boostButton, boostParams);

        Obstacle finish = new Obstacle(0.5, RACE_LENGTH, 500, 50, new ImageView(this));
        finish.image.setBackgroundColor(Color.BLACK);
        props.add(finish);

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

            Intent mainIntent = new Intent(Race.this.getApplicationContext(), MainActivity.class);
            mainIntent.putExtra("playerWon", playerWon);
            Race.this.startActivity(mainIntent);
        }
    }

    private void draw() {
        final int width = rl.getWidth();
        final int height = rl.getMeasuredHeight();
        final double fact = height / 400.0;
        final int carSize = (int) (50*fact);

        final double lastPos = Math.min(playerCar.position, opponentCar.position);

        for (Obstacle prop : props) {
            rl.removeView(prop.image);
            int propWidth = (int) (prop.width*fact);
            int propHeight = (int) (prop.height*fact);
            RelativeLayout.LayoutParams propParams = new RelativeLayout.LayoutParams(propWidth, propHeight);
            propParams.leftMargin = (int) (width*prop.x) - propWidth/2;
            propParams.topMargin = height - ((int) ((prop.y - lastPos + 50)*fact));
            rl.addView(prop.image, propParams);
        }

        rl.removeView(playerCar.image);
        RelativeLayout.LayoutParams playerParams = new RelativeLayout.LayoutParams(carSize, carSize);
        playerParams.leftMargin = width/3 - carSize/2;
        playerParams.topMargin = height - ((int) ((playerCar.position - lastPos + 50)*fact)) - carSize;
        rl.addView(playerCar.image, playerParams);

        rl.removeView(opponentCar.image);
        RelativeLayout.LayoutParams opponentParams = new RelativeLayout.LayoutParams(carSize, carSize);
        opponentParams.leftMargin = 2*width/3 - carSize/2;
        opponentParams.topMargin = height - ((int) ((opponentCar.position - lastPos + 50)*fact)) - carSize;
        rl.addView(opponentCar.image, opponentParams);
    }

    private void update(long elapsedMillis) {
        double playerBoost = 1.0;
        if (playerCar.boostTime > 0.0001) {
            playerCar.boostTime -= elapsedMillis;
            playerBoost = playerCar.boostStrength;
        }
        playerCar.position += playerCar.speed * playerBoost * (elapsedMillis/1000.0);

        double opponentBoost = 1.0;
        if (opponentCar.boostTime > 0.0001) {
            opponentCar.boostTime -= elapsedMillis;
            opponentBoost = opponentCar.boostStrength;
        }
        opponentCar.position += opponentCar.speed * opponentBoost * (elapsedMillis/1000.0);

        final double lastPos = Math.min(playerCar.position, opponentCar.position);

        props.removeIf(prop -> prop.y < lastPos - 100);
        if (Math.random() > 0.9) {
            Obstacle prop = new Obstacle(Math.random(), lastPos + 500, 5 + Math.random()*7, 5 + Math.random()*7, new ImageView(this));
            prop.image.setBackgroundColor(Color.GRAY);
            props.add(prop);
        }

        if (Math.max(opponentCar.position, playerCar.position) > RACE_LENGTH) {
            raceOver = true;
            playerWon = playerCar.position >= opponentCar.position;
        }
    }
}
