package app.controller;

import app.model.Graphic;
import app.repositories.PointRepository;
import app.repositories.UserRepository;
import app.entities.Point;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.*;

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

    @CrossOrigin
    @GetMapping
    Object[] allPoints(Principal user) {
        Collection<Point> allUserPoints = pointRepository.findAllByUser(userRepository.findOneByUsername(user.getName()));

        Object[] arrayPoints = allUserPoints.toArray();

        Arrays.sort(arrayPoints, (a, b) -> ((Point) a).getId() > ((Point) b).getId() ? 1 : -1);
        return arrayPoints;
    }

    @CrossOrigin
    @PostMapping
    Point newPoint(@RequestBody Point newPoint, Principal user) {

        newPoint.setResult(graphic.isInArea(newPoint));
        newPoint.setUser(userRepository.findOneByUsername(user.getName()));
        return pointRepository.save(newPoint);
    }

    @CrossOrigin
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

    @CrossOrigin
    @PutMapping
    Point updatePoint(@RequestBody Point changedPoint, Principal user) {
        Point point = pointRepository.findById(changedPoint.getId()).get();
        point.setR(changedPoint.getR());
        point.setResult(graphic.isInArea(point));
        return pointRepository.save(point);
    }

}
