package com.mordekai.poggtech.ui.bottomsheets;

import android.os.Bundle;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mordekai.poggtech.R;
import com.mordekai.poggtech.ui.activity.MainActivity;
import com.mordekai.poggtech.ui.fragments.CartFragment;
import com.mordekai.poggtech.ui.fragments.ProductManageFragment;
import com.mordekai.poggtech.utils.Utils;

import java.util.Objects;

public class ProductAddedBottomSheet extends BottomSheetDialogFragment {

    private String imageProduct;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_product_added, container, false);

        ImageView productImg = view.findViewById(R.id.productImg);
        TextView tvGoToCart = view.findViewById(R.id.tvGoToCart);

        if(getArguments() != null) {
            imageProduct = getArguments().getString("image_product");
            Utils.loadImageBasicAuth(productImg, imageProduct);
        } else {
            Log.e("ProductAddedBottomSheet", "Erro ao buscar imagem do produto");
        }

        tvGoToCart.setOnClickListener(v -> {
           if(tvGoToCart.isHapticFeedbackEnabled()) {
               tvGoToCart.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
           }

           goToCart();
        });

        return view;
    }

    private void goToCart() {
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.save);
    }
}
