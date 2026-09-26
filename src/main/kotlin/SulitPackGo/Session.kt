package SulitPackGo

object Session {

    var isLoggedIn = false
    var name = ""
    var email = ""
    var idToken = ""

    fun logout() {
        isLoggedIn = false
        name = ""
        email = ""
        idToken = ""

        println("Logged out successfully.")
    }
}