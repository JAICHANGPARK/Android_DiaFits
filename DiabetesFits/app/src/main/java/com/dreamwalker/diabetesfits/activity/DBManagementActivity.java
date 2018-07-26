package com.dreamwalker.diabetesfits.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.dreamwalker.diabetesfits.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;
import io.realm.Realm;

public class DBManagementActivity extends AppCompatActivity {

    @BindView(R.id.export_button)
    Button exportButton;
    @BindView(R.id.backup_button)
    Button backupButton;
    @BindView(R.id.delete_button)
    Button deleteButton;

    @BindView(R.id.home)
    ImageView backImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbmanagement);
        ButterKnife.bind(this);
        Paper.init(this);
        Realm.init(this);
        setStatusBar();
    }


    @OnClick(R.id.backup_button)
    public void onClickedBackupButton(){

    }

    @OnClick(R.id.export_button)
    public void onClickedExportButton(){

    }

    @OnClick(R.id.delete_button)
    public void onClickedDeletesButton(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning");
        builder.setMessage("All Data will be erase");
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();


    }

    @OnClick(R.id.home)
    public void onClickedBackImageButton(){
        finish();
    }




    private void setStatusBar() {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.default_background));
    }
}
