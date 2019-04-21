package models

import java.util.Locale.IsoCountryCode

case class Address(countryCode: IsoCountryCode, state: String, city: String, street: String, number: Number) {


}
