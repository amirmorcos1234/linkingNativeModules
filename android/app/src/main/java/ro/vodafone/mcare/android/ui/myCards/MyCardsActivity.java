package ro.vodafone.mcare.android.ui.myCards;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.injection.Injection;
import ro.vodafone.mcare.android.ui.activities.MenuActivity;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;

public class MyCardsActivity extends MenuActivity {

    public NavigationHeader navigationHeader;

    MyCardsPresenter myCardsPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigationHeader = (NavigationHeader) findViewById(R.id.navigation_header);
        navigationHeader.setActivity(this)
                .setTitle(getResources().getString(R.string.item_my_cards))
                .displayDefaultHeader();
        addFragment();
    }

    @Override
    public void switchFragmentOnCreate(String fragment, @Nullable String extraParameter) throws Exception {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected int setContent() {
        return R.layout.activity_my_cards;
    }

    private void addFragment() {
        MyCardsFragment myCardsFragment = (MyCardsFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (myCardsFragment == null) {
            myCardsFragment = MyCardsFragment.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.contentFrame, myCardsFragment);
            transaction.commit();
        }
        myCardsPresenter = new MyCardsPresenter(Injection.provideCardsRepository(), myCardsFragment);
    }
}
