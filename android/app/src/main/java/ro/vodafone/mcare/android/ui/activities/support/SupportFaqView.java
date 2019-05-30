package ro.vodafone.mcare.android.ui.activities.support;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.UserRole;
import ro.vodafone.mcare.android.client.model.realm.appconfig.AppConfiguration;
import ro.vodafone.mcare.android.client.model.realm.system.SupportLabels;
import ro.vodafone.mcare.android.interfaces.OnErrorIconClickListener;
import ro.vodafone.mcare.android.interfaces.factory.InterfaceFAQContentModelsFactory;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.ui.activities.support.supportModels.CategoriesList;
import ro.vodafone.mcare.android.ui.activities.support.supportModels.Content;
import ro.vodafone.mcare.android.ui.activities.support.supportModels.FaqPOJO;
import ro.vodafone.mcare.android.ui.activities.support.supportModels.FaqsList;
import ro.vodafone.mcare.android.client.adapters.support.FAQsRecyclerAdapter;
import ro.vodafone.mcare.android.ui.activities.support.supportModels.TrendingSearches;
import ro.vodafone.mcare.android.ui.utils.Fonts;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.utils.TextUtils;
import ro.vodafone.mcare.android.ui.utils.listeners.adapter.item_click.ScrollableTabAdapterOnItemClickListener;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.widget.TabMenu.TabAdapter;

/**
 * Created by Bogdan M. on 24/06/2017.
 */
public class SupportFaqView extends MyChatView implements
        InterfaceFAQContentModelsFactory {

    final SupportWindow window;

    FAQsRecyclerAdapter faqRecyclerAdapter;
    FAQsRecyclerAdapter searchFaqRecyclerAdapter;

    List<CategoriesList> categoriesList;
    List<FaqsList> faqList;
    List<String> allKeywords;
    List<String> filteredKeywords;
    List<String> popularKeywords;
    PopupWindow autoCompletePopupWindow;
    ArrayAdapter<String> autoCompleteAdapter;
    boolean isFromSelectedHint = false;
    Thread sThread = new Thread(new Runnable() {
        @Override
        public void run() {

        }
    });
    List<FaqsList> unmodifiedDisplayedFaqList;
    List<FaqsList> searchFaqListResults;
    String searchTerm;
    int sideMargins = ScreenMeasure.dpToPx(12);
    int upDownMargins = ScreenMeasure.dpToPx(5);
    boolean isErrorDisplayed = false;
    View.OnClickListener refreshFAQClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            window.inflateFaqLayout();
            init();
        }
    };

    public SupportFaqView(@NonNull SupportWindow w) {
        super(w.getContext());
        this.window = w;
        init();
    }

    void refreshErrorPage() {
        if (isErrorDisplayed)
            init();
    }

    public void pushEmailToStack() {
        if (window.views != null) {
            window.pushNavigationItemToBackStack(window.views.navigation.getMenu().getItem(SupportWindow.EMAIL_ITEM_POSITION));
            window.pushNavigationItemToBackStack(window.views.navigation.getMenu().getItem(SupportWindow.FAQ_ITEM_POSITION));
        }
    }

    void init() {
        D.w("refresh faq page");
        isErrorDisplayed = false;
        window.changeMenuItemCheckedStateColor(window.views.navigation);

        searchFaqListResults = new ArrayList<>();
        unmodifiedDisplayedFaqList = new ArrayList<>();
        faqList = new ArrayList<>();
        allKeywords = new ArrayList<>();
        popularKeywords = new ArrayList<>();
        categoriesList = new ArrayList<>();

        updateFooterItems();
        initViewElements();
        downloadJSON();
        initRecyclerView();
        initTabCard();
        D.d("helpandsupport 1");

        //Tealium trigger survey
        //Tealium Track view
        Map<String, Object> tealiumMapView = new HashMap(4);
        tealiumMapView.put("screen_name", "helpandsupport");
        //add Qualtrics survey
        TealiumHelper.addQualtricsCommand();
        //track
        TealiumHelper.trackView("helpandsupport", tealiumMapView);
    }

    void updateFooterItems() {
        if (!AppConfiguration.isEmailButtonVisible().toLowerCase().equals("true")
                && !AppConfiguration.isChatButtonVisible().toLowerCase().equals("true"))
            window.displayAllowedButtons();
        else {
            window.views.dock_menu.setVisibility(View.VISIBLE);
            window.setBottomNavigationMenuVisibility(true);

            VodafoneController.getInstance().handler.post(new Runnable() {
                @Override
                public void run() {
                    window.setBottomNavigationMenuVisibility(true);
                    window.views.dock_menu.setVisibility(View.VISIBLE);
                    if (VodafoneController.currentActivity() != null) {
                        VodafoneController.currentActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                window.setBottomNavigationMenuVisibility(true);
                                window.views.dock_menu.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }
            });
        }
    }

    public void showFAQSearchButton()
    {
        if (!ChatBubbleSingleton.getInstance().isClosedByAgentButNotLoggedOut()) {
            window.views.faq_search_button.setVisibility(!window.isSnapCard() && !isErrorDisplayed && faqList.size() > 0 ? View.VISIBLE : View.GONE);

            if (searchTerm != null && searchTerm.length() > 0)
                showSearchBox();
        }

    }

    public void hideFAQSearchButton()
    {
        window.views.faq_search_button.setVisibility(View.GONE);
    }

    void initViewElements() {

        window.setShopRequestInProcess(false);
        window.setLastDisplayType(SupportWindow.DisplayType.FAQ);

        window.views.faq_search_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                D.d("clicked search icon in header");

                if (window.views.window_search_box.getVisibility() == VISIBLE)
                    hideSearchBox();
                else
                    showSearchBox();

            }
        });

        window.views.window_search_back_arrow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                D.d("clicked back icon in header");
                hideSearchBox();
                hideSearchContainer();
            }
        });

        window.views.searchbox_search_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                D.d("clicked search icon in header");
                performSearch();
            }
        });
    }

    void performSearch() {

        searchTerm = TextUtils.NullIfEmptyAndTrim(window.views.searchbox_input_field.getText().toString());

        if (searchTerm == null || searchTerm.length() < 1)
            return;

        searchFaqListResults.clear();

        boolean itemAdded = false;
        for (FaqsList item : faqList) {

            if (searchFaqListResults.size() >= AppConfiguration.getFAQMaxArticleSearchLimit())
                break;

            itemAdded = false;

            if (item.getTitle().toLowerCase().contains(searchTerm.toLowerCase())
                    || item.getTitle().toLowerCase().contains(TextUtils.removeDiacriticalMarks(searchTerm.toLowerCase()))
                    || TextUtils.removeDiacriticalMarks(item.getTitle().toLowerCase()).contains(searchTerm.toLowerCase())
                    || TextUtils.removeDiacriticalMarks(item.getTitle().toLowerCase()).contains(TextUtils.removeDiacriticalMarks(searchTerm.toLowerCase()))
                    )
             {
                searchFaqListResults.add(item.resetExpanded());
                itemAdded = true;
                continue;
            }

            if(itemAdded)
                continue;

            for (String keyWord : item.getKeywords()) {
                if (keyWord.toLowerCase().contains(searchTerm.toLowerCase())
                        || keyWord.toLowerCase().contains(TextUtils.removeDiacriticalMarks(searchTerm.toLowerCase()))
                        || TextUtils.removeDiacriticalMarks(keyWord.toLowerCase()).contains(searchTerm.toLowerCase())
                        || TextUtils.removeDiacriticalMarks(keyWord.toLowerCase()).contains(TextUtils.removeDiacriticalMarks(searchTerm.toLowerCase()))
                        )
                 {
                    searchFaqListResults.add(item.resetExpanded());
                    itemAdded = true;
                    break;
                }
            }

            if(itemAdded)
                continue;

            for (Content content : item.getContent()) {
                if (content.getType().equals("text")) {
                    if (content.getValue().toLowerCase().contains(searchTerm.toLowerCase())
                            || content.getValue().toLowerCase().contains(TextUtils.removeDiacriticalMarks(searchTerm.toLowerCase()))
                            || TextUtils.removeDiacriticalMarks(content.getValue().toLowerCase()).contains(searchTerm.toLowerCase())
                            || TextUtils.removeDiacriticalMarks(content.getValue().toLowerCase()).contains(TextUtils.removeDiacriticalMarks(searchTerm.toLowerCase()))
                            )
                     {
                        searchFaqListResults.add(item.resetExpanded());
                        itemAdded = true;
                        break;
                    }
                }
            }

            if(itemAdded)
                continue;

        }


        Collections.sort(searchFaqListResults, new Comparator<FaqsList>() {
            @Override
            public int compare(FaqsList o1, FaqsList o2) {
                return o1.getPriority() < o2.getPriority() ? 1 : -1;
            }
        });

        searchFaqRecyclerAdapter.setItemsList(searchFaqListResults);

        closeKeyboard();

        showSearchContainer();
    }

    void createAutoCompletePopup() {
        filteredKeywords = new ArrayList<>();
        filteredKeywords.addAll(allKeywords);

        autoCompleteAdapter = new ArrayAdapter<String>(window.activity, R.layout.support_faq_hints_spinner_item, filteredKeywords);

        autoCompletePopupWindow = new PopupWindow(window.activity);

        ListView listView = new ListView(window.activity);
        listView.setAdapter(autoCompleteAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isFromSelectedHint = true;
                window.views.searchbox_input_field.setText(autoCompleteAdapter.getItem(position));
                performSearch();

                if (autoCompletePopupWindow.isShowing()) {
                    autoCompletePopupWindow.dismiss();
                }
            }
        });


        autoCompletePopupWindow.setOutsideTouchable(true);

        autoCompletePopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.support_faq_hints_spinner_bg));
        autoCompletePopupWindow.setWidth(window.views.searchbox_input_field.getWidth());
        autoCompletePopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        autoCompletePopupWindow.setContentView(listView);

    }

    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindowToken(), 0);
    }

    void initSearchView() {

        showFAQSearchButton();
        /*ContainsFilterAutocompleteAdapter<String> adapter = new ContainsFilterAutocompleteAdapter<>(getContext(), allKeywords, 5, true);
        window.views.searchbox_input_field.setAdapter(adapter);

        window.views.searchbox_input_field.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                InputMethodManager in = (InputMethodManager) window.views.searchbox_input_field.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(window.views.searchbox_input_field.getApplicationWindowToken(), 0);
            }
        });*/

        window.views.searchbox_input_field.setHint(SupportLabels.getFAQSearchFieldHint());

        window.views.searchbox_input_field.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    performSearch();
                }
                return true;
            }
        });


        window.views.searchbox_input_field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (window.views.searchbox_input_field.getText().toString().length() > 0)
                    window.views.searchbox_input_field.setBackgroundresourceAndFieldIcon(R.drawable.gray_default_input_border, R.drawable.close_48);
                else
                    window.views.searchbox_input_field.setBackgroundresourceAndFieldIcon(R.drawable.gray_default_input_border);

                if (!isFromSelectedHint) {
                    final String filter = s.toString();

                    if (filter.length() >= 1) {
                        sThread.interrupt();


                        sThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                filteredKeywords.clear();

                                for (String keyword : allKeywords) {

                                    if (keyword.toLowerCase().startsWith(filter.toLowerCase())
                                            || keyword.toLowerCase().startsWith(TextUtils.removeDiacriticalMarks(filter.toLowerCase()))
                                            || TextUtils.removeDiacriticalMarks(keyword.toLowerCase()).startsWith(filter.toLowerCase())
                                            || TextUtils.removeDiacriticalMarks(keyword.toLowerCase()).startsWith(TextUtils.removeDiacriticalMarks(filter.toLowerCase()))
                                            ) {
                                        filteredKeywords.add(keyword);
                                    }

                                    if (filteredKeywords.size() >= AppConfiguration.getFAQMaxHintsLimit())
                                        break;
                                }

                                if (filteredKeywords.size() > 0) {
                                    autoCompleteAdapter.notifyDataSetChanged();

                                    window.activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (!autoCompletePopupWindow.isShowing()) {
                                                autoCompletePopupWindow.setWidth(window.views.searchbox_input_field.getWidth());
                                                autoCompletePopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

                                                final int[] location = new int[2];
                                                window.views.searchbox_input_field.getLocationInWindow(location);
                                                autoCompletePopupWindow.showAtLocation(window.activity.getWindow().getDecorView(), Gravity.TOP | Gravity.START, location[0], location[1] + window.views.searchbox_input_field.getHeight() + window.getStatusBarHeight());
                                            }
                                        }
                                    });


                                } else if (autoCompletePopupWindow.isShowing()) {
                                    window.activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            autoCompletePopupWindow.dismiss();
                                        }
                                    });
                                }

                            }
                        });
                        sThread.run();
                    } else {
                        window.activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                autoCompletePopupWindow.dismiss();
                            }
                        });
                    }

                } else {
                    isFromSelectedHint = false;
                }

            }
        });

        window.views.searchbox_input_field.setOnErrorIconClickListener(new OnErrorIconClickListener() {
            @Override
            public void onErrorIconClickListener() {
                searchTerm = TextUtils.NullIfEmptyAndTrim(window.views.searchbox_input_field.getText().toString());
            }
        });

        searchFaqRecyclerAdapter = new FAQsRecyclerAdapter(window);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(window.activity, LinearLayoutManager.VERTICAL, false);
        window.views.window_search_recyclerView.setLayoutManager(mLayoutManager);
        window.views.window_search_recyclerView.setAdapter(searchFaqRecyclerAdapter);


        window.views.window_search_no_results_hint.setText(SupportLabels.getFAQSearchResultsNoneForKeywordHint());

        window.views.window_search_no_results_hints_container.removeAllViews();
        for (final String keyword : popularKeywords) {
            VodafoneTextView hintTextView = new VodafoneTextView(getContext());

            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            llp.setMargins((int) getResources().getDimension(R.dimen.support_hint_top_margin), (int) getResources().getDimension(R.dimen.support_hint_top_margin),
                    (int) getResources().getDimension(R.dimen.support_hint_top_margin), (int) getResources().getDimension(R.dimen.support_hint_top_margin));

            hintTextView.setLayoutParams(llp);

            hintTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.medium_text_size));
            hintTextView.setFont(VodafoneTextView.TextStyle.VODAFONE_RG);
            hintTextView.setTextColor(getResources().getColor(R.color.white));
            hintTextView.setBackground(getResources().getDrawable(R.drawable.round_button_black));
            hintTextView.setPadding((int) getResources().getDimension(R.dimen.support_hint_left_padding), (int) getResources().getDimension(R.dimen.support_hint_top_padding),
                    (int) getResources().getDimension(R.dimen.support_hint_left_padding), (int) getResources().getDimension(R.dimen.support_hint_top_padding));

            hintTextView.setText(keyword);

            hintTextView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    isFromSelectedHint = true;
                    window.views.searchbox_input_field.setText(keyword);
                    performSearch();
                }
            });

            window.views.window_search_no_results_hints_container.addView(hintTextView);

        }
    }


    void showSearchBox() {
        window.views.triangle_view.setVisibility(View.VISIBLE);
        window.views.window_search_box.setVisibility(View.VISIBLE);

        window.views.window_search_top_delimiter.setVisibility(GONE);

        if (searchTerm != null && searchTerm.length() > 0)
            showSearchContainer();
        else
            hideSearchContainer();

        createAutoCompletePopup();
    }

    void hideSearchBox() {
        window.views.triangle_view.setVisibility(View.GONE);
        window.views.window_search_box.setVisibility(View.GONE);

        if (autoCompletePopupWindow.isShowing())
            autoCompletePopupWindow.dismiss();

        closeKeyboard();

        if(window.views.window_search_container.getVisibility() == VISIBLE) {
            window.views.window_search_top_delimiter.setVisibility(View.VISIBLE);
            window.views.triangle_view.setVisibility(View.VISIBLE);
        }
        else {
            window.views.window_search_top_delimiter.setVisibility(View.GONE);
            window.views.triangle_view.setVisibility(View.GONE);
        }

    }

    void showSearchContainer() {

        if (autoCompletePopupWindow.isShowing()) {
            autoCompletePopupWindow.dismiss();
        }

        window.views.window_search_container.setVisibility(VISIBLE);

        window.views.window_scrollable_tab_card.setVisibility(GONE);
        window.views.recyclerView.setVisibility(GONE);

        if (searchFaqListResults.size() > 0) {
            showSearchResult();
        } else {

            showSearchNoResults();
        }
    }

    void hideSearchContainer() {
        if(window.views.window_search_box.getVisibility() != VISIBLE) {
            window.views.triangle_view.setVisibility(View.GONE);
        }

        window.views.window_search_container.setVisibility(GONE);

        window.views.window_scrollable_tab_card.setVisibility(VISIBLE);
        window.views.recyclerView.setVisibility(VISIBLE);
    }

    void showSearchResult() {
        window.views.window_search_no_results_container.setVisibility(GONE);
        window.views.window_search_recyclerView.setVisibility(VISIBLE);

        window.views.window_search_back_arrow_textView.setText(String.format(SupportLabels.getFAQSearchResults(), searchFaqListResults.size()));
    }

    void showSearchNoResults() {
        window.views.window_search_no_results_container.setVisibility(VISIBLE);
        window.views.window_search_recyclerView.setVisibility(GONE);

        window.views.window_search_back_arrow_textView.setText(Html.fromHtml(SupportLabels.getFAQSearchResultsNoneForKeyword()));
        window.views.window_search_no_results_keyword.setText(searchTerm);
    }

    void downloadJSON() {
        window.showLoadingDialog();
        window.getChatService().getFAQJson().subscribe(new RequestSessionObserver<FaqPOJO>() {
            @Override
            public void onNext(FaqPOJO faqPojo) {
                D.d("ON NEXT");
                if (VodafoneController.isAnyActivityNotNull()) {
                    window.stopLoadingDialog();

					//region create contextual result
					String userRole = null;
					try {
						if (EbuMigratedIdentityController.getInstance() != null && EbuMigratedIdentityController.getInstance().getSelectedIdentity() != null) {
							userRole = EbuMigratedIdentityController.getInstance().getSelectedIdentity().getCrmRole();
						} else if (VodafoneController.getInstance().getUserProfile().getUserRole() == UserRole.HYBRID) {
							userRole = VodafoneController.getInstance().getUserProfile().getCustomerType();
						} else {
							userRole = VodafoneController.getInstance().getUserProfile().getUserRoleString();
						}

					} catch (Exception e) {
						userRole = "nonvfuser";
						e.printStackTrace();
					}

                    FaqPOJO result = new FaqPOJO();

                    result.setCachingTime(faqPojo.getCachingTime());

                    //region Popular Search Terms
                    List<String> popularSearchTerms = new ArrayList<>();


                    if(faqPojo.getTrendingSearches() == null || faqPojo.getTrendingSearches().isEmpty())
					{
                        popularSearchTerms.addAll(faqPojo.getPopularSearchTerms());
					}
					else
                    {
                        for(TrendingSearches trendingSearch : faqPojo.getTrendingSearches())
                        {
                            if(trendingSearch.getRole().equalsIgnoreCase(userRole))
                            {
                                popularSearchTerms.addAll(trendingSearch.getSearches());
                            }
                        }
                    }

                    result.setPopularSearchTerms(popularSearchTerms);
                    //endregion

					//region Articles
                    Set<String> categoriesSet = new TreeSet<>();
					List<FaqsList> faqs = new ArrayList<>();

					for(FaqsList faq : faqPojo.getFaqsList())
					{
						if(faq.getUserRoles() == null || faq.getUserRoles().isEmpty())
						{
							faqs.add(faq);
							categoriesSet.addAll(faq.getCategoryTags());
						}
						else
						{
							for (String string : faq.getUserRoles()) {
								if (string.equalsIgnoreCase(userRole)) {
									faqs.add(faq);
									categoriesSet.addAll(faq.getCategoryTags());
									break;
								}
							}
						}
					}

					result.setFaqsList(faqs);

					//endregion

                    //region Categories

					List<CategoriesList> categoriesLis = new ArrayList<>();

					for(CategoriesList category : faqPojo.getCategoriesList())
					{
						if(categoriesSet.contains(category.getName()))
							categoriesLis.add(category);
					}

                    result.setCategoriesList(categoriesLis);

                    //endregion

                    //endregion

                    initCategories(result.getCategoriesList());
                    initFaqs(result.getFaqsList());
                    initKeywords(result);

                    initSearchView();
                }

            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);

                try {
                    window.stopLoadingDialog();
                    isErrorDisplayed = true;
                    D.e("error = " + e);
                    e.printStackTrace();

                    if (window.views != null) {
                        window.views.window_title.setText("FAQs");
                    }
                    window.inflateError("Sistem momentan indisponibil. \nApasă pentru a reîncerca.", 0, refreshFAQClickListener, false);

                    if (window.views != null)
                        window.changeMenuItemCheckedStateColor(window.views.navigation);
                } catch (Exception er) {
                    er.printStackTrace();
                }
            }
        });
    }

    void initTabCard() {
        ArrayList<String> tabArray = new ArrayList<>();
        TabAdapter adapter = new TabAdapter(VodafoneController.currentActivity(), tabArray);
        window.views.window_scrollable_tab_card.setAdapter(adapter);
        window.views.window_scrollable_tab_card.unselectAll();

        window.views.window_scrollable_tab_card.setOnItemClickListener(new ScrollableTabAdapterOnItemClickListener(window.views.window_scrollable_tab_card) {
            @Override
            public void onItemClick(AdapterView<?> adapterView, final View view, int i, long l) {
                super.onItemClick(adapterView, view, i, l);

                window.views.window_scrollable_tab_card.post(new Runnable() {
                    @Override
                    public void run() {
                        window.views.window_scrollable_tab_card.scrollTo(view.getLeft(), 0);
                    }
                });

//                getDisplayedFaqsOrderedByPriority(categoriesList.get(i).getName());//todo okso reset adapter
                getDisplayedFaqsOrderedByPriority(categoriesList.get(i).getName());

                window.views.recyclerView.scrollToPosition(0);
                faqRecyclerAdapter.setItemsList(unmodifiedDisplayedFaqList);
            }
        });
    }

    void initRecyclerView() {
        categoriesList.clear();

        faqRecyclerAdapter = new FAQsRecyclerAdapter(window);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(window.activity, LinearLayoutManager.VERTICAL, false);
        window.views.recyclerView.setLayoutManager(mLayoutManager);
        window.views.recyclerView.setAdapter(faqRecyclerAdapter);
    }

    void initCategories(List<CategoriesList> categoriesList) {
        Collections.sort(categoriesList, new Comparator<CategoriesList>() {
            @Override
            public int compare(CategoriesList o1, CategoriesList o2) {
                if (!o1.getPriority().equals(o2.getPriority()))
                    return o1.getPriority() < o2.getPriority() ? 1 : -1;        //THIS CHANGES ORDER
                else
                    return o1.getName().compareTo(o2.getName());                //THIS CHANGES ORDER

            }
        });
//        D.showList(categoriesList);
        this.categoriesList = categoriesList;
        updateTabItems();
    }

    void updateTabItems() {
        ArrayList<String> tabArray = new ArrayList<>();

        for (CategoriesList cat : categoriesList)
            tabArray.add(cat.getName());

        TabAdapter adapter = new TabAdapter(VodafoneController.currentActivity(), tabArray);
        window.views.window_scrollable_tab_card.setAdapter(adapter);
        window.views.window_scrollable_tab_card.unselectAll();
        window.views.window_scrollable_tab_card.setHighlighted(0, true);
    }

    void initFaqs(List<FaqsList> faqList) {
        this.faqList = faqList;

        Collections.sort(faqList, new Comparator<FaqsList>() {
            @Override
            public int compare(FaqsList o1, FaqsList o2) {
                return o1.getPriority() < o2.getPriority() ? 1 : -1;
            }
        });

        getDisplayedFaqsOrderedByPriority(categoriesList.get(0).getName());

        faqRecyclerAdapter.setItemsList(unmodifiedDisplayedFaqList);
    }

    void initKeywords(FaqPOJO faqPojo) {
        SortedSet<String> internalSet = new TreeSet<>();

        popularKeywords.clear();
        internalSet.addAll(faqPojo.getPopularSearchTerms());
        popularKeywords.addAll(internalSet);

        allKeywords.clear();
        internalSet.clear();

        for (FaqsList faqItem : faqPojo.getFaqsList()) {
            internalSet.addAll(faqItem.getKeywords());
        }
        allKeywords.addAll(internalSet);

    }

    void getDisplayedFaqsOrderedByPriority(String categoryTitle) {
        List<FaqsList> lfl = new ArrayList<>();

        for (FaqsList faqlist : faqList)
            if (faqlist.getCategoryTags().contains(categoryTitle))
                lfl.add(faqlist.resetExpanded());// TODO THIS HERE BECAUSE WE DONT WANT TO KEEP THE CARDS EXPANDED , so we reset each one :
        //TODO bad behaviour: if faq1 belongs to cat1 and cat2 , when we switch from cat1 to cat2 and faq1 IS EXPANDED in cat1 it will ALSO BE EXPANDED in cat2

        Collections.sort(lfl, new Comparator<FaqsList>() {
            @Override
            public int compare(FaqsList o1, FaqsList o2) {
                return o1.getPriority() < o2.getPriority() ? 1 : -1;
            }
        });

        unmodifiedDisplayedFaqList = lfl;
    }


    @Override
    public View getFAQTextModel(String value) {
        LinearLayout textModel = new LinearLayout(VodafoneController.currentActivity());

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        lp.setMargins(sideMargins, upDownMargins, sideMargins, upDownMargins);
        textModel.setLayoutParams(lp);

        textModel.setBackgroundColor(ContextCompat.getColor(VodafoneController.currentActivity(), R.color.white));
        textModel.setOrientation(LinearLayout.VERTICAL);
        textModel.setMinimumHeight(30);

        VodafoneTextView tv = new VodafoneTextView(getContext());
        tv.setText(TextUtils.fromHtml(value));
        tv.setTextColor(ContextCompat.getColor(getContext(), R.color.blackNormal));
        tv.setTextSize(16);

        TextUtils.setTextViewClickableLinks(tv);
        tv.setGravity(View.TEXT_ALIGNMENT_TEXT_START);

        textModel.addView(tv);

        return textModel;
    }

    @Override
    public View getFAQImageModel(String value) {
        LinearLayout imageModel = new LinearLayout(VodafoneController.currentActivity());

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(sideMargins, upDownMargins, sideMargins, upDownMargins);
        lp.gravity = Gravity.CENTER;

        imageModel.setLayoutParams(lp);
        imageModel.setOrientation(LinearLayout.VERTICAL);

        ImageView iv = new ImageView(VodafoneController.currentActivity());

        ViewGroup.LayoutParams imageViewParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        iv.setMinimumHeight(ScreenMeasure.dpToPx(96));
        iv.setLayoutParams(imageViewParams);

        Glide.with(VodafoneController.currentActivity())
                .load(value)
                .placeholder(R.drawable.faq_image_placeholder)
                .into(iv);

        imageModel.addView(iv);
        imageModel.setGravity(Gravity.CENTER);
        return imageModel;
    }

    @Override
    public View getFAQVideoModel(String value) {
        LinearLayout videoModel = new LinearLayout(VodafoneController.currentActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        lp.setMargins(sideMargins, upDownMargins, sideMargins, upDownMargins);

        videoModel.setLayoutParams(lp);
        videoModel.setBackgroundColor(ContextCompat.getColor(VodafoneController.currentActivity(), R.color.blue_chart_top_color));
        videoModel.setOrientation(LinearLayout.VERTICAL);
        return videoModel;
    }

    @Override
    public View getFAQButtonModel() {
        VodafoneButton emailButton;
        emailButton = new VodafoneButton(getContext(), null, R.style.CardPrimaryButton);
        emailButton.setTransformationMethod(null);
        emailButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        emailButton.setTextColor(ContextCompat.getColor(getContext(), R.color.white_text_color));
        emailButton.setTypeface(Fonts.getVodafoneRG());
        emailButton.setBackgroundResource(R.drawable.selector_button_background_card_primary);
        emailButton.setText("Trimite email");
        emailButton.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(sideMargins, sideMargins, sideMargins, sideMargins);
        emailButton.setLayoutParams(params);

        emailButton.setMinimumHeight(ScreenMeasure.dpToPx(40));
        return emailButton;
    }
}