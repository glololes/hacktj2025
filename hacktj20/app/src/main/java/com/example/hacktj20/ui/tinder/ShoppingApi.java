package com.example.hacktj20.ui.tinder;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ShoppingApi {
    @GET("search")
    Call<ShoppingResponse> getShoppingResults(
            @Query("q") String query, // Search query
            @Query("location") String location, // Location for localized results
            @Query("hl") String language, // Language
            @Query("gl") String country, // Country
            @Query("api_key") String apiKey // Your SerpApi API key
    );
}