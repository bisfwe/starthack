package ch.starthack.volvo.nitro;

import android.widget.ImageView;

class Obstacle {
    double x, y, width, height;
    ImageView image;

    Obstacle(double x, double y, double width, double height, ImageView image) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = image;
    }
}
