package ru.neoflex.cloudflow.training

import org.apache.flink.streaming.api.scala._
import cloudflow.streamlets.StreamletShape
import cloudflow.streamlets.avro._
import cloudflow.flink._
import org.apache.flink.streaming.api.windowing.time.Time
import ru.neoflex.cloudflow.training.vtb.{EnrichedPayment, Payment}

class PaymentWindowingFlink extends FlinkStreamlet {

  // Step 1: Define inlets and outlets. Note for the outlet you need to specify
  //         the partitioner function explicitly : here we are using the
  //         rideId as the partitioner
  @transient val in = AvroInlet[Payment]("in")
  @transient val out = AvroOutlet[EnrichedPayment]("out", _.payerId.toString)

  // Step 2: Define the shape of the streamlet. In this example the streamlet
  //         has 2 inlets and 1 outlet
  @transient val shape = StreamletShape.withInlets(in).withOutlets(out)

  // Step 3: Provide custom implementation of `FlinkStreamletLogic` that defines
  //         the behavior of the streamlet
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