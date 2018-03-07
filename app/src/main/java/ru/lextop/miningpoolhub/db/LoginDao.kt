package ru.lextop.miningpoolhub.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import ru.lextop.miningpoolhub.vo.Login

@Dao
interface LoginDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLogin(login: Login)

    @Query("select * from login order by name asc")
    fun loadLogins(): LiveData<List<Login>>

    @Delete
    fun deleteLogin(login: Login)
}
