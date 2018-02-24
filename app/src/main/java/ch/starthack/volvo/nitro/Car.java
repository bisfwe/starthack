package ch.starthack.volvo.nitro;

import android.widget.ImageView;

class Car {
    ImageView image;
    final int speed;
    double boostStrength = 1.0;
    double boostTime = 0.0;
    double position = 0.0;

    Car(ImageView image, int speed) {
        this.image = image;
        this.speed = speed;
    }
}
