package com.bingzhu;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Map extends AppCompatActivity {
    Button button1;
    Button button2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //设置联系人的按钮活动-------------------------------------
        button2 = findViewById(R.id.settingcontact_button);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListDialog();
            }
        });

    }

    //设置联系人的对话框方法--------------------------------------------
    private void showListDialog(){
        final String[] items = {"联系人1", "联系人2"};
        AlertDialog.Builder listDialog = new AlertDialog.Builder(Map.this);
        listDialog.setTitle("选取您要设置的紧急联系人");
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(Map.this,"点击了"+items[which],Toast.LENGTH_SHORT).show();
            }
        });
        listDialog.show();
    }
 }
