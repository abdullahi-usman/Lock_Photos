package com.dahham.lockphotos.zenquotes

import android.content.Context
import androidx.room.*


@Dao
interface ZenQuoteDaO{

    @Query("SELECT * FROM ZenQuote")
    fun getAll(): List<ZenQuote>?

    @Query("SELECT * FROM ZenQuote ORDER BY noHasBeenUsed ASC LIMIT 1")
    fun getleastUsed(): ZenQuote?

    @Insert
    fun putAll(vararg zenquotes: ZenQuote)

    @Delete
    fun remove(vararg zenquotes: ZenQuote)

    @Query("SELECT * FROM ZenQuote WHERE noHasBeenUsed >= :maxNoOfUse")
    fun getPurgeable(maxNoOfUse: Int): Array<ZenQuote>

    @Transaction
    fun Purge(maxNoOfUse: Int){
        remove(*getPurgeable(maxNoOfUse))
    }


}

@Database(entities = [ZenQuote::class], version = 1)
abstract class ZenQuoteDatabase : RoomDatabase(){

    abstract fun getDaO(): ZenQuoteDaO

    suspend fun cacheQuotes(zenquotes: Array<ZenQuote>){
        getDaO().putAll(*zenquotes)
    }

    suspend fun getQuote(): ZenQuote?{
        val dao = getDaO()

        return dao.getleastUsed().also {
            if (it != null) {
                it.noHasBeenUsed += 1
                dao.putAll(it)
            }
        }
    }

    suspend fun getQuotes(): List<ZenQuote>?{
        return getDaO().getAll()
    }

    suspend fun purge(maxNoOfUse: Int){
        getDaO().Purge(maxNoOfUse)
    }

    companion object {
        @Volatile
        private var INSTANCE: ZenQuoteDatabase? = null

        fun getInstance(context: Context) = INSTANCE ?: synchronized(this){
            INSTANCE ?: Room.databaseBuilder(context.applicationContext, ZenQuoteDatabase::class.java, "zen-quotes.db").allowMainThreadQueries().build().also {
                INSTANCE = it
            }
        }
    }
}