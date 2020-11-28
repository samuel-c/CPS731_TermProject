package com.example.cps731_termproject;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withChild;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class AlarmTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.ACCESS_COARSE_LOCATION");

    public void authenticationTest() throws InterruptedException {
        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.editTextEmail),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("samuel.chow@ryerson.ca"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.editTextPassword),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("cps731"), closeSoftKeyboard());

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.buttonLogin), withText("Sign In"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        materialButton.perform(click());

        Thread.sleep(1500);

        ViewInteraction recycle = onView(withId(R.id.recycler_viewAlarms));
        recycle.check(matches(isDisplayed()));
    }

    public void clearAlarmTest(){
        ViewInteraction floatingActionButton = onView(withId(R.id.btn_reset)).perform(click());
        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.recycler_viewAlarms),
                        withChild(withId(R.id.recycler_alarmItem)),
                        isDisplayed()));
        recyclerView.check(doesNotExist());
    }

    public void createAlarmTest(){
        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.fab), withContentDescription("Add Alarm"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                5),
                        isDisplayed()));
        floatingActionButton.perform(click());

        ViewInteraction materialButton2 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        materialButton2.perform(scrollTo(), click());

        ViewInteraction viewGroup = onView(
                allOf(withParent(allOf(withId(R.id.recycler_viewAlarms),
                        withParent(IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class)))),
                        isDisplayed()));
        viewGroup.check(matches(isDisplayed()));
    }

    public void changeAlarmNameTest(){

        ViewInteraction appCompatImageView = onView(
                allOf(withId(R.id.btn_edit),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.recycler_viewAlarms),
                                        0),
                                0),
                        isDisplayed()));
        appCompatImageView.perform(click());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.edit_text), withText("Alarm #1"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText3.perform(replaceText("Test Alarm Name"));

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.edit_text), withText("Test Alarm Name"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText4.perform(closeSoftKeyboard());

        ViewInteraction materialButton3 = onView(
                allOf(withId(R.id.btn_update), withText("Update"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                12),
                        isDisplayed()));
        materialButton3.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.text_name), withText("Test Alarm Name"),
                        withParent(withParent(withId(R.id.recycler_viewAlarms))),
                        isDisplayed()));
        textView.check(matches(withText("Test Alarm Name")));
    }

    public void deleteAlarmTest(){
        ViewInteraction appCompatImageView2 = onView(
                allOf(withId(R.id.btn_delete),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.recycler_viewAlarms),
                                        0),
                                2),
                        isDisplayed()));
        appCompatImageView2.perform(click());


        ViewInteraction recyclerView2 = onView(
                allOf(withId(R.id.recycler_viewAlarms),
                        withChild(withId(R.id.recycler_alarmItem)),
                        isDisplayed()));
        recyclerView2.check(doesNotExist());
    }

    public void logOffTest(){
        ViewInteraction appCompatImageView2 = onView(withId(R.id.logoutMenu));
        appCompatImageView2.perform(click());

        ViewInteraction logout = onView(withId(R.id.textView2));
        logout.check(matches(isDisplayed()));
    }

    @Test
    public void AlarmTest() throws InterruptedException {

        authenticationTest();
        clearAlarmTest();
        createAlarmTest();
        changeAlarmNameTest();
        deleteAlarmTest();
        logOffTest();

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
