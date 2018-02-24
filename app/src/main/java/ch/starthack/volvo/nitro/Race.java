package ch.starthack.volvo.nitro;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Race extends AppCompatActivity {

    private RelativeLayout rl;
    private Car playerCar, opponentCar;
    private static final int FRAME_RATE = 15;
    private static final int RACE_LENGTH = 3500;
    private boolean raceOver = false;
    private boolean playerWon = false;
    private List<Obstacle> props = new ArrayList<>();
    private List<Obstacle> opponentBoosts = new ArrayList<>();
    private TextView playerLabel, opponentLabel, countdownLabel;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race);

        rl = findViewById(R.id.game_layout);

        playerCar = new Car(new ImageView(this), 200);
        playerCar.image.setImageResource(R.drawable.player_car);

        opponentCar = new Car(new ImageView(this), 225);
        opponentCar.image.setImageResource(R.drawable.opponent_car);

        Obstacle finish = new Obstacle(0.5, RACE_LENGTH, 500, 50, new ImageView(this));
        finish.image.setImageResource(R.drawable.finish_line);
        props.add(finish);

        Arrays.asList(380, 1500, 2200, 3100).forEach(y -> {
            ImageView boostButton = new AppCompatImageButton(this) {
                @Override
                public boolean performClick() {
                    return super.performClick();
                }
            };
            boostButton.setBackgroundColor(Color.YELLOW);
            Obstacle boost = new Obstacle(0.1 + Math.random()*0.3, y, 35, 35, boostButton);
            boostButton.setOnTouchListener((view, event) -> {
                playerCar.boostStrength = 1.5;
                playerCar.boostTime = 800;
                props.remove(boost);
                rl.removeView(view);
                return false;
            });
            props.add(boost);
        });

        Arrays.asList(815, 1850, 2650).forEach(y -> {
            ImageView boostButton = new AppCompatImageButton(this) {
                @Override
                public boolean performClick() {
                    return super.performClick();
                }
            };
            boostButton.setBackgroundColor(Color.BLUE);
            Obstacle boost = new Obstacle(0.1 + Math.random()*0.3, y, 40, 40, boostButton);
            boostButton.setOnTouchListener((view, event) -> {
                playerCar.boostStrength = 1.7;
                playerCar.boostTime = 900;
                props.remove(boost);
                rl.removeView(view);
                return false;
            });
            props.add(boost);
        });

        Arrays.asList(860, 1430, 2300, 2940).forEach(y -> {
            ImageView boostImage = new ImageView(this);
            boostImage.setBackgroundColor(Color.YELLOW);
            Obstacle boost = new Obstacle(0.6 + Math.random()*0.3, y, 35, 35, boostImage);
            props.add(boost);
            opponentBoosts.add(boost);
        });

        playerLabel = findViewById(R.id.player_label);
        playerLabel.setAllCaps(true);
        playerLabel.setZ(1000);
        playerLabel.setText("YOU");

        opponentLabel = findViewById(R.id.opponent_label);
        opponentLabel.setZ(1000);
        opponentLabel.setText(getIntent().getStringExtra("playerName"));

        countdownLabel = findViewById(R.id.countdown_label);
        countdownLabel.setZ(1000);
        countdownLabel.setText("");

        new GameThread().start();
    }

    private class GameThread extends Thread {

        @Override
        public void run() {
            try {
                runOnUiThread(Race.this::draw);
                sleep(1000);
                runOnUiThread(() -> countdownLabel.setText("3"));
                sleep(1000);
                runOnUiThread(() -> countdownLabel.setText("2"));
                sleep(1000);
                runOnUiThread(() -> countdownLabel.setText("1"));
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> countdownLabel.setText("GO"));
            new Thread(() -> {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(() -> countdownLabel.setVisibility(View.INVISIBLE));
            }).start();

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

            runOnUiThread(() -> {
                countdownLabel.setText("YOU " + (playerWon ? "WIN!" : "LOSE!"));
                countdownLabel.setVisibility(View.VISIBLE);
            });

            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
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
        final int carSize = (int) (55*fact);

        final double lastPos = Math.min(playerCar.position, opponentCar.position);

        for (Obstacle prop : props) {
            rl.removeView(prop.image);
            int propWidth = (int) (prop.width*fact);
            int propHeight = (int) (prop.height*fact);
            RelativeLayout.LayoutParams propParams = new RelativeLayout.LayoutParams(propWidth, propHeight);
            propParams.leftMargin = (int) (width*prop.x) - propWidth/2;
            propParams.topMargin = height - ((int) ((prop.y - lastPos + 50)*fact)) - carSize;
            rl.addView(prop.image, propParams);
        }

        rl.removeView(playerCar.image);
        RelativeLayout.LayoutParams playerParams = new RelativeLayout.LayoutParams(carSize, carSize);
        playerParams.leftMargin = width/3 - carSize/2 + (int) (playerCar.boostTime > 0.001 ? (Math.random() - 0.5) * 2 * fact : 0);
        playerParams.topMargin = height - ((int) ((playerCar.position - lastPos + 50)*fact)) - carSize;
        rl.addView(playerCar.image, playerParams);

        rl.removeView(opponentCar.image);
        RelativeLayout.LayoutParams opponentParams = new RelativeLayout.LayoutParams(carSize, carSize);
        opponentParams.leftMargin = 2*width/3 - carSize/2 + (int) (opponentCar.boostTime > 0.001 ? (Math.random() - 0.5) * 2 * fact : 0);
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

        props.removeIf(prop -> prop.y < lastPos - 130);
        if (Math.random() > 0.9) {
            Obstacle prop = new Obstacle(Math.random(), lastPos + 500, 5 + Math.random()*7, 5 + Math.random()*7, new ImageView(this));
            prop.image.setBackgroundColor(Color.GRAY);
            props.add(prop);
        }

        if (Math.max(opponentCar.position, playerCar.position) > RACE_LENGTH) {
            raceOver = true;
            playerWon = playerCar.position >= opponentCar.position;
        }

        if (opponentCar.position > 780 && opponentBoosts.size() == 4
                || opponentCar.position > 1380 && opponentBoosts.size() == 3
                || opponentCar.position > 2210 && opponentBoosts.size() == 2
                || opponentCar.position > 1840 && opponentBoosts.size() == 1) {
            Obstacle boost = opponentBoosts.get(0);
            runOnUiThread(() -> rl.removeView(boost.image));
            props.remove(boost);
            opponentBoosts.remove(0);
            opponentCar.boostStrength = 1.4;
            opponentCar.boostTime = 1000;
        }
    }
}
