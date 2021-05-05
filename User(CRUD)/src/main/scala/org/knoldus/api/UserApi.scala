package org.knoldus.api

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import org.knoldus.api.UserActor._
import org.knoldus.db.UserDatabase
import org.knoldus.jsonSupport.UserJsonProtocol
import org.knoldus.models.{User, UserType}
import scala.concurrent.Future
import scala.concurrent.duration._
object UserActor {

  case object GetAllUsers
  case class AddUser(user:User)
  case class GetUserById(id:Int)
  case class DeleteUser(id:Int)
  case class UpdateUser(id:Int,name:String,age:Int)
  case object OperationSuccess
}

class UserActor extends Actor with ActorLogging {

  override def receive: Receive = {
    case AddUser(user) =>
      log.info(s"To add user $user")
      UserDatabase.insert(user)
      sender()!OperationSuccess

    case GetUserById(id)=>
      log.info(s"Getting user by id $id")
      val data = UserDatabase.getUserById(id)
      sender() ! data

    case GetAllUsers =>
      log.info("Getting all users")
      sender()!UserDatabase.get

    case DeleteUser(id) =>
      log.info(s"To remove user by id $id")
      sender()!UserDatabase.delete(id)

    case UpdateUser(id,name,age)=>
      log.info(s"Update user by $id")
      sender()!UserDatabase.update(id ,name,age)
  }
}


  object UserApi extends App with UserJsonProtocol with SprayJsonSupport{
    implicit val system: ActorSystem = ActorSystem("System")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    import system.dispatcher

    val userDb = system.actorOf(Props[UserActor],"UserManagementSystem")
    val userList = List(
      User(1, "Meenakshi", 22, "Admin"),
      User(2, "Prabhat", 25, "Customer"),
      User(3, "Aditi", 18, "Customer"))
    userList.foreach{
      user => userDb!AddUser(user)
    }

    implicit val timeout = Timeout(2 seconds)
    val userManagementSystem =
      pathPrefix("api"/"user") {
        get {
          path("list") {
            val userlistfuture = (userDb ? GetAllUsers).mapTo[Future[Seq[User]]]
            complete(userlistfuture)
          }
        }~
          get{
            path( "getUser"/ IntNumber){ id =>
              val userlistfuture = (userDb ? GetUserById(id)).mapTo[Future[Seq[User]]]
              complete(userlistfuture)
            }
          }~
          post{
            entity(as[User]){ users =>
              complete((userDb ? AddUser(users)).map(_ => StatusCodes.OK))
            }
          }

        path("delete"/IntNumber){id =>
            delete{
              val deleteUser = (userDb ? DeleteUser(id)).map {
                case 1 => StatusCodes.OK
                case 0 => StatusCodes.NotFound
              }
              complete(deleteUser)
            }
          }~
        path("update"/IntNumber/Segment/IntNumber){(id:Int,name:String,age:Int) =>
          put{
            val updateUser = (userDb ? UpdateUser(id,name,age)).map{
              case 1 => StatusCodes.OK
              case 0 => StatusCodes.NotFound
            }
            complete(updateUser)
          }
        }
      }
        Http().bindAndHandle(userManagementSystem , "localhost" , 8080)
      }





