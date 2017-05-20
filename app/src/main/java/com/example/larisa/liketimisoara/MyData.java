package com.example.larisa.liketimisoara;

import android.content.Intent;

/**
 * Created by Larisa on 2/20/2017.
 */

public class MyData {

    public static Integer[] nameArray = { R.string.museum_card, R.string.squares_card, R.string.parks_card, R.string.church_card, R.string.restaurants_card, R.string.cafes_card, R.string.clubs_card, R.string.hotels_card, R.string.hostels_cards, R.string.guest_house_card };

    public static Integer[] drawableArray = { R.drawable.muzeu,
            R.drawable.piata, R.drawable.parc, R.drawable.biserica, R.drawable.restaurant,
            R.drawable.cafenea, R.drawable.club, R.drawable.hotel, R.drawable.hostel, R.drawable.pensiune };

    public static Integer[] id_ = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};

    public static AttractionType[] attractionTypeArray = {AttractionType.MUZEU, AttractionType.PIATA, AttractionType.PARC, AttractionType.CATEDRALA, AttractionType.RESTAURANT, AttractionType.CAFENEA, AttractionType.CLUB, AttractionType.HOTEL, AttractionType.HOSTEL, AttractionType.PENSIUNE};
}
