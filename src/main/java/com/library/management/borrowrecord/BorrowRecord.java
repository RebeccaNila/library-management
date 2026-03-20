package com.library.management.borrowrecord;

import com.library.management.book.Book;
import com.library.management.borrower.Borrower;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import java.util.UUID;

import java.time.LocalDateTime;

@Entity
@Table(name = "borrow_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowRecord {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID loadId;
//    @Id
//    @GeneratedValue
//    @UuidGenerator
//    @Column(updatable = false, nullable = false)
//    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrower_id", nullable = false)
    private Borrower borrower;

    private String message;

    @Enumerated(EnumType.STRING)
    private LoanStatus status = LoanStatus.BORROWED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(updatable = false, nullable = false)
    private LocalDateTime borrowedAt;

    @Column
    private LocalDateTime returnedAt;

    @PrePersist
    protected void onCreate() {
        this.borrowedAt = LocalDateTime.now();
    }
}
