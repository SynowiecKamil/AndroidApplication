package synowiec.application.controller;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RestApi {


    @GET("physiotherapist.php")
    Call<ResponseModel> retrievePhysio();


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
    Call<String> getPhysioLogin(@Field("action") String action,
                                @Field("email") String email,
                                @Field("password") String password);

    @FormUrlEncoded
    @POST("patient.php")
    Call<String> getPatientLogin(@Field("action") String action,
                                 @Field("email") String email,
                                 @Field("password") String password);

    @FormUrlEncoded
    @POST("physiotherapist.php")
    Call<String> getPhysioData(@Field("action") String action,
                               @Field("id") String id);

    @FormUrlEncoded
    @POST("patient.php")
    Call<String> getPatientData(@Field("action") String action,
                                @Field("id") String id);

    @FormUrlEncoded
    @POST("physiotherapist.php")
    Call<ResponseModel> updatePhysio(@Field("action") String action,
                                     @Field("id") String id,
                                     @Field("name") String name,
                                     @Field("email") String email,
                                     @Field("surname") String surname,
                                     @Field("profession_number") String profession_number,
                                     @Field("cabinet") String cabinet,
                                     @Field("description") String description);

    @FormUrlEncoded
    @POST("patient.php")
    Call<ResponseModel> updatePatient(@Field("action") String action,
                                      @Field("id") String id,
                                      @Field("name") String name,
                                      @Field("email") String email);

    @FormUrlEncoded
    @POST("patient.php")
    Call<ResponseModel> uploadImagePatient(@Field("action") String action,
                                           @Field("id") String id,
                                           @Field("photo") String photo);

    @FormUrlEncoded
    @POST("physiotherapist.php")
    Call<ResponseModel> uploadImagePhysio(@Field("action") String action,
                                          @Field("id") String id,
                                          @Field("photo") String photo);

    @FormUrlEncoded
    @POST("patient.php")
    Call<ResponseModel> search(@Field("action") String action,
                               @Field("query") String query,
                               @Field("start") String start,
                               @Field("limit") String limit);

    @FormUrlEncoded
    @POST("patient.php")
    Call<String> fillSpinner(@Field("action") String action);

    @FormUrlEncoded
    @POST("patient.php")
    Call<ResponseModel> searchByFilter(@Field("action") String action,
                                        @Field("query") String query);

    @FormUrlEncoded
    @POST("patient.php")
    Call<ResponseModel> removePatient(@Field("action") String action, @Field("id") String id);

    @FormUrlEncoded
    @POST("physiotherapist.php")
    Call<ResponseModel> removePhysio(@Field("action") String action, @Field("id") String id);
}
