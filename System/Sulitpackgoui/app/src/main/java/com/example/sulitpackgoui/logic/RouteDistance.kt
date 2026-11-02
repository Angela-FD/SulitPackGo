package com.example.sulitpackgoui.logic

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

import org.json.JSONObject

import java.io.IOException


class RouteDistance {


    private var client =
        OkHttpClient()


    fun getRoadDistance(
        deliveryLocation: DeliveryLocation,
        onResult: (Boolean, Double, String) -> Unit
    ) {


        if (
            locationIsMissing(
                deliveryLocation
            )
        ) {

            onResult(
                false,
                0.0,
                "Pickup and drop-off locations are required."
            )

            return
        }


        var url =
            createRouteUrl(
                deliveryLocation
            )


        var request =
            Request
                .Builder()
                .url(url)
                .get()
                .build()


        client
            .newCall(request)
            .enqueue(

                object : Callback {


                    override fun onFailure(
                        call: Call,
                        error: IOException
                    ) {

                        onResult(
                            false,
                            0.0,
                            "Unable to calculate road distance."
                        )
                    }


                    override fun onResponse(
                        call: Call,
                        response: Response
                    ) {

                        response.use {


                            if (
                                response.isSuccessful == false
                            ) {

                                onResult(
                                    false,
                                    0.0,
                                    "Routing service error."
                                )

                                return
                            }


                            var responseText =
                                response
                                    .body
                                    .string()


                            readRoadDistance(
                                responseText,
                                onResult
                            )
                        }
                    }
                }
            )
    }


    private fun locationIsMissing(
        deliveryLocation: DeliveryLocation
    ): Boolean {


        if (
            deliveryLocation.pickUpLatitude ==
            0.0
        ) {

            return true
        }


        if (
            deliveryLocation.pickUpLongitude ==
            0.0
        ) {

            return true
        }


        if (
            deliveryLocation.dropLatitude ==
            0.0
        ) {

            return true
        }


        if (
            deliveryLocation.dropLongitude ==
            0.0
        ) {

            return true
        }


        return false
    }


    private fun createRouteUrl(
        deliveryLocation: DeliveryLocation
    ): String {


        var pickupLongitude =
            deliveryLocation
                .pickUpLongitude
                .toString()


        var pickupLatitude =
            deliveryLocation
                .pickUpLatitude
                .toString()


        var dropLongitude =
            deliveryLocation
                .dropLongitude
                .toString()


        var dropLatitude =
            deliveryLocation
                .dropLatitude
                .toString()


        var url =
            "https://router.project-osrm.org/" +
                    "route/v1/driving/" +
                    pickupLongitude +
                    "," +
                    pickupLatitude +
                    ";" +
                    dropLongitude +
                    "," +
                    dropLatitude +
                    "?overview=false"


        return url
    }


    private fun readRoadDistance(
        responseText: String,
        onResult: (Boolean, Double, String) -> Unit
    ) {


        try {


            var json =
                JSONObject(
                    responseText
                )


            var code =
                json.getString(
                    "code"
                )


            if (code != "Ok") {

                onResult(
                    false,
                    0.0,
                    "No road route found."
                )

                return
            }


            var routes =
                json.getJSONArray(
                    "routes"
                )


            if (
                routes.length() == 0
            ) {

                onResult(
                    false,
                    0.0,
                    "No road route found."
                )

                return
            }


            var route =
                routes.getJSONObject(
                    0
                )


            var distanceMeters =
                route.getDouble(
                    "distance"
                )


            var distanceKm =
                distanceMeters /
                        1000.0


            onResult(
                true,
                distanceKm,
                "Road distance calculated."
            )


        } catch (error: Exception) {


            onResult(
                false,
                0.0,
                "Unable to read routing information."
            )
        }
    }
}