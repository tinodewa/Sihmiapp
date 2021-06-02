package com.roma.android.sihmi.model.network;

import com.roma.android.sihmi.model.response.AboutUsResponse;
import com.roma.android.sihmi.model.response.AgendaResponse;
import com.roma.android.sihmi.model.response.AgendaSingleResponse;
import com.roma.android.sihmi.model.response.AlamatResponse;
import com.roma.android.sihmi.model.response.ContactResponse;
import com.roma.android.sihmi.model.response.ContactSingleResponse;
import com.roma.android.sihmi.model.response.FileResponse;
import com.roma.android.sihmi.model.response.GeneralResponse;
import com.roma.android.sihmi.model.response.JobResponse;
import com.roma.android.sihmi.model.response.KonstituisiResponse;
import com.roma.android.sihmi.model.response.LeaderResponse;
import com.roma.android.sihmi.model.response.LevelResponse;
import com.roma.android.sihmi.model.response.LoginResponse;
import com.roma.android.sihmi.model.response.MasterResponse;
import com.roma.android.sihmi.model.response.MedsosResponse;
import com.roma.android.sihmi.model.response.PendidikanResponse;
import com.roma.android.sihmi.model.response.PengajuanAdminResponse;
import com.roma.android.sihmi.model.response.PengajuanLK1Response;
import com.roma.android.sihmi.model.response.PengajuanResponse;
import com.roma.android.sihmi.model.response.ProfileResponse;
import com.roma.android.sihmi.model.response.SejarahResponse;
import com.roma.android.sihmi.model.response.TotalResponse;
import com.roma.android.sihmi.model.response.TrainingResponse;
import com.roma.android.sihmi.model.response.UploadFileResponse;
import com.roma.android.sihmi.model.response.tempat.KotaResponse;
import com.roma.android.sihmi.model.response.tempat.ProvinsiResponse;
import com.roma.android.sihmi.model.response.tempat.WilayahResponse;

import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface MasterService {

    // Account
    @POST("login")
    Call<LoginResponse> login(
            @Query("username") String username,
            @Query("password") String password,
            @Query("device_name") String device_name
    );

    @POST("login")
    Call<LoginResponse> login(@Body RequestBody data);


    @POST("login")
    Call<LoginResponse> login(@FieldMap HashMap<String, String> params);


    @POST("register")
    Call<GeneralResponse> register(
            @Query("badko") String badko,
            @Query("cabang") String cabang,
            @Query("korkom") String korkom,
            @Query("komisariat") String komisariat,
            @Query("id_roles") String id_roles, //
            @Query("image") String image, //
            @Query("nama_depan") String nama_depan,
            @Query("nama_belakang") String nama_belakang,
            @Query("nama_panggilan") String nama_panggilan,
            @Query("jenis_kelamin") String jenis_kelamin,
            @Query("nomor_hp") String nomor_hp,
            @Query("alamat") String alamat,
            @Query("username") String username,
            @Query("password") String password,
            @Query("tempat_lahir") String tempat_lahir,
            @Query("tanggal_lahir") String tanggal_lahir,
            @Query("status_perkawinan") String status_perkawinan,
            @Query("keterangan") String keterangan
    );

    @GET("logout")
    Call<GeneralResponse> logout(@Header("access-token") String token);

    @POST("forgotpassword")
    Call<GeneralResponse> forgotpassword(@Query("username") String username,
                                         @Query("email") String email);

    @GET("me")
    Call<ProfileResponse> profile(@Header("access-token") String token);

    @GET("me")
    Call<ProfileResponse> getProfile(@Header("access-token") String token);

    @POST("me")
    Call<GeneralResponse> updateProfile(
            @Header("access-token") String token,
            @Query("badko") String badko,
            @Query("cabang") String cabang,
            @Query("korkom") String korkom,
            @Query("komisariat") String komisariat,
            @Query("id_roles") String id_roles, //
            @Query("image") String image, //
            @Query("nama_depan") String nama_depan,
            @Query("nama_belakang") String nama_belakang,
            @Query("nama_panggilan") String nama_panggilan,
            @Query("jenis_kelamin") String jenis_kelamin,
            @Query("nomor_hp") String nomor_hp,
            @Query("alamat") String alamat,
            @Query("username") String username,
            @Query("tempat_lahir") String tempat_lahir,
            @Query("tanggal_lahir") String tanggal_lahir,
            @Query("status_perkawinan") String status_perkawinan,
            @Query("keterangan") String keterangan,
            @Query("email") String email
    );

    @POST("me")
    Call<GeneralResponse> updateProfile(
            @Header("access-token") String token,
            @Query("badko") String badko,
            @Query("cabang") String cabang,
            @Query("korkom") String korkom,
            @Query("komisariat") String komisariat,
            @Query("id_roles") String id_roles, //
            @Query("image") String image, //
            @Query("nama_depan") String nama_depan,
            @Query("nama_belakang") String nama_belakang,
            @Query("nama_panggilan") String nama_panggilan,
            @Query("jenis_kelamin") String jenis_kelamin,
            @Query("nomor_hp") String nomor_hp,
            @Query("alamat") String alamat,
            @Query("username") String username,
            @Query("tempat_lahir") String tempat_lahir,
            @Query("tanggal_lahir") String tanggal_lahir,
            @Query("status_perkawinan") String status_perkawinan,
            @Query("keterangan") String keterangan,
            @Query("email") String email,
            @Query("akun_sosmed") String akun_sosmed,
            @Query("domisili_cabang") String domisili_cabang,
            @Query("pekerjaan") String pekerjaan,
            @Query("jabatan") String jabatan,
            @Query("alamat_kerja") String alamat_kerja,
            @Query("kontribusi") String kontribusi
    );

    @PUT("me")
    Call<ProfileResponse> changePassword(
            @Header("access-token") String token,
            @Query("newpassword") String newpassword,
            @Query("oldpassword") String oldpassword
    );


    // About Us
    @GET("aboutus")
    Call<AboutUsResponse> aboutUs();


    @POST("aboutus")
    Call<GeneralResponse> addAboutUs(@Header("access-token") String token,
                                        @Query("nama") String nama,
                                        @Query("deskripsi") String deskripsi);

    @PUT("aboutus")
    Call<GeneralResponse> updateAboutUs(@Header("access-token") String token,
                                       @Query("id_aboutus") String id_aboutus,
                                       @Query("nama") String nama,
                                       @Query("deskripsi") String deskripsi);


    @HTTP(method = "DELETE", path = "aboutus", hasBody = true)
    Call<GeneralResponse> deleteAboutUs(@Header("access-token") String token,
                                       @Query("id_aboutus") String id_aboutus);

    // Agenda
    @GET("agenda")
    Call<AgendaResponse> getAgenda(@Header("access-token") String token,
                                   @Query("type") String type);

    @GET("agenda")
    Call<AgendaSingleResponse> getAgenda(@Header("access-token") String token,
                                         @Query("type") String type,
                                         @Query("agendaId") String agendaId);

    @POST("agenda")
    Call<GeneralResponse> addAgenda(@Header("access-token") String token,
                                    @Query("nama") String nama,
                                    @Query("deskripsi") String deskripsi,
                                    @Query("image") String image,
                                    @Query("tempat") String tempat,
                                    @Query("lokasi") String lokasi,
                                    @Query("long_expired") long long_expired,
                                    @Query("type") String type);


    @PUT("agenda")
    Call<GeneralResponse> updateAgenda(@Header("access-token") String token,
                                       @Query("id") String id,
                                       @Query("nama") String nama,
                                       @Query("deskripsi") String deskripsi,
                                       @Query("image") String image,
                                       @Query("tempat") String tempat,
                                       @Query("lokasi") String lokasi,
                                       @Query("type") String type);


    @HTTP(method = "DELETE", path = "agenda", hasBody = true)
    Call<GeneralResponse> deleteAgenda(@Header("access-token") String token,
                                       @Query("id") String id);


    //Alamat
    @GET("address")
    Call<AlamatResponse> getAddress(@Header("access-token") String token,
                                     @Query("type") String type);


    @POST("address")
    Call<GeneralResponse> addAddress(@Header("access-token") String token,
                                     @Query("nama") String nama,
                                     @Query("alamat") String alamat,
                                     @Query("latitude") double latitude,
                                     @Query("longitude") double longitude,
                                     @Query("type") String type,
                                     @Query("keterangan") String keterangan);


    @PUT("address")
    Call<GeneralResponse> updateAddress(@Header("access-token") String token,
                                        @Query("id") String id,
                                        @Query("nama") String nama,
                                        @Query("alamat") String alamat,
                                        @Query("latitude") double latitude,
                                        @Query("longitude") double longitude,
                                        @Query("type") String type,
                                        @Query("keterangan") String keterangan);

    @HTTP(method = "DELETE", path = "address", hasBody = true)
    Call<GeneralResponse> deleteAddress(@Header("access-token") String token,
                                        @Query("id") String id);

    // Files
    @GET("files")
    Call<FileResponse> files(@Header("access-token") String token);

    @Multipart
    @POST("files")
    Call<UploadFileResponse> uploadFile(@Header("access-token") String token,
                                        @PartMap Map<String, RequestBody> map);
//                                     @Part("files") RequestBody name);

    //Konstitusi
    @GET("konstitusi")
    Call<KonstituisiResponse> getKonstitusi(@Query("type") String type);

    @POST("konstitusi")
    Call<GeneralResponse> addKonstitusi(@Header("access-token") String token,
                                        @Query("nama") String nama,
                                        @Query("nama_file") String nama_file,
                                        @Query("deskripsi") String deskripsi,
                                        @Query("file") String file,
                                        @Query("keterangan") String keterangan,
                                        @Query("type") String type);

    @PUT("konstitusi")
    Call<GeneralResponse> updateKonstitusi(@Header("access-token") String token,
                                           @Query("id") String id,
                                           @Query("nama") String nama,
                                           @Query("nama_file") String nama_file,
                                           @Query("deskripsi") String deskripsi,
                                           @Query("file") String file,
                                           @Query("keterangan") String keterangan,
                                           @Query("type") String type);

    @HTTP(method = "DELETE", path = "konstitusi", hasBody = true)
    Call<GeneralResponse> deleteKonstitusi(@Header("access-token") String token,
                                           @Query("id") String id);

    //Sejarah
    @GET("sejarah")
    Call<SejarahResponse> getSejarah(@Query("type") int type);

    @POST("sejarah")
    Call<GeneralResponse> addSejarah(@Header("access-token") String token,
                                     @Query("judul") String judul,
                                     @Query("deskripsi") String deskripsi,
                                     @Query("type") String type);

    @PUT("sejarah")
    Call<GeneralResponse> updateSejarah(@Header("access-token") String token,
                                        @Query("id_sejarah") String id_sejarah,
                                        @Query("judul") String judul,
                                        @Query("deskripsi") String deskripsi,
                                        @Query("type") String type);


    @HTTP(method = "DELETE", path = "sejarah", hasBody = true)
    Call<GeneralResponse> deleteSejarah(@Header("access-token") String token,
                                        @Query("id_sejarah") String id_sejarah);

    //Contact
    @GET("user")
    Call<ContactResponse> getContact(@Header("access-token") String token);

    @GET("user")
    Call<ContactSingleResponse> getContact(@Header("access-token") String token,
                                           @Query("userId") String userId);

    //Level LK
    @GET("levellk")
    Call<LevelResponse> getLevelLK();

    //Pengajuan User
    @GET("pengajuanuser")
    Call<PengajuanResponse> getPengajuanUser(@Header("access-token") String token);

    @POST("pengajuanuser")
    Call<GeneralResponse> pengajuanUser(@Header("access-token") String token, @QueryMap HashMap<String, String> params);

    @PUT("pengajuanuser")
    Call<GeneralResponse> updatePengajuanUser(@Header("access-token") String token,
                                              @Query("id_user") String id_user,
                                              @Query("status") int status,
                                              @Query("tujuan_pengajuan") int tujuan_pengajuan);

    //Wilayah
    @GET("provinsi")
    Call<ProvinsiResponse> getProvinsi();

    @GET("kota/{id_provinsi}")
    Call<KotaResponse> getKota(@Path("id_provinsi") int id_provinsi);

    @GET("wilayah/{id_kota}")
    Call<WilayahResponse> getWilayah(@Path("id_kota") int id_kota);

    @GET("kampus/{id_wilayah}")
    Call<ContactResponse> getKampus(@Path("id_wilayah") int id_wilayah);

    @GET("fakultas/{id_kampus}")
    Call<ContactResponse> getFakultas(@Path("id_kampus") int id_kota);


    // Job
    @GET("/job")
    Call<JobResponse> getJob(@Header("access-token") String token);


    @POST("/job")
    Call<GeneralResponse> addJob(@Header("access-token") String token,
                                 @Query("nama_perusahaan") String nama_perusahaan,
                                 @Query("jabatan") String jabatan,
                                 @Query("alamat") String alamat,
                                 @Query("tahun_mulai") String tahun_mulai,
                                 @Query("tahun_berakhir") String tahun_berakhir);


    @PUT("/job")
    Call<GeneralResponse> updateJob(@Header("access-token") String token,
                                    @Query("idjob") String id,
                                    @Query("nama_perusahaan") String nama_perusahaan,
                                    @Query("jabatan") String jabatan,
                                    @Query("alamat") String alamat,
                                    @Query("tahun_mulai") String tahun_mulai,
                                    @Query("tahun_berakhir") String tahun_berakhir);


    @HTTP(method = "DELETE", path = "/job", hasBody = true)
    Call<GeneralResponse> deleteJob(@Header("access-token") String token,
                                    @Query("idjob") String id);

    // Kuliah
    @GET("/kuliah")
    Call<PendidikanResponse> getPendidikan(@Header("access-token") String token,
                                           @Query("user_id") String user_id);


    @POST("/kuliah")
    Call<GeneralResponse> addPendidikan(@Header("access-token") String token,
                                        @Query("tahun") String tahun,
                                        @Query("strata") String strata,
                                        @Query("kampus") String kampus,
                                        @Query("jurusan") String jurusan);


    @PUT("/kuliah")
    Call<GeneralResponse> updatePendidikan(@Header("access-token") String token,
                                           @Query("tahun") String tahun,
                                           @Query("strata") String strata,
                                           @Query("kampus") String kampus,
                                           @Query("jurusan") String jurusan);


    @HTTP(method = "DELETE", path = "/kuliah", hasBody = true)
    Call<GeneralResponse> deletePendidikan(@Header("access-token") String token,
                                           @Query("idkuliah") String id);

    //Medsos
    @GET("/medsos")
    Call<MedsosResponse> getMedsos(@Header("access-token") String token);

    @POST("/medsos")
    Call<GeneralResponse> addMedsos(@Header("access-token") String token,
                                    @Query("medsos") String medsos,
                                    @Query("username") String username);


    @PUT("/medsos")
    Call<GeneralResponse> updateMedsos(@Header("access-token") String token,
                                       @Query("idmedsos") String id,
                                       @Query("medsos") String medsos,
                                       @Query("username") String username);


    @HTTP(method = "DELETE", path = "/medsos", hasBody = true)
    Call<GeneralResponse> deleteMedsos(@Header("access-token") String token,
                                       @Query("idmedsos") String id);

    //Training
    @GET("/training")
    Call<TrainingResponse> getTraining(@Header("access-token") String token,
                                       @Query("user_id") String user_id);

    @POST("/training")
    Call<GeneralResponse> addTraining(@Header("access-token") String token,
                                      @Query("tipe") String tipe,
                                      @Query("tahun") String tahun,
                                      @Query("nama") String nama,
                                      @Query("lokasi") String lokasi);

    @POST("/training")
    Call<GeneralResponse> addTraining(@Header("access-token") String token,
                                      @Query("userId") String userId,
                                      @Query("tipe") String tipe,
                                      @Query("tahun") String tahun,
                                      @Query("nama") String nama,
                                      @Query("lokasi") String lokasi);


    @PUT("/training")
    Call<GeneralResponse> updateTraining(@Header("access-token") String token,
                                         @Query("idtraining") String id,
                                         @Query("tipe") String tipe,
                                         @Query("tahun") String tahun,
                                         @Query("nama") String nama,
                                         @Query("lokasi") String lokasi);


    @HTTP(method = "DELETE", path = "/training", hasBody = true)
    Call<GeneralResponse> deleteTraining(@Header("access-token") String token,
                                         @Query("idtraining") String id);

    @HTTP(method = "DELETE", path = "/training", hasBody = true)
    Call<GeneralResponse> deleteTraining(@Header("access-token") String token,
                                         @Query("userId") String userId,
                                         @Query("type") String type);

    //Total All
    @GET("/getTotalAll")
    Call<TotalResponse> getTotal();

    @PUT("/updateuserlevel")
    Call<GeneralResponse> updateUserLevel(@Header("access-token") String token,
                                           @Query("id_user") String id_user,
                                           @Query("id_level") int level);

    //Master
    @GET("/master")
    Call<MasterResponse> getMaster(@Header("access-token") String token,
                                   @Query("type") String type);

    Call<MasterResponse> getMaster(@Header("access-token") String token,
                                   @Query("type") String type,
                                   @Query("parentId") String parentId);

    @POST("/master")
    Call<GeneralResponse> addMaster(@Header("access-token") String token,
                                    @Query("type") String type,
                                    @Query("value") String value);

    @POST("/master")
    Call<GeneralResponse> addMaster(@Header("access-token") String token,
                                     @Query("type") String type,
                                     @Query("value") String value,
                                     @Query("parentId") String parentId);

    @PUT("/master")
    Call<GeneralResponse> updateMaster(@Header("access-token") String token,
                                       @Query("id_master") String id_master,
                                       @Query("type") String type,
                                       @Query("value") String value);

    @PUT("/master")
    Call<GeneralResponse> updateMaster(@Header("access-token") String token,
                                        @Query("id_master") String id_master,
                                        @Query("type") String type,
                                        @Query("value") String value,
                                        @Query("parentId") String parentId);


    @HTTP(method = "DELETE", path = "/master", hasBody = true)
    Call<GeneralResponse> deleteMaster(@Header("access-token") String token,
                                        @Query("id_master") String id_master);

    //Leader
    @GET("/leader")
    Call<LeaderResponse> getLeader(@Header("access-token") String token,
                                   @Query("type") String type);

    @POST("/leader")
    Call<GeneralResponse> addLeader(@Header("access-token") String token,
                                    @Query("nama") String nama,
                                    @Query("periode") String periode,
                                    @Query("sampai") String sampai,
                                    @Query("image") String image,
                                    @Query("type") String type);

    @PUT("/leader")
    Call<GeneralResponse> updateLeader(@Header("access-token") String token,
                                       @Query("id_leader") String id_leader,
                                       @Query("nama") String nama,
                                       @Query("periode") String periode,
                                       @Query("sampai") String sampai,
                                       @Query("image") String image,
                                       @Query("type") String type);

    @HTTP(method = "DELETE", path = "/leader", hasBody = true)
    Call<GeneralResponse> deleteLeader(@Header("access-token") String token,
                                       @Query("id_leader") String id_leader);

    //Pengajuan LK1
    @GET("pengajuanlk1")
    Call<PengajuanLK1Response> getPengajuanLK1(@Header("access-token") String token);

    @POST("pengajuanlk1")
    Call<GeneralResponse> addPengajuanLK1(@Header("access-token") String token,
                                    @Query("badko") String badko,
                                    @Query("cabang") String cabang,
                                    @Query("korkom") String korkom,
                                    @Query("komisariat") String komisariat,
                                    @Query("tanggal_lk1") String tanggal_lk1,
                                    @Query("tahun_lk1") String tahun_lk1);

    @PUT("pengajuanlk1")
    Call<GeneralResponse> updatePengajuanLK1(@Header("access-token") String token,
                                       @Query("id_pengajuan") String id_pengajuan,
                                       @Query("status") String status);

    //Pengajuan Admin
    @GET("pengajuanadmin")
    Call<PengajuanAdminResponse> getPengajuanAdmin(@Header("access-token") String token);

    @POST("pengajuanadmin")
    Call<GeneralResponse> addPengajuanAdmin(@Header("access-token") String token,
                                          @Query("roles_id") String roles_id,
                                          @Query("file") String file);

    @PUT("pengajuanadmin")
    Call<GeneralResponse> updatePengajuanAdmin(@Header("access-token") String token,
                                             @Query("id_pengajuan") String id_pengajuan,
                                             @Query("status_approve") String status);
}
