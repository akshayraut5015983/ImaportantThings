package com.jobs.shopper.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.jobs.shopper.R;
import com.jobs.shopper.api.ApiServiceProvider;
import com.jobs.shopper.api.RetrofitListener;
import com.jobs.shopper.utils.ErrorObject;
import com.jobs.shopper.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;


public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, RetrofitListener {
    private EditText ed_email, ed_pass;
    private ApiServiceProvider apiServiceProvider;
    private Dialog dialog;
    private TextView tvSplecial, btnReg, tvSignUp, tvSignIn;
    Spinner spnAccountType;
    String[] country = {"Account Type", "Candidate", "Employer"};
    private static String[] GENRES = new String[]{
            "Accounting", "Automotive and Repair", "Banking", "Design", "Digital", "Education", "Engineering",
            "Finance", "Graduate", "IT", "Legal", "Leisure and Tourism", "Manufacturing", "Marketing and PR", "Public Sector", "Retail", "Sales"
    };
    private String strSpecialization = "";
    ListView listView;
    Dialog dialogSpl;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        apiServiceProvider = ApiServiceProvider.getInstance(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.progress_dialog);
        builder.setCancelable(true);
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        findViewById(R.id.imgBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        dialogSpl = new Dialog(RegisterActivity.this);
        dialogSpl.setContentView(R.layout.layout_specialisation);
        dialogSpl.setCancelable(true);
        dialogSpl.setTitle("Title...");
        listView = (ListView) dialogSpl.findViewById(R.id.List);

        onBindData();


    }

    private void onBindData() {
        String strSignUp = "<font color='#999F9F'>Don't You have an account? </font><font color='FFFFFFFF'>Sign Up</font>";
        String strSignIn = "<font color='#999F9F'>Alreday have an account? </font><font color='FFFFFFFF'>Sign In</font>";
        tvSplecial = findViewById(R.id.edSpecia);
        spnAccountType = findViewById(R.id.sppinerAcType);
        spnAccountType.setOnItemSelectedListener(this);


        findViewById(R.id.btnSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                finish();
            }
        });
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, country);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnAccountType.setAdapter(aa);

        tvSplecial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listView.setAdapter(new ArrayAdapter<String>(RegisterActivity.this, android.R.layout.simple_list_item_multiple_choice, GENRES));
                dialogSpl.show();
            }
        });
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setItemChecked(2, true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SparseBooleanArray sp = listView.getCheckedItemPositions();
                String strSpecialization = "";
                for (int ik = 0; ik < sp.size(); ik++) {
                    strSpecialization += GENRES[sp.keyAt(ik)] + ",";
                    //  tvSplecial.setText("");
                    tvSplecial.setText(strSpecialization);
                    Log.e("TAG", "onItemClick: " + strSpecialization);
                }
                Toast.makeText(RegisterActivity.this, "" + strSpecialization, Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void validateData() {
        String email, password;
        email = ed_email.getText().toString().trim();
        password = ed_pass.getText().toString().trim();
        boolean validate_entries = true;

        if (TextUtils.isEmpty(email)) {
            validate_entries = false;
            Toast.makeText(this, "Please enter email id", Toast.LENGTH_SHORT).show();
            return;
        }

       /* if (!CommonUtils.isEmailValid(email) && email.length() != 10) {
            validate_entries = false;
            Toast.makeText(this, "Please enter correct number", Toast.LENGTH_SHORT).show();
            return;
        }*/
        if (TextUtils.isEmpty(password)) {
            validate_entries = false;
            Toast.makeText(this, "Please enter Password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (validate_entries) {
            if (NetworkUtils.isNetworkConnected(RegisterActivity.this)) {
                // CommonUtils.showLoadingDialog(LoginActivity.this).show();
                // dialog.show();
                //   apiServiceProvider.getRequestLogin(email, password, com.sam.samsuite.activity.LoginActivity.this);

            } else {
                Snackbar.make(findViewById(android.R.id.content), "Please check internet connection", Snackbar.LENGTH_SHORT).show();

            }
        }

    }

    @Override
    public void onResponseSuccess(Object responseBody, int apiFlag) {
        dialog.dismiss();
        if (responseBody == null) {
            Toast.makeText(this, "Please check username and password", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "onResponseSuccess: nulll ");
        } else {
            Log.d("TAG", "onResponseSuccess: not null");
        }
      /*  if (responseBody instanceof LoginResponse) {
            LoginResponse loginResponse = (LoginResponse) responseBody;

            //  Toast.makeText(this, "" + loginResponse.getItem().getToken().toString() + loginResponse.getItem().getFirstName(), Toast.LENGTH_LONG).show();
            SharePreferenceUtility.saveBooleanPreferences(com.sam.samsuite.activity.LoginActivity.this, Constants.AppConst.IS_LOGIN, true);
            SharePreferenceUtility.saveObjectPreferences(com.sam.samsuite.activity.LoginActivity.this, Constants.AppConst.LOGIN_ITEMS, loginResponse);
            startActivity(new Intent(getApplicationContext(), LoadingActivity.class));

        }*/
    }

    @Override
    public void onResponseError(ErrorObject errorObject, Throwable throwable, int apiFlag) {
        dialog.dismiss();
        Snackbar.make(findViewById(android.R.id.content), "Invalid username or password" + throwable.getLocalizedMessage(), Snackbar.LENGTH_SHORT).show();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        // Toast.makeText(getApplicationContext(), country[i], Toast.LENGTH_LONG).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
