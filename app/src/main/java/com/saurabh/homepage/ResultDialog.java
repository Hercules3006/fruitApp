package com.saurabh.homepage;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class ResultDialog {
    private Context context;
    private AlertDialog  dialog;
    public ResultDialog(Context context){
        this.context = context;
    }
    String classId = null;
    void initDialog(){
        dialog = new AlertDialog.Builder(context)
                .setPositiveButton("Xem chi tiết", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(context, DetailActivity.class);
                        if(!classId.equals("None")){
                            intent.putExtra("Class", classId);
                            context.startActivity(intent);
                        }else{
                            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Đóng", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        hide();
                    }
                }).create()
                ;
    }
    void show(String result){
        dialog.setTitle(result);
        dialog.show();
    }
    void hide(){
        try{
            dialog.cancel();
        }catch (Exception e){

        }
    }
}
