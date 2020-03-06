#include <jni.h>
#include <opencv2/opencv.hpp>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <android/log.h>

using namespace cv; // using namespace는 c++에서 쓰이는 것으로, 클래스명을 안쓰고도 참조메소드를 쓸 수 있음을 선언한 것이다. cv는 opencv 라이브러리 클래스를 의미함.
using namespace std; // String, vector 등의 클래스를 의미함.

//extern "C"
//JNIEXPORT jlong JNICALL
//Java_com_mymusic_orvai_high_1pitched_1tone_Camera_1facedetection_loadCascade(JNIEnv *env,
//                                                                             jclass type,
//                                                                             jstring cascadeFileName_) {

float resize(Mat img_src, Mat &img_resize, int resize_width) {

    float scale = resize_width / (float) img_src.cols;
    if (img_src.cols > resize_width) {
        int new_height = cvRound(img_src.rows * scale);
        resize(img_src, img_resize, Size(resize_width, new_height));
    } else {
        img_resize = img_src;
    }
    return scale;
}


//extern "C"
//JNIEXPORT unsigned long JNICALL
//Java_com_mymusic_orvai_high_1pitched_1tone_Camera_1facedetection_detect(JNIEnv *env, jclass type,
//                                                                        jlong cascadeClassifier_face,
//                                                                        jlong cascadeClassifier_eye,
//                                                                        jlong matAddrInput,
//                                                                        jlong matAddrResult) {
// TODO


extern "C"
JNIEXPORT void JNICALL
Java_com_mymusic_orvai_high_1pitched_1tone_Camera_1facedetection_ConvertRGBtoGray(JNIEnv *env,
                                                                                  jobject instance,
                                                                                  jlong matAddrInput,
                                                                                  jlong matAddrResult) {

    Mat &matInput = *(Mat *) matAddrInput;
    Mat &matResult = *(Mat *) matAddrResult;

    cvtColor(matInput, matResult, CV_RGB2GRAY); // 회색

    // TODO

}

extern "C"
JNIEXPORT void JNICALL
Java_com_mymusic_orvai_high_1pitched_1tone_Camera_1facedetection_ConvertRGBtoWarm(JNIEnv *env,
                                                                                  jobject instance,
                                                                                  jlong matAddrInput,
                                                                                  jlong matAddrResult) {

    Mat &matInput = *(Mat *) matAddrInput;
    Mat &matResult = *(Mat *) matAddrResult;

    cvtColor(matInput, matResult, COLOR_RGB2XYZ); // 따뜻한효과

    // TODO

}

extern "C"
JNIEXPORT void JNICALL
Java_com_mymusic_orvai_high_1pitched_1tone_Camera_1facedetection_ConvertRGBtoFancy(JNIEnv *env,
                                                                                   jobject instance,
                                                                                   jlong matAddrInput,
                                                                                   jlong matAddrResult) {

    Mat &matInput = *(Mat *) matAddrInput;
    Mat &matResult = *(Mat *) matAddrResult;

    cvtColor(matInput, matResult, CV_RGB2YCrCb); // 네온효과

    // TODO

}

extern "C"
JNIEXPORT void JNICALL
Java_com_mymusic_orvai_high_1pitched_1tone_Camera_1facedetection_ConvertRGBtoreverse(JNIEnv *env,
                                                                                     jobject instance,
                                                                                     jlong matAddrInput,
                                                                                     jlong matAddrResult) {

    Mat &matInput = *(Mat *) matAddrInput;
    Mat &matResult = *(Mat *) matAddrResult;

    cvtColor(matInput, matResult, CV_RGB2HLS_FULL); // 악마효과

    // TODO

}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_mymusic_orvai_high_1pitched_1tone_Camera_1facedetection_loadCascade(JNIEnv *env,
                                                                             jobject instance,
                                                                             jstring cascadeFileName_) {




        const char *nativeFileNameString = env->GetStringUTFChars(cascadeFileName_, 0);
// const는 final과 같음(상수)

// TODO

    string baseDir("/storage/emulated/0/");
    baseDir.append(nativeFileNameString);

    const char *pathDir = baseDir.c_str();
    jlong ret = 0;
    ret = (jlong) new CascadeClassifier(pathDir);
//    if (((CascadeClassifier *) ret)->empty()) {
//        __android_log_print(ANDROID_LOG_DEBUG, "native-lib :: ",
//                            "CascadeClassifier로 로딩 실패  %s", nativeFileNameString);
//    } else
//        __android_log_print(ANDROID_LOG_DEBUG, "native-lib :: ",
//                            "CascadeClassifier로 로딩 성공 %s", nativeFileNameString);


    env->ReleaseStringUTFChars(cascadeFileName_, nativeFileNameString);

    return ret;

}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_mymusic_orvai_high_1pitched_1tone_Camera_1facedetection_face_1detect(JNIEnv *env,
                                                                              jobject instance,
                                                                              jlong cascadeClassifier_face,
                                                                              jlong cascadeClassifier_eye,
                                                                              jlong matAddrInput,
                                                                              jlong matAddrResult) {


        Mat &img_input = *(Mat *) matAddrInput;
    Mat &img_result = *(Mat *) matAddrResult;
// &변수명 -> c++ 에서 포인터란 개념으로, 변수의 주소값을 출력함.(메모리에 어디 있는지)
// *변수명 -> 포인터를 저장하는 변수선언

    img_result = img_input.clone();

    std::vector<Rect> faces;
    Mat img_gray;

    cvtColor(img_input, img_gray, COLOR_BGR2GRAY); // 얼굴 검출을 위해서 먼저 흑백으로 변화 시킨다.
    equalizeHist(img_gray, img_gray);

    Mat img_resize;
    float resizeRatio = resize(img_gray, img_resize, 640); // 촬영 영상을 리사이징 한다.

//-- Detect faces

    ((CascadeClassifier *) cascadeClassifier_face)-> detectMultiScale(img_resize, faces, 1.1, 3, 0 | CASCADE_SCALE_IMAGE, Size(30, 30));
// 원래 2로 되었던 걸, 3으로 바꿈.

//    __android_log_print(ANDROID_LOG_DEBUG, (char *) "native-lib :: ", (char *) "face %d found ", faces.size());
    // 안드로이드에 로그 찍기, 필요 없을듯 하여 주석 처리.

    for (int i = 0; i < faces.size(); i++) {
        double real_facesize_x = faces[i].x / resizeRatio;
        double real_facesize_y = faces[i].y / resizeRatio;
        double real_facesize_width = faces[i].width / resizeRatio;
        double real_facesize_height = faces[i].height / resizeRatio;

        Point center(real_facesize_x + real_facesize_width / 2,
                     real_facesize_y + real_facesize_height / 2);

        ellipse(img_result, center, Size(real_facesize_width / 2, real_facesize_height / 2), 0, 0,
                360, Scalar(157, 226, 255), 10, 8, 0);
// 촬영영상(img_result), 포인트(center), 얼굴검출 크기(size), 기울기(0), 기울기(시작점,0), 기울기(끝,360), 얼굴 테두리 윤곽색, 얼굴 테두리 크기, lineType, shift(?))


        Rect face_area(real_facesize_x, real_facesize_y, real_facesize_width, real_facesize_height);
        Mat faceROI = img_gray(face_area);
        std::vector<Rect> eyes;

//        -- In each face, detect eyes
        ((CascadeClassifier *) cascadeClassifier_eye)->detectMultiScale(faceROI, eyes, 1.1, 3,
                                                                        0 | CASCADE_SCALE_IMAGE,
                                                                        Size(30, 30));
// 마찬가지로 2로 되었던걸 3으로 바꿈.


        for (size_t j = 0; j < eyes.size(); j++) {
            Point eye_center(real_facesize_x + eyes[j].x + eyes[j].width / 2,
                             real_facesize_y + eyes[j].y + eyes[j].height / 2);
            int radius = cvRound((eyes[j].width + eyes[j].height) * 0.25);

            circle(img_result, eye_center, radius, Scalar(243, 255, 115), 10, 8, 0);
//             촬영영상(img_result), 포인트(center), 얼굴검출 크기(size), 기울기(0), 눈 테두리 윤곽색, 눈 테두리 크기, lineType, shift(?))
        }
    }
    return faces.size(); // 검출된 얼굴 숫자를 리턴한다.


}