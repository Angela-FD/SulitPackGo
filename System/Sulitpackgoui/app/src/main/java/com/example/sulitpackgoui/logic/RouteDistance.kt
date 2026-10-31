package com.example.sulitpackgoui.logic

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class RouteDistance {

    private var client = OkHttpClient()

    fun getRoadDistance(
        deliveryLocation: DeliveryLocation,
        onResult: (Boolean, Double, String) -> Unit
    ) {

        var pickupLatitude =
            deliveryLocation.pickUpLatitude

        var pickupLongitude =
            deliveryLocation.pickUpLongitude

        var dropLatitude =
            deliveryLocation.dropLatitude

        var dropLongitude =
            deliveryLocation.dropLongitude

        if (
            pickupLatitude == 0.0 ||
            pickupLongitude == 0.0 ||
            dropLatitude == 0.0 ||
            dropLongitude == 0.0
        ) {

            onResult(
                false,
                0.0,
                "Pickup and drop-off locations are required."
            )

            return
        }

        // OSRM requires:
        // longitude,latitude
        var url =
            "https://router.project-osrm.org/route/v1/driving/" +
                    "$pickupLongitude,$pickupLatitude;" +
                    "$dropLongitude,$dropLatitude" +
                    "?overview=false"

        var request =
            Request.Builder()
                .url(url)
                .get()
                .build()

        client.newCall(request)
            .enqueue(

                object : Callback {

                    override fun onFailure(
                        call: Call,
                        e: IOException
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

                            if (!response.isSuccessful) {

                                onResult(
                                    false,
                                    0.0,
                                    "Routing service error."
                                )

                                return
                            }

                            var responseText =
                                response.body.string()

                            try {

                                var json =
                                    JSONObject(responseText)

                                if (
                                    json.getString("code") !=
                                    "Ok"
                                ) {

                                    onResult(
                                        false,
                                        0.0,
                                        "No road route found."
                                    )

                                    return
                                }

                                var routes =
                                    json.getJSONArray("routes")

                                if (routes.length() == 0) {

                                    onResult(
                                        false,
                                        0.0,
                                        "No road route found."
                                    )

                                    return
                                }

                                var route =
                                    routes.getJSONObject(0)

                                // OSRM returns distance in meters
                                var distanceMeters =
                                    route.getDouble("distance")

                                // Convert meters to kilometers
                                var distanceKm =
                                    distanceMeters / 1000.0

                                onResult(
                                    true,
                                    distanceKm,
                                    "Road distance calculated."
                                )

                            } catch (e: Exception) {

                                onResult(
                                    false,
                                    0.0,
                                    "Unable to read routing information."
                                )
                            }
                        }
                    }
                }
            )
    }
}