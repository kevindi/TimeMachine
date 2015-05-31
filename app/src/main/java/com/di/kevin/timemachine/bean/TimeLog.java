package com.di.kevin.timemachine.bean;

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

    long timeLogId;
    long myLocationId;

    public Date getEnterTime() {
        return enterTime;
    }

    public void setEnterTime(Date enterTime) {
        this.enterTime = enterTime;
    }

    public Date getLeaveTime() {
        return leaveTime;
    }

    public void setLeaveTime(Date leaveTime) {
        this.leaveTime = leaveTime;
    }

    Date enterTime;
    Date leaveTime;

}
