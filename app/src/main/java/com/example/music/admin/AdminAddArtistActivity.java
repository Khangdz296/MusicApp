package com.example.music.admin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.music.R;
import com.example.music.api.ApiService;
import com.example.music.api.RetrofitClient;
import com.example.music.model.Artist;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminAddArtistActivity extends AppCompatActivity {

    private EditText edtName, edtImage;
    private Button btnSave, btnCancel;
    private TextView tvHeader;
    private Artist mArtistEdit = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_artist);

        edtName = findViewById(R.id.edtName);
        edtImage = findViewById(R.id.edtImage);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        tvHeader = findViewById(R.id.tvHeader);

        btnCancel.setOnClickListener(v -> finish());

        // Kiểm tra xem có dữ liệu truyền qua để Sửa không
        if (getIntent().getExtras() != null) {
            mArtistEdit = (Artist) getIntent().getSerializableExtra("DATA_ARTIST");
        }

        if (mArtistEdit != null) {
            setupEditMode();
        }

        btnSave.setOnClickListener(v -> {
            if (edtName.getText().toString().isEmpty()) {
                Toast.makeText(this, "Nhập tên ca sĩ!", Toast.LENGTH_SHORT).show();
                return;
            }
            saveData();
        });
    }

    private void setupEditMode() {
        tvHeader.setText("Cập Nhật Ca Sĩ");
        btnSave.setText("LƯU THAY ĐỔI");
        edtName.setText(mArtistEdit.getName());
        edtImage.setText(mArtistEdit.getImageUrl());
    }

    private void saveData() {
        // Tạo object mới hoặc dùng object cũ
        Artist artist = (mArtistEdit == null) ? new Artist() : mArtistEdit;

        artist.setName(edtName.getText().toString());
        artist.setImageUrl(edtImage.getText().toString());

        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        Call<Artist> call;

        if (mArtistEdit == null) {
            // Thêm mới (POST)
            call = api.addArtist(artist);
        } else {
            // Cập nhật (PUT)
            call = api.updateArtist(artist.getId(), artist);
        }

        call.enqueue(new Callback<Artist>() {
            @Override
            public void onResponse(Call<Artist> call, Response<Artist> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminAddArtistActivity.this, "Thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AdminAddArtistActivity.this, "Lỗi server!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<Artist> call, Throwable t) {
                Toast.makeText(AdminAddArtistActivity.this, "Lỗi mạng!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}