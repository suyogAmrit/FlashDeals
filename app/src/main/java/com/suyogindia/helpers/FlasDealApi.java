package com.suyogindia.helpers;

import com.suyogindia.model.OrderDetailResponse;
import com.suyogindia.model.OrderResponse;
import com.suyogindia.model.Profile;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Tanmay on 10/21/2016.
 */

public interface FlasDealApi {
    @POST("user_order_show.php")
    Call<OrderResponse> getMyOrders(@Body Map<String,String>object);
    @POST("user_order_detail_show.php")
    Call<OrderDetailResponse> getAllOrderDetails(@Body Map<String,String>object);
    @POST("show_user_profile.php")
    Call<Profile>getProfileInfo(@Body Map<String,String>object);
}
