package knoldus

import com.typesafe.config.ConfigFactory
import akka.actor._
import java.util.Scanner

object RemoteApplication extends App {
  var originalSender: ActorRef = _

  val configString = """akka {actor {
                provider = "akka.remote.RemoteActorRefProvider" }
             remote {netty { hostname = "127.0.0.1"}}}
              akka {
             remote.netty.port = 2559}"""
  val customConf = ConfigFactory.parseString(configString)
  val remoteSystem = ActorSystem("RemoteApplication", ConfigFactory.load(customConf))
  val remoteActorRef = remoteSystem.actorOf(Props[RemoteActor], "remote")
  val scanner = new Scanner(System.in)
  println("Waiting for message from Local")

  while (true) {

    val input = scanner.nextLine()
    originalSender ! Get(input)
  }

}

class RemoteActor extends Actor {

  def receive = {
    case msg: Send =>
      println("------------------------------------------------------")
      println("Message Received from Local:" + msg.message)
      RemoteApplication.originalSender = sender
      println("------------------------------------------------------")
  }

}

case class Send(message: String)
case class Get(message: String)