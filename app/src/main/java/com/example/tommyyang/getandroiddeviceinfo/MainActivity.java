package com.example.tommyyang.getandroiddeviceinfo;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import com.example.tommyyang.utils.GetTime;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    TextView tx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tx = (TextView) findViewById(R.id.displayInfo);
        final TelephonyManager telephonyManager = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        final WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        displayDeviceInfo(telephonyManager);
    }

    public void writeFile(View view) {
        Toast.makeText(this, "开始写入", Toast.LENGTH_SHORT).show();
        final TelephonyManager telephonyManager = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        writeFileToInternalStorage(getDeviceInfo(telephonyManager));
        Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
    }

    private String getDeviceInfo(TelephonyManager telephonyManager) {
        String fileContent = "guid|" + getGuid(this) + "\r\n" + "imei|" + getImei(telephonyManager) + "\r\n" +
                "devicebrand|" + getDeviceBrand() + "\r\n" + "devicemodel|" + getDeviceModel() + "\r\n" + "devicename|" + getDeviceName() + "\r\n" +
                "displayname|" + getDisplayName() + "\r\n" + "incrementalname|" + getIncrementalName() + "\r\n" + "ostype|" + getOSType() + "\r\n" +
                "osvername|" + getOsverName() + "\r\n" + "osversioncode|" + getOsversionCode() + "\r\n" + "iccid|" + getIccid(telephonyManager) + "\r\n" +
                "imsi|" + getImsi(telephonyManager) + "\r\n" + "mac|" + getMacAddress(this) + "\r\n" + "androidId|" + getAndroidId() + "\r\n" +
                "phonenumber|" + getPhoneNumber(telephonyManager) + "\r\n" + "ssid|" + getSSid(this) + "\r\n" + "serial|" + getSerial() + "\r\n" +
                "buildnumber|" + getIncrementalName() + "\r\n" + "bluetooth|" + getBluetooth() + "\r\n" + "routermac|" + getRouterMac(this);
        return fileContent;
    }

    private void writeFileToInternalStorage(String fileContent) {
        String FILENAME = System.currentTimeMillis() + "deviceinfo" + ".txt";
        try {
            File file = new File(Environment.getExternalStorageDirectory(),
                    FILENAME);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream outStream = new FileOutputStream(file, true);
            outStream.write(fileContent.getBytes());
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayDeviceInfo(TelephonyManager telephonyManager) {
        tx.setText("guid: " + getGuid(this) + "\n" + "imei: " + getImei(telephonyManager) + "\n" +
                "DeviceBrand: " + getDeviceBrand() + "\n" + "devicemodel: " + getDeviceModel() + "\n" + "devicename: " + getDeviceName() + "\n" +
                "displayname: " + getDisplayName() + "\n" + "incrementalname: " + getIncrementalName() + "\n" + "ostype: " + getOSType() + "\n" +
                "osvername: " + getOsverName() + "\n" + "osversioncode: " + getOsversionCode() + "\n" + "iccid: " + getIccid(telephonyManager) + "\n" +
                "imsi: " + getImsi(telephonyManager) + "\n" + "mac: " + getMacAddress(this) + "\n" + "androidId: " + getAndroidId() + "\n" +
                "phonenumber: " + getPhoneNumber(telephonyManager) + "\n" + "ssid: " + getSSid(this) + "\n" + "serial: " + getSerial() + "\n" +
                "buildnumber: " + getIncrementalName() + "\n" + "bluetooth: " + getBluetooth() + "\n" + "routermac: " + getRouterMac(this));
    }

    private String getRouterMac(Context context) {
        WifiManager wifiManager = (WifiManager)
                getSystemService(context.WIFI_SERVICE);
        List<ScanResult> results = wifiManager.getScanResults();
        String message = "No results. Check wireless is on";
        if (results != null) {
            final int size = results.size();
            if (size == 0) message = "No access points in range";
            else {
                ScanResult bestSignal = results.get(0);
                int count = 1;
                for (ScanResult result : results) {
                    if (WifiManager.compareSignalLevel(bestSignal.level,
                            result.level) < 0)
                        bestSignal = result;
                }
                message = bestSignal.BSSID;
            }
        }
        return message;
    }

    private synchronized static String getGuid(Context context) {
        String sID;
        File installation = new File(context.getFilesDir(), "INSTALLATION");
        try {
            if (!installation.exists())
                writeInstallationFile(installation);
            sID = readInstallationFile(installation);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String guid = "A" + sID;
        return guid.replace("-", "").substring(0, 16);
    }

    private static String readInstallationFile(File installation) throws IOException {
        RandomAccessFile f = new RandomAccessFile(installation, "r");
        byte[] bytes = new byte[(int) f.length()];
        f.readFully(bytes);
        f.close();
        return new String(bytes);
    }

    private static void writeInstallationFile(File
                                                      installation) throws IOException {
        FileOutputStream out = new FileOutputStream(installation);
        String id = UUID.randomUUID().toString();
        out.write(id.getBytes());
        out.close();
    }

//	private String getGuid(TelephonyManager telephonyManager){
//	   String deviceId = getImei(telephonyManager);
//	   String androidId = getAndroidId();
//	   String serial = getSerial(telephonyManager);
//		UUID deviceUuid = new UUID(androidId.hashCode(), ((long)deviceId.hashCode() << 32) | serial.hashCode());
//	    String uniqueId = deviceUuid.toString();
//        return uniqueId.replace("-", "");
//    }

    private String getImei(TelephonyManager telephonyManager) {
        return telephonyManager.getDeviceId();
    }

    /*
   * 获取手机品牌
   * */
    private String getDeviceBrand() {
        return Build.BRAND;
    }

    /*
    * 获取手机型号
    * */
    private String getDeviceModel() {
        return Build.MODEL;
    }

    /*
    * 获取手机名称
    * */
    private String getDeviceName() {
        return Build.MANUFACTURER;
    }

    private String getDisplayName() {
        return Build.DISPLAY;
    }

    private String getIncrementalName() {
        //return Build.FINGERPRINT;
//    	String osBuildNumber = Build.FINGERPRINT;  //"google/shamu/shamu:5.1.1/LMY48Y/2364368:user/release-keys”
//        final String forwardSlash = "/";
//        String osReleaseVersion = Build.VERSION.RELEASE + forwardSlash;
//        try {
//            osBuildNumber = osBuildNumber.substring(osBuildNumber.indexOf(osReleaseVersion));  //"5.1.1/LMY48Y/2364368:user/release-keys”
//            osBuildNumber = osBuildNumber.replace(osReleaseVersion, "");  //"LMY48Y/2364368:user/release-keys”
//            osBuildNumber = osBuildNumber.substring(0, osBuildNumber.indexOf(forwardSlash)); //"LMY48Y"
//        } catch (Exception e) {
//           return "null";
//        }
//
//        return osBuildNumber;
        return Build.VERSION.INCREMENTAL;
    }

    private String getOSType() {
        return "android-" + android.os.Build.VERSION.SDK;
    }

    private String getOsverName() {
        return android.os.Build.VERSION.RELEASE;
    }

    private String getOsversionCode() {
        return android.os.Build.VERSION.SDK;
    }

    private String getIccid(TelephonyManager telephonyManager) {
        return telephonyManager.getSimSerialNumber();
    }

    private String getImsi(TelephonyManager telephonyManager) {
        return telephonyManager.getSubscriberId();
    }

    private static String getMacAddress(Context context) {
        // 获取mac地址：
        String macAddress = "00:00:00:00:00:00";
        try {
            WifiManager wifiMgr = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
            if (null != info) {
                if (!TextUtils.isEmpty(info.getMacAddress()))
                    macAddress = info.getMacAddress();
                else
                    return macAddress;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return macAddress;
        }
        return macAddress;
    }

    private String getAndroidId() {
        return Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private String getPhoneNumber(TelephonyManager telephonyManager) {
        return telephonyManager.getLine1Number();
    }

    private String getSSid(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getSSID();
    }

    private String getMacAddress() {
        String result = "";
        String Mac = "";
        result = callCmd("busybox ifconfig", "HWaddr");
        if (result == null) {
            return "网络出错，请检查网络";
        }
        if (result.length() > 0 && result.contains("HWaddr")) {
            Mac = result.substring(result.indexOf("HWaddr") + 6, result.length() - 1);
            if (Mac.length() > 1) {
                result = Mac.toLowerCase();
            }
        }
        return result.trim();
    }

    private String getSerial() {
        return Build.SERIAL;
    }

    private String getBluetooth() {
        if (BluetoothAdapter.getDefaultAdapter() == null) {
            return "no bluetooth";
        }
        return BluetoothAdapter.getDefaultAdapter().getAddress();
    }

    private int getWidth(WindowManager windowManager) {
        return windowManager.getDefaultDisplay().getWidth();
    }

    private int getHeight(WindowManager windowManager) {
        return windowManager.getDefaultDisplay().getHeight();
    }

    private String getLanguage() {
        return Locale.getDefault().getLanguage();
    }


    private String callCmd(String cmd, String filter) {
        String result = "";
        String line = "";
        try {
            Process proc = Runtime.getRuntime().exec(cmd);
            InputStreamReader is = new InputStreamReader(proc.getInputStream());
            BufferedReader br = new BufferedReader(is);
            //执行命令cmd，只取结果中含有filter的这一行
            while ((line = br.readLine()) != null && line.contains(filter) == false) {
                result += line;
            }
            result = line;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
