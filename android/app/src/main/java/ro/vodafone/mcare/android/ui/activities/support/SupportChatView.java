package ro.vodafone.mcare.android.ui.activities.support;

import android.support.annotation.NonNull;
import android.view.View;

import java.util.concurrent.TimeUnit;

import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.utils.D;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by cristi on 24/06/2017.
 * .
 */

public class SupportChatView extends MyChatView {

    final SupportWindow window;

    public SupportChatView(@NonNull final SupportWindow w) {
        super(w.getContext());
        this.window = w;

        Observable.timer(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        if (window.isShopRequestInProcess())
                            window.showLoadingDialog();
                    }
                });

        if (!window.isChatSecondView() || window.getSecondViewIndex() == -1)
            window.createChatRequest();

        if (ChatBubbleSingleton.getInstance().isClosedByAgentButNotLoggedOut()) {
            window.stopLoading();
            window.currentView = new SlyceMessagingView(window);
            window.views.scrollView.setEnableScrolling(true);
            window.inflateLayout();
            window.hideFaqSearchButton();
        } else {

            window.getChatService().getChatState(true, true).subscribe(new RequestSessionObserver<ChatStateResponse>() {
                @Override
                public void onNext(ChatStateResponse response) {
                    D.w("     CHAT STATE     = " + response.getChatState());

                    switch (response.getChatState()) {
                        case "0":
                            window.stopLoading();
                            window.currentView = (window.isChatSecondView() && window.getSecondViewIndex() != -1)
                                    ? new SupportChatSecondView(window, window.getSecondViewIndex())
                                    : new SupportChatFirstView(window);

                            window.inflateLayout();
                            break;
                        case "1":
                            window.stopLoading();
                            window.currentView = new SlyceMessagingView(window);
                            window.inflateLayout();
                            break;
                        case "2":
                            window.stopLoading();
                            window.currentView = new SlyceMessagingView(window);
                            window.inflateLayout();
                            break;
                        default:
                            window.inflateError();
                            break;
                    }
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    D.e("e = " + e);
                    window.stopLoading();
                    window.inflateError();
                }

                @Override
                public void onCompleted() {
                    super.onCompleted();
                }
            });

        }
    }
}
