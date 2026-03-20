package com.library.management.book;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.library.management.common.exception.ConflictException;

import java.util.Optional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Override
    public BookResponse saveBook(BookRequest request) {

        // If a book with this ISBN already exists,
        // validate that title and author match
        String cleanIsbn = request.isbn().trim();

        log.info("Creating book with ISBN: {}", cleanIsbn);

        Optional<Book> existingOpt = bookRepository.findFirstByIsbn(cleanIsbn);

        if (existingOpt.isPresent()) {
            Book existing = existingOpt.get();

            log.info("Existing book found [ID: {}, Title: {}, Author: {}]",
                    existing.getId(), existing.getTitle(), existing.getAuthor());

            validateIsbnConsistency(existing, request);
        }

        Book book = new Book();
        book.setIsbn(cleanIsbn);
        book.setTitle(request.title());
        book.setAuthor(request.author());

        Book saved = bookRepository.save(book);

        return mapToResponse(saved);
    }

    private void validateIsbnConsistency(Book existing, BookRequest request) {

        boolean titleMismatch = !existing.getTitle().equalsIgnoreCase(request.title().trim());
        boolean authorMismatch = !existing.getAuthor().equalsIgnoreCase(request.author().trim());

        if (titleMismatch || authorMismatch) {
            log.warn("ISBN conflict detected for ISBN: {}", existing.getIsbn());

            throw new ConflictException(
                    "ISBN already exists with different title or author"
            );
        }
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
