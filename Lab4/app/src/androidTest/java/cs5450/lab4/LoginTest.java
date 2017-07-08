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
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class LoginTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void loginTest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textView = onView(
                allOf(withId(R.id.post_desc), withText("screenshot"),
                        childAtPosition(
                                allOf(withId(R.id.post_content_layout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                1)),
                                0),
                        isDisplayed()));
        textView.check(matches(isDisplayed()));

        ViewInteraction appCompatTextView = onView(
                allOf(withText("PRIVATE"), isDisplayed()));
        appCompatTextView.perform(click());

        ViewInteraction viewPager = onView(
                allOf(withId(R.id.container), isDisplayed()));
        viewPager.perform(swipeLeft());

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.messages_list),
                        childAtPosition(
                                withParent(withId(R.id.container)),
                                0),
                        isDisplayed()));
        recyclerView.check(matches(isDisplayed()));

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        ViewInteraction appCompatTextView2 = onView(
                allOf(withId(R.id.title), withText("login"), isDisplayed()));
        appCompatTextView2.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(3454202);
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
            Thread.sleep(3499286);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatTextView3 = onView(
                allOf(withText("PRIVATE"), isDisplayed()));
        appCompatTextView3.perform(click());

        ViewInteraction viewPager2 = onView(
                allOf(withId(R.id.container), isDisplayed()));
        viewPager2.perform(swipeLeft());

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.post_desc), withText("first pri"),
                        childAtPosition(
                                allOf(withId(R.id.post_content_layout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                1)),
                                0),
                        isDisplayed()));
        textView2.check(matches(isDisplayed()));

        ViewInteraction appCompatTextView4 = onView(
                allOf(withText("PUBLIC"), isDisplayed()));
        appCompatTextView4.perform(click());

        ViewInteraction viewPager3 = onView(
                allOf(withId(R.id.container), isDisplayed()));
        viewPager3.perform(swipeRight());

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.post_desc), withText("screenshot"),
                        childAtPosition(
                                allOf(withId(R.id.post_content_layout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                1)),
                                0),
                        isDisplayed()));
        textView3.check(matches(isDisplayed()));

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
