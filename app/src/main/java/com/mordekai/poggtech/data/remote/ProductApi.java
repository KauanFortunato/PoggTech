package com.mordekai.poggtech.data.remote;

import com.mordekai.poggtech.data.model.Product;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ProductApi {
    @GET("GetAllProducts.php")
    Call<List<Product>> getAllProducts();

    @FormUrlEncoded
    @POST("GetProduct.php")
    Call<Product> getProductById(
            @Field("product_id") int product_id
    );
}
