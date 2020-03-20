package com.bingzhu;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Map extends AppCompatActivity {
    //初始化map界面元素
    Button button1;
    Button setcontactbutton;

    //oncreate()在这里
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //按钮活动-------------------------------------
        setcontactbutton = findViewById(R.id.settingcontact_button);
        setcontactbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListDialog();
            }
        });

    }


    //方法

    //方法：主方法设置联系人
    private void showListDialog(){
        final String[] items = {"联系人1", "联系人2"};
        AlertDialog.Builder listDialog = new AlertDialog.Builder(Map.this);
        listDialog.setTitle("选取您要设置的紧急联系人");

        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            //点击联系人之后
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String contactor = items[which];
                String saved_number;
                //新的对话窗口弹出
                AlertDialog.Builder bulider = new AlertDialog.Builder(Map.this);
                View view1 =View.inflate(Map.this,R.layout.dialog_contactsetting,null);
                bulider.setView(view1);//加载进去
                final AlertDialog dialog2 = bulider.create();
                final EditText inputcontact = view1.findViewById(R.id.inputcontact_edittext);
                Button savecontact = view1.findViewById(R.id.savecontact_button);
                saved_number = load(contactor);//根据不同联系人加载电话号码
                dialog2.show();

                //加载之前设置的联系人
                if(!TextUtils.isEmpty(saved_number)) {
                    inputcontact.setText(saved_number);
                    inputcontact.setSelection(saved_number.length());
                }

                //设置联系人
                savecontact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String new_number = inputcontact.getText().toString();
                            switch(contactor){
                            case "联系人1" :
                                save("1", new_number);
                                break;
                            case "联系人2" :
                                save("2", new_number);
                                break;
                            default:
                                break;
                        }
                        dialog2.dismiss();
                    }
                });
            }
        });
        listDialog.show();
    }

    //方法：加载已存联系人
    private String load(String key){
        SharedPreferences contact = getSharedPreferences("contactlist",MODE_PRIVATE);
        String number;
        switch(key){
            case "联系人1" :
                number = contact.getString("1",null);
                break;
            case "联系人2" :
                number = contact.getString("2",null);
                break;
            default:
                number = null;
                break;
        }
        return number;
    }


    //方法：存储更新联系人
    private void save(String key , String callnumber ){
        SharedPreferences.Editor editor = getSharedPreferences("contactlist",MODE_PRIVATE).edit();
        editor.putString(key , callnumber  );
        editor.apply();
    }

 }
