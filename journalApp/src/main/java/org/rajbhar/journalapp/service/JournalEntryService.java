package org.rajbhar.journalapp.service;

import org.bson.types.ObjectId;
import org.rajbhar.journalapp.entity.JournalEntry;
import org.rajbhar.journalapp.repository.JournalEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class JournalEntryService {
    @Autowired
    private JournalEntryRepository journalEntryRepository;    //AccessModifier  DataType/ClassName   VariableName;

    //create service
    public void saveEntry(JournalEntry journalEntry){
        journalEntryRepository.save(journalEntry);
    }

    //get service
    public List<JournalEntry> getAll(){
        return  journalEntryRepository.findAll();        //extended mongodb have this findAll property
    }

    //get by id service
    public Optional<JournalEntry> findById(ObjectId id){   //optional -> box (may or may not have data)
        return journalEntryRepository.findById(id);
    }

    //delete by id service
    public void deleteById(ObjectId id){
        journalEntryRepository.deleteById(id);
    }
}


