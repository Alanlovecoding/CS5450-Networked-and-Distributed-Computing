package cs5450.lab4;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class PrivatePublicPostTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void privatePublicPostTest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textView = onView(
                allOf(withId(R.id.post_author), withText("bob"),
                        childAtPosition(
                                allOf(withId(R.id.post_author_layout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                0)),
                                1),
                        isDisplayed()));
        textView.check(matches(withText("bob")));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.post_desc), withText("public"),
                        childAtPosition(
                                allOf(withId(R.id.post_content_layout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                1)),
                                0),
                        isDisplayed()));
        textView2.check(matches(withText("public")));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.post_author), withText("a"),
                        childAtPosition(
                                allOf(withId(R.id.post_author_layout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                0)),
                                1),
                        isDisplayed()));
        textView3.check(matches(withText("a")));

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.post_desc), withText("screenshot"),
                        childAtPosition(
                                allOf(withId(R.id.post_content_layout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                1)),
                                0),
                        isDisplayed()));
        textView4.check(matches(withText("screenshot")));

        ViewInteraction textView5 = onView(
                allOf(withId(R.id.post_author), withText("bob"),
                        childAtPosition(
                                allOf(withId(R.id.post_author_layout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                0)),
                                1),
                        isDisplayed()));
        textView5.check(matches(withText("bob")));

        ViewInteraction textView6 = onView(
                allOf(withId(R.id.post_desc), withText("random"),
                        childAtPosition(
                                allOf(withId(R.id.post_content_layout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                1)),
                                0),
                        isDisplayed()));
        textView6.check(matches(withText("random")));

        ViewInteraction textView7 = onView(
                allOf(withId(R.id.post_desc), withText("random"),
                        childAtPosition(
                                allOf(withId(R.id.post_content_layout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                1)),
                                0),
                        isDisplayed()));
        textView7.check(matches(withText("random")));

        ViewInteraction appCompatTextView = onView(
                allOf(withText("PRIVATE"), isDisplayed()));
        appCompatTextView.perform(click());

        ViewInteraction viewPager = onView(
                allOf(withId(R.id.container), isDisplayed()));
        viewPager.perform(swipeLeft());

        ViewInteraction appCompatTextView2 = onView(
                allOf(withText("PRIVATE"), isDisplayed()));
        appCompatTextView2.perform(click());

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        ViewInteraction appCompatTextView3 = onView(
                allOf(withId(R.id.title), withText("login"), isDisplayed()));
        appCompatTextView3.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(3529276);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.field_email),
                        withParent(withId(R.id.layout_email_password)),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("bob@gmail.com"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.field_password),
                        withParent(withId(R.id.layout_email_password)),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("bobbob"), closeSoftKeyboard());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.button_sign_in), withText("sign in"),
                        withParent(withId(R.id.layout_buttons)),
                        isDisplayed()));
        appCompatButton.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(3555055);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textView8 = onView(
                allOf(withId(R.id.post_author), withText("bob"),
                        childAtPosition(
                                allOf(withId(R.id.post_author_layout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                0)),
                                1),
                        isDisplayed()));
        textView8.check(matches(withText("bob")));

        ViewInteraction textView9 = onView(
                allOf(withId(R.id.post_desc), withText("public"),
                        childAtPosition(
                                allOf(withId(R.id.post_content_layout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                1)),
                                0),
                        isDisplayed()));
        textView9.check(matches(withText("public")));

        ViewInteraction textView10 = onView(
                allOf(withId(R.id.post_author), withText("a"),
                        childAtPosition(
                                allOf(withId(R.id.post_author_layout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                0)),
                                1),
                        isDisplayed()));
        textView10.check(matches(withText("a")));

        ViewInteraction textView11 = onView(
                allOf(withId(R.id.post_desc), withText("screenshot"),
                        childAtPosition(
                                allOf(withId(R.id.post_content_layout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                1)),
                                0),
                        isDisplayed()));
        textView11.check(matches(withText("screenshot")));

        ViewInteraction textView12 = onView(
                allOf(withId(R.id.post_author), withText("bob"),
                        childAtPosition(
                                allOf(withId(R.id.post_author_layout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                0)),
                                1),
                        isDisplayed()));
        textView12.check(matches(withText("bob")));

        ViewInteraction textView13 = onView(
                allOf(withId(R.id.post_desc), withText("random"),
                        childAtPosition(
                                allOf(withId(R.id.post_content_layout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                1)),
                                0),
                        isDisplayed()));
        textView13.check(matches(withText("random")));

        ViewInteraction textView14 = onView(
                allOf(withId(R.id.post_desc), withText("random"),
                        childAtPosition(
                                allOf(withId(R.id.post_content_layout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                1)),
                                0),
                        isDisplayed()));
        textView14.check(matches(withText("random")));

        ViewInteraction appCompatTextView4 = onView(
                allOf(withText("PRIVATE"), isDisplayed()));
        appCompatTextView4.perform(click());

        ViewInteraction viewPager2 = onView(
                allOf(withId(R.id.container), isDisplayed()));
        viewPager2.perform(swipeLeft());

        ViewInteraction textView15 = onView(
                allOf(withId(R.id.post_author), withText("bob"),
                        childAtPosition(
                                allOf(withId(R.id.post_author_layout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                0)),
                                1),
                        isDisplayed()));
        textView15.check(matches(withText("bob")));

        ViewInteraction textView16 = onView(
                allOf(withId(R.id.post_desc), withText("private"),
                        childAtPosition(
                                allOf(withId(R.id.post_content_layout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                1)),
                                0),
                        isDisplayed()));
        textView16.check(matches(withText("private")));

        ViewInteraction textView17 = onView(
                allOf(withId(R.id.post_author), withText("bob"),
                        childAtPosition(
                                allOf(withId(R.id.post_author_layout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                0)),
                                1),
                        isDisplayed()));
        textView17.check(matches(withText("bob")));

        ViewInteraction textView18 = onView(
                allOf(withId(R.id.post_desc), withText("first pri"),
                        childAtPosition(
                                allOf(withId(R.id.post_content_layout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                1)),
                                0),
                        isDisplayed()));
        textView18.check(matches(withText("first pri")));

        ViewInteraction textView19 = onView(
                allOf(withId(R.id.post_desc), withText("first pri"),
                        childAtPosition(
                                allOf(withId(R.id.post_content_layout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                1)),
                                0),
                        isDisplayed()));
        textView19.check(matches(withText("first pri")));

        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.new_post_button), isDisplayed()));
        floatingActionButton.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(3539148);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.description), isDisplayed()));
        appCompatEditText3.perform(replaceText("another private"), closeSoftKeyboard());

        ViewInteraction switch_ = onView(
                allOf(withId(R.id.is_pri_switch), withText("private"), isDisplayed()));
        switch_.perform(click());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.select_img), withText("Upload From Gallary"), isDisplayed()));
        appCompatButton2.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(3576519);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction floatingActionButton2 = onView(
                allOf(withId(R.id.new_post_button), isDisplayed()));
        floatingActionButton2.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(3596621);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.description), isDisplayed()));
        appCompatEditText4.perform(replaceText("new private"), closeSoftKeyboard());

        ViewInteraction switch_2 = onView(
                allOf(withId(R.id.is_pri_switch), withText("private"), isDisplayed()));
        switch_2.perform(click());

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.select_img), withText("Upload From Gallary"), isDisplayed()));
        appCompatButton3.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(3576617);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatTextView5 = onView(
                allOf(withText("PRIVATE"), isDisplayed()));
        appCompatTextView5.perform(click());

        ViewInteraction textView20 = onView(
                allOf(withId(R.id.post_author), withText("bob"),
                        childAtPosition(
                                allOf(withId(R.id.post_author_layout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                0)),
                                1),
                        isDisplayed()));
        textView20.check(matches(withText("bob")));

        ViewInteraction textView21 = onView(
                allOf(withId(R.id.post_desc), withText("new private"),
                        childAtPosition(
                                allOf(withId(R.id.post_content_layout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                1)),
                                0),
                        isDisplayed()));
        textView21.check(matches(withText("new private")));

        ViewInteraction floatingActionButton3 = onView(
                allOf(withId(R.id.new_post_button), isDisplayed()));
        floatingActionButton3.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(3574456);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.description), isDisplayed()));
        appCompatEditText5.perform(replaceText("new public"), closeSoftKeyboard());

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.select_img), withText("Upload From Gallary"), isDisplayed()));
        appCompatButton4.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(3585323);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textView22 = onView(
                allOf(withId(R.id.post_author), withText("bob"),
                        childAtPosition(
                                allOf(withId(R.id.post_author_layout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                0)),
                                1),
                        isDisplayed()));
        textView22.check(matches(withText("bob")));

        ViewInteraction textView23 = onView(
                allOf(withId(R.id.post_desc), withText("new public"),
                        childAtPosition(
                                allOf(withId(R.id.post_content_layout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                1)),
                                0),
                        isDisplayed()));
        textView23.check(matches(withText("new public")));

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        ViewInteraction appCompatTextView6 = onView(
                allOf(withId(R.id.title), withText("logout"), isDisplayed()));
        appCompatTextView6.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(3581231);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatTextView7 = onView(
                allOf(withText("PRIVATE"), isDisplayed()));
        appCompatTextView7.perform(click());

        ViewInteraction appCompatTextView8 = onView(
                allOf(withText("PUBLIC"), isDisplayed()));
        appCompatTextView8.perform(click());

        ViewInteraction textView24 = onView(
                allOf(withId(R.id.post_author), withText("bob"),
                        childAtPosition(
                                allOf(withId(R.id.post_author_layout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                0)),
                                1),
                        isDisplayed()));
        textView24.check(matches(withText("bob")));

        ViewInteraction textView25 = onView(
                allOf(withId(R.id.post_desc), withText("new public"),
                        childAtPosition(
                                allOf(withId(R.id.post_content_layout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                1)),
                                0),
                        isDisplayed()));
        textView25.check(matches(withText("new public")));

        ViewInteraction textView26 = onView(
                allOf(withId(R.id.post_author), withText("bob"),
                        childAtPosition(
                                allOf(withId(R.id.post_author_layout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                0)),
                                1),
                        isDisplayed()));
        textView26.check(matches(withText("bob")));

        ViewInteraction textView27 = onView(
                allOf(withId(R.id.post_desc), withText("public"),
                        childAtPosition(
                                allOf(withId(R.id.post_content_layout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                1)),
                                0),
                        isDisplayed()));
        textView27.check(matches(withText("public")));

        ViewInteraction textView28 = onView(
                allOf(withId(R.id.post_author), withText("a"),
                        childAtPosition(
                                allOf(withId(R.id.post_author_layout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                0)),
                                1),
                        isDisplayed()));
        textView28.check(matches(withText("a")));

        ViewInteraction textView29 = onView(
                allOf(withId(R.id.post_desc), withText("screenshot"),
                        childAtPosition(
                                allOf(withId(R.id.post_content_layout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                1)),
                                0),
                        isDisplayed()));
        textView29.check(matches(withText("screenshot")));

        ViewInteraction textView30 = onView(
                allOf(withId(R.id.post_desc), withText("screenshot"),
                        childAtPosition(
                                allOf(withId(R.id.post_content_layout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                1)),
                                0),
                        isDisplayed()));
        textView30.check(matches(withText("screenshot")));

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
