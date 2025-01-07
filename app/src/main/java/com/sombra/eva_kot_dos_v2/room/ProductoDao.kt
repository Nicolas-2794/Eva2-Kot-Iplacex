package com.sombra.eva_kot_dos_v2.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ProductoDao {

    @Query("select count(*) from producto")
    fun count():Int

    @Query("select * from producto")
    fun getAll():List<Producto>

    @Query("select * from producto where id = :id")
    fun findById(id:Int):Producto

    @Insert
    fun insert(producto:Producto):Long

    @Insert
    fun insertAll(vararg productos:Producto)

    @Update
    fun update(vararg productos:Producto)

    @Delete
    fun delete(producto:Producto)

}