package com.mistminds.repository;

import org.springframework.data.mongodb.repository.MongoRepository;


import com.mistminds.domain.Transaction;

public interface TransactionRepository  extends MongoRepository<Transaction,String> {


}
