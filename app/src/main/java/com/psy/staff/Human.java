package com.psy.staff;

import java.io.Serializable;
import java.util.Calendar;

class Human implements Serializable
{
    public String mFirstName;
    public String mLastName;
    public boolean mGender; //true - male, false - female
    public Calendar mBirthDate;

    /**
     * Constructor
     * @param firstName - str
     * @param lastName - str
     * @param gender true - male, false - female
     * @param birthDate - Calendar object
     */
    public Human (String firstName, String lastName, boolean gender, Calendar birthDate)
    {
        mFirstName = firstName;
        mLastName = lastName;
        mGender = gender;
        mBirthDate = birthDate;
    }

    /**
     * BirthDay to String
     * @return  "dd/mm/yyyy"
     */
    public String getBirthDateString()
    {
        String str = "";
        int day = mBirthDate.get(Calendar.DAY_OF_MONTH);
        str += ((day<10)?"0":"") + day + "/";
        int month = mBirthDate.get(Calendar.MONTH) + 1;
        str += ((month<10)?"0":"") + month + "/";
        str += mBirthDate.get(Calendar.YEAR);

        return str;
    }

    /**
     * make Calendar object with specified date
     * @param day - day
     * @param month - month 1-12
     * @param year - year
     * @return - obj calendar
     */
    public Calendar makeCalendar(int day, int month, int year)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(0);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.MONTH, month-1);
        calendar.set(Calendar.YEAR, year);
        return calendar;
    }



}
