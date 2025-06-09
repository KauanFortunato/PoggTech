package com.mordekai.poggtech.utils;

import android.util.Log;

import com.mordekai.poggtech.data.adapter.ProductAdapter;
import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.Product;
import com.mordekai.poggtech.domain.ProductManager;

import java.util.List;

public class ProductLoader {

    // Avisa que o loading terminou
    public interface ProductLoadListener {
        void onLoaded();
    }

    public static void loadForYouProducts(
            ProductManager productManager,
            int userId,
            ProductAdapter adapter,
            List<Product> productList,
            List<Integer> favoriteIds,
            int limit,
            int type,
            ProductLoadListener listener
    ) {
        productManager.getProductsFavCategories(userId, limit, new RepositoryCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> result) {
                productList.clear();
                productList.addAll(result);

                productManager.verifProductsSaved(userId, type, new RepositoryCallback<List<Integer>>() {
                    @Override
                    public void onSuccess(List<Integer> favorites) {
                        favoriteIds.clear();
                        favoriteIds.addAll(favorites);

                        adapter.setSavedIds(favoriteIds);
                        adapter.updateProducts(result);

                        if (listener != null) listener.onLoaded();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.e("ProductLoader", "Erro ao buscar favoritos", t);
                        if (listener != null) listener.onLoaded();
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("ProductLoader", "Erro ao buscar produtos favoritos", t);
                if (listener != null) listener.onLoaded();
            }
        });
    }
}
