package com.example.larisa.liketimisoara.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.larisa.liketimisoara.Attraction;
import com.example.larisa.liketimisoara.AttractionType;
import com.example.larisa.liketimisoara.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Database implementation.
 */

public class DB extends SQLiteOpenHelper {

    public static final String TAG = DB.class.getName();

    private static DB instance;

    private DB(Context context) {
        super(context, DBContract.DATABASE_NAME, null, DBContract.DATABASE_VERSION);
    }

    public static DB getInstance(Context context) {

        if (instance == null) {
            instance = new DB(context);
        }

        return instance;
    }

    /**
     * Called when the database is created for the first time.
     * This is where the creation of tables and the initial population
     * of the tables should happen.
     *
     * @param db the database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBContract.AttractionsTable.CREATE_TABLE);
    }

    /**
     * Called when the database needs to be upgraded.
     * The implementation should use this method to drop tables,
     * add tables, or do anything else it needs to upgrade to the new schema version.
     *
     * @param db         the database
     * @param oldVersion the old database version
     * @param newVersion the new database version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DBContract.AttractionsTable.DELETE_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // delete old DB
        onUpgrade(db, oldVersion, newVersion);
    }

    public long insertAttraction(Attraction attraction) throws DBException {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBContract.AttractionsTable.COLUMN_TYPE, attraction.getType().ordinal());
        contentValues.put(DBContract.AttractionsTable.COLUMN_NAME, attraction.getName());
        contentValues.put(DBContract.AttractionsTable.COLUMN_INFO, attraction.getInfo());
        contentValues.put(DBContract.AttractionsTable.COLUMN_IMAGE_RESOURCE_ID, attraction.getImageResourceId());
        contentValues.put(DBContract.AttractionsTable.COLUMN_PHONE, attraction.getPhone());
        contentValues.put(DBContract.AttractionsTable.COLUMN_IS_TOP_10, String.valueOf(attraction.isTop10()));
        contentValues.put(DBContract.AttractionsTable.COLUMN_LONGITUDE, attraction.getLongitude());
        contentValues.put(DBContract.AttractionsTable.COLUMN_LATITUDE, attraction.getLatitude());

        long rowId = db.insert(DBContract.AttractionsTable.TABLE_NAME, null, contentValues);

        if (rowId == -1) {
            Log.w(TAG, "Error inserting attraction in DB");
            throw new DBException("cannot insert row");
        }

        return rowId;
    }

    public List<Attraction> getAttractions(AttractionType type) {

        List<Attraction> results = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        // Define a projection that specifies which columns from the database you will actually use after this query.
        String[] projection = {
                DBContract.AttractionsTable._ID,
                DBContract.AttractionsTable.COLUMN_TYPE,
                DBContract.AttractionsTable.COLUMN_NAME,
                DBContract.AttractionsTable.COLUMN_INFO,
                DBContract.AttractionsTable.COLUMN_IMAGE_RESOURCE_ID,
                DBContract.AttractionsTable.COLUMN_PHONE,
                DBContract.AttractionsTable.COLUMN_IS_TOP_10,
                DBContract.AttractionsTable.COLUMN_LONGITUDE,
                DBContract.AttractionsTable.COLUMN_LATITUDE,
        };

        // The columns for the WHERE clause
        String selection = "";
        String whereOperator = "";
        if (type != null) {
            selection += whereOperator + " " + DBContract.AttractionsTable.COLUMN_TYPE + " = " + type.ordinal();
        }

        Cursor c = null;
        try {
            c = db.query(
                    DBContract.AttractionsTable.TABLE_NAME,     // The table to query
                    projection,                                 // The columns to return
                    selection,                                  // The columns for the WHERE clause
                    null,                                       // The values for the WHERE clause
                    null,                                       // don't group the rows
                    null,                                       // don't filter by row groups
                    null,                                       // The sort order
                    null
            );

            c.moveToFirst();

            while (!c.isAfterLast()) {
                long rowId = c.getLong(c.getColumnIndex(DBContract.AttractionsTable._ID));

                Attraction attraction = new Attraction();

                attraction.setType(AttractionType.values()[c.getInt(c.getColumnIndex(DBContract.AttractionsTable.COLUMN_TYPE))]);
                attraction.setName(c.getString(c.getColumnIndex(DBContract.AttractionsTable.COLUMN_NAME)));
                attraction.setInfo(c.getString(c.getColumnIndex(DBContract.AttractionsTable.COLUMN_INFO)));
                attraction.setImageResourceId(c.getInt(c.getColumnIndex(DBContract.AttractionsTable.COLUMN_IMAGE_RESOURCE_ID)));
                attraction.setPhone(c.getString(c.getColumnIndex(DBContract.AttractionsTable.COLUMN_PHONE)));
                attraction.setTop10(Boolean.valueOf(c.getString(c.getColumnIndex(DBContract.AttractionsTable.COLUMN_IS_TOP_10))));
                attraction.setLongitude(c.getDouble(c.getColumnIndex(DBContract.AttractionsTable.COLUMN_LONGITUDE)));
                attraction.setLatitude(c.getDouble(c.getColumnIndex(DBContract.AttractionsTable.COLUMN_LATITUDE)));

                results.add(attraction);

                c.moveToNext();

            }

        } finally {
            if (c != null) {
                c.close();
            }
        }

        return results;
    }

    public void deleteAttractions() {

        SQLiteDatabase db = this.getWritableDatabase();

        int rowsAffected = db.delete(
                DBContract.AttractionsTable.TABLE_NAME,
                null,
                null
        );

        Log.d(TAG, "DELETED - " + rowsAffected);
    }

    public void initDatabase(Context context) {

        DB database = DB.getInstance(context);

        try {
            database.insertAttraction(new Attraction(0, context.getString(R.string.extreme_fitness_name), AttractionType.SALA_FITNESS, context.getString(R.string.extreme_fitness_info),
                    R.drawable.extreme, context.getString(R.string.extreme_fitness_phone), false, 21.2386, 45.728675))  ;
            database.insertAttraction(new Attraction(0, context.getString(R.string.friends_arena_name), AttractionType.SALA_FITNESS, context.getString(R.string.friends_arena_info),
                    R.drawable.friends, context.getString(R.string.friends_arena_phone), false, 21.2013, 45.7301899))  ;
            database.insertAttraction(new Attraction(0, context.getString(R.string.iguana_fitness_name), AttractionType.SALA_FITNESS, context.getString(R.string.iguana_fitness_info),
                    R.drawable.iguana, context.getString(R.string.iguana_fitness_phone),false, 21.2561, 45.7639368))  ;
            database.insertAttraction(new Attraction(0, context.getString(R.string.stil_fitness_name), AttractionType.SALA_FITNESS, context.getString(R.string.stil_fitness_info),
                    R.drawable.stil, context.getString(R.string.stil_fitness_phone), false, 21.2441, 45.7434774))  ;
            database.insertAttraction(new Attraction(0, context.getString(R.string.body_time_name), AttractionType.SALA_FITNESS, context.getString(R.string.body_time_info),
                    R.drawable.bodytime, context.getString(R.string.body_time_phone), false, 21.2313, 45.7739105))  ;
            database.insertAttraction(new Attraction(0, context.getString(R.string.smart_fit3_name), AttractionType.SALA_FITNESS, context.getString(R.string.smart_fit3_info),
                    R.drawable.smartfit, context.getString(R.string.smart_fit3_phone), false, 21.1998, 45.724749))  ;
            database.insertAttraction(new Attraction(0, context.getString(R.string.continental_fitness_name), AttractionType.SALA_FITNESS, context.getString(R.string.continental_fitness_info),
                    R.drawable.continental, context.getString(R.string.continental_fitness_phone), false,21.2324, 45.7553))  ;
            database.insertAttraction(new Attraction(0, context.getString(R.string.world_class_name), AttractionType.SALA_FITNESS, context.getString(R.string.world_class_info),
                    R.drawable.worldclass, context.getString(R.string.world_class_phone), false, 21.2297,45.7627697))  ;
            database.insertAttraction(new Attraction(0, context.getString(R.string.banu_fitness_name), AttractionType.SALA_FITNESS, context.getString(R.string.banu_fitness_info),
                    R.drawable.banusport,context.getString(R.string.banu_sport_phone), false, 21.242, 45.74354))  ;

            database.insertAttraction(new Attraction(0, context.getString(R.string.squash_sport_name), AttractionType.SALA_SQUASH, context.getString(R.string.squash_sport_info),
                    R.drawable.squasharena, context.getString(R.string.squash_sport_phone), false, 21.2161, 45.7728636))  ;
            database.insertAttraction(new Attraction(0, context.getString(R.string.squash_club_name), AttractionType.SALA_SQUASH, context.getString(R.string.squash_club_info),
                    R.drawable.squashclub, context.getString(R.string.squash_club_phone),  false, 21.246, 45.7546726))  ;
            database.insertAttraction(new Attraction(0, context.getString(R.string.helios_squash_name), AttractionType.SALA_SQUASH, context.getString(R.string.helios_squash_info),
                    R.drawable.helios, context.getString(R.string.helios_squash_phone), false, 21.238, 45.7385141))  ;
            database.insertAttraction(new Attraction(0, context.getString(R.string.the_squashers_name), AttractionType.SALA_SQUASH, context.getString(R.string.the_squashers_info),
                    R.drawable.squashers, context.getString(R.string.the_squashers_phone), false, 21.9050, 47.048961))  ;

            database.insertAttraction(new Attraction(0, context.getString(R.string.arena_pool_name), AttractionType.BAZIN_INOT, context.getString(R.string.arena_pool_info),
                    R.drawable.arenapool, context.getString(R.string.arena_pool_phone), false, 21.2672, 45.769224))  ;
            database.insertAttraction(new Attraction(0, context.getString(R.string.ice_dyp_name), AttractionType.BAZIN_INOT, context.getString(R.string.ice_dyp_info),
                    R.drawable.icedyp, context.getString(R.string.ice_dyp_phone),false, 21.25910, 45.765198))  ;
            database.insertAttraction(new Attraction(0, context.getString(R.string.smart_fit3_name), AttractionType.BAZIN_INOT, context.getString(R.string.smart_fit3_info),
                    R.drawable.smartfit,context.getString(R.string.smart_fit3_phone), false, 21.1998, 45.724749))  ;

            database.insertAttraction(new Attraction(0, context.getString(R.string.club_tivoli_name), AttractionType.TEREN_TENIS, context.getString(R.string.club_tivoli_info),
                    R.drawable.tivoli, context.getString(R.string.club_tivoli_phone), false, 21.275, 45.763754))  ;
            database.insertAttraction(new Attraction(0, context.getString(R.string.club_helios_name), AttractionType.TEREN_TENIS, context.getString(R.string.club_helios_info),
                    R.drawable.helios, context.getString(R.string.club_helios_phone),false, 21.238, 45.7385141))  ;
            database.insertAttraction(new Attraction(0, context.getString(R.string.berlin_sport_name), AttractionType.TEREN_TENIS, context.getString(R.string.berlin_sport_info),
                    R.drawable.berlin, context.getString(R.string.berlin_sport_phone),false, 21.2337, 45.7883605))  ;
            database.insertAttraction(new Attraction(0, context.getString(R.string.banu_sport_name), AttractionType.TEREN_TENIS, context.getString(R.string.banu_sport_info),
                    R.drawable.banusport, context.getString(R.string.banu_sport_phone),false, 21.242, 45.74354))  ;

            database.insertAttraction(new Attraction(0, context.getString(R.string.tudo_taxi_name), AttractionType.COMPANIE_TAXI, context.getString(R.string.tudo_taxi_info),
                    R.drawable.tudo, context.getString(R.string.tudo_taxi_phone), false, 21.2672, 45.769224))  ;
            database.insertAttraction(new Attraction(0, context.getString(R.string.fan_taxi_name), AttractionType.COMPANIE_TAXI, context.getString(R.string.fan_taxi_info),
                    R.drawable.fantaxi,context.getString(R.string.fan_taxi_phone), false, 21.25910, 45.765198))  ;
            database.insertAttraction(new Attraction(0, context.getString(R.string.radio_taxi_name), AttractionType.COMPANIE_TAXI, context.getString(R.string.radio_taxi_info),
                    R.drawable.taxi, context.getString(R.string.radio_taxi_phone), false, 21.1998, 45.724749))  ;
            database.insertAttraction(new Attraction(0, context.getString(R.string.pro_taxi_name), AttractionType.COMPANIE_TAXI, context.getString(R.string.pro_taxi_info),
                    R.drawable.taxi, context.getString(R.string.pro_taxi_phone), false, 21.238, 45.7385141))  ;
            database.insertAttraction(new Attraction(0, context.getString(R.string.grup_taxi_name), AttractionType.COMPANIE_TAXI, context.getString(R.string.grup_taxi_info),
                    R.drawable.taxi, context.getString(R.string.grup_taxi_phone), false, 21.2337, 45.7883605))  ;
            database.insertAttraction(new Attraction(0, context.getString(R.string.taxi_timisoara_name), AttractionType.COMPANIE_TAXI, context.getString(R.string.taxi_timisoara_info),
                    R.drawable.taxi, context.getString(R.string.taxi_timisoara_phone),false, 21.242, 45.74354))  ;

            database.insertAttraction(new Attraction(0, context.getString(R.string.deluxe_rent_name), AttractionType.INCHIRIERI_AUTO, context.getString(R.string.deluxe_rent_info),
                    R.drawable.deluxe, context.getString(R.string.deluxe_rent_phone), false, 21.23161, 45.7290652))  ;
            database.insertAttraction(new Attraction(0, context.getString(R.string.novum_rent_name), AttractionType.INCHIRIERI_AUTO, context.getString(R.string.novum_rent_info),
                    R.drawable.novum, context.getString(R.string.novum_rent_phone), false, 21.2124, 45.743281))  ;
            database.insertAttraction(new Attraction(0, context.getString(R.string.marvomil_rent_name), AttractionType.INCHIRIERI_AUTO, context.getString(R.string.marvomil_rent_info),
                    R.drawable.narvomil, context.getString(R.string.marvomil_rent_phone),false, 21.2322, 45.774424))  ;
            database.insertAttraction(new Attraction(0, context.getString(R.string.nataly_rent_name), AttractionType.INCHIRIERI_AUTO, context.getString(R.string.nataly_rent_info),
                    R.drawable.nataly, context.getString(R.string.nataly_rent_phone), false, 21.2400, 45.7605894))  ;
            database.insertAttraction(new Attraction(0, context.getString(R.string.swiso_rent_name), AttractionType.INCHIRIERI_AUTO, context.getString(R.string.swiso_rent_info),
                    R.drawable.swiso, context.getString(R.string.swiso_rent_phone),false, 21.207169, 45.742574))  ;

            database.insertAttraction(new Attraction(0, context.getString(R.string.velo_tm_name), AttractionType.STATIE_BICICLETE, context.getString(R.string.nataly_rent_info),
                    R.drawable.nataly, context.getString(R.string.extreme_fitness_phone), false, 21.22369, 45.75140))  ;
            database.insertAttraction(new Attraction(0, context.getString(R.string.velo_tm_name), AttractionType.STATIE_BICICLETE, context.getString(R.string.swiso_rent_info),
                    R.drawable.swiso, context.getString(R.string.extreme_fitness_phone),false, 21.19121, 45.71155))  ;
            database.insertAttraction(new Attraction(0, context.getString(R.string.velo_tm_name), AttractionType.STATIE_BICICLETE, context.getString(R.string.swiso_rent_info),
                    R.drawable.swiso,context.getString(R.string.extreme_fitness_phone), false, 21.21781, 45.75857))  ;
            database.insertAttraction(new Attraction(0, context.getString(R.string.velo_tm_name), AttractionType.STATIE_BICICLETE, context.getString(R.string.swiso_rent_info),
                    R.drawable.swiso, context.getString(R.string.extreme_fitness_phone),false, 21.20471, 45.72830))  ;
            database.insertAttraction(new Attraction(0, context.getString(R.string.velo_tm_name), AttractionType.STATIE_BICICLETE, context.getString(R.string.swiso_rent_info),
                    R.drawable.swiso,context.getString(R.string.extreme_fitness_phone), false, 21.22122, 45.77504))  ;
            database.insertAttraction(new Attraction(0, context.getString(R.string.velo_tm_name), AttractionType.STATIE_BICICLETE, context.getString(R.string.swiso_rent_info),
                    R.drawable.swiso,context.getString(R.string.extreme_fitness_phone), false, 21.22218, 45.75748))  ;
            database.insertAttraction(new Attraction(0, context.getString(R.string.velo_tm_name), AttractionType.STATIE_BICICLETE, context.getString(R.string.swiso_rent_info),
                    R.drawable.swiso,context.getString(R.string.extreme_fitness_phone), false, 21.22218, 45.75748))  ;
            database.insertAttraction(new Attraction(0, context.getString(R.string.velo_tm_name), AttractionType.STATIE_BICICLETE, context.getString(R.string.swiso_rent_info),
                    R.drawable.swiso, context.getString(R.string.extreme_fitness_phone),false, 21.24957, 45.75904))  ;
            database.insertAttraction(new Attraction(0, context.getString(R.string.velo_tm_name), AttractionType.STATIE_BICICLETE, context.getString(R.string.swiso_rent_info),
                    R.drawable.swiso,context.getString(R.string.extreme_fitness_phone), false, 21.24306, 45.74479))  ;
            database.insertAttraction(new Attraction(0, context.getString(R.string.velo_tm_name), AttractionType.STATIE_BICICLETE, context.getString(R.string.swiso_rent_info),
                    R.drawable.swiso,context.getString(R.string.extreme_fitness_phone), false, 21.20994, 45.74716))  ;

            database.insertAttraction(new Attraction(0, context.getString(R.string.art_museum_name), AttractionType.MUZEU, context.getString(R.string.art_museum_info),
                    R.drawable.muzeuarta,context.getString(R.string.extreme_fitness_phone), false, 21.2293, 45.75738))  ;
            database.insertAttraction(new Attraction(0, context.getString(R.string.banat_museum_name), AttractionType.MUZEU, context.getString(R.string.banat_museum_info),
                    R.drawable.mbanatului, context.getString(R.string.extreme_fitness_phone),true, 21.233961, 45.757258))  ;
            database.insertAttraction(new Attraction(0, context.getString(R.string.communist_museum_name), AttractionType.MUZEU, context.getString(R.string.commmunist_museum_info),
                    R.drawable.mconsumatorului,context.getString(R.string.extreme_fitness_phone), false, 21.22425, 45.74323))  ;
            database.insertAttraction(new Attraction(0, context.getString(R.string.banat_village_museum_name), AttractionType.MUZEU, context.getString(R.string.banat_village_museum_info),
                    R.drawable.msatului,context.getString(R.string.extreme_fitness_phone), true, 21.2653, 45.77818))  ;
            database.insertAttraction(new Attraction(0, context.getString(R.string.liberty_square_name), AttractionType.PIATA, context.getString(R.string.liberty_square_info),
                    R.drawable.liberatatiisquare, context.getString(R.string.extreme_fitness_phone),true, 21.22778, 45.755))  ;
            database.insertAttraction(new Attraction(0, context.getString(R.string.victory_square_name), AttractionType.PIATA, context.getString(R.string.victory_square_info),
                    R.drawable.victorieisquare, context.getString(R.string.extreme_fitness_phone),true, 21.225003, 45.75286))  ;
            database.insertAttraction(new Attraction(0, context.getString(R.string.traian_square_name), AttractionType.PIATA, context.getString(R.string.traian_square_info),
                    R.drawable.traiansquare, context.getString(R.string.extreme_fitness_phone),false, 21.2493, 45.7573))  ;
            database.insertAttraction(new Attraction(0, context.getString(R.string.st_gheorghe_square_name), AttractionType.PIATA, context.getString(R.string.st_gheorghe_square_info),
                    R.drawable.gheorghesquare, context.getString(R.string.extreme_fitness_phone),false, 21.228, 45.75577))  ;
            database.insertAttraction(new Attraction(0, context.getString(R.string.union_square_name), AttractionType.PIATA, context.getString(R.string.union_square_info),
                    R.drawable.uniriisquare, context.getString(R.string.extreme_fitness_phone),true, 21.2290, 45.7580))  ;

            database.insertAttraction(new Attraction(0, context.getString(R.string.childrens_park_name), AttractionType.PARC, context.getString(R.string.childrens_park_info),
                    R.drawable.copiilor,context.getString(R.string.extreme_fitness_phone), false, 21.233, 45.751611))  ;
            database.insertAttraction(new Attraction(0, context.getString(R.string.roses_park_name), AttractionType.PARC, context.getString(R.string.roses_park_info),
                    R.drawable.rozelor,context.getString(R.string.extreme_fitness_phone), true, 21.231682, 45.7500943))  ;
            database.insertAttraction(new Attraction(0, context.getString(R.string.botanical_park_name), AttractionType.PARC, context.getString(R.string.botanical_park_info),
                    R.drawable.botanic, context.getString(R.string.extreme_fitness_phone),false, 21.22530, 45.7601963))  ;
            database.insertAttraction(new Attraction(0, context.getString(R.string.cathedral_park_name), AttractionType.PARC, context.getString(R.string.cathedral_park_info),
                    R.drawable.catedralei, context.getString(R.string.extreme_fitness_phone),false, 21.2237, 45.74996))  ;
            database.insertAttraction(new Attraction(0, context.getString(R.string.civic_park_name), AttractionType.PARC, context.getString(R.string.civic_park_info),
                    R.drawable.civic,context.getString(R.string.extreme_fitness_phone), false, 21.231559, 45.7540275))  ;
            database.insertAttraction(new Attraction(0, context.getString(R.string.justice_park_name), AttractionType.PARC, context.getString(R.string.justice_park_info),
                    R.drawable.justitiei, context.getString(R.string.extreme_fitness_phone),false, 21.227336, 45.7499622))  ;
            database.insertAttraction(new Attraction(0, context.getString(R.string.central_park_name),AttractionType.PARC, context.getString(R.string.info_central_park),
                    R.drawable.parculcentral, context.getString(R.string.extreme_fitness_phone),true, 21.2211, 45.7513));
            database.insertAttraction(new Attraction(0, context.getString(R.string.bastion_name),AttractionType.ALTE_OBIECTIVE, context.getString(R.string.bastion_info),
                    R.drawable.bastion, context.getString(R.string.extreme_fitness_phone),true, 21.23392, 45.75721));
            database.insertAttraction(new Attraction(0, context.getString(R.string.timisoreana_name),AttractionType.ALTE_OBIECTIVE, context.getString(R.string.timisoreana_info),
                    R.drawable.timisoreana, context.getString(R.string.extreme_fitness_phone),true, 21.248941, 45.754762));
            database.insertAttraction(new Attraction(0, context.getString(R.string.millenium_cathedral_name),AttractionType.CATEDRALA, context.getString(R.string.millenium_cathedral_info),
                    R.drawable.bisericamillenium, context.getString(R.string.extreme_fitness_phone),true, 21.247785, 45.756680));
            database.insertAttraction(new Attraction(0, context.getString(R.string.ortodox_cathedral_name),AttractionType.CATEDRALA, context.getString(R.string.ortodox_cathedral_info),
                    R.drawable.cathedral,context.getString(R.string.extreme_fitness_phone), false, 21.2242708, 45.7507216));
            database.insertAttraction(new Attraction(0, context.getString(R.string.assumption_church_name),AttractionType.CATEDRALA, context.getString(R.string.assumption_church_info),
                    R.drawable.assumption,context.getString(R.string.extreme_fitness_phone), false, 21.219880, 45.7725689));
            database.insertAttraction(new Attraction(0, context.getString(R.string.catherine_church_name), AttractionType.CATEDRALA, context.getString(R.string.catherine_church_info),
                    R.drawable.sfecaterina,context.getString(R.string.extreme_fitness_phone), false, 21.2280593, 45.7543008));
            database.insertAttraction(new Attraction(0, context.getString(R.string.roman_catholic_catedral_name),AttractionType.CATEDRALA, context.getString(R.string.roman_catholic_catedral_info),
                    R.drawable.cathedralstgheorghe,context.getString(R.string.extreme_fitness_phone), false, 21.2300940, 45.7581453));
            database.insertAttraction(new Attraction(0, context.getString(R.string.elim_church_name),AttractionType.CATEDRALA, context.getString(R.string.elim_church_info),
                    R.drawable.elimjpg,context.getString(R.string.extreme_fitness_phone), false, 21.2188, 45.7446917));
            database.insertAttraction(new Attraction(0, context.getString(R.string.birth_mary_name),AttractionType.CATEDRALA, context.getString(R.string.birth_mary_info),
                    R.drawable.birthofmarychurch, context.getString(R.string.extreme_fitness_phone),false, 21.21646, 45.746083));

            database.insertAttraction(new Attraction(0, context.getString(R.string.merlot_restaurant_name),AttractionType.RESTAURANT, context.getString(R.string.merlot_restaurant_info),
                    R.drawable.merlot, context.getString(R.string.merlot_restaurant_phone ),false, 21.2420, 45.756454));
            database.insertAttraction(new Attraction(0, context.getString(R.string.sabres_restaurant_name),AttractionType.RESTAURANT, context.getString(R.string.sabres_restaurant_info),
                    R.drawable.sabres, context.getString(R.string.sabres_restaurant_phone),false, 21.238341, 45.742456));
            database.insertAttraction(new Attraction(0, context.getString(R.string.grill_to_chill_name),AttractionType.RESTAURANT, context.getString(R.string.grill_to_chill_info),
                    R.drawable.grilltochill, context.getString(R.string.grill_to_chill_phone), false, 21.228497, 45.756759));
            database.insertAttraction(new Attraction(0, context.getString(R.string.caruso_restaurant_name),AttractionType.RESTAURANT, context.getString(R.string.caruso_restaurant_info),
                    R.drawable.carusojpg, context.getString(R.string.caruso_restaurant_phone),false, 21.229376, 45.7552659));
            database.insertAttraction(new Attraction(0, context.getString(R.string.amphora_enoteca_restaurant_name),AttractionType.RESTAURANT, context.getString(R.string.amphora_enoteca_restaurant_info),
                    R.drawable.amphoraenoteca, context.getString(R.string.amphora_enoteca_restaurant_phone),false, 21.233603, 45.756395));
            database.insertAttraction(new Attraction(0, context.getString(R.string.belvedere_restaurant_name),AttractionType.RESTAURANT, context.getString(R.string.belvedere_restaurant_info),
                    R.drawable.belvedere,context.getString(R.string.belvedere_restaurant_phone), false, 21.22538, 45.754426));
            database.insertAttraction(new Attraction(0, context.getString(R.string.enoteca_restaurant_name),AttractionType.RESTAURANT, context.getString(R.string.enoteca_restaurant_info),
                    R.drawable.enotecasavoy,context.getString(R.string.enoteca_restaurant_phone), false, 21.2283462, 45.75667079));
            database.insertAttraction(new Attraction(0, context.getString(R.string.mercy_restaurant_name),AttractionType.RESTAURANT, context.getString(R.string.merlot_restaurant_info),
                    R.drawable.mercy,context.getString(R.string.mercy_restaurant_phone), false, 21.22868, 45.7563201));
            database.insertAttraction(new Attraction(0, context.getString(R.string.ristorante_al_duomo_name),AttractionType.RESTAURANT, context.getString(R.string.ristorante_al_duomo_info),
                    R.drawable.ristorantealduomojpg, context.getString(R.string.ristorante_al_duomo_phone),false, 21.230911, 45.7579943));
            database.insertAttraction(new Attraction(0, context.getString(R.string.timsoreana_restaurant_name),AttractionType.RESTAURANT, context.getString(R.string.timisoareana_restaurant_info),
                    R.drawable.restimisoreana,context.getString(R.string.timisoareana_restaurant_phone), false, 21.225171, 45.7534005));
            database.insertAttraction(new Attraction(0, context.getString(R.string.chinese_restaurant_name),AttractionType.RESTAURANT, context.getString(R.string.chinese_restaurant_info),
                    R.drawable.chinezesc,context.getString(R.string.chinese_restaurant_phone), false, 21.2512388, 45.7632856));
            database.insertAttraction(new Attraction(0, context.getString(R.string.riviere_restaurant_name),AttractionType.RESTAURANT, context.getString(R.string.riviere_restaurant_info),
                    R.drawable.riviere,context.getString(R.string.riviere_restaurant_phone), false, 21.22661, 45.7488825));
            database.insertAttraction(new Attraction(0, context.getString(R.string.banat_garden_name),AttractionType.RESTAURANT, context.getString(R.string.banat_garden_info),
                    R.drawable.gradinabanateana, context.getString(R.string.banat_garden_phone), false, 21.226525, 45.7489663));
            database.insertAttraction(new Attraction(0, context.getString(R.string.la_strada_name),AttractionType.RESTAURANT, context.getString(R.string.la_strada_info),
                    R.drawable.lastrada,context.getString(R.string.la_strada_phone), false, 21.22915, 45.7660743));
            database.insertAttraction(new Attraction(0, context.getString(R.string.sky_restaurant_name),AttractionType.RESTAURANT, context.getString(R.string.sky_restaurant_info),
                    R.drawable.skyrestaurant,context.getString(R.string.sky_restaurant_phone), false, 21.22185, 45.756769));
            database.insertAttraction(new Attraction(0, context.getString(R.string.flower_house_name),AttractionType.RESTAURANT, context.getString(R.string.flower_house_info),
                    R.drawable.casacuflori,context.getString(R.string.flower_house_phone), false, 21.226839, 45.7546834));

            database.insertAttraction(new Attraction(0, context.getString(R.string.tucano_cafe_name),AttractionType.CAFENEA, context.getString(R.string.tucano_cafe_info),
                    R.drawable.tucanojpg, context.getString(R.string.tucano_cafe_phone),false, 21.2271253, 45.7567436));
            database.insertAttraction(new Attraction(0, context.getString(R.string.starbucks_name),AttractionType.CAFENEA, context.getString(R.string.starbucks_info),
                    R.drawable.starbucksjpg,context.getString(R.string.starbucks_phone), false, 21.2270906, 45.766370));
            database.insertAttraction(new Attraction(0, context.getString(R.string.segafredo_name),AttractionType.CAFENEA, context.getString(R.string.segafredo_info),
                    R.drawable.segafredo,context.getString(R.string.segafredo_phone), false, 21.22459, 45.7522865));
            database.insertAttraction(new Attraction(0, context.getString(R.string.cafeneaua_verde_name),AttractionType.CAFENEA, context.getString(R.string.cafeneaua_verde_info),
                    R.drawable.cafeneauaverde, context.getString(R.string.cafeneaua_verde_phone),false, 21.2268, 45.756137));
            database.insertAttraction(new Attraction(0, context.getString(R.string.viviani_cafe_name),AttractionType.CAFENEA, context.getString(R.string.viviani_cafe_info),
                    R.drawable.viviani,context.getString(R.string.viviani_cafe_phone), false, 21.21778, 45.7631301));
            database.insertAttraction(new Attraction(0, context.getString(R.string.garage_cafe_name),AttractionType.CAFENEA, context.getString(R.string.garage_cafe_info),
                    R.drawable.garagecafe, context.getString(R.string.garage_cafe_phone),false, 21.230150, 45.7573328));
            database.insertAttraction(new Attraction(0, context.getString(R.string.bierhaus_name),AttractionType.CAFENEA, context.getString(R.string.bierhaus_info),
                    R.drawable.birhaus,context.getString(R.string.bierhaus_phone), false, 21.22714, 45.7585487));
            database.insertAttraction(new Attraction(0, context.getString(R.string.toniq_bar_name),AttractionType.CAFENEA, context.getString(R.string.toniq_bar_info),
                    R.drawable.toniq, context.getString(R.string.toniq_bar_phone),false, 21.2269, 45.7490329));
            database.insertAttraction(new Attraction(0, context.getString(R.string.vineri_15_name),AttractionType.CAFENEA, context.getString(R.string.vineri_15_name),
                    R.drawable.vineri15, context.getString(R.string.vineri_15_phone),false, 21.230367, 45.748054));
            database.insertAttraction(new Attraction(0, context.getString(R.string.la_capite_name),AttractionType.CAFENEA, context.getString(R.string.la_capite_info),
                    R.drawable.lacapite, context.getString(R.string.la_capite_phone),false, 21.230972, 45.7482099));

            database.insertAttraction(new Attraction(0, context.getString(R.string.heaven_club_name),AttractionType.CLUB, context.getString(R.string.heaven_club_info),
                    R.drawable.heaven, context.getString(R.string.heaven_club_phone),false, 21.2491382, 45.7396));
            database.insertAttraction(new Attraction(0, context.getString(R.string.epic_club_name),AttractionType.CLUB, context.getString(R.string.epic_club_info),
                    R.drawable.epic, context.getString(R.string.epic_club_phone),false, 21.222902, 45.7692854));
            database.insertAttraction(new Attraction(0, context.getString(R.string.fratelli_club_name),AttractionType.CLUB, context.getString(R.string.fratelli_club_info),
                    R.drawable.fratelipng, context.getString(R.string.fratelli_club_phone),false, 21.2366, 45.750845));
            database.insertAttraction(new Attraction(0, context.getString(R.string.club_taine_name),AttractionType.CLUB, context.getString(R.string.club_taine_info),
                    R.drawable.taine,context.getString(R.string.club_taine_phone), false, 21.2302, 45.758429));
            database.insertAttraction(new Attraction(0, context.getString(R.string.club_daos_name),AttractionType.CLUB, context.getString(R.string.club_daos_info),
                    R.drawable.daos, context.getString(R.string.club_daos_phone),false, 21.21652, 45.7504715));
            database.insertAttraction(new Attraction(0, context.getString(R.string.bunker_name),AttractionType.CLUB, context.getString(R.string.bunker_info),
                    R.drawable.bunker, context.getString(R.string.bunker_phone), false, 21.22704, 45.7586585));
            database.insertAttraction(new Attraction(0, context.getString(R.string.nouveau_name),AttractionType.CLUB, context.getString(R.string.nouveau_info),
                    R.drawable.nouveau,context.getString(R.string.nouveau_phone), false, 21.226541, 45.7576361));
            database.insertAttraction(new Attraction(0, context.getString(R.string.storia_name),AttractionType.CLUB, context.getString(R.string.storia_info),
                    R.drawable.storia, context.getString(R.string.storia_phone),false, 21.228027, 45.756657));
            database.insertAttraction(new Attraction(0, context.getString(R.string.wakeup_name),AttractionType.CLUB, context.getString(R.string.wakeup_info),
                    R.drawable.wakeup,context.getString(R.string.wakeup_phone), false, 21.238395, 45.7500517));
            database.insertAttraction(new Attraction(0, context.getString(R.string.jardin_name),AttractionType.CLUB, context.getString(R.string.jardin_info),
                    R.drawable.jardin, context.getString(R.string.jardin_phone),false, 21.22924, 45.7480009));
            database.insertAttraction(new Attraction(0, context.getString(R.string.scottish_name),AttractionType.CLUB, context.getString(R.string.scottish_info),
                    R.drawable.sccotish,context.getString(R.string.scottish_phone), false, 21.2286, 45.756618));
            database.insertAttraction(new Attraction(0, context.getString(R.string.atu_name),AttractionType.CLUB, context.getString(R.string.atu_info),
                    R.drawable.atu, context.getString(R.string.atu_phone),false, 21.23854, 45.7499828));

            database.insertAttraction(new Attraction(0, context.getString(R.string.hotel_timisoara_name),AttractionType.HOTEL, context.getString(R.string.hotel_timisoara_info),
                    R.drawable.hoteltimisoara, context.getString(R.string.club_daos_phone),false, 21.225327, 45.754758));
            database.insertAttraction(new Attraction(0, context.getString(R.string.iosefin_residence_name),AttractionType.HOTEL, context.getString(R.string.iosefin_residence_info),
                    R.drawable.iosefinresidence, context.getString(R.string.club_daos_phone),false, 21.2158, 45.74664));
            database.insertAttraction(new Attraction(0, context.getString(R.string.hotel_central_name),AttractionType.HOTEL, context.getString(R.string.hotel_central_info),
                    R.drawable.centralhostel, context.getString(R.string.club_daos_phone),false, 21.226835, 45.757676));
            database.insertAttraction(new Attraction(0, context.getString(R.string.hotel_savoy_name),AttractionType.HOTEL, context.getString(R.string.hotel_savoy_info),
                    R.drawable.hotelsavoy, context.getString(R.string.club_daos_phone),false, 21.2243426, 45.747708));
            database.insertAttraction(new Attraction(0, context.getString(R.string.hotel_excelsior_name),AttractionType.HOTEL, context.getString(R.string.hotel_excelsior_info),
                    R.drawable.hotelexcelsior,context.getString(R.string.club_daos_phone), false, 21.219738, 45.7430566));
            database.insertAttraction(new Attraction(0, context.getString(R.string.hotel_perla_name),AttractionType.HOTEL, context.getString(R.string.hotel_perla_info),
                    R.drawable.hotelperla,context.getString(R.string.club_daos_phone), false, 21.227554, 45.7421759));
            database.insertAttraction(new Attraction(0, context.getString(R.string.hotel_nh_name),AttractionType.HOTEL, context.getString(R.string.hotel_nh_info),
                    R.drawable.nh, context.getString(R.string.club_daos_phone),false, 21.2420786, 45.754617));
            database.insertAttraction(new Attraction(0, context.getString(R.string.hotel_president_name),AttractionType.HOTEL, context.getString(R.string.hotel_president_info),
                    R.drawable.hotelpresident, context.getString(R.string.club_daos_phone),false, 21.212774, 45.758356));
            database.insertAttraction(new Attraction(0, context.getString(R.string.hotel_arta_name),AttractionType.HOTEL, context.getString(R.string.hotel_arta_info),
                    R.drawable.hotelarta, context.getString(R.string.club_daos_phone),false, 21.289125, 45.773353));
            database.insertAttraction(new Attraction(0, context.getString(R.string.hotel_euro_name),AttractionType.HOTEL, context.getString(R.string.hotel_euro_info),
                    R.drawable.hoteleuro, context.getString(R.string.club_daos_phone),false, 21.24464, 45.7509937));
            database.insertAttraction(new Attraction(0, context.getString(R.string.hotel_pacific_name),AttractionType.HOTEL, context.getString(R.string.hotel_pacific_info),
                    R.drawable.hotelpacific, context.getString(R.string.club_daos_phone),false, 21.24685599, 45.764335));

            database.insertAttraction(new Attraction(0, context.getString(R.string.hostel_nord_name),AttractionType.HOSTEL, context.getString(R.string.hostel_nord_info),
                    R.drawable.hostelnord,context.getString(R.string.club_daos_phone), false, 21.20877, 45.749113));
            database.insertAttraction(new Attraction(0, context.getString(R.string.central_hostel_name),AttractionType.HOSTEL, context.getString(R.string.central_hostel_info),
                    R.drawable.centralhostel, context.getString(R.string.club_daos_phone),false, 21.22677, 45.754769));
            database.insertAttraction(new Attraction(0, context.getString(R.string.hotel_costel_name),AttractionType.HOSTEL, context.getString(R.string.hotel_costel_info),
                    R.drawable.hostelcostel,context.getString(R.string.club_daos_phone), false, 21.244692, 45.758361));
            database.insertAttraction(new Attraction(0, context.getString(R.string.hostel_exit_name),AttractionType.HOSTEL, context.getString(R.string.hostel_exit_info),
                    R.drawable.exithostel,context.getString(R.string.club_daos_phone), false, 21.23808, 45.74456));
            database.insertAttraction(new Attraction(0, context.getString(R.string.hostel_downtown_name),AttractionType.HOSTEL, context.getString(R.string.hostel_downtown_info),
                    R.drawable.downtownhostel,context.getString(R.string.club_daos_phone), false, 21.225529, 45.752525));
            database.insertAttraction(new Attraction(0, context.getString(R.string.hostel_freeborn_name),AttractionType.HOSTEL, context.getString(R.string.hostel_freeborn_info),
                    R.drawable.freebornhostel,context.getString(R.string.club_daos_phone), false, 21.228861, 45.752934));

            database.insertAttraction(new Attraction(0, context.getString(R.string.pensiune_elisei_name),AttractionType.PENSIUNE, context.getString(R.string.pensiune_elisei_info),
                    R.drawable.elisei, context.getString(R.string.club_daos_phone),false, 21.218500, 45.7346672));
            database.insertAttraction(new Attraction(0, context.getString(R.string.pensiune_normandia_name),AttractionType.PENSIUNE, context.getString(R.string.pensiune_normandia_info),
                    R.drawable.normandia,context.getString(R.string.club_daos_phone), false, 21.231151, 45.733085));
            database.insertAttraction(new Attraction(0, context.getString(R.string.pensiune_timisreana_name),AttractionType.PENSIUNE, context.getString(R.string.pensiune_timisoreana_info),
                    R.drawable.pensiunetimisoara, context.getString(R.string.club_daos_phone),false, 21.21080, 45.7400199));
            database.insertAttraction(new Attraction(0, context.getString(R.string.pensiune_venus_name),AttractionType.PENSIUNE, context.getString(R.string.pensiune_venus_info),
                    R.drawable.pensiunevenus,context.getString(R.string.club_daos_phone), false, 21.227288, 45.7354789));
            database.insertAttraction(new Attraction(0, context.getString(R.string.pensiune_vladut_name),AttractionType.PENSIUNE, context.getString(R.string.pensiune_vladut_info),
                    R.drawable.pensiunevlad, context.getString(R.string.club_daos_phone),false, 21.278629, 45.75612));
            database.insertAttraction(new Attraction(0, context.getString(R.string.pensiune_yasmine_name),AttractionType.PENSIUNE, context.getString(R.string.pensiune_yasmine_info),
                    R.drawable.yasmine, context.getString(R.string.club_daos_phone),false, 21.26158, 45.759727));
            database.insertAttraction(new Attraction(0, context.getString(R.string.pensiune_voiaj_name),AttractionType.PENSIUNE, context.getString(R.string.pensiune_voiaj_info),
                    R.drawable.pensiunevoiajjpg,context.getString(R.string.club_daos_phone), false, 21.21456, 45.7745));

        } catch (DBException ex) {


        }

    }
}
