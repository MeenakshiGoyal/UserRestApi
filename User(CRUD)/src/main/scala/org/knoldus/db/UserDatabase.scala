package org.knoldus.db

import org.knoldus.jsonSupport.UserJsonProtocol
import org.knoldus.models.User
import org.knoldus.models.UserType.UserType
import slick.dbio.DBIOAction
import slick.jdbc.MySQLProfile.api._
import slick.lifted.ProvenShape

import java.util.UUID
import scala.concurrent.Future

class UserSchema(tag: Tag)extends Table[User](tag,"User") {
 def * : ProvenShape[User] = (userId,name,age,userRole)<>(User.tupled,User.unapply)
  def userId:Rep[Int] = column[Int]("ID" , O.PrimaryKey , O.AutoInc)
  def name:Rep[String] = column[String]("NAME")
  def age:Rep[Int] = column[Int]("AGE")
  def userRole:Rep[String] = column[String]("USERROLE")

 }

object UserDatabase extends TableQuery(new UserSchema(_)){
 val db = Database.forConfig("mydb")
 val users = TableQuery[UserSchema]

 def init() = db.run(DBIOAction.seq(users.schema.create))
 def drop() = db.run(DBIOAction.seq(users.schema.drop))

  def createTable:Future[Unit] = {
   val query = TableQuery[UserSchema].schema.createIfNotExists
   db.run(query)
  }
 def insert(user:User):Future[Int]={
  val query   = this += user
  db.run(query)
 }
 def delete(userId: Int):Future[Int]={
  val query = this.filter(_.userId === userId).delete
  db.run(query)
 }
 def get:Future[Seq[User]] ={
  db.run(this.result)
 }
 def getUserById(userId:Int):Future[Seq[User]] ={
  val query = this.filter(_.userId === userId).result
  db.run(query)
 }
 def update(userId:Int, name:String,age:Int):Future[Int]={
  val query = this.filter(_.userId === userId).map(user =>
   (user.name,user.age)).update(name,age)
  db.run(query)
 }
}
