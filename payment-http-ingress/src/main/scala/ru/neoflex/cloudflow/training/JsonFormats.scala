package ru.neoflex.cloudflow.training


import ru.neoflex.cloudflow.training.vtb.Payment
import spray.json.{DefaultJsonProtocol, DeserializationException, JsNumber, JsObject, JsString, JsValue, RootJsonFormat}

object PaymentJsonProtocol extends DefaultJsonProtocol {

  implicit object PaymentJsonFormat extends RootJsonFormat[Payment] {
    def write(p: Payment) = JsObject(
      "amount"  -> JsNumber(p.amount),
      "payerId"  -> JsNumber(p.payerId),
      "countryTo" -> JsString(p.countryTo)
    )

    def read(value: JsValue) = {
      value.asJsObject.getFields("amount", "payerId", "countryTo") match {
        case Seq(JsNumber(amount), JsString(payerId), JsString(countryTo)) ⇒
          Payment(
            amount.intValue(),
            payerId,
            countryTo
          )
        case _ ⇒ throw new DeserializationException("Payment expected")
      }
    }
  }

}
