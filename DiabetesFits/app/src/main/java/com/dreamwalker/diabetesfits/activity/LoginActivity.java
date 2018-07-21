package com.dreamwalker.diabetesfits.activity;

import android.app.LoaderManager;
import android.content.CursorLoader;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.materiallogin.DefaultLoginView;
import com.dreamwalker.materiallogin.DefaultRegisterView;
import com.dreamwalker.materiallogin.MaterialLoginView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

import static android.Manifest.permission.READ_CONTACTS;

public class LoginActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

//    @BindView(R.id.login)
//    MaterialLoginView loginView;

    private AutoCompleteTextView emailAutoComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
