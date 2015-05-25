package com.di.kevin.timemachine.object;

import java.util.Date;

/**
 * Created by dike on 25/5/2015.
 */
public class TimeLog {

    public long getTimeLogId() {
        return timeLogId;
    }

    public void setTimeLogId(long timeLogId) {
        this.timeLogId = timeLogId;
    }

    public long getMyLocationId() {
        return myLocationId;
    }

    public void setMyLocationId(long myLocationId) {
        this.myLocationId = myLocationId;
    }

    public Date getLogTime() {
        return logTime;
    }

    public void setLogTime(Date logTime) {
        this.logTime = logTime;
    }

    long timeLogId;
    long myLocationId;
    Date logTime;

}
