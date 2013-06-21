package com.deploymentzone.library.domain.serialization

import org.json4s.CustomSerializer
import com.deploymentzone.library.domain.{Address, Account}
import org.json4s.JsonAST.{JArray, JObject, JString}

class AccountSerializer extends CustomSerializer[Account](format => ({
  case JObject(
  ("id", JString(id)) ::
    ("pinCode", JString(pin)) ::
    ("primaryEmail", JString(email)) ::
    ("fallbackEmails", JArray(fallbackEmails)) ::
    ("address", JObject(
      ("street", JString(street)) ::
      ("city", JString(city)) ::
      ("country", JString(country)) :: Nil)) :: Nil) =>
  Account(id, pin, email, fallbackEmails.map(_.toString), Address(street, city, country))
}, {
  case account: Account =>
    JObject.apply(
      "id" -> JString(account.id),
      "pinCode" -> JString(account.pinCode),
      "primaryEmail" -> JString(account.primaryEmail),
      "fallbackEmails" -> JArray(account.emails.map(JString(_)).toList),
      "address" -> JObject(
        "street" -> JString(account.address.street),
        "city" -> JString(account.address.city),
        "country" -> JString(account.address.country)))
}))
