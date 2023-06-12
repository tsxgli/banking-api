package nl.inholland.bankingapi.model.pages;

import lombok.Data;

@Data
public class TransactionPage {
    private int pageNumber=0;
    private int pageSize=10;
    private String sortBy="id";
    private String sortDirection="ASC";
}
