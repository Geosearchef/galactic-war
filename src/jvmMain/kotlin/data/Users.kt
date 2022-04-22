package data

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Users : IntIdTable("users") {
//    val id = integer("id").autoIncrement()
    val fafId = integer("faf_id").uniqueIndex()
    val username = varchar("username", 100)
}

class User(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, User>(Users)

    var fafId by Users.fafId
    var username by Users.username
}