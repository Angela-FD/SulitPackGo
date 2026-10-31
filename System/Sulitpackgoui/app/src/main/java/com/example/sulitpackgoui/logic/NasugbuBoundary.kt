package com.example.sulitpackgoui.logic

import android.content.Context
import com.example.sulitpackgoui.R
import org.json.JSONObject

class NasugbuBoundary(
    private var context: Context
) {

    fun isInsideNasugbu(
        latitude: Double,
        longitude: Double
    ): Boolean {

        var geoJsonText =
            context.resources
                .openRawResource(R.raw.nasugbu_boundary)
                .bufferedReader()
                .use { it.readText() }

        var geoJson =
            JSONObject(geoJsonText)

        var features =
            geoJson.getJSONArray("features")

        for (i in 0 until features.length()) {

            var feature =
                features.getJSONObject(i)

            var geometry =
                feature.getJSONObject("geometry")

            var geometryType =
                geometry.getString("type")

            var coordinates =
                geometry.getJSONArray("coordinates")

            if (geometryType == "Polygon") {

                if (
                    checkPolygon(
                        latitude,
                        longitude,
                        coordinates
                    )
                ) {
                    return true
                }

            } else if (geometryType == "MultiPolygon") {

                for (j in 0 until coordinates.length()) {

                    var polygon =
                        coordinates.getJSONArray(j)

                    if (
                        checkPolygon(
                            latitude,
                            longitude,
                            polygon
                        )
                    ) {
                        return true
                    }
                }
            }
        }

        return false
    }

    private fun checkPolygon(
        latitude: Double,
        longitude: Double,
        polygon: org.json.JSONArray
    ): Boolean {

        if (polygon.length() == 0) {
            return false
        }

        // First ring is the outer boundary.
        var outerRing =
            polygon.getJSONArray(0)

        var insideOuter =
            isPointInsideRing(
                latitude,
                longitude,
                outerRing
            )

        if (!insideOuter) {
            return false
        }

        // Remaining rings are holes inside the polygon.
        for (i in 1 until polygon.length()) {

            var hole =
                polygon.getJSONArray(i)

            if (
                isPointInsideRing(
                    latitude,
                    longitude,
                    hole
                )
            ) {
                return false
            }
        }

        return true
    }

    private fun isPointInsideRing(
        latitude: Double,
        longitude: Double,
        ring: org.json.JSONArray
    ): Boolean {

        var inside = false
        var j = ring.length() - 1

        for (i in 0 until ring.length()) {

            var pointI =
                ring.getJSONArray(i)

            var pointJ =
                ring.getJSONArray(j)

            // GeoJSON coordinates are:
            // [longitude, latitude]

            var longitudeI =
                pointI.getDouble(0)

            var latitudeI =
                pointI.getDouble(1)

            var longitudeJ =
                pointJ.getDouble(0)

            var latitudeJ =
                pointJ.getDouble(1)

            var intersects =
                ((latitudeI > latitude) !=
                        (latitudeJ > latitude)) &&
                        (
                                longitude <
                                        (longitudeJ - longitudeI) *
                                        (latitude - latitudeI) /
                                        (latitudeJ - latitudeI) +
                                        longitudeI
                                )

            if (intersects) {
                inside = !inside
            }

            j = i
        }

        return inside
    }
}