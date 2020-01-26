package ru.neoflex.cloudflow.training

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import cloudflow.akkastream.util.scaladsl.HttpServerLogic
import cloudflow.akkastream.{AkkaServerStreamlet, AkkaStreamletLogic}
import cloudflow.streamlets.StreamletShape
import cloudflow.streamlets.avro.AvroOutlet
import ru.neoflex.cloudflow.training.PaymentJsonProtocol._
import ru.neoflex.cloudflow.training.vtb.Payment

class PaymentHttpIngress extends AkkaServerStreamlet {
  val paymentOutlet = AvroOutlet[Payment]("ingress-payment-out", _.payerId)

  override def shape(): StreamletShape = StreamletShape.withOutlets(paymentOutlet)

  override protected def createLogic(): AkkaStreamletLogic = HttpServerLogic.default(this, paymentOutlet)

}
