package com.studio.artaban.leclassico.data.codes;

import com.studio.artaban.leclassico.data.tables.AbonnementsTable;
import com.studio.artaban.leclassico.data.tables.ActualitesTable;
import com.studio.artaban.leclassico.data.tables.AlbumsTable;
import com.studio.artaban.leclassico.data.tables.CamaradesTable;
import com.studio.artaban.leclassico.data.tables.CommentairesTable;
import com.studio.artaban.leclassico.data.tables.EvenementsTable;
import com.studio.artaban.leclassico.data.tables.LocationsTable;
import com.studio.artaban.leclassico.data.tables.MessagerieTable;
import com.studio.artaban.leclassico.data.tables.MusicTable;
import com.studio.artaban.leclassico.data.tables.NotificationsTable;
import com.studio.artaban.leclassico.data.tables.PhotosTable;
import com.studio.artaban.leclassico.data.tables.PresentsTable;
import com.studio.artaban.leclassico.data.tables.VotesTable;
import com.studio.artaban.leclassico.data.tables.persistent.LinksTable;

/**
 * Created by pascal on 11/08/16.
 * Tables ID
 */
public final class Tables {

    public static final byte ID_CAMARADES = 1; // Always start with 1 (0 is reserved)
    public static final byte ID_ABONNEMENTS = 2;
    public static final byte ID_ACTUALITES = 3;
    public static final byte ID_EVENEMENTS = 4;
    public static final byte ID_MESSAGERIE = 5;
    public static final byte ID_MUSIC = 6;
    public static final byte ID_VOTES = 7;
    public static final byte ID_PHOTOS = 8;
    public static final byte ID_ALBUMS = 9;
    public static final byte ID_COMMENTAIRES = 10; // NB: Must be > ID_ACTUALITES & ID_PHOTOS coz fill
    public static final byte ID_PRESENTS = 11;     //     comments according publications & photos
    public static final byte ID_NOTIFICATIONS = 12;
    public static final byte ID_LOCATIONS = 13;

    public static final byte ID_LAST = ID_LOCATIONS;

    // Persistent tables
    public static final byte ID_LINKS = 14;

    //////
    public static final int ID_MAX = 127; // Max positive byte value

    public static String getName(byte id) { // Return DB table name according its ID
        //Logs.add(Logs.Type.V, "id: " + id);
        switch (id) {

            case Tables.ID_CAMARADES: return CamaradesTable.TABLE_NAME;
            case Tables.ID_ABONNEMENTS: return AbonnementsTable.TABLE_NAME;
            case Tables.ID_ACTUALITES: return ActualitesTable.TABLE_NAME;
            case Tables.ID_ALBUMS: return AlbumsTable.TABLE_NAME;
            case Tables.ID_EVENEMENTS: return EvenementsTable.TABLE_NAME;
            case Tables.ID_MESSAGERIE: return MessagerieTable.TABLE_NAME;
            case Tables.ID_MUSIC: return MusicTable.TABLE_NAME;
            case Tables.ID_PHOTOS: return PhotosTable.TABLE_NAME;
            case Tables.ID_COMMENTAIRES: return CommentairesTable.TABLE_NAME;
            case Tables.ID_PRESENTS: return PresentsTable.TABLE_NAME;
            case Tables.ID_VOTES: return VotesTable.TABLE_NAME;
            case Tables.ID_NOTIFICATIONS: return NotificationsTable.TABLE_NAME;
            case Tables.ID_LOCATIONS: return LocationsTable.TABLE_NAME;

            // Persistent tables
            case Tables.ID_LINKS: return LinksTable.TABLE_NAME;
        }
        return null;
    }

}
