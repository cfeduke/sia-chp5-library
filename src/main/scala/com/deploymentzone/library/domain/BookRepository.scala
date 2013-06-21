package com.deploymentzone.library.domain

object BookRepository {
  private def uuid = java.util.UUID.randomUUID().toString

  private val _books = collection.mutable.Map[String, Book]()

  def books: List[Book] = _books.values.toList

  def create(title: String, year: String, publisher: String) = {
    val book = Book(uuid, title, year, publisher)
    _books(book.id) = book
    book
  }

  def find(id: String): Option[Book] = _books.get(id)

  def update(book: Book) {
    _books(book.id) = book
  }

  def delete(id: String): Boolean = {
    _books.remove(id).isDefined
  }
}
