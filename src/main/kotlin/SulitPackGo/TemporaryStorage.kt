package SulitPackGo
object TemporaryStorage {

    val users = mutableListOf<User>()

    fun addUser(user: User) {
        users.add(user)
    }
}