package app.controller;

import app.model.Graphic;
import app.repositories.PointRepository;
import app.repositories.UserRepository;
import app.entities.Point;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/points")
public class PointController {
    private final PointRepository pointRepository;
    private final UserRepository userRepository;
    private final Graphic graphic;


    PointController(PointRepository pointRepository, UserRepository userRepository, Graphic graphic) {
        this.pointRepository = pointRepository;
        this.graphic = graphic;
        this.userRepository = userRepository;
    }

    @GetMapping
    Collection<Point> allPoints(Principal user) {

        return pointRepository.findAllByUser(userRepository.findOneByUsername(user.getName()));
    }

    @PostMapping
    Point newPoint(@RequestBody Point newPoint, Principal user) {

        newPoint.setResult(graphic.isInArea(newPoint));
        newPoint.setUser(userRepository.findOneByUsername(user.getName()));
        return pointRepository.save(newPoint);
    }


    @GetMapping("recalculate")
    Collection<Point> allPointsRecalculation(Double r, Principal user) {

        List<Point> recalculated = new ArrayList<>();
        Collection<Point> points = pointRepository.findAllByUser(userRepository.findOneByUsername(user.getName()));

        for (Point p : points) {
            Point point = new Point(null, p.getX(), p.getY(), r, false, null);
            point.setResult(graphic.isInArea(point));
            recalculated.add(point);
        }

        return recalculated;
    }

    @PostMapping("updatePoint")
    Point collectionWithChangedPoint(@RequestBody Point changedPoint, Principal user) {
        Point point = pointRepository.findById(changedPoint.getId()).get();
        point.setR(changedPoint.getR());
        point.setResult(graphic.isInArea(point));
        return pointRepository.save(point);

    }

}
