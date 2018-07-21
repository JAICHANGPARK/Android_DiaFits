package com.dreamwalker.diabetesfits.activity;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
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

import com.dreamwalker.diabetesfits.R;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Paper.init(this);
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL).build();
        service = retrofit.create(IUploadAPI.class);
//        bindView();
        final MaterialLoginView loginView = (MaterialLoginView) findViewById(R.id.login);
        ((DefaultLoginView) loginView.getLoginView()).setListener((loginUser, loginPass) -> {
            String user = loginUser.getEditText().getText().toString();
            if (user.isEmpty()) {
                loginUser.setError("User name can't be empty");
                return;
            }
            loginUser.setError("");

            String pass = loginPass.getEditText().getText().toString();
            if (!pass.equals(user)) {
                loginPass.setError("Wrong password");
                return;
            }
            loginPass.setError("");


            // TODO: 2018-07-21 로그인 구현 하기 - 박제창  
            Snackbar.make(loginView, "Login success!", Snackbar.LENGTH_LONG).show();
        });

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

            // TODO: 2018-07-21  등록 구현하기.

            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle("확인");
            builder.setMessage("등록하시겠어요?");
            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Call<ResponseBody> commnet = service.registerUser(userName, userPassword);
                    commnet.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                Log.e(TAG, "onResponse: " + response.toString());
                                Log.e(TAG, "onResponse: " + response.body().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            Paper.book().write("userName", userName);
                            Paper.book().write("userPwd", userPassword);


                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setTitle("Error");
                            builder.setMessage("에러가 발생 하였습니다." + t.getMessage());
                            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.show();
                        }
                    });
                }
            });
            builder.show();

            Snackbar.make(loginView, "Register success!", Snackbar.LENGTH_LONG).show();
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
