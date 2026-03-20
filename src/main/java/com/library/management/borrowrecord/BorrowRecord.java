package com.library.management.borrowrecord;

import com.library.management.book.Book;
import com.library.management.borrower.Borrower;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
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
    @GeneratedValue(strategy = GenerationType.UUID) // or your existing strategy
    @JdbcTypeCode(java.sql.Types.VARCHAR)           // Force storage as VARCHAR
    @Column(name = "loan_id", length = 36, updatable = false, nullable = false)
    private UUID loadId;

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
