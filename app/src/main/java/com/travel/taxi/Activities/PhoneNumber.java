package com.travel.taxi.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.rilixtech.CountryCodePicker;
import com.travel.taxi.ApiResponse.Login.LoginResponse;
import com.travel.taxi.ApiResponse.SignupResponse.SignUpResponse;
import com.travel.taxi.Connection.Services;
import com.travel.taxi.Connection.Utils;
import com.travel.taxi.MainActivity;
import com.travel.taxi.Map.Intermediate;
import com.travel.taxi.Model.User;
import com.travel.taxi.R;
import com.travel.taxi.Utils.Constants;
import com.travel.taxi.Utils.LocalPersistence;
import com.travel.taxi.Utils.NetworkUtil;
import com.travel.taxi.Utils.SharedPreferenceHelper;

import java.util.concurrent.TimeUnit;

import cdflynn.android.library.checkview.CheckView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;
public class PhoneNumber extends AppCompatActivity implements View.OnClickListener {
    CountryCodePicker ccp;

    CheckView check;
    Button sentortrybtn;
    private ImageView errormsg;
    private TextView nextbtn, otpnext;
    private LinearLayout sentlayout, phonelayout, otplayout;
    //Add it below the lines where you declared the fields
    FirebaseAuth mAuth;
    public EditText phoneNumber;
    private User currentUser;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String mVerificationId;
    private Services mApi;
    Context context;
    String smsCode;
    Bundle bundle;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_phone_number);
        initialization();
        if (ActivityCompat.checkSelfPermission(this, READ_SMS) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, READ_PHONE_NUMBERS) ==
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager tMgr = (TelephonyManager)   this.getSystemService(Context.TELEPHONY_SERVICE);
            String mPhoneNumber = tMgr.getLine1Number();
            Log.e("PhoneNumber ", "onRequestPermissionsResult: "+mPhoneNumber );

            return;
        } else {
            requestPermission();
        }

//        phoneNumber.setText(tMgr.getLine1Number());
        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validatePhoneNumber()) {
                    Log.e("NUMBER", "onClick:getFullNumber() "+ccp.getFullNumber());
                    Log.e("NUMBER", "onClick:getFullNumberWithPlus "+ccp.getFullNumberWithPlus());
                    Log.e("NUMBER", "onClick:getNumber "+ccp.getNumber());
                    Log.e("NUMBER", "onClick:getDefaultCountryCode "+ccp.getDefaultCountryCode());
                    Log.e("NUMBER", "onClick:getSelectedCountryCode "+ccp.getSelectedCountryCode());
                    startPhoneNumberVerification(ccp.getFullNumberWithPlus().concat(phoneNumber.getText().toString()));

                }
                phonelayout.setVisibility(View.GONE);
                sentlayout.setVisibility(View.VISIBLE);


            }
        });

        sentortrybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sentlayout.setVisibility(View.GONE);
                otplayout.setVisibility(View.VISIBLE);
            }
        });

        otpnext.setOnClickListener(next ->
        {
            startActivity(new Intent(getApplicationContext(), Intermediate.class));
        });


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.e("phone", "onVerificationCompleted:" + credential);
                Log.e("phone", "onVerificationCompleted: getSmsCode " + credential.getSmsCode());
                smsCode=credential.getSmsCode();
//                signInWithPhoneAuthCredential(credential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                Log.w("phone", "onVerificationFailed" + e.getLocalizedMessage());
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(PhoneNumber.this, "invalid phone number", Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
//                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
//                            Snackbar.LENGTH_SHORT).show();
                    Toast.makeText(PhoneNumber.this, "Quota exceeded", Toast.LENGTH_SHORT).show();

                }
                errormsg.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                Log.e("phone", "onCodeSent:" + verificationId);
                mVerificationId = verificationId;
                mResendToken = token;
                check.check();

            }
        };

        final PinEntryEditText pinEntry = (PinEntryEditText) findViewById(R.id.txt_pin_entry);
        if (pinEntry != null) {
            pinEntry.setOnPinEnteredListener(new PinEntryEditText.OnPinEnteredListener() {
                @Override
                public void onPinEntered(CharSequence str) {
                    if (str.toString().equals(smsCode)) {
                        signup(ccp.getFullNumberWithPlus()+phoneNumber.getText().toString());
                        Intent intent= new Intent(getApplicationContext(), Intermediate.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(PhoneNumber.this, "please enter correct code", Toast.LENGTH_SHORT).show();
                        pinEntry.setText(null);
                    }
                }
            });
        }


    }
private void signup(String number){
    currentUser.setmPhoneNumber(number);
//    ProgressDialog dialog=new ProgressDialog(PhoneNumber.this,R.style.AppCompatAlertDialogStyle);
//    dialog.setMessage("Signing up");
//    dialog.show();
    if(NetworkUtil.isConnectedToWifi(PhoneNumber.this)||NetworkUtil.isConnectedToMobileNetwork(PhoneNumber.this)){

        mApi.signup("android",
            currentUser.getmDeviceToken(),
            currentUser.getmDeviceId(),
            "manual",
            currentUser.getmFirstName(),
            currentUser.getmLastName(),
            currentUser.getmEmail(),
            currentUser.getmPhoneNumber(),
            currentUser.getmPassword()).enqueue(new Callback<SignUpResponse>() {
        @Override
        public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
if(response.isSuccessful()){
    Constants.curr_password = currentUser.getmPassword();
    Toast.makeText(PhoneNumber.this, "Successfully signup", Toast.LENGTH_SHORT).show();
    Log.e("SignUp", "onResponse: "+response.body().getResponse().getUser().getEmail());
    LoginResponse loginResponse=new LoginResponse();
    com.travel.taxi.ApiResponse.Login.Response response1=new com.travel.taxi.ApiResponse.Login.Response();
    com.travel.taxi.ApiResponse.Login.User user=new com.travel.taxi.ApiResponse.Login.User();
    com.travel.taxi.ApiResponse.SignupResponse.User user1=response.body().getResponse().getUser();
    user.setFirstName(user1.getFirstName());
    user.setDeviceId(user1.getDeviceId());
    user.setDeviceToken(user.getDeviceToken());
    user.setEmail(user1.getEmail());
    user.setLastName(user1.getLastName());
    user.setId(user1.getId());
    user.setMobile(user1.getMobile());
    user.setDeviceType(user1.getDeviceType());
    user.setPicture("https://firebasestorage.googleapis.com/v0/b/favour-ab5af.appspot.com/o/Anas%20bhai%2FHTB1qSxEJpXXXXX2XXXXq6xXFXXXx.jpg?alt=media&token=a98fc4ef-052d-43fc-b7ee-f2ab620a299d");
response1.setUser(user);
response1.setAccessToken(response.body().getResponse().getAccessToken());
response1.setTokenType(response.body().getResponse().getTokenType());
    LocalPersistence.witeObjectToFile(PhoneNumber.this,response1);

    SharedPreferenceHelper.setSharedPreferenceString(PhoneNumber.this,"accesstoken",response.body().getResponse().getAccessToken());
//dialog.dismiss();
}
else{
//    dialog.dismiss();
    Toast.makeText(PhoneNumber.this, "Failed", Toast.LENGTH_SHORT).show();

}
        }
        @Override
        public void onFailure(Call<SignUpResponse> call, Throwable t) {
//            dialog.dismiss();
            Toast.makeText(PhoneNumber.this, "Failure", Toast.LENGTH_SHORT).show();

        }

            });
    }
    else{
//        dialog.dismiss();
        NetworkUtil.showNoInternetAvailableErrorDialog(PhoneNumber.this);
    }



}
    private void initialization() {
        bundle=new Bundle();
        sentortrybtn = findViewById(R.id.sentortrybtn);
        check = findViewById(R.id.check);
        phoneNumber=findViewById(R.id.phonenumber);
        nextbtn = findViewById(R.id.phonenextbtn);
        otpnext = findViewById(R.id.otpcontinue);
        phonelayout = findViewById(R.id.phonelayout);
        sentlayout = findViewById(R.id.sentlayout);
        otplayout = findViewById(R.id.otplayout);
        errormsg=findViewById(R.id.errormsg);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
             currentUser = (User)getIntent().getSerializableExtra("currentUser"); //Obtaining data
        }
        ccp = findViewById(R.id.ccp);
        //Add it in the onCreate method, after calling method initFields()
        mAuth = FirebaseAuth.getInstance();
        mApi= Utils.getApiService();
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("phone", "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            startActivity(new Intent(PhoneNumber.this, MainActivity.class));
                            finish();
                        } else {
                            Log.e("phone", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(PhoneNumber.this, "Invalid toast", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                120,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    private boolean validatePhoneNumber() {

        if (TextUtils.isEmpty(phoneNumber.getText().toString())) {
        phoneNumber.setError("empty phone number");
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {


    }


    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{READ_SMS, READ_PHONE_NUMBERS, READ_PHONE_STATE}, 100);
        }
    }
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100:
                TelephonyManager tMgr = (TelephonyManager)  this.getSystemService(Context.TELEPHONY_SERVICE);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) !=
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED  &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) !=      PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                String mPhoneNumber = tMgr.getLine1Number();
                Log.e("PhoneNumber ", "onRequestPermissionsResult: "+mPhoneNumber );
                phoneNumber.setText(mPhoneNumber);
                break;
        }
    }
}
