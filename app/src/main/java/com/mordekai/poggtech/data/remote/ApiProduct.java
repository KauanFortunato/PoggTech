package com.mordekai.poggtech.data.remote;

import com.mordekai.poggtech.data.model.ApiResponse;
import com.mordekai.poggtech.data.model.Category;
import com.mordekai.poggtech.data.model.Product;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiProduct {

    /*
     *   Product
     * */

    @Multipart
    @POST("products")
    Call<ResponseBody> uploadProduct(
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part("location") RequestBody location,
            @Part("price") RequestBody price,
            @Part("user_id") RequestBody userId,
            @Part("category") RequestBody category,
            @Part List<MultipartBody.Part> images
    );

    // POST /products/{id} → atualizar produto
    @Multipart
    @POST("products/{product_id}")
    Call<ResponseBody> updateProduct(
            @Path("product_id") int productId,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part("location") RequestBody location,
            @Part("price") RequestBody price,
            @Part("category") RequestBody category,
            @Part("existing_images[]") List<RequestBody> existingImages,
            @Part List<MultipartBody.Part> images
    );


    @DELETE("products/{product_id}")
    Call<ApiResponse<Void>> deleteProduct(@Path("product_id") int product_id);

    @GET("products/{product_id}")
    Call<ApiResponse<Product>> getProductById(@Path("product_id") int product_id);

    @GET("products")
    Call<List<Product>> getAllProducts();

    @GET("products/category/{category}/{all}")
    Call<ApiResponse<List<Product>>> getProductsByCategory(@Path("category") String category, @Path("all") Boolean all);

    @GET("products/popular/{all}/{quantity}")
    Call<ApiResponse<List<Product>>> getPopularProducts(@Path("all") Boolean all, @Path("quantity") int quantity);

    @GET("products/recommended/{user_id}")
    Call<ApiResponse<List<Product>>> getContinueBuy(@Path("user_id") int userId);

    @GET("products/search/{query}")
    Call<ApiResponse<List<Product>>> searchProducts(@Path("query") String search);

    // sugestões para autocomplete
    @GET("products/suggestions/{query}")
    Call<ApiResponse<List<String>>> getSuggestions(@Path("query") String query);

    // Produtos do usuário
    @GET("products/my/{user_id}")
    Call<ApiResponse<List<Product>>> getMyProducts(@Path("user_id") int user_id);

    // Pega os produtos baseado em apenas uma categoria
    @GET("products/fav/{user_id}")
    Call<ApiResponse<List<Product>>> getProductsFromFavCategory(@Path("user_id") int userId);

    // Pega os produtos baseado em varias categorias
    @GET("products/favCategories/{user_id}/{quantity}")
    Call<ApiResponse<List<Product>>> getProductsFavCategories(
            @Path("user_id") int user_id,
            @Path("quantity") int quantity
    );

    /*
     *   Cart
     * */

    // Adiciona ao carrinho
    @FormUrlEncoded
    @POST("cart/add")
    Call<ApiResponse<Void>> addToCart(
            @Field("product_id") int product_id,
            @Field("user_id") int user_id,
            @Field("quantity") int quantity
    );

    // Salva o produto
    @FormUrlEncoded
    @POST("product/save")
    Call<ApiResponse<Void>> saveProduct(
            @Field("product_id") int product_id,
            @Field("user_id") int user_id
    );

    // Remove apenas uma unidade do produto do carrinho
    @FormUrlEncoded
    @POST("cart/removeOne")
    Call<ApiResponse<Void>> removeOneFromCart(
            @Field("product_id") int product_id,
            @Field("user_id") int user_id,
            @Field("tipo") int tipo
    );

    // Remove o produto do carrinho
    @FormUrlEncoded
    @POST("cart/removeProduct")
    Call<ResponseBody> removeFromCart(
            @Field("product_id") int product_id,
            @Field("user_id") int user_id,
            @Field("tipo") int tipo
    );

    // pegar todos produtos do carrinho ou favoritos (tipo = '0' ou '1')
    @GET("cart/{user_id}/{tipo}")
    Call<ApiResponse<List<Product>>> getCartProducts(
            @Path("user_id") int user_id,
            @Path("tipo") int tipo
    );

    // Verifica se produto está no carrinho ou favoritos
    @GET("cart/verify/{user_id}/{product_id}/{tipo}")
    Call<ApiResponse<Integer>> verifyProductOnCart(
            @Path("user_id") int userId,
            @Path("product_id") int productId,
            @Path("tipo") int tipo
    );

    @GET("cart/favOrCart/{user_id}/{tipo}")
    Call<ApiResponse<List<Integer>>> getUserFavOrCart(@Path("user_id") int userId, @Path("tipo") int tipo);

    // Salva o produto
    @FormUrlEncoded
    @POST("product/quantity")
    Call<ApiResponse<Void>> reduceQuantity(
            @Field("product_id") int product_id,
            @Field("quantity") int quantity
    );

    /*
     *   Gallery
     * */

    @GET("gallery/product/{product_id}")
    Call<ApiResponse<List<String>>> getProductImages(@Path("product_id") int product_id);

    /*
     *   Category
     * */

    @GET("category")
    Call<ApiResponse<List<Category>>> getAllCategories();
}
