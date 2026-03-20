package com.library.management.borrower;

import java.util.List;

public interface BorrowerService {

    BorrowerResponse registerBorrower(BorrowerRequest request);

    List<BorrowerResponse> getAllBorrowers();
}
