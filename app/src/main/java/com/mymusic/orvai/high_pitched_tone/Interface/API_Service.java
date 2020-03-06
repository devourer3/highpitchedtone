package com.mymusic.orvai.high_pitched_tone.Interface;

import com.mymusic.orvai.high_pitched_tone.API_Result.Bbs_Comment_Api_Result;
import com.mymusic.orvai.high_pitched_tone.API_Result.Bbs_Comment_delete_API_Result;
import com.mymusic.orvai.high_pitched_tone.API_Result.Bbs_Comment_view_API_Result;
import com.mymusic.orvai.high_pitched_tone.API_Result.Bbs_View_API_Result;
import com.mymusic.orvai.high_pitched_tone.API_Result.Bbs_View_content_API_Result;
import com.mymusic.orvai.high_pitched_tone.API_Result.Bbs_View_delete_API_Result;
import com.mymusic.orvai.high_pitched_tone.API_Result.Bbs_View_modify_API_Result;
import com.mymusic.orvai.high_pitched_tone.API_Result.Bbs_View_recommend_API_Result;
import com.mymusic.orvai.high_pitched_tone.API_Result.Bbs_Write_API_Result;
import com.mymusic.orvai.high_pitched_tone.API_Result.Game_high_pitch_challenge_API_Result;
import com.mymusic.orvai.high_pitched_tone.API_Result.Game_high_pitch_challenge_rank_API_Result;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by orvai on 2018-01-31.
 */

public interface API_Service {
    @Headers({"Accept: application/json"}) // 헤더는 넣어도 되고 안넣어도 되고

    @Multipart
    @POST("High_Pitch_Bbs.php?action_call=registration") // 게시글 멀티파트 업로드 방식 뒤에는 DB로 넘겨줄 php 주소를 적어준다.
    Call<Bbs_Write_API_Result> bbs_registration(
            @Part("Bbs_Subject") RequestBody subject, // 제목
            @Part("Bbs_Content") RequestBody content, // 내용
            @Part("Bbs_User") RequestBody user, // 유저 아이디
            @Part MultipartBody.Part file, // 이미지파일
            @Part MultipartBody.Part file1 // 동영상파일
    );

    @FormUrlEncoded
    @POST("High_Pitch_Bbs.php?action_call=viewbbs") // 배열 째로 가져와, recycler view에 구현하는 방식 페이징 적용
    Call<List<Bbs_View_API_Result>> getBbs_view(
            @Field("paging_no") int page);

    @FormUrlEncoded
    @POST("High_Pitch_Bbs.php?action_call=viewcontent") // 게시글을 읽기 위해 값을 하나의 int 값을 요청(parsing 하긴 했는데 그냥 String으로 줄 걸 그랬어...)
    Call<Bbs_View_content_API_Result> request_Bbs_content_view(
            @Field("bbs_request_no") String request_no,
            @Field("bbs_rec_user") String username
    );

    @FormUrlEncoded
    @POST("High_Pitch_Bbs.php?action_call=contentdelete") // 게시글을 지우기 위해 하나의 String 값을 요청
    Call<Bbs_View_delete_API_Result> request_Bbs_content_delete(
            @Field("bbs_request_no") String request_no);

    @Multipart
    @POST("High_Pitch_Bbs.php?action_call=contentmodify") // 게시글을 지우기 위해 하나의 String 값을 요청
    Call<Bbs_View_modify_API_Result> request_Bbs_content_modify(
            @Part("bbs_request_no") RequestBody request_no,
            @Part("Bbs_Subject") RequestBody subject,
            @Part("Bbs_Content") RequestBody content,
            @Part MultipartBody.Part m_file1,
            @Part MultipartBody.Part m_file2
    );


    @FormUrlEncoded
    @POST("High_Pitch_Bbs.php?action_call=content_recommend_do")
    Call<Bbs_View_recommend_API_Result> request_Bbs_recommend(
            @Field("bbs_rec_parent_no") String parent,
            @Field("bbs_rec_user") String user);

    @FormUrlEncoded
    @POST("High_Pitch_Bbs.php?action_call=content_comment")
    Call<Bbs_Comment_Api_Result> request_Bbs_comment(
            @Field("bbs_com_parent_no") String parent,
            @Field("bbs_com_user") String user,
            @Field("bbs_comment") String comment);

    @FormUrlEncoded
    @POST("High_Pitch_Bbs.php?action_call=comment_view")
    Call<List<Bbs_Comment_view_API_Result>> view_Bbs_comment(
            @Field("com_parent_no") String parent);

    @FormUrlEncoded
    @POST("High_Pitch_Bbs.php?action_call=comment_delete")
    Call<Bbs_Comment_delete_API_Result> Bbs_comment_delete(
            @Field("com_no") String number,
            @Field("com_parent_no") String parent);

    @FormUrlEncoded
    @POST("High_Pitch_game.php?action_call=high_pitch_challenge")
    Call<Game_high_pitch_challenge_API_Result> request_pitch_update(
            @Field("high_pitch_user") String user_id,
            @Field("high_pitch_note") String pitch_note,
            @Field("high_pitch_no") int number);

    @GET("High_Pitch_game.php?action_call=high_pitch_rank")
    Call<List<Game_high_pitch_challenge_rank_API_Result>> request_pitch_rank();








//    @GET("High_Pitch_Bbs.php?action_call=content_paging")
//    Call<Bbs_View_API_Result_test> getBbs_view_test(
//            @Query("number") int number);


    /*
    @Path("userId") String id : id로 들어간 String 값을 1.에서 말한 {userId}로 넘겨줍니다.
                                즉, http://jsonplaceholder.typicode.com/posts/{userId}
                                 id에 "1"이라는 값이 들어가면 통신의 최종적인 주소는 http://jsonplaceholder.typicode.com/posts/1 이 되는 것입니다.
     */

}