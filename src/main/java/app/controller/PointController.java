package app.controller;

import app.model.Graphic;
import app.repositories.PointRepository;
import app.repositories.UserRepository;
import app.entities.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(PointController.class);

    PointController(PointRepository pointRepository, UserRepository userRepository, Graphic graphic) {
        this.pointRepository = pointRepository;
        this.graphic = graphic;
        this.userRepository = userRepository;
    }

//    @CrossOrigin
    @GetMapping
    Collection<Point> allPoints(Principal user) {
        logger.info("all points request from "+user.getName());
        return pointRepository.findAllByUser(userRepository.findOneByUsername(user.getName()));
    }

//    @CrossOrigin
    @PostMapping
    Point newPoint(@RequestBody Point newPoint, Principal user) {
        logger.info("New point request from "+user.getName());
        newPoint.setResult(graphic.isInArea(newPoint));
        newPoint.setUser(userRepository.findOneByUsername(user.getName()));
        return pointRepository.save(newPoint);
    }


//    @CrossOrigin
    @GetMapping("recalculate")
    Collection<Point> allPointsRecalculation(Double r, Principal user) {
        logger.info("Recalculate points request from "+user.getName());
        List<Point> recalculated = new ArrayList<>();
        Collection<Point> points = pointRepository.findAllByUser(userRepository.findOneByUsername(user.getName()));

        for (Point p : points) {
            Point point = new Point(null, p.getX(), p.getY(), r, false, null);
            point.setResult(graphic.isInArea(point));
            recalculated.add(point);
        }

        return recalculated;
    }

}
