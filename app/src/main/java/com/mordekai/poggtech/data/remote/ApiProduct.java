package com.mordekai.poggtech.data.remote;

import com.mordekai.poggtech.data.model.ApiResponse;
import com.mordekai.poggtech.data.model.Category;
import com.mordekai.poggtech.data.model.Product;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiProduct {
    @GET("Product/GetAllProducts.php")
    Call<List<Product>> getAllProducts();

    @GET("Product/GetProductsByCategory.php")
    Call<List<Product>> getProductsByCategory(@Query("category") String category);

    @GET("Product/GetProduct.php")
    Call<Product> getProductById(
            @Query("product_id") int product_id
    );

    @FormUrlEncoded
    @POST("Product/AddProductOnCartOrFav.php")
    Call<ResponseBody> addToCart(
            @Field("product_id") int product_id,
            @Field("user_id") int user_id,
            @Field("tipo") int tipo
    );

    @FormUrlEncoded
    @POST("Product/RemoveProductOnCartOrFav.php")
    Call<ResponseBody> removeFromCart(
            @Field("product_id") int product_id,
            @Field("user_id") int user_id,
            @Field("tipo") int tipo
    );

    @GET("Product/GetProductsFromCart.php")
    Call<ApiResponse<List<Product>>> getCartProducts(
            @Query("user_id") int user_id,
            @Query("tipo") int tipo
    );

    @GET("Product/GetUserFavOrCart.php")
    Call<ApiResponse<List<Integer>>> getUserFavOrCart(
            @Query("user_id") int userId,
            @Query("tipo") int tipo
    );

    @GET("Product/VerifyProductOnCart.php")
    Call<ApiResponse> verifyProductOnCart(
            @Query("product_id") int productId,
            @Query("user_id") int userId,
            @Query("tipo") int tipo
    );

    @GET("Product/GetPopularProducts.php")
    Call<ApiResponse<List<Product>>> getPopularProducts();

    @GET("Product/GetRecommendedProducts.php")
    Call<ApiResponse<List<Product>>> getRecommendedProducts(
            @Query("user_id") int userId
    );

    @GET("Product/GetProductsFavCategory.php")
    Call<ApiResponse<List<Product>>> getProductsFavCategory(
            @Query("user_id") int userId,
            @Query("quantity") int quantity
    );

    @GET("Product/SearchProducts.php")
    Call<ApiResponse<List<Product>>> searchProducts (
            @Query("search") String search
    );

    @GET("Product/GetSuggestions.php")
    Call<ApiResponse<List<String>>> getSuggestions (
            @Query("query") String query
    );

    @GET("Category/GetAllCategories.php")
    Call<ApiResponse<List<Category>>> getAllCategories();

    @Multipart
    @POST("Product/UploadProduct.php")
    Call<ResponseBody> uploadProduct(
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part("location") RequestBody location,
            @Part("price") RequestBody price,
            @Part("user_id") RequestBody userId,
            @Part("category") RequestBody category,
            @Part List<MultipartBody.Part> images
    );

    @GET("Product/DeleteProduct.php")
    Call<ApiResponse<Void>> deleteProduct(
            @Query("product_id") int product_id
    );

    @GET("Gallery/GetProductImages.php")
    Call<ApiResponse<List<String>>> getProductImages(
            @Query("product_id") int product_id
    );

    @GET("Product/GetMyProducts.php")
    Call<ApiResponse<List<Product>>> getMyProducts(
            @Query("user_id") int user_id
    );
}
