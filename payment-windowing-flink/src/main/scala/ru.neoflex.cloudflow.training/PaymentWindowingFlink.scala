package ru.neoflex.cloudflow.training

import org.apache.flink.streaming.api.scala._
import cloudflow.streamlets.StreamletShape
import cloudflow.streamlets.avro._
import cloudflow.flink._
import org.apache.flink.streaming.api.windowing.time.Time
import ru.neoflex.cloudflow.training.vtb.{EnrichedPayment, Payment}

class PaymentWindowingFlink extends FlinkStreamlet {

  @transient val in = AvroInlet[Payment]("in")
  @transient val out = AvroOutlet[EnrichedPayment]("out", _.payerId.toString)

  @transient val shape = StreamletShape.withInlets(in).withOutlets(out)

  override def createLogic() = new FlinkStreamletLogic {
    override def buildExecutionGraph = {
      val stream: DataStream[EnrichedPayment] =
        readStream(in)
          .keyBy("payerId")
          .timeWindow(Time.seconds(2), Time.seconds(5))
          .sum("amount")
          .filter(p => p.amount > 50000)
          .map(p => EnrichedPayment(p.amount, p.payerId))

      writeStream(out, stream)
    }
  }
}