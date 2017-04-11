package com.mistminds.config;

import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;

@ChangeLog(order = "001")
public class MyMongoChangeSet {

   @ChangeSet(order = "001", id = "ChangeSet-1", author = "jhipster")
   public void someChange(DB db) {
      DBCollection mycollection = db.getCollection("T_AUTHORITY");
      BasicDBObject doc1 = new BasicDBObject().append("_id", "ROLE_ADMIN");
      BasicDBObject doc2 = new BasicDBObject().append("_id", "ROLE_USER");
      mycollection .insert(doc1);
      mycollection .insert(doc2);
   }
}
