package com.bingzhu;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.share.LocationShareURLOption;
import com.baidu.mapapi.search.share.OnGetShareUrlResultListener;
import com.baidu.mapapi.search.share.ShareUrlResult;
import com.baidu.mapapi.search.share.ShareUrlSearch;

import java.util.ArrayList;
import java.util.List;

public class Map extends AppCompatActivity {
    //初始化map界面元素
    private Button sendcontactbutton;
    private Button setcontactbutton;
    private MapView mapview;
    public LocationClient locationClient;
    private BaiduMap baidumap;
    private boolean isFirstLocate = true;
    private ShareUrlSearch shareurlsearch;
    String shareUrl ;
    double latitude;
    double longitude;
    OnGetShareUrlResultListener listener ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationClient = new LocationClient(getApplicationContext());
        locationClient.registerLocationListener(new MyLocationListener());
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_map);
        //mapview显示
        mapview = findViewById(R.id.bdmapview);
        baidumap = mapview.getMap();
        setMap(baidumap);
        //按钮活动设置联系人
        setcontactbutton = findViewById(R.id.settingcontact_button);
        setcontactbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListDialog();
            }
        });
        //按钮发送位置信息
        sendcontactbutton = findViewById(R.id.send_button);

        //建立一个onGetShareUrllistener对象用于监听共享位置
         listener = new OnGetShareUrlResultListener() {
            @Override
            public void onGetLocationShareUrlResult(ShareUrlResult shareUrlResult) {
                shareUrl  = shareUrlResult.getUrl();
            }
            //没吊用的两个方法
            @Override
            public void onGetPoiDetailShareUrlResult(ShareUrlResult shareUrlResult) {
            }
            @Override
            public void onGetRouteShareUrlResult(ShareUrlResult shareUrlResult) {
            }
        };
         shareurlsearch = ShareUrlSearch.newInstance();
         shareurlsearch.setOnGetShareUrlResultListener(listener);

         sendcontactbutton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if(!shareUrl.equals("")){
                     sendmsg();
                 }
                 else{
                     Toast.makeText(Map.this , "异常：地址url未获取" , Toast.LENGTH_SHORT).show();
                 }
             }
         });


        //一连串的请求权限
        List<String> permissionlist = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(Map.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionlist.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(Map.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionlist.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(Map.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionlist.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(Map.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            permissionlist.add(Manifest.permission.SEND_SMS);
        }
        if(!permissionlist.isEmpty()){
            String[] permissions = permissionlist.toArray(new String[0]);
            ActivityCompat.requestPermissions(Map.this , permissions , 1);
        }
        else{
            //启动定位功能\
            requestLocation();
        }
    }
    //释放资源的一系列函数簇
    @Override
    protected void onResume() {
        super.onResume();
        mapview.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mapview.onPause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationClient.stop();
        baidumap.setMyLocationEnabled(false);
        if (mapview != null){
            mapview.onDestroy();
        }
        if(shareurlsearch != null) {
            shareurlsearch.destroy();
        }
    }




    //$位置分享方法类、群
    //方法：发送短信
    private void sendmsg(){
        SmsManager smsmanager = SmsManager.getDefault();
        SharedPreferences pref = getSharedPreferences("contactlist" , MODE_PRIVATE);
        String receiver1 = pref.getString("1" , "" );
        String receiver2 = pref.getString("2" , "" );
        if(!TextUtils.isEmpty(receiver1)){
            smsmanager.sendTextMessage(receiver1 , null , "我的位置在此\n"+shareUrl , null , null);
            Toast.makeText(Map.this , "发送联系人1成功" , Toast.LENGTH_SHORT).show();
        }
        if(!TextUtils.isEmpty(receiver2)){
            smsmanager.sendTextMessage(receiver2 , null ,"我的位置在此\n"+ shareUrl , null , null);
            Toast.makeText(Map.this , "发送联系人2成功" , Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(receiver1) && TextUtils.isEmpty(receiver2)){
            Toast.makeText(Map.this , "未设置联系人" , Toast.LENGTH_SHORT).show();
        }
    }






    //&定位方法、类群
    //初始化map图层显示方式
    private void setMap(BaiduMap baidumap){
        baidumap.setMyLocationEnabled(true);
        baidumap.setMyLocationConfiguration( new MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING , false , null));
    }
    //设置location信息方法方法
    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(5000);
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        locationClient.setLocOption(option);
    }
    //方法：navigateTo（）将地图光标移动至当前location
    private void navigateTo(BDLocation location){
        if (isFirstLocate) {
            LatLng ll = new LatLng(location.getLatitude() , location.getLongitude());
            MapStatusUpdate update =  MapStatusUpdateFactory.newLatLng(ll);
            baidumap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(18f);
            baidumap.animateMapStatus(update);
            isFirstLocate = false ;
        }
        MyLocationData.Builder locationbuilder = new MyLocationData.Builder();
        locationbuilder.latitude(location.getLatitude());
        locationbuilder.longitude(location.getLongitude());
        locationbuilder.accuracy(location.getRadius());
        MyLocationData locationdata = locationbuilder.build();
        baidumap.setMyLocationData(locationdata);
    }
    //MyLocationListener定位监听器类
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(final BDLocation location) {
            //处理定位信息逻辑
            if(location.getLocType() == BDLocation.TypeGpsLocation || location.getLocType() == BDLocation.TypeNetWorkLocation  ){
                navigateTo(location);
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                shareurlsearch.requestLocationShareUrl(new LocationShareURLOption()
                        .location(new LatLng(latitude, longitude)).name("位置分享") //分享点名称
                        .snippet("分享人所在地点"));
                Toast.makeText(Map.this , shareUrl +" " +  latitude + longitude , Toast.LENGTH_LONG ).show();
            }
        }
    }
    //方法：定位启动
    private void requestLocation(){
        locationClient.start();
        initLocation();
    }




    //&危险权限的运行时申请
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       if(requestCode == 1) {
                if(grantResults.length > 0 ){
                    for(int result : grantResults){
                        if(result != PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this , "需要同意所有权限才能运行程序" , Toast.LENGTH_LONG).show();
                        }
                            finish();
                            return;
                    }
                    requestLocation();
                }
        }
    }




    //&方法联系人设置方法、类群
    //方法1：联系人设置的主方法，弹出一个选择联系人对话框，由此调用其他方法2、3
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
                //加载一个新的view对象。使按钮和edittext可以使用
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
                //新设置联系人
                savecontact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String new_number = inputcontact.getText().toString();//从edittext中获取号码
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
                        Toast.makeText(Map.this, "修改成功", Toast.LENGTH_SHORT).show();
                        dialog2.dismiss();
                    }
                });
            }
        });
        listDialog.show();
    }

    //方法2：加载已存联系人
    private String load(String key){
        SharedPreferences contact = getSharedPreferences("contactlist",MODE_PRIVATE);
        String number;
        switch(key){
            case "联系人1" :
                number = contact.getString("1","");
                break;
            case "联系人2" :
                number = contact.getString("2","");
                break;
            default:
                number = null;
                break;
        }
        return number;
    }

    //方法3：存储更新联系人
    private void save(String key , String callnumber ){
        SharedPreferences.Editor editor = getSharedPreferences("contactlist",MODE_PRIVATE).edit();
        editor.putString(key , callnumber  );
        editor.apply();
    }

}
