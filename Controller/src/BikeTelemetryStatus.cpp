#include <BikeTelemetryStatus.h>

BikeTelemetryStatus ITelemetryStatusReportable::getStatus() {
    return status;
}

void ITelemetryStatusReportable::setStatus(BikeTelemetryStatus status) {
    this->status = status;
}