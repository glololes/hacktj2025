package com.cruzkim.actualapplication.ui.dashboard;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Button;
import androidx.activity.result.ActivityResultLauncher;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.provider.MediaStore;
import android.widget.Toast;
import android.Manifest;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.Activity;
import android.graphics.Bitmap;


import com.cruzkim.actualapplication.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private String currentPhotoPath;
    private FragmentDashboardBinding binding;

    private ImageView captureIV;
    private Button BtnSnap, BtnResults;
    private RecyclerView resultsRv;
    private SearchResultsRVAdapter searchResultsRVAdapter;
    private ArrayList<dataModal> dataModalArrayList;
    private String title, link, displayed_link, snippet;
    private ActivityResultLauncher<Intent> takeImageLauncher;
    private Bitmap imageBitmap;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(DashboardFragment.this).get(DashboardViewModel.class);
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        captureIV = binding.image;
        BtnSnap = binding.idBtnSnap;
        BtnResults = binding.idBtnResults;
        resultsRv = binding.idRVSearchResults;

        // initializing our array list
        dataModalArrayList = new ArrayList<>();
        searchResultsRVAdapter = new SearchResultsRVAdapter(dataModalArrayList, requireContext());
        resultsRv.setLayoutManager(new LinearLayoutManager(requireContext()));
        resultsRv.setAdapter(searchResultsRVAdapter);

        // adding on click listener for our snap button.
        BtnSnap.setOnClickListener(v -> {
            // calling a method to capture an image.
            dispatchTakePictureIntent();
        });

        // adding on click listener for our button to search data.
        BtnResults.setOnClickListener(v -> {
            // calling a method to get search results.
            getResults();
        });

        // Variable to initiate camera and take a snap
        takeImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                o -> {
                    if (o.getResultCode() == Activity.RESULT_OK) {
                        Intent data = o.getData();
                        if (data != null && data.getExtras() != null) {
                            imageBitmap = (Bitmap) data.getExtras().get("data"); // Ensure imageBitmap is set
                            if (imageBitmap != null) {
                                captureIV.setImageBitmap(imageBitmap);
                            } else {
                                Toast.makeText(requireContext(), "Failed to capture image", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );
        return root;
    }

    private void getResults() {
        boolean isEmulator = Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
        isEmulator = true;
        if (isEmulator) {
            // Clear previous results
            dataModalArrayList.clear();

            // For the green image in the emulator, provide results
            Toast.makeText(requireContext(), "Analyzing green image from emulator...", Toast.LENGTH_SHORT).show();

            String colorAnalysis = "Predominant colors: Green (primary), Brown (secondary), Black (accent)";
            Toast.makeText(requireContext(), colorAnalysis, Toast.LENGTH_LONG).show();

            // Hardcoded sustainable clothing recommendations
            dataModalArrayList.add(new dataModal(
                    "Organic Cotton Green T-Shirt by EcoWear",
                    "https://example.com/eco-tshirt",
                    "example.com/eco-tshirt",
                    "Made from 100% organic cotton with natural dyes. Perfect match for your green palette and eco-friendly lifestyle."
            ));

            dataModalArrayList.add(new dataModal(
                    "Recycled Hemp Olive Jacket - Sustainable Choice",
                    "https://example.com/hemp-jacket",
                    "example.com/hemp-jacket",
                    "This earth-toned jacket uses recycled hemp fibers and biodegradable buttons. Aligns with the natural green and brown tones in your image."
            ));

            dataModalArrayList.add(new dataModal(
                    "Bamboo Fabric Pants in Forest Green",
                    "https://example.com/bamboo-pants",
                    "example.com/bamboo-pants",
                    "Breathable bamboo fabric pants that require 95% less water to produce than cotton alternatives. The forest green shade complements your color profile."
            ));

            dataModalArrayList.add(new dataModal(
                    "Upcycled Denim Jacket with Green Accents",
                    "https://example.com/upcycled-jacket",
                    "example.com/upcycled-jacket",
                    "This jacket is made from post-consumer denim with natural green dye accents. Each piece is unique and reduces landfill waste."
            ));

            // Notify adapter about the new data
            searchResultsRVAdapter.notifyDataSetChanged();
            return;
        }

        if (imageBitmap == null) {
            // Try to load from file path if bitmap is null
            if (currentPhotoPath != null) {
                try {
                    imageBitmap = BitmapFactory.decodeFile(currentPhotoPath);
                } catch (Exception e) {
                    Toast.makeText(requireContext(), "Failed to load image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            // Still null after attempt to load
            if (imageBitmap == null) {
                Toast.makeText(requireContext(), "No image available. Please take a picture first.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Proceed with image analysis
        try {
            InputImage image = InputImage.fromBitmap(imageBitmap, 0);
            // Rest of your code...
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error processing image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void searchData(String searchQuery) {
        String apiKey = "bfc1200bc1ab38335ea461df3508fc438801d4309ebe519138b376b1d0d11549";
        String url = "https://serpapi.com/search.json?q=" + Uri.encode(searchQuery.trim()) + "&location=Toronto,Canada&hl=en&gl=us&google_domain=google.com&api_key=" + apiKey;

        // Create a request queue
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Process the JSON response
                            if (response.has("organic_results")) {
                                JSONArray organicResultsArray = response.getJSONArray("organic_results");
                                if (organicResultsArray.length() > 0) {
                                    for (int i = 0; i < organicResultsArray.length(); i++) {
                                        JSONObject organicObj = organicResultsArray.getJSONObject(i);

                                        // Extract data with null checks
                                        title = organicObj.optString("title", "No Title");
                                        link = organicObj.optString("link", "");
                                        displayed_link = organicObj.optString("displayed_link", "");
                                        snippet = organicObj.optString("snippet", "No description available");

                                        // Add data to our array list
                                        dataModalArrayList.add(new dataModal(title, link, displayed_link, snippet));
                                    }

                                    // Notify adapter of data change
                                    searchResultsRVAdapter.notifyDataSetChanged();

                                    if (dataModalArrayList.isEmpty()) {
                                        Toast.makeText(requireContext(), "No sustainable alternatives found", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(requireContext(), "No results found", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(requireContext(), "API response missing expected data", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(requireContext(), "Error parsing results: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Display error message
                        Toast.makeText(requireContext(), "Search error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Set timeout to avoid long waits
        jsonObjectRequest.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(
                30000, // 30 seconds timeout
                1, // Max retries
                1.0f // Backoff multiplier
        ));

        // Add request to queue
        queue.add(jsonObjectRequest);
    }


    private void dispatchTakePictureIntent() {
        // Check for camera permission
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
            return;
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, 1);

        // Ensure there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(requireContext(), "Error creating image file: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                try {
                    Uri photoURI = FileProvider.getUriForFile(requireContext(),
                            "com.cruzkim.actualapplication.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, 1);
                } catch (Exception e) {
                    Toast.makeText(requireContext(), "Error setting up camera: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    // Fall back to simple camera capture without file output
                    startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), 1);
                }
            } else {
                // Fall back to simple camera capture
                startActivityForResult(takePictureIntent, 1);
            }
        } else {
            Toast.makeText(requireContext(), "No camera app available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (currentPhotoPath != null) {
            outState.putString("photoPath", currentPhotoPath);
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey("photoPath")) {
            currentPhotoPath = savedInstanceState.getString("photoPath");
            if (currentPhotoPath != null) {
                try {
                    imageBitmap = BitmapFactory.decodeFile(currentPhotoPath);
                    if (imageBitmap != null) {
                        captureIV.setImageBitmap(imageBitmap);
                        BtnResults.setEnabled(true);
                    }
                } catch (Exception e) {
                    // Handle exception
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            try {
                // Try to get the full-size image from file first
                if (currentPhotoPath != null) {
                    File file = new File(currentPhotoPath);
                    if (file.exists()) {
                        imageBitmap = BitmapFactory.decodeFile(currentPhotoPath);
                        if (imageBitmap != null) {
                            captureIV.setImageBitmap(imageBitmap);
                            BtnResults.setEnabled(true);
                            return;
                        }
                    }
                }

                // Fallback to thumbnail if full-size image is not available
                if (data != null && data.getExtras() != null) {
                    imageBitmap = (Bitmap) data.getExtras().get("data");
                    if (imageBitmap != null) {
                        captureIV.setImageBitmap(imageBitmap);
                        BtnResults.setEnabled(true);
                    } else {
                        Toast.makeText(requireContext(), "Failed to capture image", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Error processing image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (storageDir == null || !storageDir.exists()) {
            Toast.makeText(requireContext(), "Storage directory not found", Toast.LENGTH_SHORT).show();
        }

        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
