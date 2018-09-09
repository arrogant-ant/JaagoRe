package iris.jaagore.sabita_sant.alarm.backend;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

/**
 * Created by Sud on 7/28/18.
 */
@Dao
public interface QuoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addQuote(Quote quote);

    @Update
    void updateQuote(Quote quote);

    @Delete
    void  deleteQuote(Quote quote);

    @Query("SELECT * FROM  Quotes WHERE s_no =:s_no LIMIT 1")
    Quote getQuote(int s_no);
}
