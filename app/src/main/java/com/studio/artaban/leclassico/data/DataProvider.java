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
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 28/07/16.
 * Data content provider class
 */
public class DataProvider extends ContentProvider {

    public static final String CONTENT_URI = "content://" + Constants.DATA_CONTENT_URI + "/";

    private static final String MIME_TYPE_SINGLE = "vnd.android.cursor.item/vnd." + Constants.APP_COMPANY + ".";
    private static final String MIME_TYPE = "vnd.android.cursor.dir/vnd." + Constants.APP_COMPANY + ".";
    private static final String SINGLE_ROW = "/#";

    private static final UriMatcher URI_MATCHER_SINGLE;
    private static final UriMatcher URI_MATCHER;
    static {

        // Table requests
        URI_MATCHER_SINGLE = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER_SINGLE.addURI(Constants.DATA_CONTENT_URI, CamaradesTable.TABLE_NAME + SINGLE_ROW,
                Constants.DATA_CAMARADES_TABLE_ID);
        URI_MATCHER_SINGLE.addURI(Constants.DATA_CONTENT_URI, AbonnementsTable.TABLE_NAME + SINGLE_ROW,
                Constants.DATA_ABONNEMENTS_TABLE_ID);
        URI_MATCHER_SINGLE.addURI(Constants.DATA_CONTENT_URI, ActualitesTable.TABLE_NAME + SINGLE_ROW,
                Constants.DATA_ACTUALITES_TABLE_ID);
        URI_MATCHER_SINGLE.addURI(Constants.DATA_CONTENT_URI, AlbumsTable.TABLE_NAME + SINGLE_ROW,
                Constants.DATA_ALBUMS_TABLE_ID);
        URI_MATCHER_SINGLE.addURI(Constants.DATA_CONTENT_URI, CommentairesTable.TABLE_NAME + SINGLE_ROW,
                Constants.DATA_COMMENTAIRES_TABLE_ID);
        URI_MATCHER_SINGLE.addURI(Constants.DATA_CONTENT_URI, EvenementsTable.TABLE_NAME + SINGLE_ROW,
                Constants.DATA_EVENEMENTS_TABLE_ID);
        URI_MATCHER_SINGLE.addURI(Constants.DATA_CONTENT_URI, MessagerieTable.TABLE_NAME + SINGLE_ROW,
                Constants.DATA_MESSAGERIE_TABLE_ID);
        URI_MATCHER_SINGLE.addURI(Constants.DATA_CONTENT_URI, MusicTable.TABLE_NAME + SINGLE_ROW,
                Constants.DATA_MUSIC_TABLE_ID);
        URI_MATCHER_SINGLE.addURI(Constants.DATA_CONTENT_URI, PhotosTable.TABLE_NAME + SINGLE_ROW,
                Constants.DATA_PHOTOS_TABLE_ID);
        URI_MATCHER_SINGLE.addURI(Constants.DATA_CONTENT_URI, PresentsTable.TABLE_NAME + SINGLE_ROW,
                Constants.DATA_PRESENTS_TABLE_ID);
        URI_MATCHER_SINGLE.addURI(Constants.DATA_CONTENT_URI, VotesTable.TABLE_NAME + SINGLE_ROW,
                Constants.DATA_VOTES_TABLE_ID);

        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
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

    private String getUriTable(UriMatcher matcher, Uri uri) {
     // Return DB table request according the URI

        Logs.add(Logs.Type.V, "matcher: " + matcher + " uri: " + uri);
        switch (matcher.match(uri)) {

            case Constants.DATA_CAMARADES_TABLE_ID: return CamaradesTable.TABLE_NAME;
            case Constants.DATA_ABONNEMENTS_TABLE_ID: return AbonnementsTable.TABLE_NAME;
            case Constants.DATA_ACTUALITES_TABLE_ID: return ActualitesTable.TABLE_NAME;
            case Constants.DATA_ALBUMS_TABLE_ID: return AlbumsTable.TABLE_NAME;
            case Constants.DATA_COMMENTAIRES_TABLE_ID: return CommentairesTable.TABLE_NAME;
            case Constants.DATA_EVENEMENTS_TABLE_ID: return EvenementsTable.TABLE_NAME;
            case Constants.DATA_MESSAGERIE_TABLE_ID: return MessagerieTable.TABLE_NAME;
            case Constants.DATA_MUSIC_TABLE_ID: return MusicTable.TABLE_NAME;
            case Constants.DATA_PHOTOS_TABLE_ID: return PhotosTable.TABLE_NAME;
            case Constants.DATA_PRESENTS_TABLE_ID: return PresentsTable.TABLE_NAME;
            case Constants.DATA_VOTES_TABLE_ID: return VotesTable.TABLE_NAME;
        }
        return null;
    }

    //////
    @Override
    public boolean onCreate() {
        mDB = new DB(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Logs.add(Logs.Type.V, null);

        // Open database (if not already opened)
        if (!mDB.isOpened())
            mDB.open(true); // Always open DB in writable mode

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        String table = getUriTable(URI_MATCHER_SINGLE, uri);

        // Check single row request
        if (table != null)
            builder.appendWhere(IDataTable.DataField.COLUMN_ID + "=" + uri.getPathSegments().get(1));
        else {

            table = getUriTable(URI_MATCHER, uri);
            if (table == null)
                throw new IllegalArgumentException("Unexpected content URI: " + uri);
        }
        builder.setTables(table);
        return builder.query(mDB.getDB(), projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        switch (URI_MATCHER_SINGLE.match(uri)) {
            case Constants.DATA_CAMARADES_TABLE_ID: return MIME_TYPE_SINGLE + CamaradesTable.TABLE_NAME;
            case Constants.DATA_ABONNEMENTS_TABLE_ID: return MIME_TYPE_SINGLE + AbonnementsTable.TABLE_NAME;
            case Constants.DATA_ACTUALITES_TABLE_ID: return MIME_TYPE_SINGLE + ActualitesTable.TABLE_NAME;
            case Constants.DATA_ALBUMS_TABLE_ID: return MIME_TYPE_SINGLE + AlbumsTable.TABLE_NAME;
            case Constants.DATA_COMMENTAIRES_TABLE_ID: return MIME_TYPE_SINGLE + CommentairesTable.TABLE_NAME;
            case Constants.DATA_EVENEMENTS_TABLE_ID: return MIME_TYPE_SINGLE + EvenementsTable.TABLE_NAME;
            case Constants.DATA_MESSAGERIE_TABLE_ID: return MIME_TYPE_SINGLE + MessagerieTable.TABLE_NAME;
            case Constants.DATA_MUSIC_TABLE_ID: return MIME_TYPE_SINGLE + MusicTable.TABLE_NAME;
            case Constants.DATA_PHOTOS_TABLE_ID: return MIME_TYPE_SINGLE + PhotosTable.TABLE_NAME;
            case Constants.DATA_PRESENTS_TABLE_ID: return MIME_TYPE_SINGLE + PresentsTable.TABLE_NAME;
            case Constants.DATA_VOTES_TABLE_ID: return MIME_TYPE_SINGLE + VotesTable.TABLE_NAME;
        }
        switch (URI_MATCHER.match(uri)) {
            case Constants.DATA_CAMARADES_TABLE_ID: return MIME_TYPE + CamaradesTable.TABLE_NAME;
            case Constants.DATA_ABONNEMENTS_TABLE_ID: return MIME_TYPE + AbonnementsTable.TABLE_NAME;
            case Constants.DATA_ACTUALITES_TABLE_ID: return MIME_TYPE + ActualitesTable.TABLE_NAME;
            case Constants.DATA_ALBUMS_TABLE_ID: return MIME_TYPE + AlbumsTable.TABLE_NAME;
            case Constants.DATA_COMMENTAIRES_TABLE_ID: return MIME_TYPE + CommentairesTable.TABLE_NAME;
            case Constants.DATA_EVENEMENTS_TABLE_ID: return MIME_TYPE + EvenementsTable.TABLE_NAME;
            case Constants.DATA_MESSAGERIE_TABLE_ID: return MIME_TYPE + MessagerieTable.TABLE_NAME;
            case Constants.DATA_MUSIC_TABLE_ID: return MIME_TYPE + MusicTable.TABLE_NAME;
            case Constants.DATA_PHOTOS_TABLE_ID: return MIME_TYPE + PhotosTable.TABLE_NAME;
            case Constants.DATA_PRESENTS_TABLE_ID: return MIME_TYPE + PresentsTable.TABLE_NAME;
            case Constants.DATA_VOTES_TABLE_ID: return MIME_TYPE + VotesTable.TABLE_NAME;
            default:
                throw new IllegalArgumentException("Unexpected content URI: " + uri);
        }
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
