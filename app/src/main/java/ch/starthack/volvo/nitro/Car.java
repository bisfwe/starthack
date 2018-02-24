package ch.starthack.volvo.nitro;

import android.widget.ImageView;

class Car {
    ImageView image, flame;
    final int speed;
    double boostStrength = 1.0;
    double boostTime = 0.0;
    double position = 0.0;

    Car(ImageView image, ImageView flame, int speed) {
        this.image = image;
        this.speed = speed;
        this.flame = flame;
    }
}
