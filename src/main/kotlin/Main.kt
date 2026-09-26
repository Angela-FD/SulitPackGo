import SulitPackGo.User

fun main() {
    var user = User()
    user.register()
    println("\n--- REGISTERED USER ---")
    println("Name: ${user.name}")
    println("Password: ${user.email}")

}