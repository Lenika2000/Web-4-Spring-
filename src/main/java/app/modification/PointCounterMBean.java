package app.modification;

public interface PointCounterMBean {
    int getUserPointCounter(String username);
    int getUserPointHitsCounter(String username);
    int getCommonPointCounter();
    int getCommonPointHitsCounter();
}
