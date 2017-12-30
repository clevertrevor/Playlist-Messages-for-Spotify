package com.blogspot.spartandeveloper.playlistmessagesforspotify;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.android21buttons.fragmenttestrule.FragmentTestRule;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.test.common.TestComponentRule;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.ui.login.LoginFragment;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.ui.main.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.AdditionalMatchers.not;


@RunWith(AndroidJUnit4.class)
public class LoginFragmentTest {

    public final TestComponentRule component =
            new TestComponentRule(InstrumentationRegistry.getTargetContext());

    @Rule
    public FragmentTestRule<?, LoginFragment> fragmentTestRule =
            FragmentTestRule.create(LoginFragment.class);

    // allows mocking of intents
    @Rule
    public IntentsTestRule<MainActivity> intentsTestRule =
            new IntentsTestRule<>(MainActivity.class);

    // TestComponentRule needs to go first to make sure the Dagger ApplicationTestComponent is set
    // in the Application before any Activity is launched.
    @Rule
    public final TestRule chain = RuleChain.outerRule(component).around(fragmentTestRule);


    @Test
    public void whenLoginFailed_thenShowFailureToast() {




        onView(withText(R.string.login_failed))
                .inRoot(withDecorView(not(is(fragmentTestRule.getFragment().getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));

    }



}
