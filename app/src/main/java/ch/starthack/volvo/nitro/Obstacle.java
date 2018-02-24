package ch.starthack.volvo.nitro;

import android.widget.ImageView;

class Obstacle {
    double x, y, size;
    ImageView image;

    Obstacle(double x, double y, double size, ImageView image) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.image = image;
    }
}
