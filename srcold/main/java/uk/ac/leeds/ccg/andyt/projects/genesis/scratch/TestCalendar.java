/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.projects.genesis.scratch;

import java.util.Calendar;
import java.util.HashSet;

/**
 *
 * @author geoagdt
 */
public class TestCalendar {

    Calendar _Calendar;

    public TestCalendar() {
    }

    public static void main(String[] args) {
        TestCalendar _TestCalendar = new TestCalendar();
        _TestCalendar.run();
    }

    public void run() {
        HashSet _Calendar_HashSet = new HashSet();
        for (int i = 0; i < 1000; i++) {
            _Calendar = Calendar.getInstance();
            _Calendar.set(i, 0, 0);
            if (!_Calendar_HashSet.add(_Calendar)) {
                System.out.println("Calendar is scarey!");
            }
            System.out.println(_Calendar.getTime());

        }
    }
}
