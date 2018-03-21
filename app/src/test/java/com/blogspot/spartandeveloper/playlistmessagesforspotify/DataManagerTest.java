package com.blogspot.spartandeveloper.playlistmessagesforspotify;

import com.blogspot.spartandeveloper.playlistmessagesforspotify.data.DataManager;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.data.local.DatabaseHelper;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.data.local.PreferencesHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * This test class performs local unit tests without dependencies on the Android framework
 * For testing methods in the DataManager follow this approach:
 * 1. Stub mock helper classes that your method relies on. e.g. RetrofitServices or DatabaseHelper
 * 2. Test the Observable using TestSubscriber
 * 3. Optionally write a SEPARATE test that verifies that your method is calling the right helper
 * using Mockito.verify()
 */
@RunWith(MockitoJUnitRunner.class)
public class DataManagerTest {

    @Mock DatabaseHelper mMockDatabaseHelper;
    @Mock PreferencesHelper mMockPreferencesHelper;
    private DataManager mDataManager;

    @Before
    public void setUp() {
        mDataManager = new DataManager(mMockPreferencesHelper,
                mMockDatabaseHelper);
    }

    @Test
    public void dummyTest() {
        assert(true);
    }

}
