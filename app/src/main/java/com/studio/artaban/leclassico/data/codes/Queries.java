package com.studio.artaban.leclassico.data.codes;

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
 * Query IDs
 */
public class Queries {

    private static final int MAX_TABLE_ID = 255;

    ////// Main
    public static final int MAIN_NOTIFICATION_FLAG = MAX_TABLE_ID;
    public static final int MAIN_NOTIFICATION_COUNT = MAX_TABLE_ID + 1;

    //////
    public static String getTableUri(int id) {
        Logs.add(Logs.Type.V, "id: " + id);

        switch (id) {

            ////// Main ////////////////////////////////////////////////////////////////////////////
            case MAIN_NOTIFICATION_FLAG:
            case MAIN_NOTIFICATION_COUNT:
                return NotificationsTable.TABLE_NAME;

            ////////////////////////////////////////////////////////////////////////////////////////
        }
        switch ((byte)id) {

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
}
