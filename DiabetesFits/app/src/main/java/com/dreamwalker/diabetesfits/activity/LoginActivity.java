package com.dreamwalker.diabetesfits.activity;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.model.Validate;
import com.dreamwalker.diabetesfits.remote.IUploadAPI;
import com.dreamwalker.materiallogin.DefaultLoginView;
import com.dreamwalker.materiallogin.DefaultRegisterView;
import com.dreamwalker.materiallogin.MaterialLoginView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import io.paperdb.Paper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.Manifest.permission.READ_CONTACTS;
import static com.dreamwalker.diabetesfits.consts.Url.BASE_URL;

public class LoginActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "LoginActivity";
    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

//    @BindView(R.id.login)
//    MaterialLoginView loginView;

    private AutoCompleteTextView emailAutoComplete;

    Retrofit retrofit;
    IUploadAPI service;

    String userName;
    String userPassword;
    String userUUID;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Paper.init(this);
        progressDialog = new ProgressDialog(this);

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(IUploadAPI.class);
//        bindView();

        String id = Paper.book("user").read("userID");
        String pwd = Paper.book("user").read("userPassword");


        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Loading..");
        progressDialog.show();

        if (id != null && pwd != null) {
            Call<Validate> autoLoginQueue = service.userLogin(id, pwd);
            autoLoginQueue.enqueue(new Callback<Validate>() {
                @Override
                public void onResponse(Call<Validate> call, Response<Validate> response) {
                    // TODO: 2018-07-22 로그인 처리
                    Log.e(TAG, "onResponse: " + response.body().getSuccess());
                    String result = response.body().getSuccess();
                    if (result.equals("true")) {
                        progressDialog.dismiss();
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        // TODO: 2018-07-22 로그인 실패
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setTitle("Error");
                        builder.setMessage("자동 로그인 실패");
                        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss());
                        builder.show();
                    }
                }

                @Override
                public void onFailure(Call<Validate> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        } else {
            progressDialog.dismiss();
        }

        final MaterialLoginView loginView = (MaterialLoginView) findViewById(R.id.login);
        // TODO: 2018-07-22 로그인 구현합니다. - 박제창  
        ((DefaultLoginView) loginView.getLoginView()).setListener((loginUser, loginPass) -> {
            String user = loginUser.getEditText().getText().toString();
            if (user.isEmpty()) {
                loginUser.setError("User name can't be empty");
                return;
            }
            loginUser.setError("");

            String pass = loginPass.getEditText().getText().toString();
//            if (!pass.equals(user)) {
//                loginPass.setError("Wrong password");
//                return;
//            }
            if (pass.length() == 0) {
                loginPass.setError("Wrong password");
                return;
            }
            loginPass.setError("");

            Call<Validate> loginQueue = service.userLogin(user, pass);

            loginQueue.enqueue(new Callback<Validate>() {
                @Override
                public void onResponse(Call<Validate> call, Response<Validate> response) {
                    // TODO: 2018-07-22 로그인 처리
                    Log.e(TAG, "onResponse: " + response.body().getSuccess());
                    String result = response.body().getSuccess();
                    if (result.equals("true")) {
                        // TODO: 2018-07-22 로그인 성공
                        // TODO: 2018-07-26 로그인 성공 시 캐시 저장 - 박제창
                        Paper.book("user").write("userID", user);
                        Paper.book("user").write("userPassword", pass);

                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));

                        finish();
                    } else {
                        // TODO: 2018-07-22 로그인 실패
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setTitle("Error");
                        builder.setMessage("정확한 정보를 입력해주세요");
                        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss());
                        builder.show();
                    }
                }

                @Override
                public void onFailure(Call<Validate> call, Throwable t) {

                    Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

//            // TODO: 2018-07-21 로그인 구현 하기 - 박제창
//            Snackbar.make(loginView, "Login success!", Snackbar.LENGTH_LONG).show();
        });

        // TODO: 2018-07-22 회원 가입  구현 합니다. - 박제창  
        ((DefaultRegisterView) loginView.getRegisterView()).setListener((registerUser, registerPass, registerPassRep) -> {
            String user = registerUser.getEditText().getText().toString();
            if (user.isEmpty()) {
                registerUser.setError("User name can't be empty");
                return;
            }
            registerUser.setError("");

            String pass = registerPass.getEditText().getText().toString();
            if (pass.isEmpty()) {
                registerPass.setError("Password can't be empty");
                return;
            }
            registerPass.setError("");

            String passRep = registerPassRep.getEditText().getText().toString();
            if (!pass.equals(passRep)) {
                registerPassRep.setError("Passwords are different");
                return;
            }
            registerPassRep.setError("");

            userName = user;
            userPassword = pass;
            // TODO: 2018-07-22 서버와 중복 확인 큐 - 박제창
            Call<Validate> validate = service.registerValidate(userName);
            validate.enqueue(new Callback<Validate>() {
                @Override
                public void onResponse(Call<Validate> call, Response<Validate> response) {
                    Log.e(TAG, "onResponse: " + response.toString());
                    Log.e(TAG, "onResponse: " + response.body());

                    String result = response.body().getSuccess();
                    Log.e(TAG, "onResponse: " + result);

                    if (result.equals("true")) {
                        // TODO: 2018-07-22 중복 없을 경우 회원가입 진행 - 박제창
                        //            // TODO: 2018-07-21  등록 구현하기.

                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setTitle("확인");
                        builder.setMessage("등록하시겠어요?");
                        builder.setPositiveButton("확인", (dialog, which) -> {
                            Call<ResponseBody> commnet = service.registerUser(userName, userPassword);
                            commnet.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call1, Response<ResponseBody> response1) {
                                    try {
                                        Log.e(TAG, "onResponse: " + response1.toString());
                                        Log.e(TAG, "onResponse: " + response1.body().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    // TODO: 2018-07-26 로그인 성공시에만 저장하도록 변경합니다. - 박제창
                                    //Paper.book().write("userName", userName);
                                    //Paper.book().write("userPassword", userPassword);

                                    Snackbar.make(loginView, "Register success!", Snackbar.LENGTH_LONG).show();

                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call1, Throwable t) {
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
                                    builder1.setTitle("Error");
                                    builder1.setMessage("에러가 발생 하였습니다." + t.getMessage());
                                    builder1.setPositiveButton("확인", (dialog1, which1) -> dialog1.dismiss());
                                    builder1.show();
                                }
                            });
                        });
                        builder.show();

                    } else {
                        // TODO: 2018-07-22 중복될 경우 알람
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setTitle("Error");
                        builder.setMessage("아이디가 중복됩니다.");
                        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss());
                        builder.show();
                    }
                }

                @Override
                public void onFailure(Call<Validate> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


//            Snackbar.make(loginView, "Register success!", Snackbar.LENGTH_LONG).show();
        });

        emailAutoComplete = (AutoCompleteTextView) findViewById(R.id.login_user_autocomplete);
        populateAutoComplete();
    }

    private void bindView() {
        ButterKnife.bind(this);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(emailAutoComplete, "Contacts permissions are needed for providing email completions.", Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, v -> requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS));
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI, ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,
                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE + " = ?", new String[]{ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        List<String> emails = new ArrayList<>();
        data.moveToFirst();
        while (!data.isAfterLast()) {

            emails.add(data.getString(ProfileQuery.ADDRESS));
            data.moveToNext();

        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(LoginActivity.this, android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        emailAutoComplete.setAdapter(adapter);
    }
}
