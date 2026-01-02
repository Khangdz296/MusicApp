package com.example.music.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyOtpActivity extends AppCompatActivity {

    EditText otp1, otp2, otp3, otp4, otp5, otp6;
    ImageButton btnVerifyOTP;
    ApiService apiService;

    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_verifyotp_huy);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        username = getIntent().getStringExtra("username");

        otp1 = findViewById(R.id.otp1);
        otp2 = findViewById(R.id.otp2);
        otp3 = findViewById(R.id.otp3);
        otp4 = findViewById(R.id.otp4);
        otp5 = findViewById(R.id.otp5);
        otp6 = findViewById(R.id.otp6);
        btnVerifyOTP = findViewById(R.id.btnVerifyOTP);

        setupAutoMove();

        btnVerifyOTP.setOnClickListener(v -> verifyOTP());
    }

    private void setupAutoMove() {
        EditText[] arr = {otp1, otp2, otp3, otp4, otp5, otp6};

        for (int i = 0; i < arr.length; i++) {
            int index = i;

            arr[i].addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if (arr[index].getText().length() == 1 && index < arr.length - 1) {
                        arr[index + 1].requestFocus();
                    }
                }
            });
        }
    }

    private void verifyOTP() {
        String otp =
                otp1.getText().toString().trim() +
                        otp2.getText().toString().trim() +
                        otp3.getText().toString().trim() +
                        otp4.getText().toString().trim() +
                        otp5.getText().toString().trim() +
                        otp6.getText().toString().trim();

        if (otp.length() != 6) {
            Toast.makeText(this, "Nhập đủ 6 số OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        VerifyOtpRequest req = new VerifyOtpRequest(username, otp);

        apiService.verifyOtp(req).enqueue(new Callback<VerifyOtpResponse>() {
            @Override
            public void onResponse(Call<VerifyOtpResponse> call, Response<VerifyOtpResponse> response) {

                if (!response.isSuccessful()) {
                    Toast.makeText(VerifyOtpActivity.this, "OTP sai hoặc hết hạn", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(VerifyOtpActivity.this, "Xác minh OTP thành công!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<VerifyOtpResponse> call, Throwable t) {
                Toast.makeText(VerifyOtpActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
