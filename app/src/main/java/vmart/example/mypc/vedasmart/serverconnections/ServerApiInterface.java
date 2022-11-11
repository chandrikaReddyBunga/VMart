package vmart.example.mypc.vedasmart.serverconnections;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import vmart.example.mypc.vedasmart.model.SubItems;

public interface ServerApiInterface {
    //public static final String BASE_URL = "http://14.98.168.118:8096/V-Mart/"; // TODO LOCAL FOR TESTING PURPOSE
    public static final String BASE_URL = "http://3.84.49.133/V-Mart/"; // TODO PlayStore Url
    //public static String Dunzo_URL = "https://apis-staging.dunzo.in/"; // TODO LOCAL FOR TESTING PURPOSE
    public static String Dunzo_URL = "https://api.dunzo.in/"; // TODO playstore Api

    //for fetching the products(NAVIGATION DRAWER VIEW)
    @GET("categories")
    Observable<Response<ResponseBody>> fetchProductsData();

    ///for fetching the subProducts
    @GET("product/{id}/{merchantId}")
    Observable<Response<ResponseBody>> fetchSubItemData(@Path("id") String id,
                                                        @Path("merchantId") String merchantId);
    ///for sign-up and signIn
    @POST("signup/{mobileNumber}/{merchantId}")
    Call<ResponseBody> SignUpSingnIn(@Path("mobileNumber") String mobilenumber,
                                     @Path("merchantId") String merchantId);

    ///For sign-in with mobile number only
    @POST("mobileVerify")
    Call<ResponseBody> SingnIn(@Body JsonObject otpObj);

    ///verifying the otp
    @POST("verify")
    Call<ResponseBody> verifyOtp(@Body JsonObject otpObj);

    // Fetching HomeProducts
   /* @GET("product")
    Observable<ArrayList<SubItems>> fetchHomeProducts(); */

    // Fetching HomeProducts
    @GET("merchant/{merchantId}")
    Observable<ArrayList<SubItems>> fetchHomeProducts(@Path("merchantId") String merchantId);

    //Add to cart API
    @POST("cart")
    Call<ResponseBody> addToCart(@Body JsonObject addCartObj);

    //update the cart API
    @PUT("cart")
    Call<ResponseBody> updateCart(@Body JsonObject updateCartObj);

    //////delete the cartItem
    @HTTP(method = "DELETE", path = "cart", hasBody = true)
    Call<ResponseBody> deleteCart(@Body JsonObject deletecart);

    //Fetching the cartList
    @GET("cart/{mobile}/{merchantId}")
    Observable<Response<ResponseBody>> fetchCartList(@Path("mobile") String mobile,
                                                     @Path("merchantId") String merchantId);

    ////deleting all items from the cart
    @DELETE("cart/{mobile}/{merchantId}")
    Call<ResponseBody> deleteAllItemsInCart(@Path("mobile") String mobile,
                                            @Path("merchantId") String merchantId);

    ////Adding a new Address
    @POST("useraddress")
    Call<ResponseBody> addNewAddress(@Body JsonObject addNewAddress);

    ////Fetching userAddress
    @GET("useraddress/{mobile}/{merchantId}")
    Observable<Response<ResponseBody>> fetchAddress(@Path("mobile") String mobile,
                                                    @Path("merchantId") String merchantId);

    ///Editing the UserAddress
    @PUT("useraddress")
    Call<ResponseBody> editAddress(@Body JsonObject editAddress);

    ///delete Address
    @HTTP(method = "DELETE", path = "useraddress", hasBody = true)
    Call<ResponseBody> deleteAdress(@Body JsonObject deleteAddress);

    ///get All Pickuplocations
    @GET("vmartaddress")
    Observable<Response<ResponseBody>> getVmartPickUpLocations();

    // Fetching pincodes
    @GET("vmartaddress/pincodes")
    Observable<Response<ResponseBody>> getVmartaddresspincodes();

    // Send feedBack
    @POST("contactUs")
    Call<ResponseBody> msgFeedback(@Body JsonObject contactDetails);

    // fetchfavourite Address
    @GET("vmartaddress/favourite/{mobileNumber}/{merchantId}")
    Call<ResponseBody> getFavourite(@Path("mobileNumber") String userid,
                                    @Path("merchantId") String merchantId);

    // favourites Address
    @PUT("vmartaddress/favourite")
    Call<ResponseBody> sendFavourite(@Body JsonObject sendFavourite);

    /////posting the orders
    @POST("orders")
    Call<ResponseBody> ordersPostingToServer(@Body JsonObject proceedOrdersObj);

    // Fetching Orders
    @GET("orders/{mobile}/{merchantId}")
    Call<ResponseBody> fetchorders(@Path("mobile") String fetchOrders,
                                   @Path("merchantId") String merchantId);

    ///posting orderConfirmation API
    @POST("orderconformation")
    Call<ResponseBody> orderconfrimation(@Body JsonObject orderDetails);

    @POST("orderconfirm")
    Call<ResponseBody> orderconfirm(@Body JsonObject orderDetails);

    //for canceling the orders
    @POST("ordercancel/{orderid}")
    Call<ResponseBody> cancelOrder(@Path("orderid") String cancerOrder);

    @POST("ordercancel")
    Call<ResponseBody> cancelOrderReason(@Body JsonObject orderid);

    /////for seller registration
    @POST("sellerregistration")
    Call<ResponseBody> sellerRegistration(@Body JsonObject sellerRegistrationObj);

    ///for seller verify OTP
    @POST("sellerverify")
    Call<ResponseBody> sellerVerifyOtp(@Body JsonObject sellerRegOtp);

    //Insterting Wishlist products
    @POST("wishlist")
    Call<ResponseBody> addToWish(@Body JsonObject addWishObj);

    //Delete Wishlist Single Item
    @HTTP(method = "DELETE", path = "wishlist", hasBody = true)
    Call<ResponseBody> deleteWish(@Body JsonObject deleteWish);

    //Delete All Wishlist Items
    @DELETE("wishlist/{userid}/{merchantId}")
    Call<ResponseBody> deleteAllItemsInWish(@Path("userid") String mobile,
                                            @Path("merchantId") String merchantId);

    //Fetching the cartList
    @GET("wishlist/{userid}/{merchantId}")
    Observable<Response<ResponseBody>> fetchWishList(@Path("userid") String mobile,
                                                     @Path("merchantId") String merchantId);

    ////Fetching userAddress
    @GET("admin/merchantlist")
    Call<ResponseBody> fetchMerchant();


    @Headers({
            //"client-id: 9ef64467-61d5-4822-9ccc-b36583e58994", // TODO LOCAL TESTING
            "client-id: dda77830-64e0-4362-85f1-b8acf1bc08e6",  //TODO PLAYSTORE update
            //"Authorization: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJkIjp7InJvbGUiOjEwMCwidWlkIjoiNDk0Zjg2ZGMtOTA0OS00NDZjLTlmMDQtYTMwNDMzMjdkMjZlIn0sIm1lcmNoYW50X3R5cGUiOm51bGwsImNsaWVudF9pZCI6IjllZjY0NDY3LTYxZDUtNDgyMi05Y2NjLWIzNjU4M2U1ODk5NCIsImF1ZCI6Imh0dHBzOi8vaWRlbnRpdHl0b29sa2l0Lmdvb2dsZWFwaXMuY29tL2dvb2dsZS5pZGVudGl0eS5pZGVudGl0eXRvb2xraXQudjEuSWRlbnRpdHlUb29sa2l0IiwibmFtZSI6IkFQSVVTRVIiLCJ1dWlkIjoiNDk0Zjg2ZGMtOTA0OS00NDZjLTlmMDQtYTMwNDMzMjdkMjZlIiwicm9sZSI6MTAwLCJkdW56b19rZXkiOiJhNWQ5YTc2NS0wYjhkLTQzNTAtOThhOC0xZjViZTYzZGM1OWYiLCJleHAiOjE3MTU5NDQ0MjksInYiOjAsImlhdCI6MTU2MDQyNDQyOSwic2VjcmV0X2tleSI6Ik5vbmUifQ.TWAI0vjlFGWlplgAJLD2-DL2mzc2s9hY0G438gT5pTM",// TODO LOCAL TESTING
            "Authorization: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJkIjp7InJvbGUiOjEwMCwidWlkIjoiODNlMjEyMWQtNzYxYy00MjgzLTg1MTctMTAyZDUzMDlmMTgwIn0sIm1lcmNoYW50X3R5cGUiOm51bGwsImNsaWVudF9pZCI6ImRkYTc3ODMwLTY0ZTAtNDM2Mi04NWYxLWI4YWNmMWJjMDhlNiIsImF1ZCI6Imh0dHBzOi8vaWRlbnRpdHl0b29sa2l0Lmdvb2dsZWFwaXMuY29tL2dvb2dsZS5pZGVudGl0eS5pZGVudGl0eXRvb2xraXQudjEuSWRlbnRpdHlUb29sa2l0IiwibmFtZSI6IlZlZGFzIiwidXVpZCI6IjgzZTIxMjFkLTc2MWMtNDI4My04NTE3LTEwMmQ1MzA5ZjE4MCIsInJvbGUiOjEwMCwiZHVuem9fa2V5IjoiZmY0NWEyY2EtZmE3OS00ZmFkLWIzNDAtYzA5ZThjN2ZmZTFiIiwiZXhwIjoxNzIzMDA4NjQ4LCJ2IjowLCJpYXQiOjE1Njc0ODg2NDgsInNlY3JldF9rZXkiOiJOb25lIn0.I9px01bGiJRHqb1or-uZqRCk5H7xX19gdpVWYy-0sPk",//TODO PLAYSTORE update
            "Accept-Language: en_US",
            "Content-Type: application/json"
    })
    @GET("api/v1/quote")
    Call<ResponseBody> getWeatherReport(@Query("pickup_lat") float lat,
                                        @Query("pickup_lng") float lng,
                                        @Query("drop_lat") float droplat,
                                        @Query("drop_lng") float droplang,
                                        @Query("category_id") String category_id);

    @Headers({
            //"client-id: 9ef64467-61d5-4822-9ccc-b36583e58994", // TODO LOCAL TESTING
            "client-id: dda77830-64e0-4362-85f1-b8acf1bc08e6",  //TODO PLAYSTORE update
            //"Authorization: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJkIjp7InJvbGUiOjEwMCwidWlkIjoiNDk0Zjg2ZGMtOTA0OS00NDZjLTlmMDQtYTMwNDMzMjdkMjZlIn0sIm1lcmNoYW50X3R5cGUiOm51bGwsImNsaWVudF9pZCI6IjllZjY0NDY3LTYxZDUtNDgyMi05Y2NjLWIzNjU4M2U1ODk5NCIsImF1ZCI6Imh0dHBzOi8vaWRlbnRpdHl0b29sa2l0Lmdvb2dsZWFwaXMuY29tL2dvb2dsZS5pZGVudGl0eS5pZGVudGl0eXRvb2xraXQudjEuSWRlbnRpdHlUb29sa2l0IiwibmFtZSI6IkFQSVVTRVIiLCJ1dWlkIjoiNDk0Zjg2ZGMtOTA0OS00NDZjLTlmMDQtYTMwNDMzMjdkMjZlIiwicm9sZSI6MTAwLCJkdW56b19rZXkiOiJhNWQ5YTc2NS0wYjhkLTQzNTAtOThhOC0xZjViZTYzZGM1OWYiLCJleHAiOjE3MTU5NDQ0MjksInYiOjAsImlhdCI6MTU2MDQyNDQyOSwic2VjcmV0X2tleSI6Ik5vbmUifQ.TWAI0vjlFGWlplgAJLD2-DL2mzc2s9hY0G438gT5pTM",// TODO LOCAL TESTING
            "Authorization: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJkIjp7InJvbGUiOjEwMCwidWlkIjoiODNlMjEyMWQtNzYxYy00MjgzLTg1MTctMTAyZDUzMDlmMTgwIn0sIm1lcmNoYW50X3R5cGUiOm51bGwsImNsaWVudF9pZCI6ImRkYTc3ODMwLTY0ZTAtNDM2Mi04NWYxLWI4YWNmMWJjMDhlNiIsImF1ZCI6Imh0dHBzOi8vaWRlbnRpdHl0b29sa2l0Lmdvb2dsZWFwaXMuY29tL2dvb2dsZS5pZGVudGl0eS5pZGVudGl0eXRvb2xraXQudjEuSWRlbnRpdHlUb29sa2l0IiwibmFtZSI6IlZlZGFzIiwidXVpZCI6IjgzZTIxMjFkLTc2MWMtNDI4My04NTE3LTEwMmQ1MzA5ZjE4MCIsInJvbGUiOjEwMCwiZHVuem9fa2V5IjoiZmY0NWEyY2EtZmE3OS00ZmFkLWIzNDAtYzA5ZThjN2ZmZTFiIiwiZXhwIjoxNzIzMDA4NjQ4LCJ2IjowLCJpYXQiOjE1Njc0ODg2NDgsInNlY3JldF9rZXkiOiJOb25lIn0.I9px01bGiJRHqb1or-uZqRCk5H7xX19gdpVWYy-0sPk",//TODO PLAYSTORE update
            "Accept-Language: en_US",
            "Content-Type: application/json"})
    @POST("api/v1/tasks")
    Call<ResponseBody> taskcreating(@Body JsonObject pickup_details);

    @Headers({
            //"client-id: 9ef64467-61d5-4822-9ccc-b36583e58994", // TODO LOCAL TESTING
            "client-id: dda77830-64e0-4362-85f1-b8acf1bc08e6",  //TODO PLAYSTORE update
            //"Authorization: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJkIjp7InJvbGUiOjEwMCwidWlkIjoiNDk0Zjg2ZGMtOTA0OS00NDZjLTlmMDQtYTMwNDMzMjdkMjZlIn0sIm1lcmNoYW50X3R5cGUiOm51bGwsImNsaWVudF9pZCI6IjllZjY0NDY3LTYxZDUtNDgyMi05Y2NjLWIzNjU4M2U1ODk5NCIsImF1ZCI6Imh0dHBzOi8vaWRlbnRpdHl0b29sa2l0Lmdvb2dsZWFwaXMuY29tL2dvb2dsZS5pZGVudGl0eS5pZGVudGl0eXRvb2xraXQudjEuSWRlbnRpdHlUb29sa2l0IiwibmFtZSI6IkFQSVVTRVIiLCJ1dWlkIjoiNDk0Zjg2ZGMtOTA0OS00NDZjLTlmMDQtYTMwNDMzMjdkMjZlIiwicm9sZSI6MTAwLCJkdW56b19rZXkiOiJhNWQ5YTc2NS0wYjhkLTQzNTAtOThhOC0xZjViZTYzZGM1OWYiLCJleHAiOjE3MTU5NDQ0MjksInYiOjAsImlhdCI6MTU2MDQyNDQyOSwic2VjcmV0X2tleSI6Ik5vbmUifQ.TWAI0vjlFGWlplgAJLD2-DL2mzc2s9hY0G438gT5pTM",// TODO LOCAL TESTING
            "Authorization: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJkIjp7InJvbGUiOjEwMCwidWlkIjoiODNlMjEyMWQtNzYxYy00MjgzLTg1MTctMTAyZDUzMDlmMTgwIn0sIm1lcmNoYW50X3R5cGUiOm51bGwsImNsaWVudF9pZCI6ImRkYTc3ODMwLTY0ZTAtNDM2Mi04NWYxLWI4YWNmMWJjMDhlNiIsImF1ZCI6Imh0dHBzOi8vaWRlbnRpdHl0b29sa2l0Lmdvb2dsZWFwaXMuY29tL2dvb2dsZS5pZGVudGl0eS5pZGVudGl0eXRvb2xraXQudjEuSWRlbnRpdHlUb29sa2l0IiwibmFtZSI6IlZlZGFzIiwidXVpZCI6IjgzZTIxMjFkLTc2MWMtNDI4My04NTE3LTEwMmQ1MzA5ZjE4MCIsInJvbGUiOjEwMCwiZHVuem9fa2V5IjoiZmY0NWEyY2EtZmE3OS00ZmFkLWIzNDAtYzA5ZThjN2ZmZTFiIiwiZXhwIjoxNzIzMDA4NjQ4LCJ2IjowLCJpYXQiOjE1Njc0ODg2NDgsInNlY3JldF9rZXkiOiJOb25lIn0.I9px01bGiJRHqb1or-uZqRCk5H7xX19gdpVWYy-0sPk",//TODO PLAYSTORE update
            "Accept-Language: en_US",
            "Content-Type: application/json"
    })
    @GET("api/v1/tasks/{task_id}/status")
    Call<ResponseBody> getOrdersstatus(@Path("task_id") String taskid);

    @Headers({
            //"client-id: 9ef64467-61d5-4822-9ccc-b36583e58994", // TODO LOCAL TESTING
            "client-id: dda77830-64e0-4362-85f1-b8acf1bc08e6",  //TODO PLAYSTORE update
            //"Authorization: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJkIjp7InJvbGUiOjEwMCwidWlkIjoiNDk0Zjg2ZGMtOTA0OS00NDZjLTlmMDQtYTMwNDMzMjdkMjZlIn0sIm1lcmNoYW50X3R5cGUiOm51bGwsImNsaWVudF9pZCI6IjllZjY0NDY3LTYxZDUtNDgyMi05Y2NjLWIzNjU4M2U1ODk5NCIsImF1ZCI6Imh0dHBzOi8vaWRlbnRpdHl0b29sa2l0Lmdvb2dsZWFwaXMuY29tL2dvb2dsZS5pZGVudGl0eS5pZGVudGl0eXRvb2xraXQudjEuSWRlbnRpdHlUb29sa2l0IiwibmFtZSI6IkFQSVVTRVIiLCJ1dWlkIjoiNDk0Zjg2ZGMtOTA0OS00NDZjLTlmMDQtYTMwNDMzMjdkMjZlIiwicm9sZSI6MTAwLCJkdW56b19rZXkiOiJhNWQ5YTc2NS0wYjhkLTQzNTAtOThhOC0xZjViZTYzZGM1OWYiLCJleHAiOjE3MTU5NDQ0MjksInYiOjAsImlhdCI6MTU2MDQyNDQyOSwic2VjcmV0X2tleSI6Ik5vbmUifQ.TWAI0vjlFGWlplgAJLD2-DL2mzc2s9hY0G438gT5pTM",// TODO LOCAL TESTING
            "Authorization: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJkIjp7InJvbGUiOjEwMCwidWlkIjoiODNlMjEyMWQtNzYxYy00MjgzLTg1MTctMTAyZDUzMDlmMTgwIn0sIm1lcmNoYW50X3R5cGUiOm51bGwsImNsaWVudF9pZCI6ImRkYTc3ODMwLTY0ZTAtNDM2Mi04NWYxLWI4YWNmMWJjMDhlNiIsImF1ZCI6Imh0dHBzOi8vaWRlbnRpdHl0b29sa2l0Lmdvb2dsZWFwaXMuY29tL2dvb2dsZS5pZGVudGl0eS5pZGVudGl0eXRvb2xraXQudjEuSWRlbnRpdHlUb29sa2l0IiwibmFtZSI6IlZlZGFzIiwidXVpZCI6IjgzZTIxMjFkLTc2MWMtNDI4My04NTE3LTEwMmQ1MzA5ZjE4MCIsInJvbGUiOjEwMCwiZHVuem9fa2V5IjoiZmY0NWEyY2EtZmE3OS00ZmFkLWIzNDAtYzA5ZThjN2ZmZTFiIiwiZXhwIjoxNzIzMDA4NjQ4LCJ2IjowLCJpYXQiOjE1Njc0ODg2NDgsInNlY3JldF9rZXkiOiJOb25lIn0.I9px01bGiJRHqb1or-uZqRCk5H7xX19gdpVWYy-0sPk",//TODO PLAYSTORE update
            "Accept-Language: en_US",
            "Content-Type: application/json",

    })
    @POST("api/v1/tasks/{task_id}/_cancel")
    Call<ResponseBody> cancelOrdersstatus(@Path("task_id") String taskid, @Body JsonObject pickup_details);
}
