package org.rajbhar.journalapp.controller;


import org.rajbhar.journalapp.entity.JournalEntry;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/journal")                 //entry point url -> /journal/xxxx
public class JournalEntryController {
    private Map<Long, JournalEntry>journalEntries = new HashMap<>();     //table (instead of db

    //localhost:8080/journal GET
    @GetMapping                               //(<empty>) -> '/'
    public List<JournalEntry>getAll(){          //method  //it should always be public
        return new ArrayList<>(journalEntries.values());
    }

    //localhost:8080/journal POST
    @PostMapping
    public boolean createEntry(@RequestBody JournalEntry myEntry){    //body,type, any name
        journalEntries.put(myEntry.getId(), myEntry);
        return true;
    }

    @GetMapping("/id/{myId}")
    public JournalEntry getJournalEntryById(@PathVariable Long myId){
        return journalEntries.get(myId);
    }

    @DeleteMapping("/id/{myId}")
    public JournalEntry deleteJournalEntryById(@PathVariable Long myId){
        return journalEntries.remove(myId);
    }

    @PutMapping("/id/{myId}")
    public JournalEntry updateJournalEntryById(@PathVariable Long myId, @RequestBody JournalEntry myEntry){
        return journalEntries.put(myId, myEntry);
    }

}
