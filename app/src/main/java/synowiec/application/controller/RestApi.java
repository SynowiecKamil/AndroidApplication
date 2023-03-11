package synowiec.application.controller;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import synowiec.application.model.Treatment;

public interface RestApi {


    //----------------- PATIENT --------------//
    @FormUrlEncoded
    @POST("patient.php")
    Call<ResponseModel> insertPatientData(@Field("action") String action,
                                          @Field("name") String name,
                                          @Field("surname") String surname,
                                          @Field("email") String email,
                                          @Field("password") String password);

    @FormUrlEncoded
    @POST("patient.php")
    Call<String> getPatientLogin(@Field("action") String action,
                                 @Field("email") String email,
                                 @Field("password") String password);

    @FormUrlEncoded
    @POST("patient.php")
    Call<ResponseModel> getPatientData(@Field("action") String action,
                                       @Field("id") String id);

    @FormUrlEncoded
    @POST("patient.php")
    Call<ResponseModel> checkPatientEmail(@Field("action") String action,
                                           @Field("email") String email);

    @FormUrlEncoded
    @POST("patient.php")
    Call<ResponseModel> updatePatient(@Field("action") String action,
                                      @Field("id") String id,
                                      @Field("name") String name,
                                      @Field("email") String email,
                                      @Field("surname") String surname);

    @FormUrlEncoded
    @POST("patient.php")
    Call<ResponseModel> uploadImagePatient(@Field("action") String action,
                                           @Field("id") String id,
                                           @Field("photo") String photo);
    @FormUrlEncoded
    @POST("patient.php")
    Call<ResponseModel> search(@Field("action") String action,
                               @Field("firstParam") String firstParam,
                               @Field("secondParam") String secondParam,
                               @Field("start") String start,
                               @Field("limit") String limit);

    @FormUrlEncoded
    @POST("patient.php")
    Call<ResponseModel> searchTreatment(@Field("action") String action,
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
    Call<ResponseModel> insertTreatment(@Field("action") String action,
                                        @Field("name") String name,
                                        @Field("id") String id,
                                        @Field("price") double price);

    @FormUrlEncoded
    @POST("patient.php")
    Call<ResponseModel> updateTreatment(@Field("action") String action,
                                        @Field("name") String name,
                                        @Field("id") String id,
                                        @Field("price") double price);

    @FormUrlEncoded
    @POST("patient.php")
    Call<ResponseModel> deleteTreatment(@Field("action") String action,
                                        @Field("name") String name,
                                        @Field("id") String id);

    @FormUrlEncoded
    @POST("patient.php")
    Call<ResponseModel> removePatient(@Field("action") String action,
                                      @Field("id") String id);

    //----------------- PHYSIO --------------//
    @GET("physiotherapist.php")
    Call<ResponseModel> retrievePhysio();

    @FormUrlEncoded
    @POST("physiotherapist.php")
    Call<ResponseModel> insertPhysioData(@Field("action") String action,
                                         @Field("name") String name,
                                         @Field("surname") String surname,
                                         @Field("email") String email,
                                         @Field("password") String password);

    @FormUrlEncoded
    @POST("physiotherapist.php")
    Call<ResponseModel> getPhysioLogin(@Field("action") String action,
                                @Field("email") String email,
                                @Field("password") String password);

    @FormUrlEncoded
    @POST("physiotherapist.php")
    Call<ResponseModel> checkPhysioEmail(@Field("action") String action,
                                        @Field("email") String email);

    @FormUrlEncoded
    @POST("physiotherapist.php")
    Call<ResponseModel> getPhysioData(@Field("action") String action,
                               @Field("email") String email);

    @FormUrlEncoded
    @POST("physiotherapist.php")
    Call<ResponseModel> updatePhysio(@Field("action") String action,
                                     @Field("id") String id,
                                     @Field("name") String name,
                                     @Field("email") String email,
                                     @Field("surname") String surname,
                                     @Field("profession_number") String profession_number,
                                     @Field("cabinet") String cabinet,
                                     @Field("description") String description,
                                     @Field("cabinet_address") String cabinet_address,
                                     @Field("days") String days,
                                     @Field("hours") String hours);

    @FormUrlEncoded
    @POST("physiotherapist.php")
    Call<ResponseModel> uploadImagePhysio(@Field("action") String action,
                                          @Field("id") String id,
                                          @Field("photo") String photo);

    @FormUrlEncoded
    @POST("physiotherapist.php")
    Call<ResponseModel> removePhysio(@Field("action") String action,
                                     @Field("id") String id);

    //----------------- appointment --------------//
    @FormUrlEncoded
    @POST("appointment.php")
    Call<ResponseModel> insertAppointment(@Field("action") String action,
                                          @Field("physioId") String physioId,
                                          @Field("patientId") String patientId,
                                          @Field("date") String date,
                                          @Field("time") String time,
                                          @Field("place") String place,
                                          @Field("treatment") String treatment,
                                          @Field("cabinet_address") String cabinet_address,
                                          @Field("price") double price);
    @FormUrlEncoded
    @POST("appointment.php")
    Call<ResponseModel> deleteAppointment(@Field("action") String action,
                                          @Field("id") String id);
    @FormUrlEncoded
    @POST("appointment.php")
    Call<ResponseModel> updateAppointment(@Field("action") String action,
                                          @Field("id") String id,
                                          @Field("date") String date,
                                          @Field("time") String time,
                                          @Field("place") String place,
                                          @Field("treatment") String treatment,
                                          @Field("cabinet_address") String cabinet_address);
    @FormUrlEncoded
    @POST("appointment.php")
    Call<ResponseModel> getAppointment(@Field("action") String action,
                                       @Field("userID") String userID);


}
