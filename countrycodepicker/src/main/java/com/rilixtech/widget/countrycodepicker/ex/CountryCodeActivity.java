package com.rilixtech.widget.countrycodepicker.ex;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rilixtech.widget.countrycodepicker.Country;
import com.rilixtech.widget.countrycodepicker.CountryUtils;
import com.rilixtech.widget.countrycodepicker.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Dialog for selecting Country.
 *
 * Created by Joielechong on 11 May 2017.
 */
public class CountryCodeActivity extends Activity {
  private static final String TAG = "CountryCodeDialog";

  private EditText mEdtSearch;
  private TextView mTvNoResult;
  private TextView mTvTitle;
  private ListView mLvCountryDialog;
  private RelativeLayout mRlyDialog;

  private List<Country> masterCountries;
  private List<Country> mFilteredCountries;
  private InputMethodManager mInputMethodManager;
  private CountryCodeAdapter mArrayAdapter;
  private List<Country> mTempCountries;

  public static final String SELECT_COUNTRY = "select_country";

  private Country mSelectCountry;
  private List<Country> mPreferredCountries;
  //this will be "AU,ID,US"
  private String mCountryPreference;
  private List<Country> mCustomMasterCountriesList;
  //this will be "AU,ID,US"
  private String mCustomMasterCountries;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.country_code_picker_layout_picker_activity);
    setupUI();
    setupData();
  }

  public void finishCurrentActivity(View view){
    finish();
  }

  private void setupUI() {
    mRlyDialog = findViewById(R.id.dialog_rly);
    mLvCountryDialog = findViewById(R.id.country_dialog_lv);
    mTvTitle = findViewById(R.id.title_tv);
    mEdtSearch = findViewById(R.id.search_edt);
    mTvNoResult = findViewById(R.id.no_result_tv);
  }

  private void setupData() {
    refreshCustomMasterList();
    refreshPreferredCountries();
    masterCountries = getCustomCountries();

    mFilteredCountries = getFilteredCountries();
    setupListView(mLvCountryDialog);

    mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    setSearchBar();
  }

  private void setupListView(ListView listView) {
    mArrayAdapter = new CountryCodeAdapter(this, mFilteredCountries);

    OnItemClickListener listener = new OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mFilteredCountries == null) {
          Log.e(TAG, "no filtered countries found! This should not be happened, Please report!");
          return;
        }

        if (mFilteredCountries.size() < position || position < 0) {
          Log.e(TAG, "Something wrong with the ListView. Please report this!");
          return;
        }

        Country country = mFilteredCountries.get(position);
        /* view is only a separator, so the country is null and we ignore it.
         see {@link #getFilteredCountries(String)} */
        if (country == null) return;

        mInputMethodManager.hideSoftInputFromWindow(mEdtSearch.getWindowToken(), 0);
        mSelectCountry = country;
        Intent result = getIntent();
        result.putExtra(SELECT_COUNTRY, mSelectCountry);
        setResult(RESULT_OK,result);
        finish();
      }
    };
    listView.setOnItemClickListener(listener);
    listView.setAdapter(mArrayAdapter);
  }

  private void refreshCustomMasterList() {
    if (mCustomMasterCountries == null || mCustomMasterCountries.length() == 0) {
      mCustomMasterCountriesList = null;
      return;
    }

    List<Country> localCountries = new ArrayList<>();
    String[] split = mCustomMasterCountries.split(",");
    for (int i = 0; i < split.length; i++) {
      String nameCode = split[i];
      Country country = CountryUtils.getByNameCodeFromAllCountries(this, nameCode);
      if (country == null) continue;
      //to avoid duplicate entry of country
      if (isAlreadyInList(country, localCountries)) continue;
      localCountries.add(country);
    }

    if (localCountries.size() == 0) {
      mCustomMasterCountriesList = null;
    } else {
      mCustomMasterCountriesList = localCountries;
    }
  }

  private boolean isAlreadyInList(Country country, List<Country> countries) {
    if (country == null || countries == null) return false;

    for (int i = 0; i < countries.size(); i++) {
      if (countries.get(i).getIso().equalsIgnoreCase(country.getIso())) {
        return true;
      }
    }

    return false;
  }


  private List<Country> getCustomCountries() {
    refreshCustomMasterList();
    return CountryUtils.getAllCountries(this);
  }

  private void refreshPreferredCountries() {
    if (mCountryPreference == null || mCountryPreference.length() == 0) {
      mPreferredCountries = null;
      return;
    }

    List<Country> localCountryList = new ArrayList<>();
    for (String nameCode : mCountryPreference.split(",")) {
      Country country =
              CountryUtils.getByNameCodeFromCustomCountries(this, mCustomMasterCountriesList,
                      nameCode);
      if (country == null) continue;
      //to avoid duplicate entry of country
      if (isAlreadyInList(country, localCountryList)) continue;
      localCountryList.add(country);
    }

    if (localCountryList.size() == 0) {
      mPreferredCountries = null;
    } else {
      mPreferredCountries = localCountryList;
    }
  }

  public List<Country> getPreferredCountries() {
    return mPreferredCountries;
  }

  private int adjustAlpha(int color, float factor) {
    int alpha = Math.round(Color.alpha(color) * factor);
    int red = Color.red(color);
    int green = Color.green(color);
    int blue = Color.blue(color);
    return Color.argb(alpha, red, green, blue);
  }

  private void setSearchBar() {
      setTextWatcher();
  }

  /**
   * add textChangeListener, to apply new query each time editText get text changed.
   */
  private void setTextWatcher() {
    if (mEdtSearch == null) return;

    mEdtSearch.addTextChangedListener(new TextWatcher() {

      @Override public void afterTextChanged(Editable s) {
      }

      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
        applyQuery(s.toString());
      }
    });

    // 收起软键盘
    if (mInputMethodManager != null) {
      mInputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
  }

  /**
   * Filter country list for given keyWord / query.
   * Lists all countries that contains @param query in country's name, name code or phone code.
   *
   * @param query : text to match against country name, name code or phone code
   */
  private void applyQuery(String query) {
    mTvNoResult.setVisibility(View.GONE);
    query = query.toLowerCase();

    //if query started from "+" ignore it
    if (query.length() > 0 && query.charAt(0) == '+') {
      query = query.substring(1);
    }

    mFilteredCountries = getFilteredCountries(query);

    if (mFilteredCountries.size() == 0) {
      mTvNoResult.setVisibility(View.VISIBLE);
    }

    mArrayAdapter.notifyDataSetChanged();
  }

  private List<Country> getFilteredCountries() {
    return getFilteredCountries("");
  }

  private List<Country> getFilteredCountries(String query) {
    if (mTempCountries == null) {
      mTempCountries = new ArrayList<>();
    } else {
      mTempCountries.clear();
    }

    List<Country> preferredCountries = getPreferredCountries();
    if (preferredCountries != null && preferredCountries.size() > 0) {
      for (Country country : preferredCountries) {
        if (country.isEligibleForQuery(query)) {
          mTempCountries.add(country);
        }
      }

      if (mTempCountries.size() > 0) { //means at least one preferred country is added.
        mTempCountries.add(null); // this will add separator for preference countries.
      }
    }

    for (Country country : masterCountries) {
      if (country.isEligibleForQuery(query)) {
        mTempCountries.add(country);
      }
    }
    return mTempCountries;
  }
}
