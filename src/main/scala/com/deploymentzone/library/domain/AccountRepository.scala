package com.deploymentzone.library.domain

object AccountRepository {

  private val _accounts = collection.mutable.Map[String, Account]()

  def accounts: List[Account] = _accounts.values.toList

  def find(id: String): Option[Account] = _accounts.get(id)

  def update(account: Account) {
    _accounts(account.id) = account
  }

  def remove(id: String) {
    _accounts.remove(id)
  }

  private def uuid = java.util.UUID.randomUUID().toString()

  def create(pinCode: String, email: String, address: Address): Account = {
    val acc = Account(uuid, pinCode, email, List(), address)
    _accounts(acc.id) = acc
    acc
  }
}
