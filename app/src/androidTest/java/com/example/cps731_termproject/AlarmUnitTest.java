package com.example.cps731_termproject;


import com.example.cps731_termproject.utils.Alarm;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(JUnit4.class)
public class AlarmUnitTest {

    private Alarm alarm;

    @Before
    public void setUp() {
        alarm = new Alarm("Test alarm", 0, 0, 0);
    }

    @After
    public void tearDown() { alarm = null; }

    @Test
    public void testSetGetName() {
        alarm.setAlarmName("New alarm name");
        assertThat(alarm.getAlarmName(), is(equalTo("New alarm name")));
    }

    @Test
    public void testSetGetID() {
        alarm.setId(998);
        assertThat(alarm.getId(), is(equalTo(998)));
    }

    @Test
    public void testSetGetHour() {
        alarm.setHours(5);
        assertThat(alarm.getHours(), is(equalTo(5)));
    }

    @Test
    public void testSetGetMinutes() {
        alarm.setMinutes(59);
        assertThat(alarm.getMinutes(), is(equalTo(59)));
    }

    @Test
    public void testSetGetDaysOfWeek() {
        alarm.setDaysOfWeek(new boolean[]{true, false, true, false, true, false, true});
        assertThat(alarm.getDaysOfWeek(), is(equalTo(new boolean[]{true, false, true, false, true, false, true})));
    }

    @Test
    public void testSetGetVibration() {
        alarm.setVibration(true);
        assertThat(alarm.isVibration(), is(equalTo(true)));
    }

    @Test
    public void testSetGetState() {
        alarm.setState(1);//STOP
        assertThat(alarm.getState(), is(equalTo(1)));
    }

    @Test
    public void testGetFormattedTime() {
        alarm.setHours(13);  //1pm
        alarm.setMinutes(59);

        assertThat(alarm.getFormattedTime(), is(equalTo("01:59 PM")));
    }
}

