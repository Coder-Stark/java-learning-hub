package org.rajbhar.journalapp.repository;

import org.bson.types.ObjectId;
import org.rajbhar.journalapp.entity.JournalEntry;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface JournalEntryRepository extends MongoRepository<JournalEntry, ObjectId> {     //<entity type, id>
}
