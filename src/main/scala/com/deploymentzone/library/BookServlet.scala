package com.deploymentzone.library

import org.json4s.JsonAST._
import org.json4s.JsonDSL._
import com.deploymentzone.library.domain.{Book, BookRepository}
import org.scalatra.{NotFound, Ok}

class BookServlet extends LibraryStack {
  val bookRepo = BookRepository

  get("/api/v1/books") {
    bookRepo.books
  }

  post("/api/v1/books") {
    parsedBody.extractOpt[Book] map { b =>
      val book = bookRepo.create(b.title, b.year, b.publisher)
      book.id
    } getOrElse halt(500)
  }

  get("/api/v1/books/:id") {
    bookRepo.find(params("id")) getOrElse halt(404)
  }

  put("/api/v1/books/:id") {
    (for {
      oldBook <- bookRepo.find(params("id"))
      newBook <- parsedBody.extractOpt[Book]
    } yield {
      val updatedBook = newBook.copy(id = oldBook.id)
      bookRepo.update(updatedBook)
    }) getOrElse halt(404)
  }

  delete("/api/v1/books/:id") {
    if (bookRepo.delete(params("id"))) Ok else NotFound
  }
}
