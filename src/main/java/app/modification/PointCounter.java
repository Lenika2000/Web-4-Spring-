package app.modification;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;

import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import java.util.HashMap;
import java.util.Map;

@Service
@ManagedResource(objectName = "lab4:type=PointCounter")
public class PointCounter extends NotificationBroadcasterSupport implements PointCounterMBean {
    private Map<String, Integer> userPointCounter = new HashMap<>();
    private Map<String, Integer> userPointHitsCounter = new HashMap<>();
    HitsPercentCounter hitsPercentCounter;
    private long sequenceNumber;
    private int commonCounter=0, commonHitsCounter=0;

    public PointCounter(HitsPercentCounter hitsPercentCounter) {
        this.hitsPercentCounter = hitsPercentCounter;
    }

    @ManagedOperation
    public int getUserPointCounter(String username) {
        return userPointCounter.get(username);
    }

    @ManagedOperation
    public int getUserPointHitsCounter(String username) {
        return userPointHitsCounter.get(username);
    }


    @ManagedAttribute
    public int getCommonPointCounter() {
        return commonCounter;
    }

    @ManagedAttribute
    public int getCommonPointHitsCounter() {
        return commonHitsCounter;
    }

    public void sendNotify(String username, int pointCount) {
        sendNotification(new Notification("Mod5Points", this, sequenceNumber++, System.currentTimeMillis(),
                String.format("Количество точек пользователя %s кратно пяти и равно %d", username, pointCount)));
    }

    public void incUserPointCount(String username) {
        commonCounter++;
        int userPointCnt = userPointCounter.compute(username, (usr, val) -> val + 1);
        if (userPointCnt % 5 == 0)
            sendNotify(username, userPointCnt);
    }

    public void incUserPointHitsCount(String username) {
        commonHitsCounter++;
        userPointHitsCounter.computeIfPresent(username, (usr, val) -> val + 1);
    }

    public void decUserPointHitsCount(String username) {
        commonHitsCounter--;
        userPointHitsCounter.computeIfPresent(username, (usr, val) -> val - 1);
    }

    public void changeHitsPercent(String username) {
        userPointCounter.putIfAbsent(username, 0);
        userPointHitsCounter.putIfAbsent(username, 0);
        hitsPercentCounter.changeUserPointCount(username, userPointCounter.get(username), userPointHitsCounter.get(username));
        hitsPercentCounter.countСommonHitsPersent(commonCounter, commonHitsCounter);
    }
}
