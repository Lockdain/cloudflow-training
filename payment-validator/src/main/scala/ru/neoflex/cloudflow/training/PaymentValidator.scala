package ru.neoflex.cloudflow.training

import cloudflow.akkastream.scaladsl.FlowWithOffsetContext
import cloudflow.akkastream.util.scaladsl.SplitterLogic
import cloudflow.akkastream.{AkkaStreamlet, AkkaStreamletLogic}
import cloudflow.streamlets.{StreamletShape}
import cloudflow.streamlets.avro.{AvroInlet, AvroOutlet}
import ru.neoflex.cloudflow.training.vtb.Payment

class PaymentValidator extends AkkaStreamlet{
  val in = AvroInlet[Payment]("in")
  val invalid = AvroOutlet[Payment]("invalid", _.payerId)
  val valid = AvroOutlet[Payment]("valid", _.payerId)

  val shape = StreamletShape(in).withOutlets(invalid, valid)

  override def createLogic(): AkkaStreamletLogic = new SplitterLogic(in, invalid, valid) {
    override def flow: FlowWithOffsetContext[Payment, Either[Payment, Payment]] =
      flowWithOffsetContext()
      .map { payment =>
        if (payment.amount < 0 || payment.payerId.isEmpty || "Tunisia".equals(payment.countryTo))
          Left(payment)
        else Right(payment)
      }
  }
}
