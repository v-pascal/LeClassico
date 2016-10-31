package com.studio.artaban.leclassico.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.studio.artaban.leclassico.data.codes.Tables;
import com.studio.artaban.leclassico.data.tables.AbonnementsTable;
import com.studio.artaban.leclassico.data.tables.ActualitesTable;
import com.studio.artaban.leclassico.data.tables.AlbumsTable;
import com.studio.artaban.leclassico.data.tables.CamaradesTable;
import com.studio.artaban.leclassico.data.tables.CommentairesTable;
import com.studio.artaban.leclassico.data.tables.EvenementsTable;
import com.studio.artaban.leclassico.data.tables.MessagerieTable;
import com.studio.artaban.leclassico.data.tables.MusicTable;
import com.studio.artaban.leclassico.data.tables.NotificationsTable;
import com.studio.artaban.leclassico.data.tables.PhotosTable;
import com.studio.artaban.leclassico.data.tables.PresentsTable;
import com.studio.artaban.leclassico.data.tables.VotesTable;
import com.studio.artaban.leclassico.helpers.Database;
import com.studio.artaban.leclassico.helpers.Logs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pascal on 28/07/16.
 * Data content provider class
 */
public class DataProvider extends ContentProvider {

    public enum Synchronized {

        TODO((byte)0), IN_PROGRESS((byte)1), DONE((byte)2), TO_DELETE((byte)3);

        //
        private final byte id;
        Synchronized(byte id) { this.id = id; }
        public byte getValue() { return this.id; }
    }

    //////
    public static final String CONTENT_URI = "content://" + Constants.DATA_CONTENT_URI + '/';

    private static final String MIME_TYPE_SINGLE = "vnd.android.cursor.item/vnd." + Constants.APP_URI_COMPANY +
            '.' + Constants.APP_URI + '.';
    private static final String MIME_TYPE = "vnd.android.cursor.dir/vnd." + Constants.APP_URI_COMPANY +
            '.' + Constants.APP_URI + '.';
    public static final String SINGLE_ROW = "/#";

    private static final UriMatcher URI_MATCHER_SINGLE;
    private static final UriMatcher URI_MATCHER;
    static { // DB tables URI

        URI_MATCHER_SINGLE = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER_SINGLE.addURI(Constants.DATA_CONTENT_URI, CamaradesTable.TABLE_NAME + SINGLE_ROW, Tables.ID_CAMARADES);
        URI_MATCHER_SINGLE.addURI(Constants.DATA_CONTENT_URI, AbonnementsTable.TABLE_NAME + SINGLE_ROW, Tables.ID_ABONNEMENTS);
        URI_MATCHER_SINGLE.addURI(Constants.DATA_CONTENT_URI, ActualitesTable.TABLE_NAME + SINGLE_ROW, Tables.ID_ACTUALITES);
        URI_MATCHER_SINGLE.addURI(Constants.DATA_CONTENT_URI, AlbumsTable.TABLE_NAME + SINGLE_ROW, Tables.ID_ALBUMS);
        URI_MATCHER_SINGLE.addURI(Constants.DATA_CONTENT_URI, CommentairesTable.TABLE_NAME + SINGLE_ROW, Tables.ID_COMMENTAIRES);
        URI_MATCHER_SINGLE.addURI(Constants.DATA_CONTENT_URI, EvenementsTable.TABLE_NAME + SINGLE_ROW, Tables.ID_EVENEMENTS);
        URI_MATCHER_SINGLE.addURI(Constants.DATA_CONTENT_URI, MessagerieTable.TABLE_NAME + SINGLE_ROW, Tables.ID_MESSAGERIE);
        URI_MATCHER_SINGLE.addURI(Constants.DATA_CONTENT_URI, MusicTable.TABLE_NAME + SINGLE_ROW, Tables.ID_MUSIC);
        URI_MATCHER_SINGLE.addURI(Constants.DATA_CONTENT_URI, PhotosTable.TABLE_NAME + SINGLE_ROW, Tables.ID_PHOTOS);
        URI_MATCHER_SINGLE.addURI(Constants.DATA_CONTENT_URI, PresentsTable.TABLE_NAME + SINGLE_ROW, Tables.ID_PRESENTS);
        URI_MATCHER_SINGLE.addURI(Constants.DATA_CONTENT_URI, VotesTable.TABLE_NAME + SINGLE_ROW, Tables.ID_VOTES);
        URI_MATCHER_SINGLE.addURI(Constants.DATA_CONTENT_URI, NotificationsTable.TABLE_NAME + SINGLE_ROW, Tables.ID_NOTIFICATIONS);

        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(Constants.DATA_CONTENT_URI, CamaradesTable.TABLE_NAME, Tables.ID_CAMARADES);
        URI_MATCHER.addURI(Constants.DATA_CONTENT_URI, AbonnementsTable.TABLE_NAME, Tables.ID_ABONNEMENTS);
        URI_MATCHER.addURI(Constants.DATA_CONTENT_URI, ActualitesTable.TABLE_NAME, Tables.ID_ACTUALITES);
        URI_MATCHER.addURI(Constants.DATA_CONTENT_URI, AlbumsTable.TABLE_NAME, Tables.ID_ALBUMS);
        URI_MATCHER.addURI(Constants.DATA_CONTENT_URI, CommentairesTable.TABLE_NAME, Tables.ID_COMMENTAIRES);
        URI_MATCHER.addURI(Constants.DATA_CONTENT_URI, EvenementsTable.TABLE_NAME, Tables.ID_EVENEMENTS);
        URI_MATCHER.addURI(Constants.DATA_CONTENT_URI, MessagerieTable.TABLE_NAME, Tables.ID_MESSAGERIE);
        URI_MATCHER.addURI(Constants.DATA_CONTENT_URI, MusicTable.TABLE_NAME, Tables.ID_MUSIC);
        URI_MATCHER.addURI(Constants.DATA_CONTENT_URI, PhotosTable.TABLE_NAME, Tables.ID_PHOTOS);
        URI_MATCHER.addURI(Constants.DATA_CONTENT_URI, PresentsTable.TABLE_NAME, Tables.ID_PRESENTS);
        URI_MATCHER.addURI(Constants.DATA_CONTENT_URI, VotesTable.TABLE_NAME, Tables.ID_VOTES);
        URI_MATCHER.addURI(Constants.DATA_CONTENT_URI, NotificationsTable.TABLE_NAME, Tables.ID_NOTIFICATIONS);
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
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Logs.add(Logs.Type.V, null);

        // Open database (if not already opened)
        if (!mDB.isOpened())
            mDB.open(true); // Always open DB in writable mode

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        String table = Tables.getName((byte) URI_MATCHER_SINGLE.match(uri));

        // Check single row request
        if (table != null)
            builder.appendWhere(IDataTable.DataField.COLUMN_ID + '=' + uri.getLastPathSegment());
        else
            table = Tables.getName((byte) URI_MATCHER.match(uri));

        Cursor result;
        if (table == null) // Raw query (for multiple table queries)
            result = mDB.getDB().rawQuery(selection, selectionArgs);
        else {

            builder.setTables(table);
            result = builder.query(mDB.getDB(), projection, selection, selectionArgs, null, null, sortOrder);
        }
        result.setNotificationUri(getContext().getContentResolver(), uri);
        return result;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        Logs.add(Logs.Type.V, null);
        switch (URI_MATCHER_SINGLE.match(uri)) {
            case Tables.ID_CAMARADES: return MIME_TYPE_SINGLE + CamaradesTable.TABLE_NAME;
            case Tables.ID_ABONNEMENTS: return MIME_TYPE_SINGLE + AbonnementsTable.TABLE_NAME;
            case Tables.ID_ACTUALITES: return MIME_TYPE_SINGLE + ActualitesTable.TABLE_NAME;
            case Tables.ID_ALBUMS: return MIME_TYPE_SINGLE + AlbumsTable.TABLE_NAME;
            case Tables.ID_COMMENTAIRES: return MIME_TYPE_SINGLE + CommentairesTable.TABLE_NAME;
            case Tables.ID_EVENEMENTS: return MIME_TYPE_SINGLE + EvenementsTable.TABLE_NAME;
            case Tables.ID_MESSAGERIE: return MIME_TYPE_SINGLE + MessagerieTable.TABLE_NAME;
            case Tables.ID_MUSIC: return MIME_TYPE_SINGLE + MusicTable.TABLE_NAME;
            case Tables.ID_PHOTOS: return MIME_TYPE_SINGLE + PhotosTable.TABLE_NAME;
            case Tables.ID_PRESENTS: return MIME_TYPE_SINGLE + PresentsTable.TABLE_NAME;
            case Tables.ID_VOTES: return MIME_TYPE_SINGLE + VotesTable.TABLE_NAME;
            case Tables.ID_NOTIFICATIONS: return MIME_TYPE_SINGLE + NotificationsTable.TABLE_NAME;
        }
        switch (URI_MATCHER.match(uri)) {
            case Tables.ID_CAMARADES: return MIME_TYPE + CamaradesTable.TABLE_NAME;
            case Tables.ID_ABONNEMENTS: return MIME_TYPE + AbonnementsTable.TABLE_NAME;
            case Tables.ID_ACTUALITES: return MIME_TYPE + ActualitesTable.TABLE_NAME;
            case Tables.ID_ALBUMS: return MIME_TYPE + AlbumsTable.TABLE_NAME;
            case Tables.ID_COMMENTAIRES: return MIME_TYPE + CommentairesTable.TABLE_NAME;
            case Tables.ID_EVENEMENTS: return MIME_TYPE + EvenementsTable.TABLE_NAME;
            case Tables.ID_MESSAGERIE: return MIME_TYPE + MessagerieTable.TABLE_NAME;
            case Tables.ID_MUSIC: return MIME_TYPE + MusicTable.TABLE_NAME;
            case Tables.ID_PHOTOS: return MIME_TYPE + PhotosTable.TABLE_NAME;
            case Tables.ID_PRESENTS: return MIME_TYPE + PresentsTable.TABLE_NAME;
            case Tables.ID_VOTES: return MIME_TYPE + VotesTable.TABLE_NAME;
            case Tables.ID_NOTIFICATIONS: return MIME_TYPE + NotificationsTable.TABLE_NAME;
            default:
                throw new IllegalArgumentException("Unexpected content URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Logs.add(Logs.Type.V, null);

        // Open database (if not already opened)
        if (!mDB.isOpened())
            mDB.open(true);

        String table = Tables.getName((byte) URI_MATCHER.match(uri));
        if (table == null)
            throw new IllegalArgumentException("Unexpected content URI: " + uri);

        // NB: No need to check exiting synchronized field value (not a null field)
        long id = mDB.getDB().insert(table, null, values);
        if (id > Constants.NO_DATA) {

            Uri result = ContentUris.withAppendedId(uri, id); // Add new Id into URI result

            // Notify observers
            getContext().getContentResolver().notifyChange(result, null);
            return result;
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Logs.add(Logs.Type.V, null);

        // Open database (if not already opened)
        if (!mDB.isOpened())
            mDB.open(true);

        String table = Tables.getName((byte) URI_MATCHER_SINGLE.match(uri));
        if (table != null)
            selection = IDataTable.DataField.COLUMN_ID + '=' + uri.getLastPathSegment() +
                    ((!TextUtils.isEmpty(selection))? " AND (" + selection + ")":"");
        else {

            table = Tables.getName((byte) URI_MATCHER.match(uri));
            if (table == null)
                throw new IllegalArgumentException("Unexpected content URI: " + uri);

            // To delete all entries add a where clause...
            if (selection == null)
                selection = "1"; // ...by assigning '1'
        }
        int result;

        // Do not delete records without "TO_DELETE" synchronized flag but set it to this value instead
        if (!selection.contains(Constants.DATA_DELETE_SELECTION)) {

            ContentValues values = new ContentValues();
            values.put(Constants.DATA_COLUMN_SYNCHRONIZED, Synchronized.TO_DELETE.getValue());
            result = mDB.getDB().update(table, values, selection, selectionArgs);

            getContext().getContentResolver().notifyChange(uri, null);

        } else
            result = mDB.getDB().delete(table, selection, selectionArgs);
            // NB: Do not notify observer when deleting records definitively

        return result; // Return deleted entries count
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Logs.add(Logs.Type.V, null);

        // Open database (if not already opened)
        if (!mDB.isOpened())
            mDB.open(true);

        String table = Tables.getName((byte) URI_MATCHER_SINGLE.match(uri));
        if (table != null)
            selection = IDataTable.DataField.COLUMN_ID + '=' + uri.getLastPathSegment() +
                    ((!TextUtils.isEmpty(selection))? " AND (" + selection + ")":"");
        else {

            table = Tables.getName((byte) URI_MATCHER.match(uri));
            if (table == null)
                throw new IllegalArgumentException("Unexpected content URI: " + uri);
        }

        // Check exiting status date & synchronized fields values (always needed)
        if (!values.containsKey(Constants.DATA_COLUMN_SYNCHRONIZED))
            values.put(Constants.DATA_COLUMN_SYNCHRONIZED, Synchronized.TODO.getValue());
        if (!values.containsKey(Constants.DATA_COLUMN_STATUS_DATE)) {

            Date now = new Date();
            DateFormat dateFormat = new SimpleDateFormat(Constants.FORMAT_DATE_TIME);
            values.put(Constants.DATA_COLUMN_STATUS_DATE, dateFormat.format(now));
        }

        int result = mDB.getDB().update(table, values, selection, selectionArgs);
        if (result > 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return result; // Return updated entries count
    }
}
