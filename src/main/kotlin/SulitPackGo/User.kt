package SulitPackGo

class User {
    var name: String = ""
    var email: String = ""

    fun register() {
        while (true) {
            print("Enter your name: ")
            val userName = readln()
            if (userName.isBlank()) {
                println("Please enter a valid name.")
            } else {
                this.name = userName
                break
            }
        }
        while (true) {
            print("Enter your email: ")
            val userEmail = readln()

            if (userEmail.isBlank()) {
                println("Email cannot be empty.")
            } else if (!userEmail.contains("@")) {
                println("Please enter a valid email.")
            } else {
                this.email = userEmail
                break
            }
        }

        var password: String
        while (true) {
            print("Enter your password: ")
            password = readln()
            print("Re-enter your password: ")
            val confirmPassword = readln()
            if (password != confirmPassword) {
                println("Passwords do not match.")
            } else if (password.length != 8) {
                println("Password should be exactly 16 characters.")
            } else {
                break
            }
        FirebaseAuthServices.register(
            email,
            password
        )
    }
}
    fun logIn() {
        print("Email: ")
        val userEmail = readln()
        print("Password: ")
        val userPassword = readln()

        FirebaseAuthServices.login(
            userEmail,
            userPassword
        )

        if (Session.isLoggedIn) {
            this.email = userEmail
            this.name = Session.name
        }
    }

    fun logOut() {
        if (Session.isLoggedIn) {
            Session.logout()
        } else {
            println("No user is currently logged in.")
        }
    }
}