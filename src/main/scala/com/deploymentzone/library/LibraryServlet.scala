package com.deploymentzone.library

import org.json4s.JsonAST._
import org.json4s.JsonDSL._
import com.deploymentzone.library.domain.{Address, AccountRepository}
import org.scalatra.Ok
import scala.collection.immutable.SortedSet

class LibraryServlet extends LibraryStack {

  post("/api/v1/accounts") {
    parsedBody match {
      case JObject(
      ("primaryEmail", JString(email)) ::
        ("pinCode", JString(pin)) ::
        ("address", JObject(
        ("street", JString(street)) ::
          ("city", JString(city)) ::
          ("country", JString(country)) :: Nil)) :: Nil) =>

        val account = AccountRepository.create(pin, email, Address(street, city, country))

        JString(account.id)

      case _ => halt(500)
    }
  }

  get("/api/v1/initialize") {
    AccountRepository.create("1234", "charles.feduke@gmail.com", new Address("15505 Spotswood", "Fredericksburg", "US"))
    Ok
  }

  get("/api/v1/accounts") {
    AccountRepository.accounts.map {
      account => JString(account.id)
    }
  }

  get("/api/v1/accounts/:id") {
    AccountRepository.find(params("id")) match {

      case Some(account) =>

        ("id" -> account.id) ~
          ("primaryEmail" -> account.primaryEmail) ~
          ("fallbackEmails" -> account.emails.map(_.toString)) ~
          ("address" ->
            ("street" -> account.address.street) ~
              ("city" -> account.address.city) ~
              ("country" -> account.address.country))

      case _ => halt(404)
    }
  }

  delete("/api/v1/accounts/:id") {
    AccountRepository.remove(params("id"))
  }

  put("/api/v1/accounts/:id/address") {
    val account = AccountRepository.find(params("id")).getOrElse(halt(404))

    parsedBody match {
      case JObject(
      ("street", JString(state)) ::
        ("city", JString(city)) ::
        ("country", JString(country)) :: Nil) =>

        val updated = account.copy(address = Address(state, city, country))
        AccountRepository.update(updated)

      case _ => halt(500, "")
    }
  }

  post("/api/v1/accounts/:id/emails") {
    parsedBody match {
      case JString(email) =>
        val account = AccountRepository.find(params("id")).getOrElse(halt(404))

        val updatedAccount = account.copy(emails = account.emails :+ email)
        AccountRepository.update(updatedAccount)

      case _ => halt(404)
    }
  }

  delete("/api/v1/accounts/:id/emails/:index") {
    (for {
      id <- params.get("id")
      idx <- params.getAs[Int]("index")
      account <- AccountRepository.find(id)
      email <- account.emails.toSeq.lift(idx)
    } yield {
      val updatedAccount = account.copy(emails = account.emails diff (email :: Nil))
      AccountRepository.update(updatedAccount)
    }) getOrElse halt(404)
  }

  put("/api/v1/accounts/:id/emails/:index") {
    parsedBody match {
      case JString(email) =>

        (for {
          id <- params.get("id")
          idx <- params.getAs[Int]("index")
          account <- AccountRepository.find(id)
          currentEmail <- account.emails.toSeq.lift(idx)
        } yield {
          val emails = SortedSet(account.emails.toSeq.updated(idx, email):_*).toList
          val updatedAccount = account.copy(emails = emails)
          AccountRepository.update(updatedAccount)
        }) getOrElse halt(404)

      case _ => halt(500)
    }
  }

  get("/api/v1/accounts/:id/emails") {
    (for {
      account <- AccountRepository.find(params("id"))
      emails <- Option(account.emails)
    } yield JArray(emails.map(e => JString(e)))) getOrElse halt(404)
  }

  get("/api/v1/accounts/:id/emails/count") {
    AccountRepository.find(params("id")).map(a => JInt(a.emails.size)).getOrElse(halt(404))
  }
}
