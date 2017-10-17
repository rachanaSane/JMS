package com.test.jms;/*
 *
 * Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries © European Union, 2015-2016.
 *
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */


import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by sanera on 12/07/2016.
 */
public class test {
    final static String FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static Date parseToUTCDate(String dateString) throws IllegalArgumentException {
        try {


            if (dateString != null) {
                DateTimeFormatter formatter = DateTimeFormat.forPattern(FORMAT).withOffsetParsed();
                DateTime dateTime = formatter.withZoneUTC().parseDateTime(dateString);
                GregorianCalendar cal = dateTime.toGregorianCalendar();
                return cal.getTime();

            } else {
                return null;
            }
        } catch (IllegalArgumentException e) {
          //  LOG.error(e.getMessage());
            throw new IllegalArgumentException(e);
        }
    }


    public static void main(String[] args){
        System.out.println("start");
        Date one = new Date();
        Date two=null;
       int value= one.compareTo(two);
       // parseToUTCDate("2014-05-27 07:47:31");
        System.out.println("end:"+value);
    }
}
