package com.library.management.book;

import java.util.List;
public interface BookService {

    BookResponse createBook(BookRequest request);

    List<BookResponse> getAllBooks();
}
