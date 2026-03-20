package com.library.management.borrower;

import com.library.management.common.exception.ConflictException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BorrowerServiceImpl implements BorrowerService {

    private final BorrowerRepository borrowerRepository;


    public BorrowerResponse registerBorrower(BorrowerRequest request) {

        String email = request.email().trim().toLowerCase();
        String name = request.name().trim();

        log.info("Registering borrower with email: {}", email);

        // Check duplicate email
        borrowerRepository.findByEmail(email)
                .ifPresent(existing -> {
                    log.warn("Borrower already exists with email: {}", email);
                    throw new ConflictException("Email already registered");
                });

        Borrower borrower = new Borrower();
        borrower.setName(name);
        borrower.setEmail(email);

        Borrower saved = borrowerRepository.save(borrower);

        log.info("Borrower registered successfully with id: {}", saved.getId());

        return mapToResponse(saved);
    }

    private BorrowerResponse mapToResponse(Borrower borrower) {
        return new BorrowerResponse(
                borrower.getId(),
                borrower.getName(),
                borrower.getEmail()
        );
    }

}
