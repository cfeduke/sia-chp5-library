package com.deploymentzone.library.domain

case class Account (
  id: String,
  pinCode: String,
  primaryEmail: String,
  emails: List[String],
  address: Address
)
