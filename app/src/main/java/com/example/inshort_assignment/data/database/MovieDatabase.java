package com.example.inshort_assignment.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.inshort_assignment.data.database.entities.BookmarkedMovie;
import com.example.inshort_assignment.data.database.entities.Movie;

@Database(
        entities = {Movie.class , BookmarkedMovie.class},
        version = 2,
        exportSchema = false
)
public abstract class MovieDatabase extends RoomDatabase {
    private static volatile MovieDatabase INSTANCE;

    public abstract MovieDao movieDao();

    public static MovieDatabase getDatabase(final Context context){
        if(INSTANCE == null){
            synchronized (MovieDatabase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),MovieDatabase.class,"movie_database").fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCE;
    }
}
