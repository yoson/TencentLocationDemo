package com.example.distancetest;

import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;

public class MainActivity extends Activity implements Observer{


  private TextView mLocationStatus;
  StringBuffer sb = new StringBuffer();
  LocationProxy mProxy = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_template);
    mLocationStatus = (TextView) findViewById(R.id.status);
	TencentLocationRequest request = TencentLocationRequest.create()
			.setInterval(5000)
			.setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_NAME)
			.setAllowCache(true);
	TencentLocationManager locationManager = TencentLocationManager
			.getInstance(this);
	mProxy = new LocationProxy(locationManager,request);
	mProxy.addObserver(this);
	mProxy.RequestLocationUpdates();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    // 退出 activity 前一定要停止定位!
    mProxy.stopLocationUpdates();
  }



  // ====== view listener

  // 响应点击"停止"
  public void stopLocation(View view) {
    Toast.makeText(getApplicationContext(), "暂停计算里程", Toast.LENGTH_SHORT).show();


	//TencentDistanceAnalysis tda = TencentLocationManager.getInstance(this).stopCalDistance(this);
	//String line = "D|confidence=" + tda.getConfidence() + ",gpscount=" + tda.getGpsCount() + ",networkcount=" + tda.getNetworkCount()+ "\n";
	
    //mLocationStatus.setText(line);
    mProxy.stopCalculateDistance();


  }

  // 响应点击"开始"
  public void startLocation(View view) {
	Toast.makeText(getApplicationContext(), "开始计算里程", Toast.LENGTH_SHORT).show();
    mProxy.startCalculateDistance();
	  
  }

  public void clearStatus(View view) {
    sb = new StringBuffer();
    mLocationStatus.setText(null);
  }

  private Handler mHandler = new Handler(){  
      @Override  
      public void handleMessage(Message msg) {  
          switch (msg.what) {  
          case 1:  
        	  mLocationStatus.setText(msg.obj.toString()); 
              break;   
           default:
        	   super.handleMessage(msg);  
          }
      }  
        
  };  

@Override
public void update(Observable observable, Object data) {
	// TODO Auto-generated method stub
	TencentLocation arg0 = mProxy.getmLocation();
    sb.append("timestamp:"+System.currentTimeMillis()/1000+"   当前位置: " +arg0.getProvider()+"," +arg0.getLatitude() + ",  " + arg0.getLongitude() + "  行驶   "+mProxy.getmDistance()
	          + " Km\n");
    Message msg = new Message();
    msg.what =1;
    msg.obj = sb;
    mHandler.sendMessage(msg);
	
}

}
