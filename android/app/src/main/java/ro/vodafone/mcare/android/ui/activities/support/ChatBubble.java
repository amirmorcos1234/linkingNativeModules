package ro.vodafone.mcare.android.ui.activities.support;

import android.graphics.Point;
import android.support.annotation.DrawableRes;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;

import java.util.Arrays;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.realm.appconfig.AppConfiguration;
import ro.vodafone.mcare.android.ui.activities.BaseActivity;
import ro.vodafone.mcare.android.ui.activities.ChatBubbleActivity;
import ro.vodafone.mcare.android.ui.activities.DashboardActivity;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.utils.ViewUtils;
import ro.vodafone.mcare.android.widget.BubbleImageView;


/**
 * Created by bogdan marica on 4/26/2017.
 * .
 */

public class ChatBubble implements SupportWindow.SupportWindowVisibilityListener {
    private static final int SCALE_DURATION = 200;
    private static final int MOVE_DURATION = 400;
    private static final float REGULAR_SCALE = 1.6f;
    private static final float SMALL_SCALE = 0.8f;
    private static final float MOVING_SCALE = 0.5f;
    private static final int MOVEMENT_UP_LIMIT = ScreenMeasure.dpToPx(64);
    private static final int MOVEMENT_BELOW_LIMIT = ScreenMeasure.dpToPx(64);
    public static String TAG = "ChatBubble";
    int initialWidth;
    private BubbleImageView chatButton;
    private int windowHeight;
    private int screenWidth;
    private int screenHeight;
    private int screenTop;
    private int screenBottom;
    private int normalX = 0;
    private int maxY = Integer.MAX_VALUE;
    //    private boolean clickedOnce = true;
    private boolean visible;
    private boolean animating;
    private BaseActivity chatBubbleActivity;
    private Runnable onAnimationEndRunnable;
    private Point lastPositionOnScreen;
    private Point lastPositionFromAnimation = new Point();


    public ChatBubble(BaseActivity menuActivity) {
        this.chatBubbleActivity = menuActivity;
    }

    public boolean isAnimating() {
        return animating;
    }

    public void setAnimating(boolean animating) {
        this.animating = animating;
    }

    public void initSupportBubble() {
        chatButton = (BubbleImageView) chatBubbleActivity.findViewById(R.id.support_imageview);
        addLayoutChangeListerToStopResettingPostionBecauseOfResize();
        ImageView imageView = chatButton.getImageview();
        chatButton.setDrawingCacheEnabled(true);

        if (imageView.getDrawable() != null) {
            if (imageView.getDrawable().getConstantState() == null)
                chatButton.setImageResource(R.drawable.support24_7);
        } else
            chatButton.setImageResource(R.drawable.support24_7);


        chatButton.setVisibility(View.GONE);
        //   D.d("support image " + chatButton);
        if (VodafoneController.currentActivity() != null)
            VodafoneController.getInstance().supportWindow(chatBubbleActivity).setVisibilityListener(this);
    }

    public BubbleImageView getChatButton() {
        return chatButton;
    }

    private void addLayoutChangeListerToStopResettingPostionBecauseOfResize() {
    }

    private void setSupportDrawable(@DrawableRes int resId) {
        chatButton.setImageResource(resId);
    }

    void setSupportDrawable(@DrawableRes int resId, int val) {
        setSupportDrawable(resId);
        chatButton.setCount(val);
    }

    public void updateBubbleBadge(int val) {
        chatButton.setCount(val);
    }

    private void animateSupportBubble(final boolean expandAsSnapCard) {
        ViewUtils.postOnPreDraw(chatButton, new Runnable() {
            @Override
            public void run() {
                animateSupportBubbleInternal(expandAsSnapCard);
            }
        });
    }

    private void animateSupportBubbleInternal(final boolean expandAsSnapCard) {
        if (animating)
            return;
        animating = true;
        int duration = 2000;
        initialWidth = chatButton.getWidth();
        chatButton.setTranslationX(initialWidth);
        chatButton.setScaleX(0.5f);
        chatButton.setScaleY(0.5f);

        chatButton.animate()
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(1000)
                .translationXBy(-initialWidth)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        chatButton.animate()
                                .setInterpolator(new BounceInterpolator())
                                .setDuration(1000)
                                .scaleX(REGULAR_SCALE)
                                .scaleY(REGULAR_SCALE)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        animating = false;

                                        if ((chatButton.getVisibility() == View.GONE)) {
                                            lastPositionOnScreen = null;
                                            return;
                                        }
                                        VodafoneController.getInstance().handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                lastPositionOnScreen = new Point();
                                                lastPositionOnScreen.x = (int) chatButton.getX();
                                                lastPositionOnScreen.y = (int) chatButton.getY();

                                                if (onAnimationEndRunnable != null) {
                                                    onAnimationEndRunnable.run();
                                                }
                                                if (expandAsSnapCard) {
                                                    VodafoneController.getInstance().supportWindow(chatBubbleActivity).show(true);
                                                }
                                            }
                                        });
                                    }
                                }).start();
                    }
                }).start();

        Point size = new Point();
        chatBubbleActivity.getWindowManager().getDefaultDisplay().getSize(size);
        windowHeight = size.y;
        chatBubbleActivity.getWindowManager().getDefaultDisplay().getRealSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
        Point screenMargins = ViewUtils.getWindowPadding(chatBubbleActivity.getWindowManager(), chatBubbleActivity.getWindow());
        screenTop = screenMargins.x;
        screenBottom = screenMargins.y;
        normalX = screenWidth - screenMargins.x - chatButton.getWidth();

        ViewUtils.postOnPreDraw(chatButton, new Runnable() {
            @Override
            public void run() {
                try {
                    int height = chatBubbleActivity.getWindow().getDecorView().getHeight();
                    if (height > 0) windowHeight = height;
                    maxY = screenHeight - (screenBottom + 400 + chatButton.getHeight());
                } catch (Exception ignored) {
                }
            }
        });

        chatButton.getImageview().setOnTouchListener(new View.OnTouchListener() {

            private final long touchSlop = ViewConfiguration.get(chatButton.getContext()).getScaledTouchSlop();
            float startX;
            float startY;
            private int CLICK_ACTION_THRESHHOLD = ScreenMeasure.dpToPx(5);
            private long CLICK_ACTION_THRESHHOLD_TIMER = 2000;
            private long lastTouchDown;
            private int chatButtonHeight;
            private boolean dragging = false;
            private boolean draggedWhileAnimating = false;
            private int upLimit;
            private int y_cord;

            private boolean isAClick(float startX, float endX, float startY, float endY) {
                float differenceX = Math.abs(startX - endX);
                float differenceY = Math.abs(startY - endY);
                return !(differenceX > CLICK_ACTION_THRESHHOLD || differenceY > CLICK_ACTION_THRESHHOLD);
            }

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (animating) return false;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_CANCEL:
                        draggedWhileAnimating = false;
                        dragging = false;
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        lastTouchDown = System.currentTimeMillis();
                        chatButtonHeight = chatButton.getHeight();
                        upLimit = windowHeight - chatButtonHeight - MOVEMENT_UP_LIMIT;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (dragging || draggedWhileAnimating) {
                            draggedWhileAnimating = false;
                            dragging = false;
                            return true;
                        }
                        float endX = event.getX();
                        float endY = event.getY();

                        if (isAClick(startX, endX, startY, endY) && (System.currentTimeMillis() - lastTouchDown < CLICK_ACTION_THRESHHOLD_TIMER))
                            processClickEvent();

                        break;
                    case MotionEvent.ACTION_MOVE:

                        //D.w("Screen density" + chatBubbleActivity.getResources().getDisplayMetrics().density );
                        //D.w("Window Height" +windowHeight);
//                        D.w("Start Y " + startY);
//                        D.w("Move y " +y_cord+" event Y "+event.getY());
//                        D.w("y_cord = windowHeight - MOVEMENT_UP_LIMIT " + (windowHeight- MOVEMENT_UP_LIMIT));
//                        D.w("y_cord > windowHeight - MOVEMENT_UP_LIMIT " + (y_cord > windowHeight- MOVEMENT_UP_LIMIT));
//                        D.w("y_cord < MOVEMENT_BELOW_LIMIT ? " + (y_cord< MOVEMENT_BELOW_LIMIT));
                        if (dragging || (Math.abs(startY - event.getY()) > touchSlop)) {
                            y_cord = (int) event.getRawY();
                            if (y_cord > upLimit) {
                                y_cord = upLimit;
                            } else if (y_cord < MOVEMENT_BELOW_LIMIT) {
                                y_cord = MOVEMENT_BELOW_LIMIT;
                            }

                            dragging = true;
                            if (animating || draggedWhileAnimating) {
                                draggedWhileAnimating = true;
                                return true;
                            }
                            chatButton.setY(y_cord - chatButtonHeight / 2);
                            final int chatButtonX = (int) chatButton.getX();
                            final int chatButtonY = (int) chatButton.getY();
                            lastPositionOnScreen.x = chatButtonX;
                            lastPositionOnScreen.y = chatButtonY;
                            lastPositionFromAnimation.x = chatButtonX;
                            lastPositionFromAnimation.y = chatButtonY;
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

    }

    public void updateBubbleForAgentClosedCase() {
        setSupportDrawable(R.drawable.support24_7);
    }

    public void updateBubble() {
        if (ChatBubbleSingleton.getInstance().getMinimized()) {
            setSupportDrawable(R.drawable.support24_7_red);
            updateBubbleBadge(ChatBubbleSingleton.getInstance().getMessagesCount());
        } else {
            setSupportDrawable(R.drawable.support24_7);
            updateBubbleBadge(0);
        }
    }

    private void showBubbleInternal() {
        updateBubble();
        chatButton.setVisibility(View.VISIBLE);
    }

    public void forceHide() {
        if (chatButton == null)
            return;

        if (chatButton.getAnimation() != null) {
            chatButton.getAnimation().cancel();
        }

        VodafoneController.getInstance().handler.post(new Runnable() {
            @Override
            public void run() {
                chatButton.setScaleX(REGULAR_SCALE);
                chatButton.setScaleY(REGULAR_SCALE);
                setSupportDrawable(R.drawable.support24_7);
                chatButton.setTranslationX(0);
                chatButton.setTranslationY(0);
                if (lastPositionOnScreen != null) {
                    chatButton.setX(lastPositionOnScreen.x);
                    chatButton.setY(lastPositionOnScreen.y);
                }
            }
        });
        chatButton.setVisibility(View.GONE);
    }

    public void forceHideAndShow() {
        if (chatButton == null)
            return;

        if (chatButton.getAnimation() != null) {
            chatButton.getAnimation().cancel();
        }

        VodafoneController.getInstance().handler.post(new Runnable() {
            @Override
            public void run() {
                chatButton.setScaleX(REGULAR_SCALE);
                chatButton.setScaleY(REGULAR_SCALE);
                setSupportDrawable(R.drawable.support24_7);
                chatButton.setTranslationX(0);
                chatButton.setTranslationY(0);
                if (lastPositionOnScreen != null) {
                    chatButton.setX(lastPositionOnScreen.x);
                    chatButton.setY(lastPositionOnScreen.y);
                }
            }
        });
        chatButton.setVisibility(View.GONE);
        chatButton.setVisibility(View.VISIBLE);
    }

    private void hideBubbleInternal() {
        chatBubbleActivity.runOnUiThread(new Runnable() {
            public void run() {
                View imgView = chatBubbleActivity.findViewById(R.id.support_imageview);
                if (imgView != null) {
                    imgView.setVisibility(View.GONE);
                }
                chatButton.setVisibility(View.GONE);
            }
        });
    }

    public ChatBubble displayBubble() {
        if (!AppConfiguration.isFaqButtonVisible().toLowerCase().equals("true"))
            return null;
        else
            return displayBubble(false);
    }

    public ChatBubble displayBubble(boolean expandAsSnapCard) {
        if (!AppConfiguration.isFaqButtonVisible().toLowerCase().equals("true"))
            return null;
        else {
            visible = true;
            showBubbleInternal();

            animateSupportBubble(expandAsSnapCard);
            return this;
        }
    }

    private void temporaryHideBubble() {
        hideBubbleInternal();
    }

    private void restoreBubbleVisibility() {
        if (visible) showBubbleInternal();
    }

    public void hideBubble() {
        visible = false;
        hideBubbleInternal();
    }

    public boolean isVisible() {
        return visible;
    }

    void animateBack(final Runnable onAnimationEnd) {
        Log.d(TAG, "-> chat back pressed");
        if (animating)
            return;
        animating = true;
        lastPositionOnScreen.x = normalX;
        chatButton.setVisibility(View.VISIBLE);
        chatButton.setTranslationX(0);
        chatButton.setTranslationY(0);
        chatButton.setX(lastPositionFromAnimation.x);
        chatButton.setY(lastPositionFromAnimation.y);

        VodafoneController.getInstance().handler.post(new Runnable() {
            @Override
            public void run() {
                scaleBubble(MOVING_SCALE, new Runnable() {
                    @Override
                    public void run() {
                        setSupportDrawable(R.drawable.support24_7);
                        moveTo(lastPositionOnScreen.x + (REGULAR_SCALE * initialWidth - chatButton.getWidth()) / 2, lastPositionOnScreen.y, new Runnable() {
                            @Override
                            public void run() {
                                scaleBubble(REGULAR_SCALE, new Runnable() {
                                    @Override
                                    public void run() {
                                        animating = false;
                                        restoreBubbleVisibility();
                                        if (onAnimationEnd != null)
                                            onAnimationEnd.run();
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });

    }

    void animateTo(final float x, float yBottom, final Runnable onAnimationEnd) {
        if (animating)
            return;
        animating = true;
        final float y = screenHeight - yBottom;

        restoreBubbleVisibility();
        Log.d(ChatBubble.class.getSimpleName(), "Initial translation: " + chatButton.getTranslationX() + " , " + chatButton.getTranslationY());
        final int[] position = new int[2];
        chatButton.getLocationInWindow(position);
        //only update the X coord
        lastPositionOnScreen.x = position[0];
        //lastPositionOnScreen.y = position[1];
        chatButton.setTranslationX(0);
        chatButton.setTranslationY(0);
        chatButton.setX(lastPositionOnScreen.x);
        chatButton.setY(lastPositionOnScreen.y);
        Log.d(ChatBubble.class.getSimpleName(), "From: " + Arrays.toString(position) + " to " + x + " , " + y);

        scaleBubble(MOVING_SCALE, new Runnable() {
            @Override
            public void run() {
                moveTo(x, y, new Runnable() {
                    @Override
                    public void run() {
                        setSupportDrawable(R.drawable.support24_7_red);
                        scaleBubble(SMALL_SCALE, new Runnable() {
                            @Override
                            public void run() {
                                animating = false;
                                lastPositionFromAnimation.x = (int) chatButton.getX();
                                lastPositionFromAnimation.y = (int) chatButton.getY();
                                temporaryHideBubble();
                                if (onAnimationEnd != null)
                                    onAnimationEnd.run();
                            }
                        });
                    }
                });
            }
        });
    }

    private void scaleBubble(float ratio, final Runnable onAnimationEnd) {
        chatButton.animate()
                .setInterpolator(new BounceInterpolator())
                .setDuration(SCALE_DURATION)
                .scaleX(ratio)
                .scaleY(ratio)
                .withEndAction(onAnimationEnd)
                .start();
    }

    private void moveTo(float x, float y, final Runnable onAnimationEnd) {
        chatButton.animate()
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(MOVE_DURATION)
                .x(x)
                .y(y)
                .withEndAction(onAnimationEnd)
                .start();
    }

    Point restore() {
        restoreBubbleVisibility();

        if (visible) {
            if (lastPositionOnScreen != null) {
                chatButton.setX(lastPositionOnScreen.x);
                chatButton.setY(lastPositionOnScreen.y);
                chatButton.setScaleX(REGULAR_SCALE);
                chatButton.setScaleY(REGULAR_SCALE);
                chatButton.setTranslationX(0);
                chatButton.setTranslationY(0);
            }

            return new Point(lastPositionOnScreen);
        }
        return null;
    }

    public void showLastDisplayTypeAfterTutorial() {
        processClickEvent();
    }

    private void processClickEvent() {
        lastPositionOnScreen = new Point();
        lastPositionOnScreen.x = (int) chatButton.getX();
        lastPositionOnScreen.y = (int) chatButton.getY();
        lastPositionFromAnimation.x = (int) chatButton.getX();
        lastPositionFromAnimation.y = (int) chatButton.getY();
        if (!VodafoneController.getInstance().supportWindow(chatBubbleActivity).isShowing()) {
            if (ChatBubbleSingleton.getInstance().getCountListener() == null)
                ChatBubbleSingleton.getInstance().setCountListener((ChatBubbleActivity) chatBubbleActivity);
            if (chatBubbleActivity instanceof DashboardActivity) {
                ChatBubbleSingleton.setMinimizeToSnapCard(false);
                goToAssistanceFAQ();
            } else {
                /*Does not displayedInfo snapCard when chat is active*/
                if (VodafoneController.getInstance().isChatConnected()) {
                    ChatBubbleSingleton.setMinimizeToSnapCard(false);
                    goToAssistanceFAQ();
                } else {
                    ChatBubbleSingleton.setMinimizeToSnapCard(true);
                    goToAssistanceSnapCard();
                }
            }
        }
    }

    /**
     * @deprecated
     */
    void enableClick() {
    }

    private void goToAssistanceFAQ() {
        VodafoneController.getInstance().supportWindow(chatBubbleActivity).show(false);
    }

    public void goToAssistanceSnapCard() {
        SupportWindow.DisplayType displayType;
        if (ChatBubbleSingleton.getInstance().getMinimized()) {
            displayType = ChatBubbleSingleton.getInstance().getDisplayType();
        } else {
            displayType = SupportWindow.DisplayType.NONE;
        }
        VodafoneController.getInstance().supportWindow(chatBubbleActivity).show(displayType);
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
        if (visible) {
            if (!animating) {
                temporaryHideBubble();
            }
        } else {
            if (!animating) {
                chatButton.setTranslationX(0);
                chatButton.setTranslationY(0);

                restoreBubbleVisibility();
            }
        }
    }

    public float getCurrentPosition() {
        return chatButton.getTranslationY();
    }

    public void moveToPosition(float positionY) {
        chatButton.setTranslationY(positionY);
    }
}
