package com.example.doan.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doan.Models.ApiResponse;
import com.example.doan.Models.LoginResponse;
import com.example.doan.Models.VerifyOtpRequest;
import com.example.doan.Network.RetrofitClient;
import com.example.doan.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpActivity extends AppCompatActivity {

    private static final String TAG = "OtpActivity";

    private EditText otpInput;
    private Button verifyButton;
    private TextView resendOtpText;
    private String userIdentifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        otpInput = findViewById(R.id.input_otp);
        verifyButton = findViewById(R.id.btn_verify_otp);
        resendOtpText = findViewById(R.id.text_resend_otp);

        if (getIntent().hasExtra("USER_IDENTIFIER")) {
            userIdentifier = getIntent().getStringExtra("USER_IDENTIFIER");
        } else {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin người dùng.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Nút Verify OTP
        verifyButton.setOnClickListener(v -> {
            String otp = otpInput.getText().toString().trim();
            if (otp.length() != 6) {
                Toast.makeText(this, "Mã OTP phải có 6 chữ số.", Toast.LENGTH_SHORT).show();
                return;
            }
            verifyOtp(otp);
        });

        // Text resend OTP
        resendOtpText.setOnClickListener(v -> resendOtp());
    }

    // ===============================
    // API #1 — VERIFY OTP
    // ===============================
    private void verifyOtp(String otp) {
        VerifyOtpRequest request = new VerifyOtpRequest(userIdentifier, otp);

        RetrofitClient.getInstance(this).getApiService()
                .verifyOtpNew(request)
                .enqueue(new Callback<ApiResponse<LoginResponse>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<LoginResponse>> call,
                                           @NonNull Response<ApiResponse<LoginResponse>> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<LoginResponse> apiResponse = response.body();

                            if (apiResponse.isSuccess()) {
                                Toast.makeText(OtpActivity.this,
                                        "Xác thực thành công! Vui lòng đăng nhập.",
                                        Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(OtpActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } else {
                                Toast.makeText(OtpActivity.this,
                                        apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(OtpActivity.this,
                                    "Lỗi server: " + response.code(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<LoginResponse>> call, @NonNull Throwable t) {
                        Toast.makeText(OtpActivity.this,
                                "Không thể kết nối đến server: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ===============================
    // API #2 — RESEND OTP
    // ===============================
    private void resendOtp() {

        RetrofitClient.getInstance(this).getApiService()
                .resendOtp(userIdentifier)
                .enqueue(new Callback<ApiResponse<String>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<String>> call,
                                           @NonNull Response<ApiResponse<String>> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<String> apiResponse = response.body();

                            if (apiResponse.isSuccess()) {
                                Toast.makeText(OtpActivity.this,
                                        "OTP đã được gửi lại!",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(OtpActivity.this,
                                        apiResponse.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(OtpActivity.this,
                                    "Lỗi server khi gửi OTP lại",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<String>> call, @NonNull Throwable t) {
                        Toast.makeText(OtpActivity.this,
                                "Không thể kết nối đến server.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
