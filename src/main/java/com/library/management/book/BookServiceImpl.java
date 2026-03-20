package com.library.management.book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Override
    public BookResponse createBook(BookRequest request) {

        Book book = new Book();
        book.setIsbn(request.isbn());
        book.setTitle(request.title());
        book.setAuthor(request.author());

        Book saved = bookRepository.save(book);

        return mapToResponse(saved);
    }

    @Override
    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private BookResponse mapToResponse(Book book) {
        return new BookResponse(
                book.getId(),
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor()
        );
    }
}
