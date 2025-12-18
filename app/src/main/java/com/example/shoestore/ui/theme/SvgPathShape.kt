package com.example.shoestore.ui.theme

import android.graphics.Matrix
import android.graphics.RectF
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.graphics.PathParser

class SvgPathShape(private val pathData: String) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        // 1. Парсим строку в Android Path
        // Используем androidx PathParser для надежности
        val path = try {
            PathParser.createPathFromPathData(pathData)
        } catch (e: Exception) {
            android.graphics.Path() // Пустой путь в случае ошибки
        }

        // 2. Вычисляем оригинальные размеры пути (из SVG)
        val bounds = RectF()
        path.computeBounds(bounds, true)

        // 3. Создаем матрицу масштабирования
        // Растягиваем путь от его оригинального размера (375x106) до размера контейнера (size.width x size.height)
        val matrix = Matrix()
        var scaleX = 1f
        var scaleY = 1f

        if (bounds.width() > 0 && bounds.height() > 0) {
            scaleX = size.width / bounds.width()
            scaleY = size.height / bounds.height()
        }
        matrix.setScale(scaleX, scaleY)

        // 4. Применяем трансформацию
        path.transform(matrix)

        // 5. Возвращаем как Compose Outline
        return Outline.Generic(path.asComposePath())
    }
}