package com.mordekai.poggtech.data.remote;

import com.mordekai.poggtech.data.model.ApiResponse;
import com.mordekai.poggtech.data.model.Product;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ProductApi {
    @GET("GetAllProducts.php")
    Call<List<Product>> getAllProducts();

    @GET("GetProductsByCategory.php")
    Call<List<Product>> getProductsByCategory(@Query("category") String category);

    @GET("GetProduct.php")
    Call<Product> getProductById(
            @Query("product_id") int product_id
    );

    @FormUrlEncoded
    @POST("AddProductOnCartOrFav.php")
    Call<ResponseBody> addToCart(
            @Field("product_id") int product_id,
            @Field("user_id") int user_id,
            @Field("tipo") int tipo
    );

    @FormUrlEncoded
    @POST("RemoveProductOnCartOrFav.php")
    Call<ResponseBody> removeFromCart(
            @Field("product_id") int product_id,
            @Field("user_id") int user_id,
            @Field("tipo") int tipo
    );

    @GET("GetProductsFromCart.php")
    Call<ApiResponse<List<Product>>> getCartProducts(
            @Query("user_id") int user_id,
            @Query("tipo") int tipo
    );

    @GET("GetUserFavOrCart.php")
    Call<ApiResponse<List<Integer>>> getUserFavOrCart(
            @Query("user_id") int userId,
            @Query("tipo") int tipo
    );

    @GET("VerifyProductOnCart.php")
    Call<ApiResponse> verifyProductOnCart(
            @Query("product_id") int productId,
            @Query("user_id") int userId,
            @Query("tipo") int tipo
    );
}
