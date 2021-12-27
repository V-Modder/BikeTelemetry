
#ifndef BIKETELEMETRYSTATUS_H_ /* Include guard */
#define BIKETELEMETRYSTATUS_H_


enum BikeTelemetryStatus {NOT_STARTED = 1, RUNNING = 2, NO_SIGNAL = 3}; 
class ITelemetryStatusReportable {
public:
    // pure virtual function providing interface framework.
    virtual BikeTelemetryStatus getStatus() = 0;
    ~ITelemetryStatusReportable();
};

#endif