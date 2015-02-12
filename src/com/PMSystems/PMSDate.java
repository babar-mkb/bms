package com.PMSystems;

import java.util.Date;
import java.util.Calendar;
import java.io.Serializable;

/**
 * This class is used to reflect date
 *
 * @version         1.0            06 Aug 2002
 * @author          Usman
 *
 * 06 Aug 2002      Usman          Initial Development of Class
 */

public abstract class PMSDate implements Serializable {

    private int year;
    private int date;
    private int month;
    private int hour;
    private int minute;
    private int second;

    /**
     * PMSDate()                constructor of PMSDate()
     *
     * @param                   year as int
     * @param                   month as int
     * @param                   date as int
     * @param                   hour as int
     * @param                   minute as int
     * @param                   second as int
     */
    public PMSDate(int year, int month, int date, int hour, int minute, int second) {
        Calendar c = Calendar.getInstance();
        c.set(year-1900, month-1, date, hour, minute, second);
        this.year   = c.get(c.YEAR) + 1900;
        this.month  = c.get(c.MONTH) + 1;
        this.date   = c.get(c.DATE);
        this.hour   = c.get(c.HOUR_OF_DAY);
        this.minute = c.get(c.MINUTE);
        this.second = c.get(c.SECOND);
    }

    /**
     * PMSDate()                constructor of PMSDate()
     *
     * @param                   year as int
     * @param                   month as int
     * @param                   date as int
     */
    public PMSDate(int year, int month, int date) {
        this(year, month, date, 0, 0, 0);
    }

    /**
     * PMSDate()                constructor of PMSDate()
     *
     * @param                   vDate as Date
     */
    public PMSDate(Date vDate) {
        if(vDate == null) {
            throw new NullPointerException("Object of Date is NULL");
        }
        Calendar c = Calendar.getInstance();
        c.setTime(vDate);
        //this.year   = c.get(c.YEAR) + 1900;
        this.year   = c.get(c.YEAR);
        this.month  = c.get(c.MONTH) + 1;
        this.date   = c.get(c.DATE);
        this.hour   = c.get(c.HOUR_OF_DAY);
        this.minute = c.get(c.MINUTE);
        this.second = c.get(c.SECOND);
    }

    /**
     * PMSDate()                constructor of PMSDate()
     *
     * @param                   vDate as Date
     * @param                   vTime as Date
     */
    public PMSDate(Date vDate, Date vTime) {
        if(vDate == null || vTime == null) {
            throw new NullPointerException("Object of Date is NULL");
        }
        Calendar c = Calendar.getInstance();
        c.setTime(vDate);
        //this.year   = c.get(c.YEAR) + 1900;
        this.year   = c.get(c.YEAR);
        this.month  = c.get(c.MONTH) + 1;
        this.date   = c.get(c.DATE);
        c.setTime(vTime);
        this.hour   = c.get(c.HOUR_OF_DAY);
        this.minute = c.get(c.MINUTE);
        this.second = c.get(c.SECOND);
    }

    /**
     * PMSDate()                constructor of PMSDate()
     *
     * @param                   date as long
     */
    public PMSDate(long date) {
        this(new Date(date));
    }

    /**
     * PMSDate()                constructor of PMSDate()
     */
    public PMSDate() {
        this(new Date());
    }

    /**
     * PMSDate(boolean)         constructor of PMSDate()
     *
     * @param                   empty boolean
     */
    public PMSDate(boolean empty) {
        if(! empty) {
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            //this.year   = c.get(c.YEAR) + 1900;
            this.year   = c.get(c.YEAR);
            this.month  = c.get(c.MONTH) + 1;
            this.date   = c.get(c.DATE);
            this.hour   = c.get(c.HOUR_OF_DAY);
            this.minute = c.get(c.MINUTE);
            this.second = c.get(c.SECOND);
        }
    }

    /**
     * getYear()                returns the year
     *
     * @returns                 year as int
     */
    public int getYear() {
        return year;
    }

    /**
     * getMonth()               returns the month
     *
     * @returns                 month as int
     */
    public int getMonth() {
        return month;
    }

    /**
     * getDate()                returns the Date
     *
     * @returns                 date as int
     */
    public int getDate() {
        return date;
    }

    /**
     * getHour()                returns the hour
     *
     * @returns                 hour as int
     */
    public int getHour() {
        return hour;
    }

    /**
     * getMinute()              returns the minutes
     *
     * @returns                 minutes as int
     */
    public int getMinute() {
        return minute;
    }

    /**
     * getSecond()              returns the second
     *
     * @returns                 second as int
     */
    public int getSecond() {
        return second;
    }

    /**
     * setYear()                sets the value of year
     *
     * @param                   year as int
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * setMonth()               sets the value of month
     *
     * @param                   month as int
     */
    public void setMonth(int month) {
        this.month = month;
    }

    /**
     * setDate()                sets the value of date
     *
     * @param                   date as int
     */
    public void setDate(int date) {
        this.date = date;
    }

    /**
     * setHour()                sets the value of Hour
     *
     * @param                   hour as int
     */
    public void setHour(int hour) {
        this.hour = hour;
    }

    /**
     * setMinute()              sets the value of minute
     *
     * @param                   minute as int
     */
    public void setMinute(int minute) {
        this.minute = minute;
    }

    /**
     *  setSecond()             sets the value of Second
     *
     *  @param                  second as int
     */
    public void setSecond(int second) {
        this.second = second;
    }

    /**
     * after(Date date)
     *
     * @param                   when as Date
     * @return                  boolean
     */
    public boolean after(Date when) {
        return (getMilliseconds() > when.getTime());
    }

    /**
     * before(Date date)
     *
     * @param                   when as Date
     * @return                  boolean
     */
    public boolean before(Date when) {
        return (getMilliseconds() < when.getTime());
    }

    /**
     * after(Date date)
     *
     * @param                   when as Date
     * @return                  boolean
     */
    public boolean after(long when) {
        return (getMilliseconds() > when);
    }

    /**
     * before(Date date)
     *
     * @param                   when as Date
     * @return                  boolean
     */
    public boolean before(long when) {
        return (getMilliseconds() < when);
    }

    /**
     * getCalendar()
     *
     * @return                  Calendar
     */
    public Calendar getCalendar() {
        Calendar c = Calendar.getInstance();
        c.set(c.YEAR, year);
        c.set(c.MONTH, month-1);
        c.set(c.DATE, date);
        c.set(c.HOUR_OF_DAY, hour);
        c.set(c.MINUTE, minute);
        c.set(c.SECOND, second);
        return c;
    }

    /**
     * setDateOnly(int year, int month, int date)
     *
     * @param                   year as int
     * @param                   month as int
     * @param                   date as int
     */
    public void setDateOnly(int year, int month, int date) {
        this.year = year;
        this.month = month;
        this.date = date;
        this.hour = 0;
        this.minute = 0;
        this.second = 0;
    }

    /**
     * setTimeOnly(int hour, int minute, int second)
     *
     * @param                   hour as int
     * @param                   minute as int
     * @param                   second as int
     */
    public void setTimeOnly(int hour, int minute, int second) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.year = 0;
        this.month = 0;
        this.date = 0;
    }

    /**
     * createDate()             returns an object of util.Date
     *
     * @returns                 java.util.Date
     */
    public Date createDate() {
        Calendar c = Calendar.getInstance();
        c.set(c.YEAR, year);
        c.set(c.MONTH, month-1);
        c.set(c.DATE, date);
        c.set(c.HOUR_OF_DAY, hour);
        c.set(c.MINUTE, minute);
        c.set(c.SECOND, second);
        return c.getTime();
    }

    /**
     * getMilliseconds()        returns number of seconds since 1900
     *
     * @returns                 long
     */
    public long getMilliseconds() {
        Calendar c = Calendar.getInstance();
        c.set(c.YEAR, year);
        c.set(c.MONTH, month-1);
        c.set(c.DATE, date);
        c.set(c.HOUR_OF_DAY, hour);
        c.set(c.MINUTE, minute);
        c.set(c.SECOND, second);
        return c.getTime().getTime();
    }

    /**
     * toString()               returns a string representing
     *
     * @return                  String representing a date
     */
    public abstract String toString();

    /**
     * date()
     *
     * @return                  String containing date without time
     */
    public abstract String date();

    /**
     * time()
     *
     * @return                  String containing time without date
     */
    public abstract String time();
}