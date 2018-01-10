package com.blogspot.spartandeveloper.playlistmessagesforspotify.base;

import com.blogspot.spartandeveloper.playlistmessagesforspotify.ui.base.BasePresenter;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.ui.base.BasePresenter.MvpViewNotAttachedException;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.ui.base.MvpView;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BasePresenterTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();
    private BasePresenter basePresenter;
    @Mock
    MvpView view;

    @Before
    public void setup() {
        basePresenter = new BasePresenter();
    }

    @After
    public void cleanup() {
        basePresenter.attachView(null);
    }

    @Test
    public void whenNoViewAttached_canAttachView() {
        basePresenter.attachView(view);
        assert(basePresenter.isViewAttached());
    }

    @Test
    public void whenNoViewAttached_throwException(){
        exception.expect(MvpViewNotAttachedException.class);
        basePresenter.checkViewAttached();
    }

    @Test
    public void whenDetachView_thenViewIsNull() {
        basePresenter.attachView(view);
        basePresenter.detachView();
        assert(!basePresenter.isViewAttached());
    }

}
