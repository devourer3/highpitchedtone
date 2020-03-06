package com.mymusic.orvai.high_pitched_tone;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class Camera_facedetection extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2, View.OnClickListener {

    private static final String TAG = "kb_Open_Cv";
    private CameraBridgeViewBase mOpenCvCameraView;
    private Mat matInput;
    private Mat matResult, matResult2;
    private long face_value;
    private Button shutter_btn;
    static final int PERMISSIONS_REQUEST_CODE = 1000;
    String[] PERMISSIONS = {"android.permission.CAMERA"};
    public long cascadeClassifier_face = 0;
    public long cascadeClassifier_eye = 0;
    private boolean c_gray, c_warm, c_fancy, c_reverse = false;
    Bitmap bitmap;

    static {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("native-lib");
    }

    public native long loadCascade(String cascadeFileName);
//
    public native long face_detect(long cascadeClassifier_face, long cascadeClassifier_eye, long matAddrInput, long matAddrResult);

    public native void ConvertRGBtoGray(long matAddrInput, long matAddrResult);

    public native void ConvertRGBtoWarm(long matAddrInput, long matAddrResult);

    public native void ConvertRGBtoFancy(long matAddrInput, long matAddrResult);

    public native void ConvertRGBtoreverse(long matAddrInput, long matAddrResult);

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    mOpenCvCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_camera_facedetection);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //퍼미션 상태 확인
            if (!hasPermissions(PERMISSIONS)) {

                //퍼미션 허가 안되어있다면 사용자에게 요청
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            } else read_cascade_file(); //추가
        } else read_cascade_file(); //추가

        mOpenCvCameraView = findViewById(R.id.face_detection);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
//        mOpenCvCameraView.setCameraIndex(0); // front-camera(1),  back-camera(0)
        mOpenCvCameraView.setCameraIndex(0); // front-camera(1),  back-camera(0)
        mOpenCvCameraView.enableFpsMeter();
        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        shutter_btn = findViewById(R.id.shutter_btn);
        findViewById(R.id.c_gray).setOnClickListener(this);
        findViewById(R.id.c_warm).setOnClickListener(this);
        findViewById(R.id.c_fancy).setOnClickListener(this);
        findViewById(R.id.c_reverse).setOnClickListener(this);
        findViewById(R.id.c_default).setOnClickListener(this);

        shutter_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (face_value == 1) {
                    if (c_reverse == false && c_gray == false && c_fancy == false && c_warm == false) {
                        bitmap = bitmap.createBitmap(matInput.cols(), matInput.rows(), Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(matInput, bitmap);
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                        String picture = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "디텍팅 임시이미지", "임시 이미지");
                        Intent intent = getIntent();
                        intent.putExtra("picture", picture);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        bitmap = bitmap.createBitmap(matResult2.cols(), matResult2.rows(), Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(matResult2, bitmap);
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                        String picture = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "디텍팅 임시이미지", "임시 이미지");
                        Intent intent = getIntent();
                        intent.putExtra("picture", picture);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                } else {
                    Toast.makeText(Camera_facedetection.this, "얼굴이 있어야만 찍을 수 있습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "onResume :: 내부에서 OpenCv 라이브러리를 찾을 수 없습니다.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "onResume :: 내부에서 OpenCv 라이브러리를 찾았습니다. 이용 가능합니다.");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }


    @Override
    public void onCameraViewStopped() {

    }


    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        matInput = inputFrame.rgba();

        if (matResult != null) matResult.release();
        if (matResult2 != null) matResult2.release();

        matResult = new Mat(matInput.rows(), matInput.cols(), matInput.type());
        matResult2 = new Mat(matInput.rows(), matInput.cols(), matInput.type());

//        Core.flip(matInput, matInput, 0);

        if (c_gray == true) {
            ConvertRGBtoGray(matInput.getNativeObjAddr(), matResult2.getNativeObjAddr());
            return matResult2;
        } else if (c_warm == true) {
            ConvertRGBtoWarm(matInput.getNativeObjAddr(), matResult2.getNativeObjAddr());
            return matResult2;
        } else if (c_fancy == true)
        {
            ConvertRGBtoFancy(matInput.getNativeObjAddr(), matResult2.getNativeObjAddr());
            return matResult2;
        } else if (c_reverse == true)
        {
            ConvertRGBtoreverse(matInput.getNativeObjAddr(), matResult2.getNativeObjAddr());
            return matResult2;
        } else
        {
            face_value = face_detect(cascadeClassifier_face, cascadeClassifier_eye, matInput.getNativeObjAddr(), matResult.getNativeObjAddr());
            if (face_value == 1) {
                shutter_btn.setBackgroundResource(R.drawable.camera_available);
            } else {
                shutter_btn.setBackgroundResource(R.drawable.camera_unavailable);
            }
            return matResult;
        }

    }


    private boolean hasPermissions(String[] permissions) {
        int result;

        //스트링 배열에 있는 퍼미션들의 허가 상태 여부 확인
        for (String perms : permissions) {

            result = ContextCompat.checkSelfPermission(this, perms);

            if (result == PackageManager.PERMISSION_DENIED) {
                //허가 안된 퍼미션 발견
                return false;
            }
        }

        //모든 퍼미션이 허가되었음
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraPermissionAccepted = grantResults[0]
                            == PackageManager.PERMISSION_GRANTED;

                    boolean writePermissionAccepted = grantResults[1]
                            == PackageManager.PERMISSION_GRANTED;

                    if (!cameraPermissionAccepted || !writePermissionAccepted) {
                        showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
                        return;
                    } else {
                        read_cascade_file();
                    }
                }
                break;
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        builder.create().show();
    }


    private void copyFile(String filename) {
        String baseDir = Environment.getExternalStorageDirectory().getPath();
        String pathDir = baseDir + File.separator + filename;

        AssetManager assetManager = this.getAssets();

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            Log.d(TAG, "copyFile :: 다음 경로로 파일복사 " + pathDir);
            inputStream = assetManager.open(filename);
            outputStream = new FileOutputStream(pathDir);

            byte[] buffer = new byte[4096];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            inputStream.close();
            inputStream = null;
            outputStream.flush();
            outputStream.close();
            outputStream = null;
        } catch (Exception e) {
            Log.d(TAG, "copyFile :: 파일 복사 중 예외 발생 " + e.toString());
        }

    }

    private void read_cascade_file() {
        copyFile("haarcascade_frontalface_alt.xml");
        copyFile("haarcascade_eye_tree_eyeglasses.xml");
        cascadeClassifier_face = loadCascade("haarcascade_frontalface_alt.xml");
        cascadeClassifier_eye = loadCascade("haarcascade_eye_tree_eyeglasses.xml");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.c_default:
                c_gray = false;
                c_fancy = false;
                c_reverse = false;
                c_warm = false;
                break;
            case R.id.c_gray:
                c_gray = true;
                c_reverse = false;
                c_fancy = false;
                c_warm = false;
                break;
            case R.id.c_warm:
                c_warm = true;
                c_fancy = false;
                c_gray = false;
                c_reverse = false;
                break;
            case R.id.c_fancy:
                c_fancy = true;
                c_warm = false;
                c_gray = false;
                c_reverse = false;
                break;
            case R.id.c_reverse:
                c_reverse = true;
                c_fancy = false;
                c_gray = false;
                c_warm = false;
                break;
        }
    }
}
