package app.modification;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@ManagedResource(objectName = "lab4:type=HitsPercentCounter")
public class HitsPercentCounter implements HitsPercentCounterMBean {
    private double commonHitsPersent;
    private Map<String, Double> userHitsPersentCounter = new HashMap<>();

    @ManagedAttribute
    public double getСommonHitsPersent(){
        return commonHitsPersent;
    }

    @ManagedOperation
    public double getUserHitsPersentCounter(String username) {
        return userHitsPersentCounter.get(username);
    }

    public void countСommonHitsPersent(int points, int hits){
        commonHitsPersent = (double)hits/points*100;
    }

    public void changeUserPointCount(String username, int points, int hits) {
        userHitsPersentCounter.put(username, (double) hits / points * 100);
    }

}
