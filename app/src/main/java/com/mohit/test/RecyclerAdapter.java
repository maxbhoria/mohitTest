package com.mohit.test;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mohit.test.Api.getClient;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private ArrayList<EmailListResponse> itemModelArrayList;
    private Context context;

    public RecyclerAdapter(Context context, ArrayList<EmailListResponse> itemModelArrayList) {
        this.itemModelArrayList = itemModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.tvSno.setText(String.valueOf(itemModelArrayList.get(holder.getAdapterPosition()).getIdtableEmail()));
        holder.tvEmail.setText(itemModelArrayList.get(holder.getAdapterPosition()).getTableEmailEmailAddress());
        holder.ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditEmail(holder);
            }
        });
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteEmail(holder);
            }
        });
    }

    private void deleteEmail(ViewHolder holder) {
        Call<Boolean> result = getClient().deleteEmail(itemModelArrayList.get(holder.getAdapterPosition()).getIdtableEmail());
        result.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.body()) {
                    Toast.makeText(context, "Email Deleted successfully", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {

            }
        });
        removeAt(holder.getAdapterPosition());
    }

    private void showEditEmail(final ViewHolder holder) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Email");

        final AppCompatEditText et_email = new AppCompatEditText(context);
        et_email.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        et_email.setText(itemModelArrayList.get(holder.getAdapterPosition()).getTableEmailEmailAddress());
        builder.setView(et_email);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (isValid(et_email)) {
                    editEmail(holder, et_email);
                }
            }
        });
        builder.show();
    }

    private boolean isValid(AppCompatEditText et_email) {
        if (!isValidEmail(et_email.getText().toString().trim())) {
            Toast.makeText(context, "Please enter valid email id", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void editEmail(final ViewHolder holder, AppCompatEditText et_email) {
        Call<EmailListResponse> result = getClient().updateEmail(itemModelArrayList.get(holder.getAdapterPosition()).getIdtableEmail(),
                new EmailListResponse(String.valueOf(et_email.getText()), true));
        result.enqueue(new Callback<EmailListResponse>() {
            @Override
            public void onResponse(Call<EmailListResponse> call, Response<EmailListResponse> response) {
                Toast.makeText(context, "Email updated successfully", Toast.LENGTH_SHORT).show();
                itemModelArrayList.set(holder.getAdapterPosition(), new EmailListResponse(
                        response.body().getIdtableEmail(),
                        response.body().getTableEmailEmailAddress(),
                        response.body().getTableEmailValidate()));
                notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<EmailListResponse> call, Throwable t) {

            }
        });
    }

    private void removeAt(int position) {
        itemModelArrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, itemModelArrayList.size());
    }

    @Override
    public int getItemCount() {
        return itemModelArrayList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivEdit, ivDelete;
        TextView tvEmail, tvSno;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSno = itemView.findViewById(R.id.tvSno);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            ivEdit = itemView.findViewById(R.id.ivEdit);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }

    public boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
