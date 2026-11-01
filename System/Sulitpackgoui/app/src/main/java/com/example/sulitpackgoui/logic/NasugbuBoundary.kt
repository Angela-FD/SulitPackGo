package com.example.sulitpackgoui.logic

import android.content.Context
import com.example.sulitpackgoui.R
import org.json.JSONArray
import org.json.JSONObject

class NasugbuBoundary(
    private var context: Context
) {


    fun isInsideNasugbu(
        latitude: Double,
        longitude: Double
    ): Boolean {

        var geoJsonText =
            readBoundaryFile()


        var geoJson =
            JSONObject(
                geoJsonText
            )


        var features =
            geoJson.getJSONArray(
                "features"
            )


        var featureNumber =
            0


        while (
            featureNumber <
            features.length()
        ) {

            var feature =
                features.getJSONObject(
                    featureNumber
                )


            var geometry =
                feature.getJSONObject(
                    "geometry"
                )


            var geometryType =
                geometry.getString(
                    "type"
                )


            var coordinates =
                geometry.getJSONArray(
                    "coordinates"
                )


            if (
                geometryType ==
                "Polygon"
            ) {

                if (
                    checkPolygon(
                        latitude,
                        longitude,
                        coordinates
                    )
                ) {

                    return true
                }

            } else if (
                geometryType ==
                "MultiPolygon"
            ) {

                var polygonNumber =
                    0


                while (
                    polygonNumber <
                    coordinates.length()
                ) {

                    var polygon =
                        coordinates.getJSONArray(
                            polygonNumber
                        )


                    if (
                        checkPolygon(
                            latitude,
                            longitude,
                            polygon
                        )
                    ) {

                        return true
                    }


                    polygonNumber =
                        polygonNumber + 1
                }
            }


            featureNumber =
                featureNumber + 1
        }


        return false
    }


    private fun readBoundaryFile(): String {

        var inputStream =
            context
                .resources
                .openRawResource(
                    R.raw.nasugbu_boundary
                )


        var reader =
            inputStream
                .bufferedReader()


        var text =
            reader.readText()


        reader.close()


        return text
    }


    private fun checkPolygon(
        latitude: Double,
        longitude: Double,
        polygon: JSONArray
    ): Boolean {

        if (
            polygon.length() == 0
        ) {

            return false
        }


        var outerRing =
            polygon.getJSONArray(
                0
            )


        var insideOuter =
            isPointInsideRing(
                latitude,
                longitude,
                outerRing
            )


        if (
            insideOuter == false
        ) {

            return false
        }


        var holeNumber =
            1


        while (
            holeNumber <
            polygon.length()
        ) {

            var hole =
                polygon.getJSONArray(
                    holeNumber
                )


            var insideHole =
                isPointInsideRing(
                    latitude,
                    longitude,
                    hole
                )


            if (insideHole) {

                return false
            }


            holeNumber =
                holeNumber + 1
        }


        return true
    }


    private fun isPointInsideRing(
        latitude: Double,
        longitude: Double,
        ring: JSONArray
    ): Boolean {

        var inside =
            false


        var previousPointNumber =
            ring.length() - 1


        var pointNumber =
            0


        while (
            pointNumber <
            ring.length()
        ) {

            var currentPoint =
                ring.getJSONArray(
                    pointNumber
                )


            var previousPoint =
                ring.getJSONArray(
                    previousPointNumber
                )


            var currentLongitude =
                currentPoint.getDouble(
                    0
                )


            var currentLatitude =
                currentPoint.getDouble(
                    1
                )


            var previousLongitude =
                previousPoint.getDouble(
                    0
                )


            var previousLatitude =
                previousPoint.getDouble(
                    1
                )


            var crossesLatitude =
                false


            if (
                currentLatitude >
                latitude
            ) {

                if (
                    previousLatitude <=
                    latitude
                ) {

                    crossesLatitude =
                        true
                }

            } else {

                if (
                    previousLatitude >
                    latitude
                ) {

                    crossesLatitude =
                        true
                }
            }


            if (crossesLatitude) {

                var crossingLongitude =
                    (
                            previousLongitude -
                                    currentLongitude
                            ) *
                            (
                                    latitude -
                                            currentLatitude
                                    ) /
                            (
                                    previousLatitude -
                                            currentLatitude
                                    ) +
                            currentLongitude


                if (
                    longitude <
                    crossingLongitude
                ) {

                    if (inside) {

                        inside =
                            false

                    } else {

                        inside =
                            true
                    }
                }
            }


            previousPointNumber =
                pointNumber


            pointNumber =
                pointNumber + 1
        }


        return inside
    }
}