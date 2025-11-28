package com.example.doan.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doan.Models.ApiResponse;
import com.example.doan.Models.OtpRequest;
import com.example.doan.Models.OtpVerificationResponse;
import com.example.doan.Network.RetrofitClient;
import com.example.doan.R;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpVerificationActivity extends AppCompatActivity {

    private static final String TAG = "OtpVerificationActivity";

    private TextInputEditText otpInput;
    private Button verifyButton;
    private TextView resendOtpText;
    private TextView otpInstructionText;
    private String userPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        otpInput = findViewById(R.id.input_otp_code);
        verifyButton = findViewById(R.id.btn_verify_otp);
        resendOtpText = findViewById(R.id.text_resend_otp);
        otpInstructionText = findViewById(R.id.text_otp_instruction);

        if (getIntent() != null && getIntent().hasExtra("phone")) {
            userPhone = getIntent().getStringExtra("phone");
            otpInstructionText.setText("Mã xác thực đã được gửi đến số điện thoại " + userPhone);
        } else {
            Toast.makeText(this, "Không nhận được thông tin số điện thoại.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        verifyButton.setOnClickListener(v -> attemptOtpVerification());
        resendOtpText.setOnClickListener(v -> resendOtp());
    }

    private void attemptOtpVerification() {
        String otp = otpInput.getText().toString().trim();

        if (otp.length() != 6) {
            Toast.makeText(this, "Vui lòng nhập mã OTP gồm 6 chữ số.", Toast.LENGTH_SHORT).show();
            return;
        }

        OtpRequest otpRequest = new OtpRequest(userPhone, otp);

        RetrofitClient.getInstance(this).getApiService().verifyOtp(otpRequest).enqueue(new Callback<ApiResponse<OtpVerificationResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<OtpVerificationResponse>> call, @NonNull Response<ApiResponse<OtpVerificationResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<OtpVerificationResponse> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        Toast.makeText(OtpVerificationActivity.this, "Xác thực tài khoản thành công!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(OtpVerificationActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        String message = apiResponse.getMessage() != null ? apiResponse.getMessage() : "Xác thực thất bại.";
                        Toast.makeText(OtpVerificationActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(OtpVerificationActivity.this, "Mã OTP không hợp lệ hoặc đã hết hạn.", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "OTP verification failed, Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<OtpVerificationResponse>> call, @NonNull Throwable t) {
                Toast.makeText(OtpVerificationActivity.this, "Không thể kết nối đến server.", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Connection error: " + t.getMessage());
            }
        });
    }

    private void resendOtp() {
        OtpRequest otpRequest = new OtpRequest(userPhone, null); // OTP is not needed for resend
        RetrofitClient.getInstance(this).getApiService().resendOtp(otpRequest).enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<String>> call, @NonNull Response<ApiResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(OtpVerificationActivity.this, "Mã OTP mới đã được gửi.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(OtpVerificationActivity.this, "Không thể gửi lại mã OTP.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<String>> call, @NonNull Throwable t) {
                Toast.makeText(OtpVerificationActivity.this, "Lỗi kết nối khi gửi lại OTP.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}