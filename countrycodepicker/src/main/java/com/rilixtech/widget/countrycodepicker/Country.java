package com.rilixtech.widget.countrycodepicker;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hbb20 on 11/1/16.
 *
 * Clean up and moving all Country related code to {@link CountryUtils}.
 * a pojo should be a pojo and no more.
 * Updated by Joielechong 13 May 2017
 *
 * Make class member final and remove setter.
 * Updated by Joielechong 22 August 2018
 *
 */
public class Country implements Parcelable {
  private final String iso;
  private final String phoneCode;
  private final String name;

  public Country(String iso, String phoneCode, String name) {
    this.iso = iso;
    this.phoneCode = phoneCode;
    this.name = name;
  }

  protected Country(Parcel in) {
    iso = in.readString();
    phoneCode = in.readString();
    name = in.readString();
  }

  public static final Creator<Country> CREATOR = new Creator<Country>() {
    @Override
    public Country createFromParcel(Parcel in) {
      return new Country(in);
    }

    @Override
    public Country[] newArray(int size) {
      return new Country[size];
    }
  };

  public String getIso() {
    return iso;
  }

  public String getPhoneCode() {
    return phoneCode;
  }

  public String getName() {
    return name;
  }

  /**
   * If country have query word in name or name code or phone code, this will return true.
   */
  public boolean isEligibleForQuery(String query) {
    query = query.toLowerCase();
    return getName().toLowerCase().contains(query)
        || getIso().toLowerCase().contains(query)
        || getPhoneCode().toLowerCase().contains(query);
  }


  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel parcel, int i) {
    parcel.writeString(iso);
    parcel.writeString(phoneCode);
    parcel.writeString(name);
  }
}
