package ru.neoflex.cloudflow.training

import akka.stream.scaladsl.{Flow, Sink}
import cloudflow.streamlets._
import cloudflow.streamlets.avro._
import cloudflow.akkastream._
import cloudflow.akkastream.scaladsl._
import ru.neoflex.cloudflow.training.vtb.Payment

class PaymentInvalidLoggerEgress extends AkkaStreamlet{
  val in = AvroInlet[Payment]("in")

  override def shape(): StreamletShape = StreamletShape(in)

  override protected def createLogic(): AkkaStreamletLogic = new RunnableGraphStreamletLogic() {
    def format(payment: Payment) =
      s"\nInvalid payment found: [${payment.payerId};${payment.amount};${payment.countryTo}]"

    def logging =
      Flow[Payment]
      .map { payment => system.log.info(format(payment)) }

    override def runnableGraph =
      plainSource(in).via(logging).to(Sink.ignore)
  }
}
