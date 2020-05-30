package app.controller;

import app.model.Graphic;
import app.modification.PointCounter;
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
    private PointCounter pointCounter;



    PointController(PointRepository pointRepository, UserRepository userRepository, Graphic graphic, PointCounter pointCounter) {
        this.pointRepository = pointRepository;
        this.graphic = graphic;
        this.userRepository = userRepository;
        this.pointCounter = pointCounter;
    }

    @GetMapping
    Object[] allPoints(Principal user) {
        Collection<Point> allUserPoints = pointRepository.findAllByUser(userRepository.findOneByUsername(user.getName()));

        Object[] arrayPoints = allUserPoints.toArray();

        Arrays.sort(arrayPoints, (a, b) -> ((Point) a).getId() > ((Point) b).getId() ? 1 : -1);
        return arrayPoints;
    }


    @PostMapping
    Point newPoint(@RequestBody Point newPoint, Principal user) {
        boolean result = graphic.isInArea(newPoint);
        newPoint.setResult(result);
        newPoint.setUser(userRepository.findOneByUsername(user.getName()));
        pointCounter.incUserPointCount(user.getName()); //для отслеживания добавления точки с помощью MBean
        if (result) pointCounter.incUserPointHitsCount(user.getName());
        pointCounter.changeHitsPercent(user.getName());
        return pointRepository.save(newPoint);
    }


    @GetMapping("recalculate")
    Collection<Point> allPointsRecalculation(Double r, Principal user) {

        List<Point> recalculated = new ArrayList<>();
        Collection<Point> points = pointRepository.findAllByUser(userRepository.findOneByUsername(user.getName()));

        for (Point p : points) {
            Point point = new Point(null, p.getX(), p.getY(), r, false, null);
            changeHits(user.getName(),p, point );
            point.setResult(graphic.isInArea(point));
            recalculated.add(point);
        }

        pointCounter.changeHitsPercent(user.getName());
        return recalculated;
    }


    @PutMapping
    Point updatePoint(@RequestBody Point changedPoint, Principal user) {
        Point point = pointRepository.findById(changedPoint.getId()).get();
        point.setR(changedPoint.getR());
        point.setResult(graphic.isInArea(point));
        return pointRepository.save(point);
    }

    void changeHits(String username, Point oldP, Point newP) {
        boolean result = graphic.isInArea(newP);
        if (oldP.isResult()) {
            if (!result) { //Если при пред. радиусе точка попадала в обл, а сейчас не попадает
                pointCounter.decUserPointHitsCount(username); //уменьшаем количество попаданий на 1
            }
        } else { //точка не попадала на предыдущем радиусе, а сейчас попадает
            if (result) {
                pointCounter.incUserPointHitsCount(username);
            }
        }

    }

}
