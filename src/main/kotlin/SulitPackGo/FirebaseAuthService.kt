package SulitPackGo
import java.net.HttpURLConnection
import java.net.URL

object FirebaseAuthServices {

    private const val API_KEY = "AIzaSyDE5KdYiAutKGrNn23tq15_SN7UodEYVB8"
    private fun escapeJson(text: String): String {
        return text
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
    }
    fun register(email: String, password: String) {

        val url =
            "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=$API_KEY"

        val jsonBody = """
            {
                "email": "$email",
                "password": "$password",
                "returnSecureToken": true
            }
        """.trimIndent()

        try {

            val connection =
                URL(url).openConnection() as HttpURLConnection

            connection.requestMethod = "POST"

            connection.setRequestProperty(
                "Content-Type",
                "application/json"
            )

            connection.doOutput = true

            connection.outputStream.use { output ->
                output.write(jsonBody.toByteArray())
            }

            val responseCode = connection.responseCode

            if (responseCode == 200) {

                val response =
                    connection.inputStream
                        .bufferedReader()
                        .readText()

                println("Registration successful!")
                println(response)

            } else {

                val error =
                    connection.errorStream
                        ?.bufferedReader()
                        ?.readText()

                println("Registration failed!")
                println(error)
            }

            connection.disconnect()

        } catch (e: Exception) {

            println("Error: ${e.message}")
        }
    }


    fun login(email: String, password: String) {

        val url =
            "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=$API_KEY"

        val jsonBody = """
            {
                "email": "$email",
                "password": "$password",
                "returnSecureToken": true
            }
        """.trimIndent()

        try {

            val connection =
                URL(url).openConnection() as HttpURLConnection

            connection.requestMethod = "POST"

            connection.setRequestProperty(
                "Content-Type",
                "application/json"
            )

            connection.doOutput = true

            connection.outputStream.use { output ->
                output.write(jsonBody.toByteArray())
            }

            val responseCode = connection.responseCode

            if (responseCode == 200) {

                val response =
                    connection.inputStream
                        .bufferedReader()
                        .readText()

                println("Login successful!")

                Session.isLoggedIn = true
                Session.email = email

            } else {

                val error =
                    connection.errorStream
                        ?.bufferedReader()
                        ?.readText()

                println("Login failed!")
                println(error)
            }

            connection.disconnect()

        } catch (e: Exception) {

            println("Error: ${e.message}")
        }
    }
    fun resetPassword(email: String) {

        val url =
            "https://identitytoolkit.googleapis.com/v1/accounts:sendOobCode?key=$API_KEY"

        val safeEmail = escapeJson(email)

        val jsonBody = """
        {
            "requestType": "PASSWORD_RESET",
            "email": "$safeEmail"
        }
    """.trimIndent()

        try {

            val connection =
                URL(url).openConnection() as HttpURLConnection

            connection.requestMethod = "POST"
            connection.setRequestProperty(
                "Content-Type",
                "application/json"
            )

            connection.doOutput = true

            connection.outputStream.use { output ->
                output.write(jsonBody.toByteArray())
            }

            val responseCode = connection.responseCode

            if (responseCode == 200) {

                println("Password reset email sent.")
                println("Please check your email.")

            } else {

                val error =
                    connection.errorStream
                        ?.bufferedReader()
                        ?.readText()

                println("Failed to send password reset email.")
                println(error)
            }

            connection.disconnect()

        } catch (e: Exception) {

            println("Error: ${e.message}")
        }
    }
}