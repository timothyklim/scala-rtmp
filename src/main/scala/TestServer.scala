import akka.actor._
import akka.util.{ ByteString, ByteStringBuilder }
import java.net.InetSocketAddress

object TestServer extends App {
  class RTMPServer(port: Int) extends Actor {
    implicit val byteOrder = java.nio.ByteOrder.BIG_ENDIAN

    val state = IO.IterateeRef.Map.async[IO.Handle]()(context.dispatcher)

    override def preStart {
      IOManager(context.system) listen new InetSocketAddress(port)
    }

    def receive = {

      case IO.NewClient(server) ⇒
        val socket = server.accept()
        state(socket) flatMap (_ ⇒ processRequest(socket))

      case IO.Read(socket, bytes) ⇒
        state(socket)(IO Chunk bytes)

      case IO.Closed(socket, cause) ⇒
        state(socket)(IO EOF)
        state -= socket

    }

    def ascii(bytes: ByteString): String = {
      val result = bytes.decodeString("US-ASCII").trim

      println("result: %s" format result)

      result
    }

    def parseVersion(bytes: ByteString): Byte = {
      val result = bytes.head

      println("version: %s" format result)

      result
    }

    def parseTime(bytes: ByteString): Int = {
      val result = (0 to 3).foldLeft(0) {
        (result, position) => result | (bytes(position) & 0xFF) << (24 - position * 8 % 32)
      }

      println("time: %s" format result)

      result
    }

    def processRequest(socket: IO.SocketHandle): IO.Iteratee[Unit] = {
      IO repeat {
        for {
          versionBytes <- IO take 1
          version = parseVersion(versionBytes)

          timeBytes <- IO take 4
          time = parseTime(timeBytes)

          zeroBytes <- IO take 4
          randomBytes <- IO take 1528
        } yield {
          if (0x3 == version) {
//            ascii(bytes)

            for (time <- timeBytes) {
              println("%s => %X" format(time, time))
            }
          }
        }
      }
    }
  }

  val system = ActorSystem()
  val server = system.actorOf(Props(new RTMPServer(1935)))
}
