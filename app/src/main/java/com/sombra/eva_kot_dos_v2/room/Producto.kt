package com.sombra.eva_kot_dos_v2.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Producto(

    @PrimaryKey(autoGenerate = true) val id:Int = 0,
    var nombre:String,
    var comprado:Boolean
)