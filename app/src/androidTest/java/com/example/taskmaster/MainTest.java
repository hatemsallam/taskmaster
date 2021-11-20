package com.example.taskmaster;

import static androidx.test.espresso.Espresso.*;
//import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class MainTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void homePageTests (){
        onView(withText("TaskMaster")).check(matches(isDisplayed()));
        onView(withId(R.id.textView)).check(matches(isDisplayed()));
        onView(withId(R.id.recyclerid)).check(matches(isDisplayed()));
        onView(withId(R.id.button)).check(matches(isDisplayed()));
        onView(withId(R.id.button2)).check(matches(isDisplayed()));
        onView(withId(R.id.button_settings)).check(matches(isDisplayed()));
    }

    @Test
    public void settingTest(){
        onView(withId(R.id.button_settings)).perform(click());
        onView(withId(R.id.text_username)).check(matches(isDisplayed()));
        onView(withId(R.id.button_save)).check(matches(isDisplayed()));
        onView(withId(R.id.text_username)).perform(replaceText("Hatem"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.button_save)).perform(click());
//        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click());
        Espresso.pressBack();
        onView(withId(R.id.text_userNameReciever)).check(matches(isDisplayed()));
        onView(withText("Hatem's tasks")).check(matches(isDisplayed()));
    }

    @Test
    public void addTaskTest(){
        onView(withId(R.id.button)).perform(click());
        onView(withId(R.id.textView2)).check(matches(isDisplayed()));
        onView(withId(R.id.plaintext_tasktitle)).perform(replaceText("study"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.plaintext_taskbody)).perform(replaceText("study for 2 hours"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.plaintext_taskstate)).perform(replaceText("new"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.choosefileid)).perform(click());
        Espresso.pressBack();
        onView(withId(R.id.recyclerid)).perform(RecyclerViewActions.actionOnItemAtPosition(2, click()));
        onView(withId(R.id.text_taskTitle)).check(matches(withText("study")));
        Espresso.pressBack();
    }




}
