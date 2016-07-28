package com.studio.artaban.leclassico.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.studio.artaban.leclassico.data.tables.AbonnementsTable;
import com.studio.artaban.leclassico.data.tables.ActualitesTable;
import com.studio.artaban.leclassico.data.tables.AlbumsTable;
import com.studio.artaban.leclassico.data.tables.CamaradesTable;
import com.studio.artaban.leclassico.data.tables.CommentairesTable;
import com.studio.artaban.leclassico.data.tables.EvenementsTable;
import com.studio.artaban.leclassico.data.tables.MessagerieTable;
import com.studio.artaban.leclassico.data.tables.MusicTable;
import com.studio.artaban.leclassico.data.tables.PhotosTable;
import com.studio.artaban.leclassico.data.tables.PresentsTable;
import com.studio.artaban.leclassico.data.tables.VotesTable;
import com.studio.artaban.leclassico.helpers.Database;

/**
 * Created by pascal on 28/07/16.
 * Data content provider class
 */
public class DataProvider extends ContentProvider {

    public static final String CONTENT_URI = "content://" + Constants.DATA_CONTENT_URI + "/";

    private static final UriMatcher URI_MATCHER;
    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

        // Table requests
        URI_MATCHER.addURI(Constants.DATA_CONTENT_URI, CamaradesTable.TABLE_NAME, Constants.DATA_CAMARADES_TABLE_ID);
        URI_MATCHER.addURI(Constants.DATA_CONTENT_URI, AbonnementsTable.TABLE_NAME, Constants.DATA_ABONNEMENTS_TABLE_ID);
        URI_MATCHER.addURI(Constants.DATA_CONTENT_URI, ActualitesTable.TABLE_NAME, Constants.DATA_ACTUALITES_TABLE_ID);
        URI_MATCHER.addURI(Constants.DATA_CONTENT_URI, AlbumsTable.TABLE_NAME, Constants.DATA_ALBUMS_TABLE_ID);
        URI_MATCHER.addURI(Constants.DATA_CONTENT_URI, CommentairesTable.TABLE_NAME, Constants.DATA_COMMENTAIRES_TABLE_ID);
        URI_MATCHER.addURI(Constants.DATA_CONTENT_URI, EvenementsTable.TABLE_NAME, Constants.DATA_EVENEMENTS_TABLE_ID);
        URI_MATCHER.addURI(Constants.DATA_CONTENT_URI, MessagerieTable.TABLE_NAME, Constants.DATA_MESSAGERIE_TABLE_ID);
        URI_MATCHER.addURI(Constants.DATA_CONTENT_URI, MusicTable.TABLE_NAME, Constants.DATA_MUSIC_TABLE_ID);
        URI_MATCHER.addURI(Constants.DATA_CONTENT_URI, PhotosTable.TABLE_NAME, Constants.DATA_PHOTOS_TABLE_ID);
        URI_MATCHER.addURI(Constants.DATA_CONTENT_URI, PresentsTable.TABLE_NAME, Constants.DATA_PRESENTS_TABLE_ID);
        URI_MATCHER.addURI(Constants.DATA_CONTENT_URI, VotesTable.TABLE_NAME, Constants.DATA_VOTES_TABLE_ID);
    }

    //
    private static class DB extends Database {

        public DB(Context context) { super(context); }
        public SQLiteDatabase getDB() { return mDatabase; }
        public boolean isOpened() { return isReady(); };
    }
    private DB mDB;

    //////
    @Override
    public boolean onCreate() {
        mDB = new DB(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        // Open database (if not already opened)
        if (!mDB.isOpened())
            mDB.open(true); // Always open DB in writable mode







        switch (URI_MATCHER.match(uri)) {
            case Constants.DATA_CAMARADES_TABLE_ID:
            case Constants.DATA_ABONNEMENTS_TABLE_ID:
            case Constants.DATA_ACTUALITES_TABLE_ID:
            case Constants.DATA_ALBUMS_TABLE_ID:
            case Constants.DATA_COMMENTAIRES_TABLE_ID:
            case Constants.DATA_EVENEMENTS_TABLE_ID:
            case Constants.DATA_MESSAGERIE_TABLE_ID:
            case Constants.DATA_MUSIC_TABLE_ID:
            case Constants.DATA_PHOTOS_TABLE_ID:
            case Constants.DATA_PRESENTS_TABLE_ID:
            case Constants.DATA_VOTES_TABLE_ID: {
                break;
            }
        }

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables("");

        //mDB.getDB().query()







        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {




        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {




        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {





        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {





        return 0;
    }
}
