package com.example.foodmeup.ui.maps;

import com.example.foodmeup.ui.base.BaseView;


public interface MapsActivityView extends BaseView {

    void hideAdressShowVenues();

    void hideVenuesShowAddress();

    void hideVenues();

    void setAdressText(String address);

    void messageNoVenuesAvailable();

    void hideVenueButton();

    void messagePressExit();

    void showVenueButton();

}
