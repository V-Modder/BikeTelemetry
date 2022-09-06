
#ifndef BIKETELEMETRYSTATUS_H_ /* Include guard */
#define BIKETELEMETRYSTATUS_H_


enum BikeTelemetryStatus {NOT_STARTED = 1, RUNNING = 2, NO_SIGNAL = 3}; 

class ITelemetryStatusReportable {
public:
    BikeTelemetryStatus getStatus();

protected:
    void setStatus(BikeTelemetryStatus status);

private:
    BikeTelemetryStatus status;
};

#endif