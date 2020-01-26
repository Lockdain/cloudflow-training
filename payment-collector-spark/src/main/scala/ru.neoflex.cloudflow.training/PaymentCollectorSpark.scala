package ru.neoflex.cloudflow.training

import cloudflow.streamlets.StreamletShape
import cloudflow.streamlets.avro._
import cloudflow.spark.{SparkStreamlet, SparkStreamletLogic, StreamletQueryExecution}
import cloudflow.spark.sql.SQLImplicits._
import org.apache.spark.sql.streaming.OutputMode
import ru.neoflex.cloudflow.training.vtb.EnrichedPayment

class PaymentCollectorSpark extends SparkStreamlet {
  val in = AvroInlet[EnrichedPayment]("in")

  override def shape(): StreamletShape = StreamletShape.withInlets(in)

  override protected def createLogic(): SparkStreamletLogic = new SparkStreamletLogic() {
    override def buildStreamingQueries: StreamletQueryExecution = {
      readStream(in).writeStream
        .format("console")
        .outputMode(OutputMode.Append())
        .start()
        .toQueryExecution
    }

  }
}
