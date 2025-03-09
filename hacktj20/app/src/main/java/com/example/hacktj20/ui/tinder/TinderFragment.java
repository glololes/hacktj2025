package com.example.hacktj20.ui.tinder;

import androidx.lifecycle.ViewModelProvider;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hacktj20.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TinderFragment extends Fragment {

    private TinderViewModel mViewModel;
    private FrameLayout swipeContainer;
    private List<ShoppingResult> shoppingResults;
    private ImageButton likeButton, dislikeButton;
    private TextView profilePrice, profileName, susLevel;
    public String[] profileNameLst, profilePriceLst, susLevelLst;
    private Integer currentNum;

    public static TinderFragment newInstance() {
        return new TinderFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tinder, container, false);

        swipeContainer = view.findViewById(R.id.swipeContainer);
        profileName = view.findViewById(R.id.profileName);
        profilePrice = view.findViewById(R.id.profilePrice);
        susLevel = view.findViewById(R.id.sustainabilityLevel);
        dislikeButton = view.findViewById(R.id.dislikeButton);
        likeButton = view.findViewById(R.id.likeButton);
        profileNameLst = new String[]{"Green Cotton Linen Dress", "Green Jacket", "Green Linen Jacket", "Green Trench Coat", "Green Trousers"};
        profilePriceLst = new String[]{"$29.99", "$39.99", "$69.00", "$59.99", "$24.99"};
        susLevelLst = new String[]{"102", "105", "95", "84", "73"};
        currentNum = 0;
/*
        Request.Builder builder = new Request.Builder();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),"");
        Request request=builder
                */
        //        .url("https://serpapi.com/search?q=eco-friendly-clothing-green&api_key=0d932665ebf5a35ea5831054a74fe96c3b9642827164485a9df897635af49b58")
        //       .post(requestBody)
        //        .build();
        //OkHttpClient client = new OkHttpClient().newBuilder().build();

        //try {
        //    okhttp3.Response response = client.newCall(request).execute();
        //    assert request.body() != null;
        //    profileName.setText(request.body().toString());
        //} catch (IOException e) {
        //    e.printStackTrace();
        //}
        //fetchShoppingResults("eco-friendly-green-clothing");
        // Add the first card to the swipe container
        addCard();

        return view;
    }

    private void fetchShoppingResults(String query) {
        ShoppingApi api = ApiClient.getClient().create(ShoppingApi.class);
        Call<ShoppingResponse> call = api.getShoppingResults(
                query, // Search query
                "United States", // Location
                "en", // Language
                "us", // Country
                "0d932665ebf5a35ea5831054a74fe96c3b9642827164485a9df897635af49b58" // Replace with your SerpApi API key
        );
        String url = "https://serpapi.com/search.json?q=" + Uri.encode(query.trim()) + "&location=UnitedStates&hl=en&gl=us&google_domain=google.com&api_key=" + "0d932665ebf5a35ea5831054a74fe96c3b9642827164485a9df897635af49b58";

        call.enqueue(new Callback<ShoppingResponse>() {
            @Override
            public void onResponse(Call<ShoppingResponse> call, Response<ShoppingResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    shoppingResults = response.body().getShoppingResults();
                    updateUI();
                } else {
                    Log.e("API Error", "Failed to fetch shopping results");
                }
            }

            @Override
            public void onFailure(Call<ShoppingResponse> call, Throwable t) {
                Log.e("API Error", "Network error: " + t.getMessage());
            }
        });
    }

    private void updateUI() {
        if (shoppingResults != null && !shoppingResults.isEmpty()) {
            ShoppingResult result = shoppingResults.get(0);

            // Update the profile details
            profileName.setText(result.getTitle());
            profilePrice.setText("Price: " + result.getPrice());
            susLevel.setText("Source: " + result.getSource());

            // Load the thumbnail image (use Glide or Picasso)
            // Glide.with(this).load(result.getThumbnail()).into(profileImage);
        }
    }

    private void addCard() {
        View cardView = getLayoutInflater().inflate(R.layout.item_card, swipeContainer, false);
        swipeContainer.addView(cardView);
        profileName.setText(profileNameLst[currentNum%profileNameLst.length]);
        profilePrice.setText(profilePriceLst[currentNum%profilePriceLst.length]);
        susLevel.setText("Sustainability Level: "+ susLevelLst[currentNum%susLevelLst.length]);
        ImageView curImg;
        curImg = cardView.findViewById(R.id.productImage);
        if (currentNum%5 == 0) {
            curImg.setImageResource(R.drawable.green_cotton_linen_dress);
        }
        else if (currentNum%5 == 1) {
            curImg.setImageResource(R.drawable.green_jacket);
        }
        else if (currentNum%5 == 2) {
            curImg.setImageResource(R.drawable.green_linen_jacket);
        }
        else if (currentNum%5 == 3) {
            curImg.setImageResource(R.drawable.green_trousers);
        }
        else if (currentNum%5 == 4) {
            curImg.setImageResource(R.drawable.green_trench_coat);
        }
        currentNum += 1;

        // Animate the card's entry
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_up);
        cardView.startAnimation(animation);

        // Set up swipe gestures
        cardView.setOnTouchListener(new OnSwipeTouchListener(getContext()) {
            @Override
            public void onSwipeLeft() {
                animateSwipeLeft(cardView);
                removeCard(cardView);
            }

            @Override
            public void onSwipeRight() {
                animateSwipeRight(cardView);
                removeCard(cardView);
            }
        });
    }

    private void removeCard(View cardView) {
        swipeContainer.removeView(cardView);
        addCard(); // Add a new card after swiping
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(TinderViewModel.class);
        // TODO: Use the ViewModel
    }

}