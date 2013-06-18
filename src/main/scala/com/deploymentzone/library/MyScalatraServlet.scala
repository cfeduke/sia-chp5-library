package com.deploymentzone.library

import org.scalatra._
import scalate.ScalateSupport
import org.scalatra.json.JacksonJsonSupport
import org.json4s.DefaultFormats

class Library extends LibraryStack with JacksonJsonSupport {

  implicit val jsonFormats = DefaultFormats

  before("""/api/v1/.*""".r) {
    contentType = "application/json"
  }

}
