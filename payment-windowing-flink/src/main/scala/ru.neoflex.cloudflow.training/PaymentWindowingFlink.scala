package ru.neoflex.cloudflow.training

import ru.neoflex.cloudflow.training.vtb.EnrichedPayment
import org.apache.flink.streaming.api.TimeCharacteristic
//import org.apache.flink.streaming.api.windowing.time.Time
import ru.neoflex.cloudflow.training.vtb.Payment
import cloudflow.flink.{ FlinkStreamlet, FlinkStreamletLogic }
import cloudflow.streamlets.avro.{ AvroInlet, AvroOutlet }
import org.apache.flink.streaming.api.scala._

import cloudflow.streamlets.StreamletShape
import cloudflow.flink._

class PaymentWindowingFlink extends FlinkStreamlet {
  @transient val in = AvroInlet[Payment]("in")

  @transient val out = AvroOutlet[EnrichedPayment]("out", _.payerId)

  @transient val shape = StreamletShape(in).withOutlets(out)


  override def createLogic(): FlinkStreamletLogic = new FlinkStreamletLogic() {

    override def buildExecutionGraph(): Unit = {
      // setup the streaming execution environment
     val env = StreamExecutionEnvironment.getExecutionEnvironment

      // set checkpoint to every 10 seconds
//      env.getCheckpointConfig.setCheckpointInterval(10 * 1000)

      // use event time for the application
      env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime)
      // configure watermark interval
//      env.getConfig.setAutoWatermarkInterval(30 * 1000) // 30 sec

     val enrichedStream = readStream(in)
        .map(p => (p.payerId, p.amount))
       /* .keyBy(_._1)
        .timeWindowAll(Time.seconds(10), Time.seconds(5)) // tumbling window
         .max(2)*/
//        .sum(2)
        .map { p =>
          log.debug(p._1)
          EnrichedPayment(p._2, p._1)
        }

      writeStream(out, enrichedStream)

      env.execute()
    }
  }
}
