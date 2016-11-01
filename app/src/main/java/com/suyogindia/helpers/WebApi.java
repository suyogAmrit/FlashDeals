package com.suyogindia.helpers;

import com.suyogindia.flashdeals.CheckDeliverPostData;
import com.suyogindia.flashdeals.CheckDeliveryResponse;
import com.suyogindia.flashdeals.OrderReviewResponse;
import com.suyogindia.flashdeals.SendCategoryPostData;
import com.suyogindia.model.AddAddressResponse;
import com.suyogindia.model.AddressResponse;
import com.suyogindia.model.ForgotPasswordPostData;
import com.suyogindia.model.ForgotPasswordResponse;
import com.suyogindia.model.GenOtpPostData;
import com.suyogindia.model.GenOtpResponse;
import com.suyogindia.model.GetDealsPostData;
import com.suyogindia.model.GetDealsResponse;
import com.suyogindia.model.ListCategoryResponse;
import com.suyogindia.model.LoginPostData;
import com.suyogindia.model.PlaceOrderPostData;
import com.suyogindia.model.PlaceOrderResponse;
import com.suyogindia.model.PostTokenData;
import com.suyogindia.model.PostTokenResponse;
import com.suyogindia.model.RegisterUserPostData;
import com.suyogindia.model.RegisterUserResponse;
import com.suyogindia.model.Result;
import com.suyogindia.model.ReviewOrderData;
import com.suyogindia.model.SendCategoryResponse;
import com.suyogindia.model.SocialLoginPostData;
import com.suyogindia.model.VerifyOtpPostData;
import com.suyogindia.model.VerifyOtpResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by suyogcomputech on 10/10/16.
 */

public interface WebApi {
    @POST("otp_generate.php")
    Call<GenOtpResponse> getGenOtpResponse(@Body GenOtpPostData genOtpPostData);

    @POST("otp_verify.php")
    Call<VerifyOtpResponse> getVeryOtpResponse(@Body VerifyOtpPostData verifyOtpPostData);

    @POST("user_register.php")
    Call<RegisterUserResponse> getRegisterResponse(@Body RegisterUserPostData data);

    @POST("user_login.php")
    Call<RegisterUserResponse> getLoginResponse(@Body LoginPostData data);

    @POST("social_login.php")
    Call<RegisterUserResponse> getSocialLogin(@Body SocialLoginPostData data);

    @POST("forgot_password_user.php")
    Call<ForgotPasswordResponse> getForgotPassword(@Body ForgotPasswordPostData data);

    @GET("getCategory.php")
    Call<ListCategoryResponse> getCategory();

    @POST("user_category_insert.php")
    Call<SendCategoryResponse> setCategoryList(@Body SendCategoryPostData data);

    @POST("user_deals.php")
    Call<GetDealsResponse> getDealsFrom(@Body GetDealsPostData data);

    @POST("user_order.php")
    Call<PlaceOrderResponse> senOrders(@Body PlaceOrderPostData data);

    @POST("user_address_insert.php")
    Call<AddAddressResponse> insertUserDetails(@Body Map<String, String> object);

    @POST("user_address_show.php")
    Call<AddressResponse> getAllAddress(@Body Map<String, String> object);

    @POST("user_address_update.php")
    Call<AddAddressResponse> updateUserDetails(@Body Map<String, String> object);

    @POST("user_address_delete.php")
    Call<Result> deleteAddress(@Body Map<String, String> object);

    @POST("token_user.php")
    Call<PostTokenResponse> sendToken(@Body PostTokenData data);

    @POST("check_delevery_area.php")
    Call<CheckDeliveryResponse> getDeliveryOptions(@Body CheckDeliverPostData data);

    @POST("user_order_review.php")
    Call<OrderReviewResponse> reviewOrders(@Body ReviewOrderData data);
}
