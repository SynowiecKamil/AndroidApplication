package synowiec.application.controller;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RestApi {


    @GET("physiotherapist.php")
    Call<ResponseModel> retrieve();


    @FormUrlEncoded
    @POST("patient.php")
    Call<ResponseModel> insertPatientData(@Field("action") String action,
                                   @Field("name") String name,
                                   @Field("email") String email,
                                   @Field("password") String password);

    @FormUrlEncoded
    @POST("physiotherapist.php")
    Call<ResponseModel> insertPhysioData(@Field("action") String action,
                                          @Field("name") String name,
                                          @Field("email") String email,
                                          @Field("password") String password);

    @FormUrlEncoded
    @POST("physiotherapist.php")
    Call<ResponseModel> login(@Field("action") String action,
                              @Field("email") String email,
                              @Field("password") String password);

    @FormUrlEncoded
    @POST("physiotherapist.php")
    Call<ResponseModel> updateData(@Field("action") String action,
                                   @Field("name") String name,
                                   @Field("email") String email,
                                   @Field("password") String password);

    @FormUrlEncoded
    @POST("physiotherapist.php")
    Call<ResponseModel> search(@Field("action") String action,
                               @Field("query") String query,
                               @Field("start") String start,
                               @Field("limit") String limit);

    @FormUrlEncoded
    @POST("patient.php")
    Call<ResponseModel> removePatient(@Field("action") String action, @Field("id") String id);

    @FormUrlEncoded
    @POST("physiotherapist.php")
    Call<ResponseModel> removePhysio(@Field("action") String action, @Field("id") String id);
}
