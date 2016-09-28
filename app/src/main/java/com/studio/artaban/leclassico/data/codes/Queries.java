package com.studio.artaban.leclassico.data.codes;

import com.studio.artaban.leclassico.data.DataTable;
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
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 07/09/16.
 * Query ID & limit
 */
public class Queries {

    public static final String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm:ss"; // Query date & time format

    ////// ID //////////////////////////////////////////////////////////////////////////////////////

    private static final int MAX_TABLE_ID = 255;

    ////// Main
    public static final int MAIN_NOTIFICATION_FLAG = MAX_TABLE_ID;
    public static final int MAIN_NOTIFICATION_COUNT = MAX_TABLE_ID + 1;
    public static final int MAIN_LAST_FOLLOWED = MAX_TABLE_ID + 2;
    public static final int MAIN_NOTIFICATIONS = MAX_TABLE_ID + 3;
    public static final int MAIN_NOTIFICATION_MAX = MAX_TABLE_ID + 4;

    //////
    public static String getTableUri(int id) {
        Logs.add(Logs.Type.V, "id: " + id);

        switch (id) { // Switch on particular table query ID & SQL query ID

            ////// Particular table query ID ///////////////////////////////////////////////////////

            // Main
            case MAIN_NOTIFICATION_FLAG:
            case MAIN_NOTIFICATION_COUNT:
            case MAIN_NOTIFICATION_MAX:
                return NotificationsTable.TABLE_NAME;

            ////// SQL query ID ////////////////////////////////////////////////////////////////////

            // Main
            case MAIN_NOTIFICATIONS:
            case MAIN_LAST_FOLLOWED:

                return DataTable.SQL_QUERY_URI; // SQL query (complex queries)
        }
        switch ((byte)id) { // Switch on table ID

            case Tables.ID_CAMARADES: return CamaradesTable.TABLE_NAME;
            case Tables.ID_ABONNEMENTS: return AbonnementsTable.TABLE_NAME;
            case Tables.ID_ACTUALITES: return ActualitesTable.TABLE_NAME;
            case Tables.ID_ALBUMS: return AlbumsTable.TABLE_NAME;
            case Tables.ID_COMMENTAIRES: return CommentairesTable.TABLE_NAME;
            case Tables.ID_EVENEMENTS: return EvenementsTable.TABLE_NAME;
            case Tables.ID_MESSAGERIE: return MessagerieTable.TABLE_NAME;
            case Tables.ID_MUSIC: return MusicTable.TABLE_NAME;
            case Tables.ID_PHOTOS: return PhotosTable.TABLE_NAME;
            case Tables.ID_PRESENTS: return PresentsTable.TABLE_NAME;
            case Tables.ID_VOTES: return VotesTable.TABLE_NAME;
            case Tables.ID_NOTIFICATIONS: return NotificationsTable.TABLE_NAME;
            default:
                throw new IllegalArgumentException("Unexpected loader ID");
        }
    }

    ////// Limit ///////////////////////////////////////////////////////////////////////////////////
    // Max entry count to display into a list view at start

    public static final short LIMIT_NOTIFICATIONS = 20;

    ////// Older ///////////////////////////////////////////////////////////////////////////////////
    // Max entry count to display into a list after the last element (when older entry is requested)

    public static final short OLDER_NOTIFICATIONS = 5;

}
