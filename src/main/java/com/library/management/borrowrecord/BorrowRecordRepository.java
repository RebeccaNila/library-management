package com.library.management.borrowrecord;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, UUID> {

    // Requirement #8: Check if book is already borrowed
    boolean existsByBookIdAndReturnedAtIsNull(Long bookId);

    // Find the active record for returning
    Optional<BorrowRecord> findByBookIdAndReturnedAtIsNull(Long bookId);
}
