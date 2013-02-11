package knoldus

import com.typesafe.config.ConfigFactory
import akka.actor.ActorSystem
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props

object LocalApplication extends App {

  val configString = """
           akka {actor {
                provider = "akka.remote.RemoteActorRefProvider"
              }
      remote {netty {hostname = "127.0.0.1"}}}
          akka {remote.netty.port = 2557 }
        """
  val customConf = ConfigFactory.parseString(configString)
  val system = ActorSystem("Remote", ConfigFactory.load(customConf))
  val remoteActorReference = system.actorFor("akka://RemoteApplication@" + "127.0.0.1" + ":" + 2559 + "/user/remote")
  val local = system.actorOf(Props(new LocalActor(remoteActorReference)))

  while(true){
  val input = Console.in.readLine()
  local ! Send(input)
  }
}

class LocalActor(remote: ActorRef) extends Actor {
  def receive = {
    case msg: Send =>
      //      println("Sending message to " + remote)
      remote ! msg
    case msg: Get =>
      println(msg.message)
  }

}