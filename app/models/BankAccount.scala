package models

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads, Writes}

case class BankAccount(var bankName: String, var iban: String, var bic: String) {

}

object BankAccount {
  implicit val bankAccountWrites: Writes[BankAccount] = (
    (JsPath \ "bankName").write[String] and
      (JsPath \ "iban").write[String] and
      (JsPath \ "bic").write[String]
    ) (unlift(BankAccount.unapply))

  implicit val bankAccountReads: Reads[BankAccount] = (
    (JsPath \ "bankName").read[String] and
      (JsPath \ "iban").read[String] and
      (JsPath \ "bic").read[String]
    ) (BankAccount.apply _)
}