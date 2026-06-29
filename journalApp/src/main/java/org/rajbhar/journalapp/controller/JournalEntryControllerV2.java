package org.rajbhar.journalapp.controller;


import org.bson.types.ObjectId;
import org.rajbhar.journalapp.entity.JournalEntry;
import org.rajbhar.journalapp.service.JournalEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/journal")                 //entry point url -> /journal/xxxx
public class JournalEntryControllerV2 {

    @Autowired
    private JournalEntryService journalEntryService;

    //localhost:8080/journal GET
    @GetMapping                               //(<empty>) -> '/'
    public List<JournalEntry>getAll(){          //method  //it should always be public
        return journalEntryService.getAll();
    }

    //localhost:8080/journal POST
    @PostMapping
    public JournalEntry createEntry(@RequestBody JournalEntry myEntry){    //body,type, any name
        myEntry.setDate(LocalDateTime.now());
        journalEntryService.saveEntry( myEntry);
        return myEntry;
    }

    @GetMapping("/id/{myId}")
    public JournalEntry getJournalEntryById(@PathVariable ObjectId myId){
        return journalEntryService.findById(myId).orElse(null);
    }

    @DeleteMapping("/id/{myId}")
    public boolean deleteJournalEntryById(@PathVariable ObjectId myId){
        journalEntryService.deleteById(myId);
        return true;
    }

    @PutMapping("/id/{myId}")
    public JournalEntry updateJournalEntryById(@PathVariable ObjectId myId, @RequestBody JournalEntry newEntry){   //myId = old, myEntry = new
        JournalEntry oldEntry = journalEntryService.findById(myId).orElse(null);
        if(oldEntry != null){
            oldEntry.setTitle(newEntry.getTitle() != null && !newEntry.getTitle().equals("") ? newEntry.getTitle() :  oldEntry.getTitle());
            oldEntry.setContent(newEntry.getContent() != null && !newEntry.getContent().equals("") ? newEntry.getContent() : oldEntry.getContent());
        }

        journalEntryService.saveEntry(oldEntry);
        return oldEntry;
    }

}
