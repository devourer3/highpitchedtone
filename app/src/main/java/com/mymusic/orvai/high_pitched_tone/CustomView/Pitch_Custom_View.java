package com.mymusic.orvai.high_pitched_tone.CustomView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.mymusic.orvai.high_pitched_tone.EventBus.Event_bus_score;
import com.mymusic.orvai.high_pitched_tone.Perfect_singer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.EventBusBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Thread.sleep;

/**
 * Created by orvai on 2018-03-17.
 */

public class Pitch_Custom_View extends View {

    public Context context;
    public Handler handler;
    public int pitch_point_x, pitch_point_y, pitch_scroll_x, song_scroll_x, song_point_x, pitch_compare_number = 0;
    private int song_compare_number = -23;
    private double[] pitch_absolute_y, song_absolute_y;
    private List<Pitch_point> pitch_points, song_points;
    public static boolean DRAW_FLAG = true;
    private int song_note_number_start = -23;
    private int song_note_number_limit = 28;
    public static double PERFECT_SINGER_SCORE;
    public boolean[] accordPoints;

    public Pitch_Custom_View(Context context) {
        super(context);
    }

    public Pitch_Custom_View(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        handler = new Handler();
        pitch_points = Collections.synchronizedList(new ArrayList<Pitch_point>());
        song_points = Collections.synchronizedList(new ArrayList<Pitch_point>());
        pitch_absolute_y = new double[40];
        song_absolute_y = new double[5000];
        accordPoints = new boolean[5000];
        good_old_days();
        Pitch_Draw_thread thread = new Pitch_Draw_thread();
        Thread draw_point = new Thread(thread);
        SystemClock.sleep(1000);
        draw_point.setDaemon(true);
        draw_point.start();
    }

    public Pitch_Custom_View(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, 4900);
    }

    @Override
    public void onDrawForeground(Canvas canvas) { // 음정 줄 그리기
        super.onDrawForeground(canvas);
        Paint paint = new Paint(); // 화면에 그려줄 도구를 셋팅하는 객체
        paint.setColor(Color.rgb(241, 244, 66)); // 색상을 지정
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        for (int i = 0; i < 50; i++) {
            canvas.drawLine(100, getHeight() - i * 100, getHeight(), getHeight() - i * 100, paint);
        }
        paint.setStrokeWidth(0);
        paint.setTextSize(30);
        paint.setColor(Color.WHITE);
        draw_note(canvas, paint);
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.SQUARE);
        paint.setColor(Color.GREEN);
        canvas.drawLine(getWidth() / 2 - 10, getHeight(), getWidth() / 2 - 10, 0, paint);
    }

    private void draw_note(Canvas canvas, Paint paint) {
        canvas.drawText("0옥 도", 0, getHeight() - 35, paint);
        canvas.drawText("0옥 도#", 0, getHeight() - 135, paint);
        canvas.drawText("0옥 레", 0, getHeight() - 235, paint);
        canvas.drawText("0옥 레#", 0, getHeight() - 335, paint);
        canvas.drawText("0옥 미", 0, getHeight() - 435, paint);
        canvas.drawText("0옥 파", 0, getHeight() - 535, paint);
        canvas.drawText("0옥 파#", 0, getHeight() - 635, paint);
        canvas.drawText("0옥 솔", 0, getHeight() - 735, paint);
        canvas.drawText("0옥 솔#", 0, getHeight() - 835, paint);
        canvas.drawText("0옥 라", 0, getHeight() - 935, paint);
        canvas.drawText("0옥 라#", 0, getHeight() - 1035, paint);
        canvas.drawText("0옥 시", 0, getHeight() - 1135, paint);
        canvas.drawText("1옥 도", 0, getHeight() - 1235, paint);
        canvas.drawText("1옥 도#", 0, getHeight() - 1335, paint);
        canvas.drawText("1옥 레", 0, getHeight() - 1435, paint);
        canvas.drawText("1옥 레#", 0, getHeight() - 1535, paint);
        canvas.drawText("1옥 미", 0, getHeight() - 1635, paint);
        canvas.drawText("1옥 파", 0, getHeight() - 1735, paint);
        canvas.drawText("1옥 파#", 0, getHeight() - 1835, paint);
        canvas.drawText("1옥 솔", 0, getHeight() - 1935, paint);
        canvas.drawText("1옥 솔#", 0, getHeight() - 2035, paint);
        canvas.drawText("1옥 라", 0, getHeight() - 2135, paint);
        canvas.drawText("1옥 라#", 0, getHeight() - 2235, paint);
        canvas.drawText("1옥 시", 0, getHeight() - 2335, paint);
        canvas.drawText("2옥 도", 0, getHeight() - 2435, paint);
        canvas.drawText("2옥 도#", 0, getHeight() - 2535, paint);
        canvas.drawText("2옥 레", 0, getHeight() - 2635, paint);
        canvas.drawText("2옥 레#", 0, getHeight() - 2735, paint);
        canvas.drawText("2옥 미", 0, getHeight() - 2835, paint);
        canvas.drawText("2옥 파", 0, getHeight() - 2935, paint);
        canvas.drawText("2옥 파#", 0, getHeight() - 3035, paint);
        canvas.drawText("2옥 솔", 0, getHeight() - 3135, paint);
        canvas.drawText("2옥 솔#", 0, getHeight() - 3235, paint);
        canvas.drawText("2옥 라", 0, getHeight() - 3335, paint);
        canvas.drawText("2옥 라#", 0, getHeight() - 3435, paint);
        canvas.drawText("2옥 시", 0, getHeight() - 3535, paint);
        canvas.drawText("3옥 도", 0, getHeight() - 3635, paint);
        canvas.drawText("3옥 도#", 0, getHeight() - 3735, paint);
        canvas.drawText("3옥 레", 0, getHeight() - 3835, paint);
        canvas.drawText("3옥 레#", 0, getHeight() - 3935, paint);
        canvas.drawText("3옥 미", 0, getHeight() - 4035, paint);
        canvas.drawText("3옥 파", 0, getHeight() - 4135, paint);
        canvas.drawText("3옥 파#", 0, getHeight() - 4235, paint);
        canvas.drawText("3옥 솔", 0, getHeight() - 4335, paint);
        canvas.drawText("3옥 솔#", 0, getHeight() - 4435, paint);
        canvas.drawText("3옥 라", 0, getHeight() - 4535, paint);
        canvas.drawText("3옥 라#", 0, getHeight() - 4635, paint);
        canvas.drawText("3옥 시", 0, getHeight() - 4735, paint);
        canvas.drawText("4옥 도", 0, getHeight() - 4835, paint);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
//        Log.d(TAG+ song_absolute_y[song_compare_number+23], String.valueOf(pitch_absolute_y[pitch_compare_number]));
    }

    @Override
    protected synchronized void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        setBackgroundColor(Color.BLACK); // 배경색을 지정
        Paint paint = new Paint(); // 화면에 그려줄 도구를 세팅하는 객체
        synchronized (song_points) { // 노래의 음정 위치
            for (int i = song_note_number_start; i < song_note_number_limit; i++) {
                paint.setColor(Color.YELLOW);
                paint.setStrokeCap(Paint.Cap.BUTT);
                paint.setStrokeWidth(100);
                if (i >= 0) {
                    song_absolute_y[i] = song_points.get(i).getY();
                    canvas.drawPoint(song_points.get(i).getX() + song_scroll_x + getWidth() / 2, song_points.get(i).getY(), paint);
                }
            }
            song_scroll_x -= 25;
        }


        synchronized (pitch_points) { // 내는 목소리의 음정 위치

            for (Pitch_point point : pitch_points) { // 목소리의 음정 위치 출력

                if(song_absolute_y[song_compare_number+23] == pitch_absolute_y[pitch_compare_number]) { // 노래 음정과 내 목소리의 음정이 같을 때
                    PERFECT_SINGER_SCORE += 1;
                    accordPoints[pitch_compare_number] = true;
                    Event_bus_score event_bus_score = new Event_bus_score(PERFECT_SINGER_SCORE);
                    EventBus.getDefault().post(event_bus_score);
                } else {
                    accordPoints[pitch_compare_number] = false;
                }
                if(pitch_compare_number == 25) pitch_compare_number = 0; // 25번째 넘어간 포인트는 초기화

                if(accordPoints[pitch_compare_number]) {
                    paint.setStrokeWidth(26);
                    paint.setStrokeCap(Paint.Cap.SQUARE);
                    paint.setColor(Color.GREEN);
                    pitch_absolute_y[pitch_compare_number] = point.getY();
                    canvas.drawPoint(point.getX() + pitch_scroll_x, point.getY(), paint);
                } else {
                    paint.setColor(Color.RED); // 기본적으로 빨강
                    paint.setStrokeWidth(26);
                    paint.setStrokeCap(Paint.Cap.SQUARE);
                    pitch_absolute_y[pitch_compare_number] = point.getY();
                    canvas.drawPoint(point.getX() + pitch_scroll_x, point.getY(), paint); // 빨강색으로 표시
                }


            }
            pitch_scroll_x -= 25;
        }


    }

    private void add_Pitch_Point(int height) {
        synchronized (pitch_points) {
            pitch_points.add(new Pitch_point(getWidth() / 2 + pitch_point_x, height));
            pitch_point_x += 25;
        }
    }

    private void removePoint() {
        synchronized (pitch_points) {
            for (int i = 0; i < pitch_points.size() / 25; i++) {
                pitch_points.remove(i);
            }
        }
    }

    private synchronized void good_old_days() {
        for (int i = 0; i < 4800; i++) {
            // 노래 note가 상하간에 싱크로율이 맞으려면 좌측변이 우측변보다 3차이가 나야 함. 또한, i 하나당 0.05초임, 또한 바의 i 값은 측정값의 -2 만큼 해야 함.
            // 8분음표 -> i값 7개.... 4분음표 -> i값 15개...
            // 쉼표는 좌측변과 우측변 3차이 날 필요 없음. 바가 아니기 때문. 또한, 4의 배수를 권장 함.
            // 무조건 한 문단 당 i값 총 합이 69로 맞추어라.☆★
            song_point_x += 25;
            int handling_number = 0;

            //94


            //쉼8
            /**
             *  1문단(94)
             */

            if (i >= handling_number + 102 && i < handling_number + 109) {
                song_points.add(new Pitch_point(song_point_x, 3850)); // ex.참 8(-2) = 7개 <- 이것처럼....

            } else if (i >= handling_number + 112 && i < handling_number + 119) {
                song_points.add(new Pitch_point(song_point_x, 3150)); // 많7

            } else if (i >= handling_number + 120 && i < handling_number + 127) {
                song_points.add(new Pitch_point(song_point_x, 3350)); // 은7

            } else if (i >= handling_number + 130 && i < handling_number + 137) {
                song_points.add(new Pitch_point(song_point_x, 3450)); // 시7

            } else if (i >= handling_number + 140 && i < handling_number + 143) {
                song_points.add(new Pitch_point(song_point_x, 3350)); // 간3

            } else if (i >= handling_number + 146 && i < handling_number + 149) {
                song_points.add(new Pitch_point(song_point_x, 3150)); // 이3

                // 쉼8

            } else if (i >= handling_number + 157 && i < handling_number + 160) {
                song_points.add(new Pitch_point(song_point_x, 3350)); // 흘3


                /**
                 * 2문단(163)
                 */


            } else if (i >= handling_number + 163 && i < handling_number + 166) {
                song_points.add(new Pitch_point(song_point_x, 3350)); // 러3

                // 쉼24

            } else if (i >= handling_number + 190 && i < handling_number + 193) {
                song_points.add(new Pitch_point(song_point_x, 3650)); // 가3

            } else if (i >= handling_number + 196 && i < handling_number + 199) {
                song_points.add(new Pitch_point(song_point_x, 3350)); // 고3

                // 쉼32


                /**
                 * 3문단(232)
                 */


            } else if (i >= handling_number + 232 && i < handling_number + 235) {
                song_points.add(new Pitch_point(song_point_x, 3850)); // 넌3

                // 쉼12

            } else if (i >= handling_number + 247 && i < handling_number + 254) {
                song_points.add(new Pitch_point(song_point_x, 3650)); // 어7

            } else if (i >= handling_number + 257 && i < handling_number + 260) {
                song_points.add(new Pitch_point(song_point_x, 3450)); // 떻3

            } else if (i >= handling_number + 263 && i < handling_number + 266) {
                song_points.add(new Pitch_point(song_point_x, 3350)); // 게3

                //쉼 16

            } else if (i >= handling_number + 282 && i < handling_number + 289) {
                song_points.add(new Pitch_point(song_point_x, 3150)); // 사7

            } else if (i >= handling_number + 292 && i < handling_number + 295) {
                song_points.add(new Pitch_point(song_point_x, 2950)); // 는3

            } else if (i >= handling_number + 298 && i < handling_number + 301) {
                song_points.add(new Pitch_point(song_point_x, 4050)); // 지3

                //쉼 8


                /**
                 * 4문단(301)
                 */

            } else if (i >= handling_number + 309 && i < handling_number + 317) {
                song_points.add(new Pitch_point(song_point_x, 2950)); // 참7

            } else if (i >= handling_number + 320 && i < handling_number + 327) {
                song_points.add(new Pitch_point(song_point_x, 3150)); // 궁7

            } else if (i >= handling_number + 330 && i < handling_number + 333) {
                song_points.add(new Pitch_point(song_point_x, 3350)); // 금3

            } else if (i >= handling_number + 336 && i < handling_number + 339) {
                song_points.add(new Pitch_point(song_point_x, 3150)); // 해3

                // 쉼24

            } else if (i >= handling_number + 363 && i < handling_number + 367) {
                song_points.add(new Pitch_point(song_point_x, 3850)); // 날4


                /**
                 * 5문단(370)
                 */

            } else if (i >= handling_number + 370 && i < handling_number + 377) {
                song_points.add(new Pitch_point(song_point_x, 3150)); // 걱7

                //쉼8

            } else if (i >= handling_number + 385 && i < handling_number + 392) {
                song_points.add(new Pitch_point(song_point_x, 3350)); // 정7

                //쉼8

            } else if (i >= handling_number + 400 && i < handling_number + 407) {
                song_points.add(new Pitch_point(song_point_x, 3450)); // 하7

            } else if (i >= handling_number + 410 && i < handling_number + 417) {
                song_points.add(new Pitch_point(song_point_x, 3350)); // 는7

            } else if (i >= handling_number + 420 && i < handling_number + 427) {
                song_points.add(new Pitch_point(song_point_x, 3150)); // 사7

            } else if (i >= handling_number + 430 && i < handling_number + 436) {
                song_points.add(new Pitch_point(song_point_x, 3350)); // 람6


                /**
                 * 6문단(439)
                 */
            } else if (i >= handling_number + 439 && i < handling_number + 454) {
                song_points.add(new Pitch_point(song_point_x, 2450)); // 들15

                //쉼 12

            } else if (i >= handling_number + 466 && i < handling_number + 469) {
                song_points.add(new Pitch_point(song_point_x, 2950)); // 에3

            } else if (i >= handling_number + 472 && i < handling_number + 475) {
                song_points.add(new Pitch_point(song_point_x, 3150)); // 게3

                //쉼 16

            } else if (i >= handling_number + 491 && i < handling_number + 497) {
                song_points.add(new Pitch_point(song_point_x, 3450)); // 다6

            } else if (i >= handling_number + 500 && i < handling_number + 505) {
                song_points.add(new Pitch_point(song_point_x, 3350)); // 잊6


                /**
                 * 7문단(508)
                 */
            } else if (i >= handling_number + 508 && i < handling_number + 519) {
                song_points.add(new Pitch_point(song_point_x, 2650)); // 었11

            } else if (i >= handling_number + 522 && i < handling_number + 525) {
                song_points.add(new Pitch_point(song_point_x, 2650)); // 단3

                //쉼 8

            } else if (i >= handling_number + 533 && i < handling_number + 535) {
                song_points.add(new Pitch_point(song_point_x, 3850)); // 거2

            } else if (i >= handling_number + 538 && i < handling_number + 540) {
                song_points.add(new Pitch_point(song_point_x, 3350)); // 짓2

            } else if (i >= handling_number + 543 && i < handling_number + 549) {
                song_points.add(new Pitch_point(song_point_x, 3450)); // 말6

            } else if (i >= handling_number + 552 && i < handling_number + 554) {
                song_points.add(new Pitch_point(song_point_x, 3650)); // 하2

            } else if (i >= handling_number + 557 && i < handling_number + 559) {
                song_points.add(new Pitch_point(song_point_x, 3750)); // 는2

            } else if (i >= handling_number + 562 && i < handling_number + 568) {
                song_points.add(new Pitch_point(song_point_x, 3650)); // 내6

            } else if (i >= handling_number + 571 && i < handling_number + 577) {
                song_points.add(new Pitch_point(song_point_x, 2850)); // 가6

                /**
                 * 8문단(577)
                 */

                //쉼 6

            } else if (i >= handling_number + 583 && i < handling_number + 586) {
                song_points.add(new Pitch_point(song_point_x, 2950)); //참3

            } else if (i >= handling_number + 589 && i < handling_number + 596) {
                song_points.add(new Pitch_point(song_point_x, 3150)); //미7

            } else if (i >= handling_number + 599 && i < handling_number + 602) {
                song_points.add(new Pitch_point(song_point_x, 3350)); //운3

            } else if (i >= handling_number + 605 && i < handling_number + 608) {
                song_points.add(new Pitch_point(song_point_x, 2950)); //나3

            } else if (i >= handling_number + 611 && i < handling_number + 614) {
                song_points.add(new Pitch_point(song_point_x, 3150)); //알3

                //쉼 32


                /**
                 * 9문단(646)
                 */

            } else if (i >= handling_number + 646 && i < handling_number + 652) {
                song_points.add(new Pitch_point(song_point_x, 2450)); //아6

            } else if (i >= handling_number + 655 && i < handling_number + 657) {
                song_points.add(new Pitch_point(song_point_x, 2450)); //름2

            } else if (i >= handling_number + 661 && i < handling_number + 671) {
                song_points.add(new Pitch_point(song_point_x, 2450)); //다10

            } else if (i >= handling_number + 674 && i < handling_number + 677) {
                song_points.add(new Pitch_point(song_point_x, 2950)); //운3

            } else if (i >= handling_number + 680 && i < handling_number + 690) {
                song_points.add(new Pitch_point(song_point_x, 3150)); //이10

            } else if (i >= handling_number + 693 && i < handling_number + 696) {
                song_points.add(new Pitch_point(song_point_x, 3350)); //별3

            } else if (i >= handling_number + 699 && i < handling_number + 706) {
                song_points.add(new Pitch_point(song_point_x, 3150)); //은7

            } else if (i >= handling_number + 709 && i < handling_number + 712) {
                song_points.add(new Pitch_point(song_point_x, 3350)); //세3


                /**
                 * 10문단(715)
                 */

            } else if (i >= handling_number + 715 && i < handling_number + 722) {
                song_points.add(new Pitch_point(song_point_x, 2450)); //상7

            } else if (i >= handling_number + 725 && i < handling_number + 728) {
                song_points.add(new Pitch_point(song_point_x, 2650)); //에3

            } else if (i >= handling_number + 731 && i < handling_number + 738) {
                song_points.add(new Pitch_point(song_point_x, 2650)); //없7

                //쉼 4

            } else if (i >= handling_number + 742 && i < handling_number + 745) {
                song_points.add(new Pitch_point(song_point_x, 3350)); //다3

            } else if (i >= handling_number + 748 && i < handling_number + 755) {
                song_points.add(new Pitch_point(song_point_x, 3450)); //지7

            } else if (i >= handling_number + 758 && i < handling_number + 761) {
                song_points.add(new Pitch_point(song_point_x, 3350)); //만3

                //쉼4

            } else if (i >= handling_number + 765 && i < handling_number + 772) {
                song_points.add(new Pitch_point(song_point_x, 3650)); //그7

            } else if (i >= handling_number + 775 && i < handling_number + 781) {
                song_points.add(new Pitch_point(song_point_x, 3350)); //때6

                /**
                 * 11문단(784)
                 */
            } else if (i >= handling_number + 784 && i < handling_number + 799) {
                song_points.add(new Pitch_point(song_point_x, 2650)); //내15

            } else if (i >= handling_number + 803 && i < handling_number + 805) {
                song_points.add(new Pitch_point(song_point_x, 2650)); //가2

                //쉼4

            } else if (i >= handling_number + 809 && i < handling_number + 811) {
                song_points.add(new Pitch_point(song_point_x, 2450)); //조2

            } else if (i >= handling_number + 814 && i < handling_number + 818) {
                song_points.add(new Pitch_point(song_point_x, 3150)); //금7

            } else if (i >= handling_number + 821 && i < handling_number + 824) {
                song_points.add(new Pitch_point(song_point_x, 3350)); //더3

                //쉼8

            } else if (i >= handling_number + 832 && i < handling_number + 840) {
                song_points.add(new Pitch_point(song_point_x, 3650)); //너8

            } else if (i >= handling_number + 843 && i < handling_number + 850) {
                song_points.add(new Pitch_point(song_point_x, 3350)); //를7

                /**
                 * 12문단(853)
                 */
            } else if (i >= handling_number + 853 && i < handling_number + 860) {
                song_points.add(new Pitch_point(song_point_x, 2850)); //편7

            } else if (i >= handling_number + 863 && i < handling_number + 866) {
                song_points.add(new Pitch_point(song_point_x, 2950)); //하3

            } else if (i >= handling_number + 869 && i < handling_number + 872) {
                song_points.add(new Pitch_point(song_point_x, 2950)); //게3

                //쉼8

            } else if (i >= handling_number + 880 && i < handling_number + 883) {
                song_points.add(new Pitch_point(song_point_x, 3150)); //보3

            } else if (i >= handling_number + 886 && i < handling_number + 889) {
                song_points.add(new Pitch_point(song_point_x, 2950)); //내3

            } else if (i >= handling_number + 892 && i < handling_number + 899) {
                song_points.add(new Pitch_point(song_point_x, 2850)); //줬7

            } else if (i >= handling_number + 902 && i < handling_number + 905) {
                song_points.add(new Pitch_point(song_point_x, 2950)); //다3

            } else if (i >= handling_number + 908 && i < handling_number + 911) {
                song_points.add(new Pitch_point(song_point_x, 2950)); //면3

                //쉼11

                /**
                 * 13문단(922)
                 */
            } else if (i >= handling_number + 922 && i < handling_number + 928) {
                song_points.add(new Pitch_point(song_point_x, 2450)); //다6

            } else if (i >= handling_number + 931 && i < handling_number + 933) {
                song_points.add(new Pitch_point(song_point_x, 2450)); //른2

            } else if (i >= handling_number + 936 && i < handling_number + 946) {
                song_points.add(new Pitch_point(song_point_x, 2450)); //사10

            } else if (i >= handling_number + 949 && i < handling_number + 952) {
                song_points.add(new Pitch_point(song_point_x, 2950)); //람3

            } else if (i >= handling_number + 955 && i < handling_number + 965) {
                song_points.add(new Pitch_point(song_point_x, 3150)); //또10

            } else if (i >= handling_number + 968 && i < handling_number + 971) {
                song_points.add(new Pitch_point(song_point_x, 3350)); //만3

            } else if (i >= handling_number + 974 && i < handling_number + 977) {
                song_points.add(new Pitch_point(song_point_x, 3150)); //나3

                //쉼8

            } else if (i >= handling_number + 985 && i < handling_number + 988) {
                song_points.add(new Pitch_point(song_point_x, 3350)); //행3

                /**
                 * 14문단(991)
                 */
            } else if (i >= handling_number + 991 && i < handling_number + 998) {
                song_points.add(new Pitch_point(song_point_x, 2450)); //복7

            } else if (i >= handling_number + 1001 && i < handling_number + 1003) {
                song_points.add(new Pitch_point(song_point_x, 2650)); //할2

            } else if (i >= handling_number + 1006 && i < handling_number + 1012) {
                song_points.add(new Pitch_point(song_point_x, 2650)); //니6

                //쉼4

            } else if (i >= handling_number + 1016 && i < handling_number + 1019) {
                song_points.add(new Pitch_point(song_point_x, 3350)); //가3

            } else if (i >= handling_number + 1022 && i < handling_number + 1029) {
                song_points.add(new Pitch_point(song_point_x, 3450)); //가7

                //쉼4

            } else if (i >= handling_number + 1033 && i < handling_number + 1035) {
                song_points.add(new Pitch_point(song_point_x, 3350)); //끔2

            } else if (i >= handling_number + 1038 && i < handling_number + 1040) {
                song_points.add(new Pitch_point(song_point_x, 3350)); //은2

                //쉼8

            } else if (i >= handling_number + 1048 && i < handling_number + 1057) {
                song_points.add(new Pitch_point(song_point_x, 2950)); //내8

                /**
                 * 15문단(1060)
                 */
            } else if (i >= handling_number + 1060 && i < handling_number + 1067) {
                song_points.add(new Pitch_point(song_point_x, 2850)); //생7

            } else if (i >= handling_number + 1070 && i < handling_number + 1073) {
                song_points.add(new Pitch_point(song_point_x, 2950)); //각3

            } else if (i >= handling_number + 1076 && i < handling_number + 1087) {
                song_points.add(new Pitch_point(song_point_x, 2850)); //할11

            } else if (i >= handling_number + 1090 && i < handling_number + 1093) {
                song_points.add(new Pitch_point(song_point_x, 2950)); //때3

            } else if (i >= handling_number + 1096 && i < handling_number + 1099) {
                song_points.add(new Pitch_point(song_point_x, 2850)); //에3

                //쉼4

            } else if (i >= handling_number + 1103 && i < handling_number + 1106) {
                song_points.add(new Pitch_point(song_point_x, 2950)); //지을3

            } else if (i >= handling_number + 1109 && i < handling_number + 1116) {
                song_points.add(new Pitch_point(song_point_x, 2850)); //표7

            } else if (i >= handling_number + 1119 && i < handling_number + 1122) {
                song_points.add(new Pitch_point(song_point_x, 2650)); //정3

            } else if (i >= handling_number + 1125 && i < handling_number + 1128) {
                song_points.add(new Pitch_point(song_point_x, 2450)); //이3

                /**
                 * 16문단(1129)
                 */

            } else if (i >= handling_number + 1128 && i < handling_number + 1132) {
                song_points.add(new Pitch_point(song_point_x, 2450)); //이4 // 위에 '이'랑 붙임

            } else if (i >= handling_number + 1135 && i < handling_number + 1138) {
                song_points.add(new Pitch_point(song_point_x, 2450)); //참3

            } else if (i >= handling_number + 1141 && i < handling_number + 1150) {
                song_points.add(new Pitch_point(song_point_x, 2450)); //궁9

            } else if (i >= handling_number + 1153 && i < handling_number + 1155) {
                song_points.add(new Pitch_point(song_point_x, 2650)); //금2

            } else if (i >= handling_number + 1158 && i < handling_number + 1170) {
                song_points.add(new Pitch_point(song_point_x, 2650)); //해12

                //쉼8

            } else if (i >= handling_number + 1178 && i < handling_number + 1185) {
                song_points.add(new Pitch_point(song_point_x, 2150)); //날7

            } else if (i >= handling_number + 1188 && i < handling_number + 1195) {
                song_points.add(new Pitch_point(song_point_x, 1850)); //보7

                /**
                 * 17문단(1198)
                 */

            } else if (i >= handling_number + 1198 && i < handling_number + 1211) {
                song_points.add(new Pitch_point(song_point_x, 1750)); //는13

            } else if (i >= handling_number + 1215 && i < handling_number + 1228) {
                song_points.add(new Pitch_point(song_point_x, 1750)); //네13

            } else if (i >= handling_number + 1231 && i < handling_number + 1246) {
                song_points.add(new Pitch_point(song_point_x, 1650)); //눈15

            } else if (i >= handling_number + 1249 && i < handling_number + 1264) {
                song_points.add(new Pitch_point(song_point_x, 1750)); //이15


                /**
                 * 18문단(1267)
                 */

            } else if (i >= handling_number + 1267 && i < handling_number + 1291) {
                song_points.add(new Pitch_point(song_point_x, 1750)); //좋24

            } else if (i >= handling_number + 1294 && i < handling_number + 1297) {
                song_points.add(new Pitch_point(song_point_x, 2450)); //아3

            } else if (i >= handling_number + 1300 && i < handling_number + 1306) {
                song_points.add(new Pitch_point(song_point_x, 1950)); //서6

                //쉼 12

            } else if (i >= handling_number + 1318 && i < handling_number + 1320) {
                song_points.add(new Pitch_point(song_point_x, 2150)); //얼2

            } else if (i >= handling_number + 1322 && i < handling_number + 1324) {
                song_points.add(new Pitch_point(song_point_x, 2250)); //굴2

            } else if (i >= handling_number + 1326 && i < handling_number + 1328) {
                song_points.add(new Pitch_point(song_point_x, 2450)); //붉2

            } else if (i >= handling_number + 1330 && i < handling_number + 1333) {
                song_points.add(new Pitch_point(song_point_x, 2650)); //히2

                /**
                 * 19문단(1336)
                 */

            } else if (i >= handling_number + 1336 && i < handling_number + 1340) {
                song_points.add(new Pitch_point(song_point_x, 2650)); //며4

            } else if (i >= handling_number + 1343 && i < handling_number + 1350) {
                song_points.add(new Pitch_point(song_point_x, 2650)); //딴7

            } else if (i >= handling_number + 1353 && i < handling_number + 1360) {
                song_points.add(new Pitch_point(song_point_x, 2450)); //청7

            } else if (i >= handling_number + 1363 && i < handling_number + 1366) {
                song_points.add(new Pitch_point(song_point_x, 2250)); //피3

            } else if (i >= handling_number + 1369 && i < handling_number + 1372) {
                song_points.add(new Pitch_point(song_point_x, 2150)); //던3

                //쉼7

            } else if (i >= handling_number + 1379 && i < handling_number + 1382) {
                song_points.add(new Pitch_point(song_point_x, 2150)); //아3

            } else if (i >= handling_number + 1385 && i < handling_number + 1392) {
                song_points.add(new Pitch_point(song_point_x, 1950)); //름7

            } else if (i >= handling_number + 1395 && i < handling_number + 1402) {
                song_points.add(new Pitch_point(song_point_x, 1750)); //답7

                /**
                 * 20문단(1405)
                 */

            } else if (i >= handling_number + 1405 && i < handling_number + 1410) {
                song_points.add(new Pitch_point(song_point_x, 2850)); //던5

            } else if (i >= handling_number + 1413 && i < handling_number + 1420) {
                song_points.add(new Pitch_point(song_point_x, 1750)); //그7

            } else if (i >= handling_number + 1423 && i < handling_number + 1430) {
                song_points.add(new Pitch_point(song_point_x, 1950)); //날7

            } else if (i >= handling_number + 1433 && i < handling_number + 1436) {
                song_points.add(new Pitch_point(song_point_x, 2150)); //처3

            } else if (i >= handling_number + 1439 && i < handling_number + 1442) {
                song_points.add(new Pitch_point(song_point_x, 1950)); //럼7

                //쉼12

            } else if (i >= handling_number + 1454 && i < handling_number + 1461) {
                song_points.add(new Pitch_point(song_point_x, 2150)); //좋7

            } else if (i >= handling_number + 1464 && i < handling_number + 1471) {
                song_points.add(new Pitch_point(song_point_x, 1950)); //은7

                /**
                 * 21문단(1474)
                 */

            } else if (i >= handling_number + 1474 && i < handling_number + 1487) {
                song_points.add(new Pitch_point(song_point_x, 1750)); //사13

            } else if (i >= handling_number + 1491 && i < handling_number + 1504) {
                song_points.add(new Pitch_point(song_point_x, 1750)); //람13

            } else if (i >= handling_number + 1507 && i < handling_number + 1522) {
                song_points.add(new Pitch_point(song_point_x, 1650)); //만15

            } else if (i >= handling_number + 1525 && i < handling_number + 1540) {
                song_points.add(new Pitch_point(song_point_x, 1450)); //나15


                /**
                 * 22문단(1543)
                 */

            } else if (i >= handling_number + 1543 && i < handling_number + 1558) {
                song_points.add(new Pitch_point(song_point_x, 1450)); //사18

            } else if (i >= handling_number + 1566 && i < handling_number + 1569) {
                song_points.add(new Pitch_point(song_point_x, 1650)); //랑3

            } else if (i >= handling_number + 1572 && i < handling_number + 1575) {
                song_points.add(new Pitch_point(song_point_x, 1750)); //받3

            } else if (i >= handling_number + 1578 && i < handling_number + 1585) {
                song_points.add(new Pitch_point(song_point_x, 1950)); //고7

                //쉼8

            } else if (i >= handling_number + 1593 && i < handling_number + 1595) {
                song_points.add(new Pitch_point(song_point_x, 2150)); //너2

            } else if (i >= handling_number + 1597 && i < handling_number + 1599) {
                song_points.add(new Pitch_point(song_point_x, 2250)); //도2

            } else if (i >= handling_number + 1601 && i < handling_number + 1603) {
                song_points.add(new Pitch_point(song_point_x, 2450)); //이2

            } else if (i >= handling_number + 1606 && i < handling_number + 1609) {
                song_points.add(new Pitch_point(song_point_x, 2650)); //젠3

                /**
                 * 23문단(1612)
                 */

            } else if (i >= handling_number + 1612 && i < handling_number + 1619) {
                song_points.add(new Pitch_point(song_point_x, 2650)); //웃7

            } else if (i >= handling_number + 1622 && i < handling_number + 1629) {
                song_points.add(new Pitch_point(song_point_x, 2650)); //을7

            } else if (i >= handling_number + 1632 && i < handling_number + 1639) {
                song_points.add(new Pitch_point(song_point_x, 2450)); //수7

            } else if (i >= handling_number + 1642 && i < handling_number + 1645) {
                song_points.add(new Pitch_point(song_point_x, 2250)); //있3

            } else if (i >= handling_number + 1648 && i < handling_number + 1655) {
                song_points.add(new Pitch_point(song_point_x, 2150)); //길7

                //쉼 8

            } else if (i >= handling_number + 1663 && i < handling_number + 1666) {
                song_points.add(new Pitch_point(song_point_x, 2150)); //찬3

            } else if (i >= handling_number + 1669 && i < handling_number + 1672) {
                song_points.add(new Pitch_point(song_point_x, 1950)); //란3

            } else if (i >= handling_number + 1675 && i < handling_number + 1678) {
                song_points.add(new Pitch_point(song_point_x, 1750)); //했3

                /**
                 * 24문단(1681)
                 */

            } else if (i >= handling_number + 1681 && i < handling_number + 1688) {
                song_points.add(new Pitch_point(song_point_x, 2850)); //던7

            } else if (i >= handling_number + 1691 && i < handling_number + 1698) {
                song_points.add(new Pitch_point(song_point_x, 1750)); //우7

            } else if (i >= handling_number + 1701 && i < handling_number + 1708) {
                song_points.add(new Pitch_point(song_point_x, 1950)); //리7

            } else if (i >= handling_number + 1711 && i < handling_number + 1714) {
                song_points.add(new Pitch_point(song_point_x, 2150)); //그3

            } else if (i >= handling_number + 1717 && i < handling_number + 1748) {
                song_points.add(new Pitch_point(song_point_x, 1950)); //날31


                /**
                 * 25문단(1750)
                 */

            } else if (i >= handling_number + 1793 && i < handling_number + 1796) {
                song_points.add(new Pitch_point(song_point_x, 2250)); //처3

            } else if (i >= handling_number + 1799 && i < handling_number + 1811) {
                song_points.add(new Pitch_point(song_point_x, 2150)); //럼12

                /**
                 * 26문단(1819) 간주1
                 */


                /**
                 * 27문단(1888) 간주2
                 */

                /**
                 * 28문단(1957) 간주3
                 */

                /**
                 * 29문단(2026) 간주4
                 */

                /**
                 * 30문단(2095)
                 */

            } else if (i >= handling_number + 2095 && i < handling_number + 2101) {
                song_points.add(new Pitch_point(song_point_x, 2450)); //비 6

            } else if (i >= handling_number + 2105 && i < handling_number + 2107) {
                song_points.add(new Pitch_point(song_point_x, 2450)); //어2

            } else if (i >= handling_number + 2111 && i < handling_number + 2122) {
                song_points.add(new Pitch_point(song_point_x, 2450)); //있10

            } else if (i >= handling_number + 2125 && i < handling_number + 2128) {
                song_points.add(new Pitch_point(song_point_x, 2950)); //는 3

            } else if (i >= handling_number + 2131 && i < handling_number + 2138) {
                song_points.add(new Pitch_point(song_point_x, 3150)); //지 7

            } else if (i >= handling_number + 2141 && i < handling_number + 2144) {
                song_points.add(new Pitch_point(song_point_x, 3350)); //갑 3

            } else if (i >= handling_number + 2147 && i < handling_number + 2150) {
                song_points.add(new Pitch_point(song_point_x, 3150)); //에 3

                //쉼 4

            } else if (i >= handling_number + 2154 && i < handling_number + 2161) {
                song_points.add(new Pitch_point(song_point_x, 3350)); //음 7

                /**
                 * 31문단(2164)
                 */

            } else if (i >= handling_number + 2164 && i < handling_number + 2171) {
                song_points.add(new Pitch_point(song_point_x, 2450)); //식7

            } else if (i >= handling_number + 2173 && i < handling_number + 2175) {
                song_points.add(new Pitch_point(song_point_x, 2650)); //점2

            } else if (i >= handling_number + 2177 && i < handling_number + 2187) {
                song_points.add(new Pitch_point(song_point_x, 2650)); //을10

            } else if (i >= handling_number + 2189 && i < handling_number + 2196) {
                song_points.add(new Pitch_point(song_point_x, 3350)); //서7

            } else if (i >= handling_number + 2199 && i < handling_number + 2206) {
                song_points.add(new Pitch_point(song_point_x, 3450)); //성7

            } else if (i >= handling_number + 2209 && i < handling_number + 2212) {
                song_points.add(new Pitch_point(song_point_x, 3350)); //이2

            } else if (i >= handling_number + 2214 && i < handling_number + 2220) {
                song_points.add(new Pitch_point(song_point_x, 3350)); //면6

            } else if (i >= handling_number + 2223 && i < handling_number + 2230) {
                song_points.add(new Pitch_point(song_point_x, 2950)); //월7

                /**
                 * 32문단(2233)
                 */

            } else if (i >= handling_number + 2233 && i < handling_number + 2240) {
                song_points.add(new Pitch_point(song_point_x, 2850)); //급7

            } else if (i >= handling_number + 2243 && i < handling_number + 2246) {
                song_points.add(new Pitch_point(song_point_x, 2950)); //날3

            } else if (i >= handling_number + 2249 && i < handling_number + 2256) {
                song_points.add(new Pitch_point(song_point_x, 2850)); //이7

            } else if (i >= handling_number + 2259 && i < handling_number + 2262) {
                song_points.add(new Pitch_point(song_point_x, 2950)); //라3

            } else if (i >= handling_number + 2265 && i < handling_number + 2272) {
                song_points.add(new Pitch_point(song_point_x, 2850)); //며7

            } else if (i >= handling_number + 2276 && i < handling_number + 2279) {
                song_points.add(new Pitch_point(song_point_x, 2950)); //손3

            } else if (i >= handling_number + 2282 && i < handling_number + 2287) {
                song_points.add(new Pitch_point(song_point_x, 3150)); //잡5

            } else if (i >= handling_number + 2290 && i < handling_number + 2293) {
                song_points.add(new Pitch_point(song_point_x, 3350)); //아3

            } else if (i >= handling_number + 2296 && i < handling_number + 2299) {
                song_points.add(new Pitch_point(song_point_x, 3850)); //이3

                /**
                 * 33문단(2302)
                 */

            } else if (i >= handling_number + 2302 && i < handling_number + 2305) {
                song_points.add(new Pitch_point(song_point_x, 3150)); //끌3

            } else if (i >= handling_number + 2308 && i < handling_number + 2311) {
                song_points.add(new Pitch_point(song_point_x, 2950)); //어3

            } else if (i >= handling_number + 2314 && i < handling_number + 2317) {
                song_points.add(new Pitch_point(song_point_x, 3150)); //주3
//
            } else if (i >= handling_number + 2320 && i < handling_number + 2327) {
                song_points.add(new Pitch_point(song_point_x, 2950)); //던7

                //쉼44

                /**
                 * 34문단(2371)
                 */

            } else if (i >= handling_number + 2371 && i < handling_number + 2377) {
                song_points.add(new Pitch_point(song_point_x, 2450)); //만6
//
            } else if (i >= handling_number + 2379 && i < handling_number + 2381) {
                song_points.add(new Pitch_point(song_point_x, 2450)); //원2
//
            } else if (i >= handling_number + 2383 && i < handling_number + 2393) {
                song_points.add(new Pitch_point(song_point_x, 2450)); //짜10
//
            } else if (i >= handling_number + 2396 && i < handling_number + 2399) {
                song_points.add(new Pitch_point(song_point_x, 2950)); //리3
//
            } else if (i >= handling_number + 2402 && i < handling_number + 2409) {
                song_points.add(new Pitch_point(song_point_x, 3150)); //커7
//
            } else if (i >= handling_number + 2412 && i < handling_number + 2415) {
                song_points.add(new Pitch_point(song_point_x, 3350)); //플3
//
            } else if (i >= handling_number + 2418 && i < handling_number + 2425) {
                song_points.add(new Pitch_point(song_point_x, 3150)); //링7
//
                //쉼5

            } else if (i >= handling_number + 2430 && i < handling_number + 2437) {
                song_points.add(new Pitch_point(song_point_x, 3350)); //고7
//

                /**
                 * 35문단(2440)
                 */

            } else if (i >= handling_number + 2440 && i < handling_number + 2447) {
                song_points.add(new Pitch_point(song_point_x, 2450)); //맙7
//
            } else if (i >= handling_number + 2450 && i < handling_number + 2452) {
                song_points.add(new Pitch_point(song_point_x, 2650)); //다2
//
            } else if (i >= handling_number + 2454 && i < handling_number + 2460) {
                song_points.add(new Pitch_point(song_point_x, 2650)); //며6

            } else if (i >= handling_number + 2463 && i < handling_number + 2470) {
                song_points.add(new Pitch_point(song_point_x, 3350)); //펑7
//
            } else if (i >= handling_number + 2473 && i < handling_number + 2480) {
                song_points.add(new Pitch_point(song_point_x, 3450)); //펑7
//
            } else if (i >= handling_number + 2483 && i < handling_number + 2485) {
                song_points.add(new Pitch_point(song_point_x, 3350)); //울2
//
            } else if (i >= handling_number + 2487 && i < handling_number + 2489) {
                song_points.add(new Pitch_point(song_point_x, 3350)); //던2

                //쉼10
//
            } else if (i >= handling_number + 2499 && i < handling_number + 2506) {
                song_points.add(new Pitch_point(song_point_x, 2950)); //과7

                /**
                 * 36문단(2509)
                 */

            } else if (i >= handling_number + 2509 && i < handling_number + 2516) {
                song_points.add(new Pitch_point(song_point_x, 2850)); //분7
//
            } else if (i >= handling_number + 2519 && i < handling_number + 2522) {
                song_points.add(new Pitch_point(song_point_x, 2950)); //한3
//
            } else if (i >= handling_number + 2525 && i < handling_number + 2532) {
                song_points.add(new Pitch_point(song_point_x, 2850)); //네7
//
            } else if (i >= handling_number + 2535 && i < handling_number + 2538) {
                song_points.add(new Pitch_point(song_point_x, 2950)); //사3
//
            } else if (i >= handling_number + 2541 && i < handling_number + 2548) {
                song_points.add(new Pitch_point(song_point_x, 2850)); //랑7
//
            } else if (i >= handling_number + 2551 && i < handling_number + 2554) {
                song_points.add(new Pitch_point(song_point_x, 2950)); //받3
//
            } else if (i >= handling_number + 2557 && i < handling_number + 2564) {
                song_points.add(new Pitch_point(song_point_x, 2850)); //을7
//
            } else if (i >= handling_number + 2567 && i < handling_number + 2572) {
                song_points.add(new Pitch_point(song_point_x, 2650)); //사5
//
            } else if (i >= handling_number + 2575 && i < handling_number + 2578) {
                song_points.add(new Pitch_point(song_point_x, 2450)); //람3

                /**
                 * 37문단(2578)
                 */

            } else if (i >= handling_number + 2578 && i < handling_number + 2585) {
                song_points.add(new Pitch_point(song_point_x, 2450)); //람7

            } else if (i >= handling_number + 2588 && i < handling_number + 2591) {
                song_points.add(new Pitch_point(song_point_x, 2450)); //참3
//
            } else if (i >= handling_number + 2593 && i < handling_number + 2603) {
                song_points.add(new Pitch_point(song_point_x, 2450)); //부10
//
            } else if (i >= handling_number + 2606 && i < handling_number + 2609) {
                song_points.add(new Pitch_point(song_point_x, 2650)); //러3
//
            } else if (i >= handling_number + 2612 && i < handling_number + 2624) {
                song_points.add(new Pitch_point(song_point_x, 2650)); //워12
//
            } else if (i >= handling_number + 2627 && i < handling_number + 2634) {
                song_points.add(new Pitch_point(song_point_x, 2150)); //날7
//
            } else if (i >= handling_number + 2637 && i < handling_number + 2644) {
                song_points.add(new Pitch_point(song_point_x, 1850)); //보7

                /**
                 * 38문단(2647)
                 */

            } else if (i >= handling_number + 1198 + 1449 && i < handling_number + 1211 + 1449) {
                song_points.add(new Pitch_point(song_point_x, 1750)); //는13

            } else if (i >= handling_number + 1215 + 1449 && i < handling_number + 1228 + 1449) {
                song_points.add(new Pitch_point(song_point_x, 1750)); //네13

            } else if (i >= handling_number + 1231 + 1449 && i < handling_number + 1246 + 1449) {
                song_points.add(new Pitch_point(song_point_x, 1650)); //눈15

            } else if (i >= handling_number + 1249 + 1449 && i < handling_number + 1264 + 1449) {
                song_points.add(new Pitch_point(song_point_x, 1750)); //이15

                /**
                 * 39문단(2716)
                 */

            } else if (i >= handling_number + 1267 + 1449 && i < handling_number + 1291 + 1449) {
                song_points.add(new Pitch_point(song_point_x, 1750)); //좋24

            } else if (i >= handling_number + 1294 + 1449 && i < handling_number + 1297 + 1449) {
                song_points.add(new Pitch_point(song_point_x, 2450)); //아3

            } else if (i >= handling_number + 1300 + 1449 && i < handling_number + 1306 + 1449) {
                song_points.add(new Pitch_point(song_point_x, 1950)); //서6

                //쉼 12

            } else if (i >= handling_number + 1318 + 1449 && i < handling_number + 1320 + 1449) {
                song_points.add(new Pitch_point(song_point_x, 2150)); //얼2

            } else if (i >= handling_number + 1322 + 1449 && i < handling_number + 1324 + 1449) {
                song_points.add(new Pitch_point(song_point_x, 2250)); //굴2

            } else if (i >= handling_number + 1326 + 1449 && i < handling_number + 1328 + 1449) {
                song_points.add(new Pitch_point(song_point_x, 2450)); //붉2

            } else if (i >= handling_number + 1330 + 1449 && i < handling_number + 1333 + 1449) {
                song_points.add(new Pitch_point(song_point_x, 2650)); //히2

                /**
                 * 40문단(2785)
                 */

            } else if (i >= handling_number + 1336 + 1449 && i < handling_number + 1340 + 1449) {
                song_points.add(new Pitch_point(song_point_x, 2650)); //며4

            } else if (i >= handling_number + 1343 + 1449 && i < handling_number + 1350 + 1449) {
                song_points.add(new Pitch_point(song_point_x, 2650)); //딴7

            } else if (i >= handling_number + 1353 + 1449 && i < handling_number + 1360 + 1449) {
                song_points.add(new Pitch_point(song_point_x, 2450)); //청7

            } else if (i >= handling_number + 1363 + 1449 && i < handling_number + 1366 + 1449) {
                song_points.add(new Pitch_point(song_point_x, 2250)); //피3

            } else if (i >= handling_number + 1369 + 1449 && i < handling_number + 1372 + 1449) {
                song_points.add(new Pitch_point(song_point_x, 2150)); //던3

                //쉼7

            } else if (i >= handling_number + 1379 + 1449 && i < handling_number + 1382 + 1449) {
                song_points.add(new Pitch_point(song_point_x, 2150)); //아3

            } else if (i >= handling_number + 1385 + 1449 && i < handling_number + 1392 + 1449) {
                song_points.add(new Pitch_point(song_point_x, 1950)); //름7

            } else if (i >= handling_number + 1395 + 1449 && i < handling_number + 1402 + 1449) {
                song_points.add(new Pitch_point(song_point_x, 1750)); //답7

                /**
                 * 41문단(2854)
                 */

            } else if (i >= handling_number + 1405 + 1449 && i < handling_number + 1410 + 1449) {
                song_points.add(new Pitch_point(song_point_x, 2850)); //던5

            } else if (i >= handling_number + 1413 + 1449 && i < handling_number + 1420 + 1449) {
                song_points.add(new Pitch_point(song_point_x, 1750)); //그7

            } else if (i >= handling_number + 1423 + 1449 && i < handling_number + 1430 + 1449) {
                song_points.add(new Pitch_point(song_point_x, 1950)); //날7

            } else if (i >= handling_number + 1433 + 1449 && i < handling_number + 1436 + 1449) {
                song_points.add(new Pitch_point(song_point_x, 2150)); //처3

            } else if (i >= handling_number + 1439 + 1449 && i < handling_number + 1442 + 1449) {
                song_points.add(new Pitch_point(song_point_x, 1950)); //럼7

                //쉼12

            } else if (i >= handling_number + 1454 + 1449 && i < handling_number + 1461 + 1449) {
                song_points.add(new Pitch_point(song_point_x, 2150)); //좋7

            } else if (i >= handling_number + 1464 + 1449 && i < handling_number + 1471 + 1449) {
                song_points.add(new Pitch_point(song_point_x, 1950)); //은7

                /**
                 * 42문단(2923)
                 */

            } else if (i >= handling_number + 1474 + 1449 && i < handling_number + 1487 + 1449) {
                song_points.add(new Pitch_point(song_point_x, 1750)); //사13

            } else if (i >= handling_number + 1491 + 1449 && i < handling_number + 1504 + 1449) {
                song_points.add(new Pitch_point(song_point_x, 1750)); //람13

            } else if (i >= handling_number + 1507 + 1449 && i < handling_number + 1522 + 1449) {
                song_points.add(new Pitch_point(song_point_x, 1650)); //만15

            } else if (i >= handling_number + 1525 + 1449 && i < handling_number + 1540 + 1449) {
                song_points.add(new Pitch_point(song_point_x, 1450)); //나15

                /**
                 * 43문단(2992)
                 */

            } else if (i >= handling_number + 1543 + 1449 && i < handling_number + 1558 + 1449) {
                song_points.add(new Pitch_point(song_point_x, 1450)); //사18

            } else if (i >= handling_number + 1566 + 1449 && i < handling_number + 1569 + 1449) {
                song_points.add(new Pitch_point(song_point_x, 1650)); //랑3

            } else if (i >= handling_number + 1572 + 1449 && i < handling_number + 1575 + 1449) {
                song_points.add(new Pitch_point(song_point_x, 1750)); //받3

            } else if (i >= handling_number + 1578 + 1449 && i < handling_number + 1585 + 1449) {
                song_points.add(new Pitch_point(song_point_x, 1950)); //고7

                //쉼8

            } else if (i >= handling_number + 1593 + 1449 && i < handling_number + 1595 + 1449) {
                song_points.add(new Pitch_point(song_point_x, 2150)); //너2

            } else if (i >= handling_number + 1597 + 1449 && i < handling_number + 1599 + 1449) {
                song_points.add(new Pitch_point(song_point_x, 2250)); //도2

            } else if (i >= handling_number + 1601 + 1449 && i < handling_number + 1603 + 1449) {
                song_points.add(new Pitch_point(song_point_x, 2450)); //이2

            } else if (i >= handling_number + 1606 + 1449 && i < handling_number + 1609 + 1449) {
                song_points.add(new Pitch_point(song_point_x, 2650)); //젠3

                /**
                 * 44문단(3061)
                 */

            } else if (i >= handling_number + 1612 + 1449 && i < handling_number + 1619 + 1449) {
                song_points.add(new Pitch_point(song_point_x, 2650)); //웃7

            } else if (i >= handling_number + 1622 + 1449 && i < handling_number + 1629 + 1449) {
                song_points.add(new Pitch_point(song_point_x, 2650)); //을7

            } else if (i >= handling_number + 1632 + 1449 && i < handling_number + 1639 + 1449) {
                song_points.add(new Pitch_point(song_point_x, 2450)); //수7

            } else if (i >= handling_number + 1642 + 1449 && i < handling_number + 1645 + 1449) {
                song_points.add(new Pitch_point(song_point_x, 2250)); //있3

            } else if (i >= handling_number + 1648 + 1449 && i < handling_number + 1655 + 1449) {
                song_points.add(new Pitch_point(song_point_x, 2150)); //길7

                //쉼 8

            } else if (i >= handling_number + 1663 + 1449 && i < handling_number + 1666 + 1449) {
                song_points.add(new Pitch_point(song_point_x, 2150)); //찬3

            } else if (i >= handling_number + 1669 + 1449 && i < handling_number + 1672 + 1449) {
                song_points.add(new Pitch_point(song_point_x, 1950)); //란3

            } else if (i >= handling_number + 1675 + 1449 && i < handling_number + 1678 + 1449) {
                song_points.add(new Pitch_point(song_point_x, 1750)); //했3

                /**
                 * 45문단(3130)
                 */

            } else if (i >= handling_number + 1681 + 1449 && i < handling_number + 1688 + 1449) {
                song_points.add(new Pitch_point(song_point_x, 2850)); //던7

            } else if (i >= handling_number + 1691 + 1449 && i < handling_number + 1698 + 1449) {
                song_points.add(new Pitch_point(song_point_x, 1750)); //우7

            } else if (i >= handling_number + 1701 + 1449 && i < handling_number + 1708 + 1449) {
                song_points.add(new Pitch_point(song_point_x, 1950)); //리7

            } else if (i >= handling_number + 3160 && i < handling_number + 3163) {
                song_points.add(new Pitch_point(song_point_x, 2150)); //그3

            } else if (i >= handling_number + 3166 && i < handling_number + 3181) {
                song_points.add(new Pitch_point(song_point_x, 1950)); //날15

                //쉼6

            } else if (i >= handling_number + 3187 && i < handling_number + 3189) {
                song_points.add(new Pitch_point(song_point_x, 2150)); //처2

            } else if (i >= handling_number + 3191 && i < handling_number + 3196) {
                song_points.add(new Pitch_point(song_point_x, 2150)); //럼5


                /**
                 * 46문단(3199)
                 */

                //쉼 20

            } else if (i >= handling_number + 3219 && i < handling_number + 3224) {
                song_points.add(new Pitch_point(song_point_x, 1750)); //문5

            } else if (i >= handling_number + 3227 && i < handling_number + 3232) {
                song_points.add(new Pitch_point(song_point_x, 1750)); //득5

            } else if (i >= handling_number + 3235 && i < handling_number + 3241) {
                song_points.add(new Pitch_point(song_point_x, 1750)); //들6

            } else if (i >= handling_number + 3244 && i < handling_number + 3247) {
                song_points.add(new Pitch_point(song_point_x, 1950)); //려3

            } else if (i >= handling_number + 3250 && i < handling_number + 3256) {
                song_points.add(new Pitch_point(song_point_x, 2150)); //오6

            } else if (i >= handling_number + 3259 && i < handling_number + 3262) {
                song_points.add(new Pitch_point(song_point_x, 2150)); //는3

            } else if (i >= handling_number + 3265 && i < handling_number + 3272) {
                song_points.add(new Pitch_point(song_point_x, 1950)); //너7

                /**
                 * 47문단(3268)
                 */

            } else if (i >= handling_number + 3276 && i < handling_number + 3278) {
                song_points.add(new Pitch_point(song_point_x, 1950)); //의2

            } else if (i >= handling_number + 3281 && i < handling_number + 3287) {
                song_points.add(new Pitch_point(song_point_x, 1950)); //소6

            } else if (i >= handling_number + 3290 && i < handling_number + 3297) {
                song_points.add(new Pitch_point(song_point_x, 1450)); //식7

            } else if (i >= handling_number + 3300 && i < handling_number + 3315) {
                song_points.add(new Pitch_point(song_point_x, 1750)); //에15

                // 쉼12

            } else if (i >= handling_number + 3328 && i < handling_number + 3330) {
                song_points.add(new Pitch_point(song_point_x, 1950)); //가2

            } else if (i >= handling_number + 3332 && i < handling_number + 3334) {
                song_points.add(new Pitch_point(song_point_x, 1750)); //끔2

                /**
                 * 48문단(3337)
                 */

            } else if (i >= handling_number + 3337 && i < handling_number + 3340) {
                song_points.add(new Pitch_point(song_point_x, 1650)); // 씩7

            } else if (i >= handling_number + 3343 && i < handling_number + 3346) {
                song_points.add(new Pitch_point(song_point_x, 1750)); // 은3

            } else if (i >= handling_number + 3349 && i < handling_number + 3356) {
                song_points.add(new Pitch_point(song_point_x, 1650)); // 혼7

            } else if (i >= handling_number + 3359 && i < handling_number + 3366) {
                song_points.add(new Pitch_point(song_point_x, 1750)); // 자7

            } else if (i >= handling_number + 3369 && i < handling_number + 3376) {
                song_points.add(new Pitch_point(song_point_x, 1650)); // 울7

            } else if (i >= handling_number + 3379 && i < handling_number + 3386) {
                song_points.add(new Pitch_point(song_point_x, 1750)); // 수7

            } else if (i >= handling_number + 3389 && i < handling_number + 3395) {
                song_points.add(new Pitch_point(song_point_x, 1650)); // 있6

            } else if (i >= handling_number + 3397 && i < handling_number + 3440) {
                song_points.add(new Pitch_point(song_point_x, 1650)); // 길43


                /**
                 * 49문단(3406) (간주1)
                 */

                /**
                 * 50문단(3475) (간주2)
                 */

                /**
                 * 51문단(3544)
                 */

                //쉼 53

            } else if (i >= handling_number + 3597 && i < handling_number + 3602) {
                song_points.add(new Pitch_point(song_point_x, 2150)); //끝5

            } else if (i >= handling_number + 3605 && i < handling_number + 3610) {
                song_points.add(new Pitch_point(song_point_x, 1850)); //가5

                /**
                 * 52문단(3613)
                 */

            } else if (i >= handling_number + 3613 && i < handling_number + 3626) {
                song_points.add(new Pitch_point(song_point_x, 1750)); //지13

            } else if (i >= handling_number + 3629 && i < handling_number + 3642) {
                song_points.add(new Pitch_point(song_point_x, 1750)); //이13

            } else if (i >= handling_number + 3645 && i < handling_number + 3661) {
                song_points.add(new Pitch_point(song_point_x, 1650)); //기15

            } else if (i >= handling_number + 3664 && i < handling_number + 3679) {
                song_points.add(new Pitch_point(song_point_x, 1750)); //적15

                /**
                 * 53문단(3682)
                 */

            } else if (i >= handling_number + 3682 && i < handling_number + 3706) {
                song_points.add(new Pitch_point(song_point_x, 1750)); //인24

            } else if (i >= handling_number + 3709 && i < handling_number + 3712) {
                song_points.add(new Pitch_point(song_point_x, 2450)); //내3

            } else if (i >= handling_number + 3715 && i < handling_number + 3721) {
                song_points.add(new Pitch_point(song_point_x, 1950)); //가6

                //쉼 12

            } else if (i >= handling_number + 3733 && i < handling_number + 3735) {
                song_points.add(new Pitch_point(song_point_x, 2150)); //이2

            } else if (i >= handling_number + 3737 && i < handling_number + 3739) {
                song_points.add(new Pitch_point(song_point_x, 2250)); //제2

            } else if (i >= handling_number + 3741 && i < handling_number + 3743) {
                song_points.add(new Pitch_point(song_point_x, 2450)); //나2

            } else if (i >= handling_number + 3745 && i < handling_number + 3748) {
                song_points.add(new Pitch_point(song_point_x, 2650)); //없3

                /**
                 * 54문단(3751)
                 */

            } else if (i >= handling_number + 3751 && i < handling_number + 3755) {
                song_points.add(new Pitch_point(song_point_x, 2650)); //이4

            } else if (i >= handling_number + 3758 && i < handling_number + 3765) {
                song_points.add(new Pitch_point(song_point_x, 2650)); //도7

            } else if (i >= handling_number + 3768 && i < handling_number + 3775) {
                song_points.add(new Pitch_point(song_point_x, 2450)); //괜7

            } else if (i >= handling_number + 3778 && i < handling_number + 3781) {
                song_points.add(new Pitch_point(song_point_x, 2250)); //찮3

            } else if (i >= handling_number + 3784 && i < handling_number + 3787) {
                song_points.add(new Pitch_point(song_point_x, 2150)); //다3

                //쉼 14

            } else if (i >= handling_number + 3801 && i < handling_number + 3803) {
                song_points.add(new Pitch_point(song_point_x, 2150)); //너2

            } else if (i >= handling_number + 3806 && i < handling_number + 3810) {
                song_points.add(new Pitch_point(song_point_x, 1950)); //에4

            } else if (i >= handling_number + 3813 && i < handling_number + 3815) {
                song_points.add(new Pitch_point(song_point_x, 1750)); //게2

            } else if (i >= handling_number + 3818 && i < handling_number + 3820) {
                song_points.add(new Pitch_point(song_point_x, 1650)); //듣2

                /**
                 * 55문단(3820)
                 */

            } else if (i >= handling_number + 3820 && i < handling_number + 3824) {
                song_points.add(new Pitch_point(song_point_x, 1650)); //듣4

            } else if (i >= handling_number + 3827 && i < handling_number + 3830) {
                song_points.add(new Pitch_point(song_point_x, 1650)); //고3

            } else if (i >= handling_number + 3833 && i < handling_number + 3839) {
                song_points.add(new Pitch_point(song_point_x, 1450)); //싶6

            } else if (i >= handling_number + 3842 && i < handling_number + 3848) {
                song_points.add(new Pitch_point(song_point_x, 1450)); //던6

            } else if (i >= handling_number + 3851 && i < handling_number + 3858) {
                song_points.add(new Pitch_point(song_point_x, 1250)); //마7

            } else if (i >= handling_number + 3861 && i < handling_number + 3863) {
                song_points.add(new Pitch_point(song_point_x, 1450)); //알2

                //쉼6

            } else if (i >= handling_number + 3869 && i < handling_number + 3876) {
                song_points.add(new Pitch_point(song_point_x, 2150)); //좋7

            } else if (i >= handling_number + 3879 && i < handling_number + 3886) {
                song_points.add(new Pitch_point(song_point_x, 1950)); //은7

                /**
                 * 56문단(3889)
                 */

            } else if (i >= handling_number + 3889 && i < handling_number + 3902) {
                song_points.add(new Pitch_point(song_point_x, 1750)); //사13

            } else if (i >= handling_number + 3906 && i < handling_number + 3919) {
                song_points.add(new Pitch_point(song_point_x, 1750)); //람13

            } else if (i >= handling_number + 3922 && i < handling_number + 3937) {
                song_points.add(new Pitch_point(song_point_x, 1650)); //만15

            } else if (i >= handling_number + 3940 && i < handling_number + 3955) {
                song_points.add(new Pitch_point(song_point_x, 1450)); //나15

                /**
                 * 57문단(3958)
                 */

                //쉼8

            } else if (i >= handling_number + 3966 && i < handling_number + 3976) {
                song_points.add(new Pitch_point(song_point_x, 950)); //미10

            } else if (i >= handling_number + 3979 && i < handling_number + 3982) {
                song_points.add(new Pitch_point(song_point_x, 2150)); //안3

            } else if (i >= handling_number + 3985 && i < handling_number + 3992) {
                song_points.add(new Pitch_point(song_point_x, 1750)); //해7

            } else if (i >= handling_number + 3995 && i < handling_number + 3998) {
                song_points.add(new Pitch_point(song_point_x, 1950)); //애3

                //쉼 12

            } else if (i >= handling_number + 4010 && i < handling_number + 4012) {
                song_points.add(new Pitch_point(song_point_x, 2150)); //다2

            } else if (i >= handling_number + 4014 && i < handling_number + 4016) {
                song_points.add(new Pitch_point(song_point_x, 2250)); //시2

            } else if (i >= handling_number + 4018 && i < handling_number + 4020) {
                song_points.add(new Pitch_point(song_point_x, 2450)); //돌2

            } else if (i >= handling_number + 4022 && i < handling_number + 4024) {
                song_points.add(new Pitch_point(song_point_x, 2650)); //아2

                /**
                 * 58문단(4027)
                 */

            } else if (i >= handling_number + 4027 && i < handling_number + 4033) {
                song_points.add(new Pitch_point(song_point_x, 2650)); //갈6

            } else if (i >= handling_number + 4035 && i < handling_number + 4041) {
                song_points.add(new Pitch_point(song_point_x, 2650)); //순6

            } else if (i >= handling_number + 4044 && i < handling_number + 4051) {
                song_points.add(new Pitch_point(song_point_x, 2950)); //없7

            } else if (i >= handling_number + 4054 && i < handling_number + 4057) {
                song_points.add(new Pitch_point(song_point_x, 1950)); //지3

            } else if (i >= handling_number + 4060 && i < handling_number + 4063) {
                song_points.add(new Pitch_point(song_point_x, 2150)); //만3

                //쉼12

            } else if (i >= handling_number + 4075 && i < handling_number + 4078) {
                song_points.add(new Pitch_point(song_point_x, 2150)); //아3

            } else if (i >= handling_number + 4081 && i < handling_number + 4086) {
                song_points.add(new Pitch_point(song_point_x, 1950)); //름5

            } else if (i >= handling_number + 4087 && i < handling_number + 4093) {
                song_points.add(new Pitch_point(song_point_x, 1750)); //답6

                /**
                 * 59문단(4096)
                 */

            } else if (i >= handling_number + 4096 && i < handling_number + 4099) {
                song_points.add(new Pitch_point(song_point_x, 2750)); //던3

            } else if (i >= handling_number + 4102 && i < handling_number + 4107) {
                song_points.add(new Pitch_point(song_point_x, 1750)); //우5

            } else if (i >= handling_number + 4110 && i < handling_number + 4115) {
                song_points.add(new Pitch_point(song_point_x, 1950)); //리5

            } else if (i >= handling_number + 4118 && i < handling_number + 4121) {
                song_points.add(new Pitch_point(song_point_x, 2150)); //그3

            } else if (i >= handling_number + 4124 && i < handling_number + 4126) {
                song_points.add(new Pitch_point(song_point_x, 2150)); //날2
            } else if (i >= handling_number + 4128 && i < handling_number + 4130) {
                song_points.add(new Pitch_point(song_point_x, 1950)); //날2
            } else if (i >= handling_number + 4132 && i < handling_number + 4134) {
                song_points.add(new Pitch_point(song_point_x, 1750)); //날2
            } else if (i >= handling_number + 4136 && i < handling_number + 4138) {
                song_points.add(new Pitch_point(song_point_x, 1950)); //날2

            } else {
                song_points.add(new Pitch_point(song_point_x, -150));
            }
        }
    }

    class Pitch_Draw_thread implements Runnable {
        @Override
        public void run() {
            while (DRAW_FLAG) {
                pitch_point_y = Perfect_singer.HANDLING_PITCH;
                try {
                    add_Pitch_Point(pitch_point_y);
                    removePoint();
                    postInvalidate();
                    sleep(49, 500000);
                    // 50만 나노초는 0.0005초
                    song_note_number_start++;
                    song_note_number_limit++;
                    pitch_compare_number++;
                    song_compare_number++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}

class Pitch_point {
    private float x, y;

    public Pitch_point(float x, float y) {
        this.x = x;
        this.y = y;
    }


    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

}