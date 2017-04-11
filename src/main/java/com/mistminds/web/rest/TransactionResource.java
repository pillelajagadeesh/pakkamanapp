package com.mistminds.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.mistminds.domain.Provider;
import com.mistminds.domain.Transaction;
import com.mistminds.repository.ProviderRepository;
import com.mistminds.repository.TransactionRepository;
import com.mistminds.web.rest.util.HeaderUtil;
@RestController
@RequestMapping("/api")
public class TransactionResource {
	@Inject
	private ProviderRepository providerRepository;
	@Inject
    private TransactionRepository transactionRepository;
private final Logger log = LoggerFactory.getLogger(TransactionResource.class);
/**
 * POST  /transactions -> Create a new transaction screen
 */
@RequestMapping(value = "/transactions",
    method = RequestMethod.POST,
    produces = MediaType.APPLICATION_JSON_VALUE)
@Timed
public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction) throws URISyntaxException {
    log.debug("REST request to save Transaction : {}", transaction);
    if (transaction.getId() != null) {
        return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("transaction", "idexists", "A new transaction cannot already have an ID")).body(null);
    }
    log.info("transaction for perticular provider is =============================================="+transaction);
    transaction.setProvider_id(transaction.getMerchant_param2());
    transaction.setTransaction_date(ZonedDateTime.now());
    Transaction result = transactionRepository.save(transaction);
    String txstatus= result.getOrder_status();
    String txmessage= result.getStatus_message();
    if(txmessage.equalsIgnoreCase("Txn Successful.") && txstatus.equalsIgnoreCase("Success")){
    	Provider provider =providerRepository.findOneById(result.getProvider_id());
    	
    	provider.setWallet_credits(provider.getWallet_credits()+result.getAmount());
    	providerRepository.save(provider);
    	
    }
    return ResponseEntity.created(new URI("/api/transactions/" + result.getId()))
        .headers(HeaderUtil.createEntityCreationAlert("transaction", result.getId().toString()))
        .body(result);
}

}