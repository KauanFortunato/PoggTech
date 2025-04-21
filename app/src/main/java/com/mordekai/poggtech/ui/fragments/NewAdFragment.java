package com.mordekai.poggtech.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import static android.app.Activity.RESULT_OK;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.Manifest;
import android.widget.TextView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;
import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.adapter.PlaceSuggestionAdapter;
import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.Category;
import com.mordekai.poggtech.data.model.User;
import com.mordekai.poggtech.data.remote.ApiProduct;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.domain.ProductManager;
import com.mordekai.poggtech.utils.SharedPrefHelper;
import com.mordekai.poggtech.utils.SnackbarUtil;
import com.mordekai.poggtech.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class NewAdFragment extends Fragment {

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    private BottomNavigationView bottomNavigationView;
    private EditText nomeUser, emailUser, contactUser, titleProduct, descriptionProduct;
    private TextView titleProductLabelCount, descriptionProductLabelCount;
    private ImageButton closeButton;
    private Spinner classSelectorSpinner;
    private ProductManager productManager;
    private User user;

    private Button addImagesButton, addImagesButtonAgain;
    private ArrayList<Uri> selectedImageUris = new ArrayList<>();
    private LinearLayout layoutImageExamples, layoutUserImages, imagePreviewContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_ad, container, false);
        SharedPrefHelper sharedPrefHelper = new SharedPrefHelper(requireContext());

        user = sharedPrefHelper.getUser();

        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), getString(R.string.google_maps_key));
        }

        productManager = new ProductManager(RetrofitClient.getRetrofitInstance().create(ApiProduct.class));

        startComponents(view);

        getClassList();

        addImagesButton.setOnClickListener(v -> {
            String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                    ? Manifest.permission.READ_MEDIA_IMAGES
                    : Manifest.permission.READ_EXTERNAL_STORAGE;

            if (ContextCompat.checkSelfPermission(requireContext(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{permission}, PERMISSION_CODE);
            } else {
                pickImagesFromGallery();
            }
        });

        addImagesButtonAgain.setOnClickListener(v -> {
            String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                    ? Manifest.permission.READ_MEDIA_IMAGES
                    : Manifest.permission.READ_EXTERNAL_STORAGE;

            if (ContextCompat.checkSelfPermission(requireContext(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{permission}, PERMISSION_CODE);
            } else {
                pickImagesFromGallery();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                String address = place.getAddress();

                EditText localProductEditText = requireView().findViewById(R.id.localProduct);
                localProductEditText.setText(address);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.e("PLACE_ERROR", status.getStatusMessage());
            }
        }

        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUris.clear();

            layoutImageExamples.setVisibility(View.GONE);
            layoutUserImages.setVisibility(View.VISIBLE);
            imagePreviewContainer.removeAllViews();

            int maxImages = 6;

            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();

                if (count > maxImages) {
                    SnackbarUtil.showErrorSnackbar(requireView(), "Só podes adicionar até 6 fotos.", requireContext());
                    count = maxImages;
                }

                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    selectedImageUris.add(imageUri);
                    addImageToLayout(imageUri);
                }
            } else if (data.getData() != null) {
                selectedImageUris.add(data.getData());
                addImageToLayout(data.getData());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImagesFromGallery();
            } else {
                SnackbarUtil.showErrorSnackbar(requireView(), "Permissão negada para aceder às imagens", requireContext());
            }
        }
    }

    private void pickImagesFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Selecionar imagens"), IMAGE_PICK_CODE);
    }

    private void addImageToLayout(Uri imageUri) {
        ShapeableImageView imageView = new ShapeableImageView(requireContext());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(250, 250);
        params.setMargins(8, 8, 8, 8);
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageURI(imageUri);

        float radius = 15f;
        imageView.setShapeAppearanceModel(
                imageView.getShapeAppearanceModel()
                        .toBuilder()
                        .setAllCorners(CornerFamily.ROUNDED, radius)
                        .build()
        );

        imagePreviewContainer.addView(imageView);
    }

    private void startComponents(View view) {
        final boolean[] shouldIgnoreTextChange = {false};

        closeButton = view.findViewById(R.id.closeBtn);
        classSelectorSpinner = view.findViewById(R.id.classSelector);
        layoutImageExamples = view.findViewById(R.id.layoutImageExamples);
        layoutUserImages = view.findViewById(R.id.layoutUserImages);
        addImagesButton = view.findViewById(R.id.addImagesButton);
        imagePreviewContainer = view.findViewById(R.id.imagePreviewContainer);
        addImagesButtonAgain = view.findViewById(R.id.addImagesButtonAgain);
        addImagesButton = view.findViewById(R.id.addImagesButton);
        nomeUser = view.findViewById(R.id.nomeUser);
        emailUser = view.findViewById(R.id.emailUser);
        contactUser = view.findViewById(R.id.contactUser);
        titleProduct = view.findViewById(R.id.titleProduct);
        descriptionProduct = view.findViewById(R.id.descriptionProduct);
        titleProductLabelCount = view.findViewById(R.id.titleProductLabelCount);
        descriptionProductLabelCount = view.findViewById(R.id.descriptionProductLabelCount);

        EditText localProductEditText = view.findViewById(R.id.localProduct);
        RecyclerView locationRecycler = view.findViewById(R.id.locationSuggestionsRecycler);

        emailUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(emailUser.getText())) {
                    emailUser.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getContext(), R.drawable.ic_check), null);
                } else {
                    emailUser.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        emailUser.setText(user.getEmail());
        nomeUser.setText(user.getName());
        contactUser.setText(user.getPhone());

        descriptionProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int currentLength = s.length();

                if (currentLength > 100) {
                    titleProduct.setError("Máximo de 9000 caracteres");
                    currentLength = 100;
                }
                descriptionProductLabelCount.setText(currentLength + "/" + 9000);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        descriptionProduct.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if(TextUtils.isEmpty(descriptionProduct.getText().toString())) {
                    descriptionProduct.setError("Campo obrigatório");
                }
            }
        });

        titleProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int currentLength = s.length();

                if (currentLength > 100) {
                    titleProduct.setError("Máximo de 100 caracteres");
                    currentLength = 100;
                }
                titleProductLabelCount.setText(currentLength + "/" + 100);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        titleProduct.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if(!TextUtils.isEmpty(titleProduct.getText().toString())) {
                    if (titleProduct.getText().toString().length() >= 14) {
                        titleProduct.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getContext(), R.drawable.ic_check), null);
                    } else {
                        titleProduct.setError("No minímo 14 caracteres");
                    }
                } else {
                    titleProduct.setError("Campo obrigatório");
                }
            }
        });

        nomeUser.setOnFocusChangeListener((v, hasfocus) -> {
            if (!hasfocus) {
                if(!TextUtils.isEmpty(nomeUser.getText().toString())) {
                    nomeUser.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getContext(), R.drawable.ic_check), null);
                } else {
                    nomeUser.setError("Campo obrigatório");
                }
            }
        });

        emailUser.setOnFocusChangeListener((v, hasfocus) -> {
            if (!hasfocus) {
                if(!TextUtils.isEmpty(emailUser.getText().toString())) {
                    emailUser.setError("Campo obrigatório");
                }
            }
        });

        PlacesClient placesClient = Places.createClient(requireContext());

        bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.GONE);

        closeButton.setOnClickListener(v -> {
            if (closeButton.isHapticFeedbackEnabled()) {
                v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            }

            requireActivity().getSupportFragmentManager().popBackStack();
        });

        PlaceSuggestionAdapter adapter = new PlaceSuggestionAdapter(prediction -> {
            shouldIgnoreTextChange[0] = true;

            localProductEditText.setText(prediction.getFullText(null).toString());
            locationRecycler.setVisibility(View.GONE);
            localProductEditText.clearFocus();

            Utils.hideKeyboard(this);
        });

        locationRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        locationRecycler.setAdapter(adapter);

        localProductEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (shouldIgnoreTextChange[0]) {
                    shouldIgnoreTextChange[0] = false;
                    return;
                }

                if (s.length() < 2) {
                    locationRecycler.setVisibility(View.GONE);
                    return;
                }

                FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                        .setQuery(s.toString())
                        .build();

                placesClient.findAutocompletePredictions(request)
                        .addOnSuccessListener(response -> {
                            adapter.setPredictions(response.getAutocompletePredictions());
                            locationRecycler.setVisibility(View.VISIBLE);
                        })
                        .addOnFailureListener(exception -> {
                            Log.e("Places", "Erro: " + exception.getMessage());
                        });
            }

            @Override public void afterTextChanged(Editable s) {}
        });

        localProductEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus) {
                locationRecycler.setVisibility(View.GONE);
            }
        });
    }

    private void getClassList() {
        productManager.getAllCategories(new RepositoryCallback<List<Category>>() {

            @Override
            public void onSuccess(List<Category> result) {
                List<String>  classNames = new ArrayList<>();
                classNames.add(getString(R.string.choose));

                for (Category category : result) {
                    classNames.add(category.getName());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        requireContext(),
                        R.layout.spinner_item,
                        classNames
                );
                adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                classSelectorSpinner.setAdapter(adapter);

            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }
}
