package com.blogspot.spartandeveloper.playlistmessagesforspotify;

import com.blogspot.spartandeveloper.playlistmessagesforspotify.data.local.DatabaseHelper;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.data.local.DbOpenHelper;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.util.DefaultConfig;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.util.RxSchedulersOverrideRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

/**
 * Unit tests integration with a SQLite Database using Robolectric
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = DefaultConfig.EMULATE_SDK)
public class DatabaseHelperTest {

    @Rule
    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();

    private DatabaseHelper mDatabaseHelper;

    @Before
    public void setup() {
        if (mDatabaseHelper == null)
            mDatabaseHelper = new DatabaseHelper(new DbOpenHelper(RuntimeEnvironment.application),
                    mOverrideSchedulersRule.getScheduler());
    }

}
