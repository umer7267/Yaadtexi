package com.travel.taxi.Connection;

import com.travel.taxi.ApiResponse.Estimated.EstimatedPrice;
import com.travel.taxi.ApiResponse.Help.HelpResponse;
import com.travel.taxi.ApiResponse.Login.LoginResponse;
import com.travel.taxi.ApiResponse.Login.User;
import com.travel.taxi.ApiResponse.Password.PasswordResponse;
import com.travel.taxi.ApiResponse.Provider.Providers;
import com.travel.taxi.ApiResponse.Rid.CancelRide;
import com.travel.taxi.ApiResponse.Rid.RideRequest;
import com.travel.taxi.ApiResponse.Rid.UpdateLocation;
import com.travel.taxi.ApiResponse.Service.Service;
import com.travel.taxi.ApiResponse.SignupResponse.SignUpResponse;
import com.travel.taxi.ApiResponse.Trip.TripResponse;
import com.travel.taxi.ApiResponse.Upcoming.UpcomingResponse;
import com.travel.taxi.ApiResponse.Wallet.WalletUserResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface Services {
@POST("api/user/yaad/signup")
    Call<SignUpResponse> signup(@Query("device_type")  String device_type,
                                @Query("device_token")  String device_token,
                                @Query("device_id")  String device_id,
                                @Query("login_by")  String login_by,
                                @Query("first_name")  String first_name,
                                @Query("last_name")  String last_name,
                                @Query("email")  String email,
                                @Query("mobile")  String mobile,
                                @Query("password")  String password);

    @POST("api/user/yaad/login")
    Call<LoginResponse> Login(@Query("email") String email,
                              @Query("password") String password);

    @POST("api/user/yaad/logout")
    Call<ResponseBody> Logout(@Header("Authorization") String token);


    @GET("api/user/yaad/help")
    Call<HelpResponse> Help(@Header("Authorization") String token);

    @GET("api/user/yaad/services")
    Call<List<Service>> Services(@Header("Authorization") String token);

    @GET("api/user/yaad/estimated/fare")
    Call<EstimatedPrice> EstimatedPrices(@Header("Authorization") String token,
                                         @Query("s_latitude") Double s_latitude,
                                         @Query("s_longitude") Double s_longitude,
                                         @Query("d_latitude") Double d_latitude,
                                         @Query("d_longitude") Double d_longitude,
                                         @Query("service_type") Integer service_type);


    @POST("api/user/yaad/send/request")
    Call<RideRequest> RequestRide(@Header("Authorization") String token,
                                  @Query("s_latitude") Double s_latitude,
                                  @Query("s_longitude") Double s_longitude,
                                  @Query("d_latitude") Double d_latitude,
                                  @Query("d_longitude") Double d_longitude,
                                  @Query("service_type") Integer service_type,
                                  @Query("distance") Double distance,
                                  @Query("payment_mode") String payment_mode,
                                  @Query("no_of_bags") Integer no_of_bags,
                                  @Query("passengers") Integer passengers
    );

    @POST("api/user/yaad/cancel/request")
    Call<CancelRide> CancelRide(@Header("Authorization") String token,
                                @Query("request_id") Double request_id,
                                @Query("cancel_reason") String cancel_reason);

    @GET("api/user/yaad/show/providers")
    Call<List<Providers>> GetProviders(@Header("Authorization") String token,
                                       @Query("latitude") Double latitude,
                                       @Query("longitude") Double longitude,
                                       @Query("service") Integer service);


    @GET("api/user/yaad/details")
    Call<User> GetUserDetails(@Header("Authorization") String token,
                              @Query("device_type") String device_type,
                              @Query("device_token") String device_token,
                              @Query("device_id") String device_id);


//
//    @POST("api/user/yaad/update/profile")
//    Call<UpdateResponse> UpdateProfile(@Header("Authorization") String token,
//                                       @Query("first_name") String first_name,
//                                       @Query("last_name") String last_name,
//                                       @Query("email") String email,
//                                       @Query("mobile") String mobile,
//
//                                      @Query MultipartBody.Part picture);
   @Multipart
    @POST("api/user/yaad/update/profile")
    Call<User> UpdateProfile(@Header("Authorization") String token,
                                       @Query("first_name") String first_name,
                                       @Query("last_name") String last_name,
                                       @Query("email") String email,
                                       @Query("mobile") String mobile,
                             @Part MultipartBody.Part picture);


    @POST("api/user/yaad/change/password")
    Call<PasswordResponse> UpdatePassword(@Header("Authorization") String token,
                                          @Query("password") String password,
                                          @Query("password_confirmation") String password_confirmation,
                                          @Query("old_password") String old_password);

    @POST("api/user/yaad/update/location")
    Call<UpdateLocation> UpdateLocation(@Header("Authorization") String token,
                                        @Query("latitude") Double latitude,
                                        @Query("longitude") Double longitude);

    @GET("api/user/yaad/trips")
    Call<List<TripResponse>> Trip(@Header("Authorization") String token);
    @GET("api/user/yaad/upcoming/trips")
    Call<List<UpcomingResponse>> Upcoming(@Header("Authorization") String token);
    @GET("api/user/yaad/add/money")
    Call<WalletUserResponse> AddMoney(@Header("Authorization") String token,
                                      @Query("amount") String amount,
                                      @Query("card_id") String card_id);

    @GET("api/user/yaad/upcoming/trips")
    Call<TripResponse> UpcomingTrip(@Header("Authorization") String token);


}