package com.example.topnewgrid.dao;

import android.content.ContentValues;

import com.example.topnewgrid.bean.ChannelItem;

import java.util.List;
import java.util.Map;


@SuppressWarnings("ALL")
public interface ChannelDaoInterface {

    boolean addCache(ChannelItem item);

    boolean deleteCache(String whereClause, String[] whereArgs);

    boolean updateCache(ContentValues values, String whereClause, String[] whereArgs);

    Map<String, String> viewCache(String selection, String[] selectionArgs);

    List<Map<String, String>> listCache(String selection, String[] selectionArgs);

    void clearFeedTable();
}
