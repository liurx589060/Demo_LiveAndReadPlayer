package com.lrxliveandreadplayer.demo.network;

import com.lrxliveandreadplayer.demo.beans.jmessage.JMMemeberBean;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2018/4/28.
 */

public interface RequestApi {
    @GET("JMessage/getChartRoomMemeberList")
    Observable<JMMemeberBean> getChartRoomMemeberList(@Query("roomId") long roomId);

    @GET("JMessage/createChartRoom")
    Observable<JMMemeberBean> createChartRoom(@Query("userName") String userName, @Query("gender") String gender
            , @Query("level") int level);

    @GET("JMessage/exitChartRoom")
    Observable<JMMemeberBean> exitChartRoom(@Query("userName") String userName,@Query("index") int index
            ,@Query("roomId") long roomId);

    @GET("JMessage/deleteChartRoom")
    Observable<JMMemeberBean> deleteChartRoom(@Query("roomId") long roomId);

    @GET("JMessage/joinChartRoom")
    Observable<JMMemeberBean> joinChartRoom(@Query("userName") String userName,@Query("gender") String gender
            ,@Query("level") int level);


}
