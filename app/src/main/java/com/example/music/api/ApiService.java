package com.example.music.api;

import com.example.music.model.Album;
import com.example.music.model.ApiResponse;
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
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
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


    @GET("api/artists")
    Call<List<Artist>> getAllArtists();

    @GET("api/artists/{id}/songs")
    Call<List<Song>> getSongsByArtist(@Path("id") Long id);

    @GET("api/songs/top-views")
    Call<List<Song>> getTopSongs();

    @GET("api/songs/search")
    Call<List<Song>> searchSongs(@Query("q") String keyword);

    @GET("api/playlists/user/{userId}")
    Call<List<Playlist>> getUserPlaylists(@Path("userId") Long userId);

    @GET("api/albums")
    Call<List<Album>> getAllAlbums();

    /**
     * 1. Lấy danh sách Public Playlists (Mục 2.7)
     * GET /api/playlists/public
     */
    @GET("playlists/public")
    Call<List<Playlist>> getPublicPlaylists();


    /**
     * 3. Lấy chi tiết Playlist
     * GET /api/playlists/{id}
     */
    @GET("playlists/{id}")
    Call<Playlist> getPlaylistDetail(@Path("id") Long playlistId);

    /**
     * 4. Thêm bài hát vào Playlist (Mục 2.5)
     * POST /api/playlists/{playlistId}/add-song/{songId}
     */
    @POST("playlists/{playlistId}/add-song/{songId}")
    Call<String> addSongToPlaylist(
            @Path("playlistId") Long playlistId,
            @Path("songId") Long songId
    );

    /**
     * 5. Tạo Playlist mới
     * POST /api/playlists
     */
    @POST("playlists")
    Call<Playlist> createPlaylist(@Body Playlist playlist);


    // ==================== SONG APIs ====================

    /**
     * 6. Toggle Favorite (Mục 2.6)
     * POST /api/songs/{songId}/favorite
     * Backend cần implement API này
     */
    @POST("songs/{songId}/favorite")
    Call<ApiResponse> toggleFavorite(
            @Path("songId") Long songId,
            @Query("userId") Long userId
    );

    /**
     * 7. Lấy danh sách bài hát yêu thích (Mục 2.6)
     * GET /api/songs/favorites/{userId}
     * Backend cần implement API này
     */
    @GET("songs/favorites/{userId}")
    Call<List<Song>> getFavoriteSongs(@Path("userId") Long userId);


}