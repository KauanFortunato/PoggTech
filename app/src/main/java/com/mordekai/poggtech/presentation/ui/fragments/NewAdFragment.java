package com.mordekai.poggtech.presentation.ui.fragments;

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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import static android.app.Activity.RESULT_OK;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.adapter.PlaceSuggestionAdapter;
import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.Category;
import com.mordekai.poggtech.data.model.Product;
import com.mordekai.poggtech.data.model.User;
import com.mordekai.poggtech.data.remote.ApiProduct;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.domain.ProductManager;
import com.mordekai.poggtech.presentation.ui.activity.MainActivity;
import com.mordekai.poggtech.utils.SharedPrefHelper;
import com.mordekai.poggtech.utils.SnackbarUtil;
import com.mordekai.poggtech.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.internal.Util;

public class NewAdFragment extends Fragment {

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    private BottomNavigationView bottomNavigationView;
    private EditText nomeUser, emailUser, contactUser, titleProduct, descriptionProduct, localProductEditText, priceProduct;
    private TextView titleProductLabelCount, descriptionProductLabelCount;
    private ImageButton closeButton;
    private AppCompatButton buttonAddAd;
    private Spinner classSelectorSpinner;
    private ProductManager productManager;
    private User user;

    private Button addImagesButton, addImagesButtonAgain;
    private ArrayList<Uri> selectedImageUris = new ArrayList<>();
    private List<String> imageUrls = new ArrayList<>();
    private LinearLayout layoutImageExamples, layoutUserImages, imagePreviewContainer;

    private Product productBeingEdited = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_ad, container, false);

        SharedPrefHelper sharedPrefHelper = new SharedPrefHelper(requireContext());
        user = sharedPrefHelper.getUser();

        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), getString(R.string.google_api_key));
        }

        productManager = new ProductManager(RetrofitClient.getRetrofitInstance().create(ApiProduct.class));

        startComponents(view);

        Product product = (Product) getArguments().getSerializable("product");

        if (product != null) {
            productBeingEdited = product;
            Log.d("NewAdFragment", "Product received: " + product.getTitle());
            setUpdateProcuct(product);
        }

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
            }
        }

        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK && data != null) {
            layoutImageExamples.setVisibility(View.GONE);
            layoutUserImages.setVisibility(View.VISIBLE);

            int maxImages = 6;
            int currentCount = selectedImageUris.size();
            int availableSlots = maxImages - currentCount;

            if (availableSlots <= 0) {
                SnackbarUtil.showErrorSnackbar(requireView(), "Já atingiste o limite de 6 fotos.", requireContext());
                return;
            }

            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();

                if (count > availableSlots) {
                    SnackbarUtil.showErrorSnackbar(requireView(), "Só podes adicionar mais " + availableSlots + " foto(s).", requireContext());
                    count = availableSlots;
                }

                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    selectedImageUris.add(imageUri);
                    addImageToLayout(imageUri);
                }
            } else if (data.getData() != null) {
                if (availableSlots <= 0) {
                    SnackbarUtil.showErrorSnackbar(requireView(), "Já atingiste o limite de 6 fotos.", requireContext());
                    return;
                }
                Uri imageUri = data.getData();
                selectedImageUris.add(imageUri);
                addImageToLayout(imageUri);
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

    private void setUpdateProcuct(Product product) {
        titleProduct.setText(product.getTitle());
        priceProduct.setText(String.valueOf(product.getPrice()));
        descriptionProduct.setText(product.getDescription());
        localProductEditText.setText(product.getLocation());
        nomeUser.setText(user.getName());
        buttonAddAd.setText(R.string.updateAd);

        productManager.getProductImages(product.getProduct_id(), new RepositoryCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> result) {
                Log.d("NewAdFragment", "Images received: " + result.size());
                if(!result.isEmpty()) {
                    layoutImageExamples.setVisibility(View.GONE);
                    layoutUserImages.setVisibility(View.VISIBLE);

                    for (String imageUrl : result) {
                        addRemoteImageToLayout(imageUrl);
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                SnackbarUtil.showErrorSnackbar(requireView(), "Erro ao carregar imagens", requireContext());
            }
        });
    }

    private void pickImagesFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Selecionar imagens"), IMAGE_PICK_CODE);
    }

    private void addImageToLayout(Uri imageUri) {
        View imageWrapper = LayoutInflater.from(requireContext()).inflate(R.layout.item_selected_image, imagePreviewContainer, false);
        ShapeableImageView imageView = imageWrapper.findViewById(R.id.imageView);
        ImageView removeButton = imageWrapper.findViewById(R.id.removeImageButton);

        imageView.setImageURI(imageUri);

        removeButton.setOnClickListener(v -> {
            if (v.isHapticFeedbackEnabled()) {
                v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            }

            Animation fadeOut = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out);
            imageWrapper.startAnimation(fadeOut);

            fadeOut.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    imagePreviewContainer.removeView(imageWrapper);
                    selectedImageUris.remove(imageUri);

                    if (selectedImageUris.isEmpty() && imageUrls.isEmpty()) {
                        layoutImageExamples.setVisibility(View.VISIBLE);
                        layoutUserImages.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        });

        imagePreviewContainer.addView(imageWrapper);
    }

    private void addRemoteImageToLayout(String imageUrl) {
        imageUrls.add(imageUrl); // Adiciona à lista

        View imageWrapper = LayoutInflater.from(requireContext()).inflate(R.layout.item_selected_image, imagePreviewContainer, false);
        ShapeableImageView imageView = imageWrapper.findViewById(R.id.imageView);
        ImageView removeButton = imageWrapper.findViewById(R.id.removeImageButton);

        Utils.loadImageBasicAuth(imageView, imageUrl);

        removeButton.setOnClickListener(v -> {
            if (v.isHapticFeedbackEnabled()) {
                v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            }

            Animation fadeOut = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out);
            imageWrapper.startAnimation(fadeOut);

            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override public void onAnimationStart(Animation animation) {}
                @Override public void onAnimationEnd(Animation animation) {
                    imagePreviewContainer.removeView(imageWrapper);
                    imageUrls.remove(imageUrl); // Remove da lista

                    if (selectedImageUris.isEmpty() && imageUrls.isEmpty()) {
                        layoutImageExamples.setVisibility(View.VISIBLE);
                        layoutUserImages.setVisibility(View.GONE);
                    }
                }
                @Override public void onAnimationRepeat(Animation animation) {}
            });
        });

        imagePreviewContainer.addView(imageWrapper);
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
        priceProduct = view.findViewById(R.id.priceProduct);
        descriptionProduct = view.findViewById(R.id.descriptionProduct);
        titleProductLabelCount = view.findViewById(R.id.titleProductLabelCount);
        descriptionProductLabelCount = view.findViewById(R.id.descriptionProductLabelCount);
        buttonAddAd = view.findViewById(R.id.buttonAddAd);
        localProductEditText = view.findViewById(R.id.localProduct);

        RecyclerView locationRecycler = view.findViewById(R.id.locationSuggestionsRecycler);

        // Title Product
        titleProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

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
            public void afterTextChanged(Editable s) {
            }
        });

        titleProduct.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (!TextUtils.isEmpty(titleProduct.getText().toString())) {
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

        // Price Product
        priceProduct.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (TextUtils.isEmpty(priceProduct.getText().toString())) {
                    priceProduct.setError("Campo obrigatório");
                } else {
                    priceProduct.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getContext(), R.drawable.ic_check), null);
                }
            }
        });

        // Description Product
        descriptionProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int currentLength = s.length();

                if (currentLength > 9000) {
                    descriptionProductLabelCount.setError("Máximo de 9000 caracteres");
                    currentLength = 9000;
                }
                descriptionProductLabelCount.setText(currentLength + "/" + 9000);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        descriptionProduct.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                int currentLength = descriptionProduct.getText().length();

                if (TextUtils.isEmpty(descriptionProduct.getText().toString())) {
                    descriptionProduct.setError("Campo obrigatório");
                } else {
                    if (currentLength < 40) {
                        descriptionProduct.setError("No minímo 40 caracteres");
                    } else {
                        descriptionProduct.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getContext(), R.drawable.ic_check), null);
                    }
                }
            }
        });

        emailUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(emailUser.getText())) {
                    emailUser.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getContext(), R.drawable.ic_check), null);
                } else {
                    emailUser.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        emailUser.setText(user.getEmail());
        nomeUser.setText(user.getName());
        contactUser.setText(user.getPhone());

        nomeUser.setOnFocusChangeListener((v, hasfocus) -> {
            if (!hasfocus) {
                if (!TextUtils.isEmpty(nomeUser.getText().toString())) {
                    nomeUser.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getContext(), R.drawable.ic_check), null);
                } else {
                    nomeUser.setError("Campo obrigatório");
                }
            }
        });

        emailUser.setOnFocusChangeListener((v, hasfocus) -> {
            if (!hasfocus) {
                if (!TextUtils.isEmpty(emailUser.getText().toString())) {
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

            NavController navController = ((MainActivity) requireActivity()).getCurrentNavController();
            navController.popBackStack();
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
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

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

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        localProductEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                locationRecycler.setVisibility(View.GONE);

                if (TextUtils.isEmpty(localProductEditText.getText().toString())) {
                    localProductEditText.setError("Campo obrigatório");
                } else {
                    localProductEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getContext(), R.drawable.ic_check), null);
                }
            }
        });

        buttonAddAd.setOnClickListener(v -> {
            if (v.isHapticFeedbackEnabled()) {
                v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            }

            validateEntries();
        });
    }

    private void getClassList() {
        productManager.getAllCategories(new RepositoryCallback<List<Category>>() {

            @Override
            public void onSuccess(List<Category> result) {
                List<String> classNames = new ArrayList<>();
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
                SnackbarUtil.showErrorSnackbar(requireView(), "Erro ao carregar categorias", requireContext());
            }
        });
    }

    private void validateEntries() {
        boolean isValid = true;

        // Nome
        if (TextUtils.isEmpty(nomeUser.getText().toString().trim())) {
            nomeUser.setError("Campo obrigatório");
            isValid = false;
        } else {
            nomeUser.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getContext(), R.drawable.ic_check), null);
        }

        // Email
        if (TextUtils.isEmpty(emailUser.getText().toString().trim())) {
            emailUser.setError("Campo obrigatório");
            isValid = false;
        }

        // Localization
        if (TextUtils.isEmpty(localProductEditText.getText().toString().trim())) {
            localProductEditText.setError("Campo obrigatório");
            isValid = false;
        }

        // Título
        String title = titleProduct.getText().toString().trim();
        if (TextUtils.isEmpty(title)) {
            titleProduct.setError("Campo obrigatório");
            isValid = false;
        } else if (title.length() < 14) {
            titleProduct.setError("No mínimo 14 caracteres");
            isValid = false;
        }

        // Preço
        if (TextUtils.isEmpty(priceProduct.getText().toString().trim())) {
            priceProduct.setError("Campo obrigatório");
            isValid = false;
        }

        // Descrição
        String description = descriptionProduct.getText().toString().trim();
        if (TextUtils.isEmpty(description)) {
            descriptionProduct.setError("Campo obrigatório");
            isValid = false;
        } else if (description.length() < 40) {
            descriptionProduct.setError("No mínimo 40 caracteres");
            isValid = false;
        }

        // Categoria
        if (classSelectorSpinner.getSelectedItemPosition() == 0) {
            classSelectorSpinner.setBackgroundResource(R.drawable.spinner_error_background);
            SnackbarUtil.showErrorSnackbar(requireView(), "Selecione uma categoria", requireContext());

            if(classSelectorSpinner.isHapticFeedbackEnabled()) {
                classSelectorSpinner.performHapticFeedback(HapticFeedbackConstants.REJECT);
            }
            isValid = false;
        } else {
            classSelectorSpinner.setBackgroundResource(R.drawable.bg_edit_text);
        }

        // Imagens
        if (selectedImageUris.isEmpty() && imageUrls.isEmpty()) {
            SnackbarUtil.showErrorSnackbar(requireView(), "Adiciona pelo menos uma imagem", requireContext());
            isValid = false;
        }

        // Se tudo estiver válido, submeter
        if (isValid) {
            if (productBeingEdited != null) {
                submitUpdate();
            } else {
                submitAd();
            }
        }
    }

    private void submitAd() {
        // Dados de texto
        RequestBody title = RequestBody.create(MediaType.parse("text/plain"), titleProduct.getText().toString().trim());
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), descriptionProduct.getText().toString().trim());
        RequestBody location = RequestBody.create(MediaType.parse("text/plain"), localProductEditText.getText().toString().trim());
        RequestBody price = RequestBody.create(MediaType.parse("text/plain"), priceProduct.getText().toString().trim());
        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(user.getUserId()));
        RequestBody category = RequestBody.create(MediaType.parse("text/plain"), classSelectorSpinner.getSelectedItem().toString());

        // Imagens
        List<MultipartBody.Part> imageParts = new ArrayList<>();
        for (int i = 0; i < selectedImageUris.size(); i++) {
            Uri uri = selectedImageUris.get(i);
            File file = Utils.getFileFromUri(requireContext(), uri);
            if (file == null) continue;

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData("images[]", file.getName(), requestFile);
            imageParts.add(part);
        }

        productManager.uploadProduct(title, description, location, price, userId, category, imageParts, new RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                SnackbarUtil.showSuccessSnackbar(requireView(), "Produto adicionado com sucesso!", requireContext());
                NavController navController = ((MainActivity) requireActivity()).getCurrentNavController();
                navController.popBackStack();
            }

            @Override
            public void onFailure(Throwable t) {
                SnackbarUtil.showErrorSnackbar(requireView(), "Erro ao adicionar produto", requireContext());
            }
        });
    }

    private void submitUpdate() {
        RequestBody title = RequestBody.create(MediaType.parse("text/plain"), titleProduct.getText().toString().trim());
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), descriptionProduct.getText().toString().trim());
        RequestBody location = RequestBody.create(MediaType.parse("text/plain"), localProductEditText.getText().toString().trim());
        RequestBody price = RequestBody.create(MediaType.parse("text/plain"), priceProduct.getText().toString().trim());
        RequestBody category = RequestBody.create(MediaType.parse("text/plain"), classSelectorSpinner.getSelectedItem().toString());

        // Imagens já existentes (mantidas)
        List<RequestBody> existingImagesParts = new ArrayList<>();
        for (String url : imageUrls) {
            existingImagesParts.add(RequestBody.create(MediaType.parse("text/plain"), url));
        }

        // Novas imagens
        List<MultipartBody.Part> newImageParts = new ArrayList<>();
        for (Uri uri : selectedImageUris) {
            File file = Utils.getFileFromUri(requireContext(), uri);
            if (file == null) continue;

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData("images[]", file.getName(), requestFile);
            newImageParts.add(part);
        }

        // Chamada Retrofit para update
        productManager.updateProduct(
                productBeingEdited.getProduct_id(),
                title, description, location, price, category,
                existingImagesParts,
                newImageParts,
                new RepositoryCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        SnackbarUtil.showSuccessSnackbar(requireView(), "Produto atualizado com sucesso!", requireContext());
                        NavController navController = ((MainActivity) requireActivity()).getCurrentNavController();
                        navController.popBackStack();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        SnackbarUtil.showErrorSnackbar(requireView(), "Erro ao atualizar produto", requireContext());
                    }
                }
        );
    }


}
