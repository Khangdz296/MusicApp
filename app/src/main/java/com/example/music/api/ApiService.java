package com.example.music.api;

import com.example.music.model.Album;
import com.example.music.model.Artist;
import com.example.music.model.Category;
import com.example.music.model.ChangePasswordResponse;
import com.example.music.model.LoginRequest;
import com.example.music.model.LoginResponse;
import com.example.music.model.Playlist;
import com.example.music.model.ProfileResponse;
import com.example.music.model.RegisterResponse;
import com.example.music.model.Song;
import com.example.music.model.User;
import com.example.music.model.VerifyOtpRequest;
import com.example.music.model.VerifyOtpResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @Headers("Content-Type: application/json")
    @POST("api/register")
    Call<RegisterResponse> register(@Body User user);

    @POST("api/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);  // ĐỔI CHỖ NÀY

    @POST("api/change-password")
    Call<ChangePasswordResponse> changePassword(
            @Header("X-Session-Key") String sessionKey,
            @Body Map<String, String> passwordData);
    @Headers("Content-Type: application/json")
    @POST("api/verify-otp")
    Call<VerifyOtpResponse> verifyOtp(@Body VerifyOtpRequest req);

    @GET("api/profile")
    Call<ProfileResponse> getProfile(@Header("X-Session-Key") String sessionKey);

    // Gọi đúng đường dẫn bên Backend Controller
    @GET("api/songs")
    Call<List<Song>> getAllSongs();

    // Gọi API nhạc mới
    @GET("api/songs/new-updated")
    Call<List<Song>> getNewSongs();

    @GET("api/categories")
    Call<List<Category>> getAllCategories();

    // API lấy bài hát theo danh mục
    @GET("api/categories/{id}/songs")
    Call<List<Song>> getSongsByCategory(@Path("id") Long id);


    @GET("api/songs/top-views")
    Call<List<Song>> getTopSongs();

    @GET("api/songs/search")
    Call<List<Song>> searchSongs(@Query("q") String keyword);

    @GET("api/albums")
    Call<List<Album>> getAllAlbums();
    @GET("api/songs/random")
    Call<List<Song>> getRandomSongs();
    @POST("api/admin/songs")
    Call<Song> addSong(@Body Song song);

    // 2. Xóa bài hát
    @DELETE("api/admin/songs/{id}")
    Call<Void> deleteSong(@Path("id") Long id);

    // 3. Lấy toàn bộ bài hát (Dùng lại API cũ hoặc tạo mới nếu cần phân trang admin)
    @GET("api/songs")
    Call<List<Song>> getAllSongsAdmin();

    // --- PHẦN NGHỆ SĨ (ARTIST) ---

    @GET("api/artists")
    Call<List<Artist>> getAllArtists();

    // ✅ SỬA LẠI: Đường dẫn này phải khớp với SongController ở Backend
    @GET("api/artists/{id}/songs")
    Call<List<Song>> getSongsByArtistId(@Path("id") Long id);

    // ✅ THÊM MỚI: API lấy danh sách Album của 1 nghệ sĩ (Để hiện thêm phần Album)
    @GET("api/artists/{id}/albums")
    Call<List<Album>> getAlbumsByArtistId(@Path("id") Long id);
    @PUT("api/admin/songs/{id}")
    Call<Song> updateSong(@Path("id") Long id, @Body Song song);
  
    @POST("api/admin/artists")
    Call<Artist> addArtist(@Body Artist artist);

    @PUT("api/admin/artists/{id}")
    Call<Artist> updateArtist(@Path("id") Long id, @Body Artist artist);

    @DELETE("api/admin/artists/{id}")
    Call<Void> deleteArtist(@Path("id") Long id);

    // --- QUẢN LÝ CATEGORY ---
    @POST("api/admin/categories")
    Call<Category> addCategory(@Body Category category);

    @PUT("api/admin/categories/{id}")
    Call<Category> updateCategory(@Path("id") Long id, @Body Category category);

    @DELETE("api/admin/categories/{id}")
    Call<Void> deleteCategory(@Path("id") Long id);
    @GET("api/albums/top-views")
    Call<List<Album>> getTop50Albums();

    // 1. Tạo Playlist mới (gửi name và image lên)
    @POST("api/playlists/user/{userId}")
    Call<Playlist> createPlaylist(@Path("userId") Long userId, @Body Playlist playlist);

    // 2. Lấy danh sách Playlist của User
    @GET("api/playlists/user/{userId}")
    Call<List<Playlist>> getUserPlaylists(@Path("userId") Long userId);

    // 3. Thêm bài hát vào Playlist
    @POST("api/playlists/{playlistId}/add-song/{songId}")
    Call<Playlist> addSongToPlaylist(@Path("playlistId") Long playlistId, @Path("songId") Long songId);

    // 4. Xóa bài hát khỏi Playlist
    @DELETE("api/playlists/{playlistId}/remove-song/{songId}")
    Call<Playlist> removeSongFromPlaylist(@Path("playlistId") Long playlistId, @Path("songId") Long songId);
}