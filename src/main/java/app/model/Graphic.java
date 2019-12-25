package app.model;

import app.entities.Point;
import org.springframework.stereotype.Component;

@Component
public class Graphic {
    private boolean isInArea(double x, double y, double r) {
        boolean triangle = x <= 0 && y <= 0 && y >= (-2*x - r);
        boolean square = x >= 0 && y >= 0 && x <= r/2 && y <= r;
        boolean sector = x >= 0 && y <= 0 && Math.sqrt(x*x + y*y) <= r;
        return triangle || square || sector;
    }

    public boolean isInArea(Point point) {
        return isInArea(point.getX(), point.getY(), point.getR());
    }

}
