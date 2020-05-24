package com.mohit.test;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ArrayList<EmailListResponse> emailList = new ArrayList<>();
    private RecyclerAdapter recyclerAdapter;
    private RecyclerView recyclerView;
    private ActionBar actionBar;
    private ImageView imageView;
    private AppCompatEditText et_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        setUpActionBar();
        onClick();
        getEmails();
    }

    private void onClick() {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
    }

    private void showDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add Email");

        et_email = new AppCompatEditText(MainActivity.this);
        LinearLayout.LayoutParams layoutParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParam.rightMargin = 40;
        et_email.setLayoutParams(layoutParam);
        et_email.setHint("Enter Email here");
        builder.setView(et_email);


        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("Add Email", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (isValid()) {
                    dialogInterface.dismiss();
                    addEmail();
                }
            }
        });
        builder.show();
    }

    private boolean isValid() {
        if (!isValidEmail(et_email.getText().toString().trim())) {
            Toast.makeText(this, "Please enter valid email id", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void setUpActionBar() {
        if (actionBar != null) {
            actionBar.setDisplayOptions(actionBar.getDisplayOptions() | ActionBar.DISPLAY_SHOW_CUSTOM);
            imageView = new ImageView(actionBar.getThemedContext());
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setImageResource(R.drawable.ic_add_black_24dp);
            ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                    ActionBar.LayoutParams.WRAP_CONTENT, Gravity.END
                    | Gravity.CENTER_VERTICAL);
            layoutParams.rightMargin = 40;
            imageView.setLayoutParams(layoutParams);
            actionBar.setCustomView(imageView);
        }
    }

    private void addEmail() {
        Call<EmailListResponse> result = Api.getClient().addEmail(new EmailListResponse(String.valueOf(et_email.getText()), true));
        result.enqueue(new Callback<EmailListResponse>() {
            @Override
            public void onResponse(Call<EmailListResponse> call, Response<EmailListResponse> response) {
                emailList.add(response.body());
                setUpAdapter();
            }

            @Override
            public void onFailure(Call<EmailListResponse> call, Throwable t) {

            }
        });
    }

    private void getEmails() {
        Call<ArrayList<EmailListResponse>> result = Api.getClient().getEmails();
        result.enqueue(new Callback<ArrayList<EmailListResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<EmailListResponse>> call, Response<ArrayList<EmailListResponse>> response) {
                emailList = response.body();
                setUpAdapter();
            }

            @Override
            public void onFailure(Call<ArrayList<EmailListResponse>> call, Throwable t) {

            }
        });
    }

    private void setUpAdapter() {
        recyclerAdapter = new RecyclerAdapter(MainActivity.this, emailList);
        recyclerView.setAdapter(recyclerAdapter);
    }

    private void initViews() {
        actionBar = getSupportActionBar();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}
