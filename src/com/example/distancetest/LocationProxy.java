package com.example.distancetest;

import java.util.Observable;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import com.tencent.map.geolocation.TencentDistanceAnalysis;
import com.tencent.map.geolocation.TencentDistanceListener;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;

public class LocationProxy extends Observable implements TencentLocationListener,TencentDistanceListener{

	private TencentLocation mLocation;
	private double mDistance;
	private double mError;
	private String mReason;
	private TencentLocationRequest mRequest;
	private int requestRet;
	private TencentLocationManager mLocationManager;
	private Runnable r;
	
	
	public TencentLocation getmLocation() {
		return mLocation;
	}
	
	public double getmDistance() {
		return mDistance;
	}

	public double getmError() {
		return mError;
	}

	public String getmReason() {
		return mReason;
	}

	public LocationProxy(TencentLocationManager locationManager,TencentLocationRequest request){
		mLocationManager = locationManager;
		mRequest = request;
	}
	
	public int RequestLocationUpdates(){
		r = new Runnable() {
			public void run() {
				requestRet = mLocationManager.requestLocationUpdates(mRequest, LocationProxy.this, WorkerThread.getLooper());
			}
		};
		WorkerThread.run(r);
		return requestRet;
	}
	public void stopLocationUpdates(){
		mLocationManager.removeUpdates(this);
		WorkerThread.remove(r);
	}
	
	public int startCalculateDistance(){
		return mLocationManager.startDistanceCalculate(this);
	}
	public TencentDistanceAnalysis stopCalculateDistance(){
		return mLocationManager.stopDistanceCalculate(this);
	}
	@Override
	public void onDistanceChanged(TencentLocation arg0, double arg1, int arg2,
			String arg3) {
		// TODO Auto-generated method stub
		mDistance = arg1;
		
	}
	@Override
	public void onLocationChanged(TencentLocation arg0, int arg1, String arg2) {
		// TODO Auto-generated method stub
		mLocation = arg0;
		mError = arg1;
		mReason = arg2;
		setChanged();
		notifyObservers();
	}
	@Override
	public void onStatusUpdate(String arg0, int arg1, String arg2) {
		// TODO Auto-generated method stub	
	}
	
	private static class WorkerThread{
        private static HandlerThread mThread;
        private static Handler mHandler;

        protected static boolean isAlive(){
            return mThread != null && mThread.isAlive();
        }

        protected static boolean start(){
            if(isAlive())
                return false;

            mThread = new HandlerThread("tecent_location_worker");
            mThread.start();
            mHandler = new Handler(mThread.getLooper());
            return true;
        }

        protected static boolean run(Runnable r){
            start();
            return mHandler.post(r);
        }

        protected static boolean remove(Runnable r){
            if(!isAlive())
                return false;
            mHandler.removeCallbacks(r);
            return true;
        }

        protected static Looper getLooper(){
            start();
            return mThread.getLooper();
        }
    }
}
