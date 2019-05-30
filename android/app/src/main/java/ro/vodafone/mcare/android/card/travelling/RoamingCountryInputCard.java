package ro.vodafone.mcare.android.card.travelling;


import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.card.VodafoneGenericCard;
import ro.vodafone.mcare.android.client.adapters.filters.ContainsFilterAutocompleteAdapter;
import ro.vodafone.mcare.android.client.model.realm.system.TravellingAboardLabels;
import ro.vodafone.mcare.android.custom.CustomAutoCompleteEditText;
import ro.vodafone.mcare.android.ui.utils.Fonts;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;

/**
 * Created by Prodan Pavel  on 29.01.2018.
 */

public class RoamingCountryInputCard extends VodafoneGenericCard {
	@BindView(R.id.country_input)
	CustomAutoCompleteEditText countryInput;
	@BindView(R.id.country_input_submit_button)
	VodafoneButton countryInputSubmitButton;
	@BindView(R.id.country_input_label)
	TextView countryInputLabel;

	List<String> countryList = null;

	private boolean isItemClicked = false;

	Thread sThread = new Thread(new Runnable() {
		@Override
		public void run() {

		}
	});

	public RoamingCountryInputCard(Context context) {
		super(context);
		init();
	}

	public RoamingCountryInputCard(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public RoamingCountryInputCard(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void  init(){
		ButterKnife.bind(this);
		countryInput.setBackgroundResource(R.drawable.gray_default_input_border);

		setCardsLabels();
	}

	@Override
	protected int setContent() {
		return R.layout.card_roaming_country_input;
	}

	public void setupPrefilledCountrySearch(String [] countryArrayList) {
		if(countryArrayList !=  null){
			countryList = new ArrayList<>(Arrays.asList(countryArrayList));
			ContainsFilterAutocompleteAdapter<String> adapter =
					new ContainsFilterAutocompleteAdapter<>(getContext(), countryList);
			countryInput.setAdapter(adapter);

			setupInputEventsListeners();
		}
	}

	private void setupInputEventsListeners(){
		countryInput.setOnKeyListener(inputOnKeyListener);
		countryInput.setOnFocusChangeListener(inputFocusChangeListener);
		countryInput.addTextChangedListener(countryInputTextWatcher);
		countryInput.setOnItemClickListener(onItemClickListener);
	}

	public void addSubmitButtonClickListener(OnClickListener clickListener){
		countryInputSubmitButton.setOnClickListener(clickListener);
	}

	private void setCardsLabels(){
		countryInputLabel.setText(TravellingAboardLabels.getTravelling_aboard_country_input_label());
		countryInput.setHint(TravellingAboardLabels.getTravelling_aboard_country_input_hint());
		countryInputSubmitButton.setText(TravellingAboardLabels.getTravelling_aboard_country_input_submit_button());
		countryInputSubmitButton.setTypeface(Fonts.getVodafoneRG());
	}

	public boolean isInputValid(){
		return countryInput.getText() != null && !countryInput.getText().toString().equals("");
	}

	public String getSearchedCountry(){
		return countryInput.getText().toString();
	}

	public CustomAutoCompleteEditText getCountryInput() {
		return countryInput;
	}

	OnKeyListener inputOnKeyListener = new OnKeyListener() {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
				//The country inserting are made within onFocusChangeListener
				countryInput.clearFocus();
				return true;
			}
			return false;
		}
	};

	OnFocusChangeListener inputFocusChangeListener = new OnFocusChangeListener() {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (!hasFocus && !isItemClicked) {
				String firstItem = "";
				if (((ContainsFilterAutocompleteAdapter) countryInput.getAdapter()).getFirstItem() != null) {
					firstItem = (String) ((ContainsFilterAutocompleteAdapter) countryInput.getAdapter()).getFirstItem();
					countryInput.setText(firstItem);
					countryInput.setSelection(countryInput.getText().length());

					if (!countryInput.getText().toString().equals(""))
						countryInputSubmitButton.setEnabled(true);

					if(getContext()!=null){
						InputMethodManager in = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
						in.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
					}
					countryInput.dismissDropDown();
				}
			} else if(hasFocus)
				isItemClicked = false;
		}
	};

	TextWatcher countryInputTextWatcher = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			try {
				sThread.interrupt();

				if (countryInput.getText().toString().equals("")) {
					countryInputSubmitButton.setEnabled(false);
				} else {
					sThread = new Thread(new Runnable() {
						@Override
						public void run() {
							if(countryList != null){
								for (int i = 0; i < countryList.size(); i++)
									if (countryList.get(i).equals(countryInput.getText().toString())) {
										countryInputSubmitButton.setEnabled(true);
										break;
									} else {
										countryInputSubmitButton.setEnabled(false);
									}
							}
						}
					});
					sThread.run();

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			isItemClicked = true;

			if (getContext() != null) {
				InputMethodManager in = (InputMethodManager) parent.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				in.hideSoftInputFromWindow(parent.getApplicationWindowToken(), 0);
			}
			if (!countryInput.getText().toString().equals(""))
				countryInputSubmitButton.setEnabled(true);
			countryInput.clearFocus();
		}
	};
}
