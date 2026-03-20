package com.library.management.book;

import java.util.List;
public interface BookService {

    BookResponse saveBook(BookRequest request);

    List<BookResponse> getAllBooks();



}
