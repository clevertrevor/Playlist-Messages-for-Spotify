package com.blogspot.spartandeveloper.playlistmessagesforspotify;

import android.content.Intent;
import android.os.Looper;
import android.test.ServiceTestCase;

import com.blogspot.spartandeveloper.playlistmessagesforspotify.data.CreatePlaylistService;

import java.util.concurrent.CountDownLatch;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

// FIXME not working yet. see https://stackoverflow.com/questions/11923181/how-to-junit-test-intentservice
public class CreatePlaylistServiceTest extends ServiceTestCase<CreatePlaylistService> {

    public CreatePlaylistServiceTest() {
        super(CreatePlaylistService.class);
    }


    public void test_onHandleIntent() throws Exception {
        Looper.prepare();

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        Intent intent = new Intent(getSystemContext(), CreatePlaylistService.class);
        final String playlistName = "playlist_name", playlistMessage = "pl_msg", userId = "123";
        intent.putExtra(CreatePlaylistService.PLAYLIST_NAME, playlistName);
        intent.putExtra(CreatePlaylistService.PLAYLIST_MESSAGE, playlistMessage);
        intent.putExtra(CreatePlaylistService.USER_ID, userId);

        getSystemContext().startService(intent);
        Looper.loop();
        assertThat(countDownLatch.getCount(), is(0L));

    }

}
