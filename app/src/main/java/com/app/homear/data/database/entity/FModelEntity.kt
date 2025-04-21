package com.app.homear.data.database.entity

import android.health.connect.datatypes.units.Length
import android.icu.text.ListFormatter.Width
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.app.homear.domain.model.FModelModel
import java.io.File
import com.app.homear.domain.model.Superficie

@Entity(tableName = "fmodel")
data class FModelEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "material") val material: String,
    @ColumnInfo(name = "keywords") val keywords: String,
    @ColumnInfo(name = "modelPath") val modelPath: String,
    @ColumnInfo(name = "imagePath") val imagePath: String,
    @ColumnInfo(name = "height") val height: Float,
    @ColumnInfo(name = "width") val width: Float,
    @ColumnInfo(name = "length") val length: Float,
    @ColumnInfo(name = "superficie") val superficie: Superficie
)

fun FModelEntity.toFModelModel(): FModelModel{
    return FModelModel(
        name = this.name,
        description = this.description,
        material = HashSet(this.material.split(",")),
        keywords = HashSet(this.keywords.split(",")),
        modelFile = File(this.modelPath),
        imageFile = File(this.imagePath),
        height = this.height,
        width = this.width,
        length = this.length,
        superficie = this.superficie
    )
}
