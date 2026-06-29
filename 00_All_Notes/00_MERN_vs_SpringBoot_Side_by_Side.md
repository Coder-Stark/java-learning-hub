# MERN vs Spring Boot (Side-by-Side)

## 1. Entry Point

``` text
┌──────────────────────────── MERN : server.js ─────────────────────────────┬──────────────────── Spring : JournalApplication.java ────────────────────┐
│ const express = require("express");                                       │ @SpringBootApplication                                                 │
│ const app = express();                                                    │ public class JournalApplication {                                      │
│ const connectDB = require("./config/db");                                 │     public static void main(String[] args) {                           │
│ const journalRoutes = require("./routes/journalRoutes");                  │         SpringApplication.run(JournalApplication.class, args);         │
│ app.use(express.json());                                                  │     }                                                                  │
│ connectDB();                                                              │ }                                                                      │
│ app.use("/journal", journalRoutes);                                       │                                                                        │
│ app.listen(8080);                                                         │                                                                        │
└───────────────────────────────────────────────────────────────────────────┴────────────────────────────────────────────────────────────────────────┘
```

## 2. Database Connection

``` text
┌────────────────────────────── MERN : config/db.js ─────────────────────────────┬──────────────────── Spring : application.properties ───────────────────┐
│ const mongoose = require("mongoose");                                          │ spring.data.mongodb.uri=mongodb+srv://user:pass@cluster/journaldb     │
│ const connectDB = async()=>{                                                   │                                                                       │
│     await mongoose.connect(process.env.MONGO_URI);                             │                                                                       │
│ };                                                                             │                                                                       │
│ module.exports = connectDB;                                                    │                                                                       │
└────────────────────────────────────────────────────────────────────────────────┴───────────────────────────────────────────────────────────────────────┘
```

## 3. Model / Entity

``` text
┌──────────────────────────── MERN : models/JournalEntry.js ─────────────────────┬──────────────────── Spring : entity/JournalEntry.java ─────────────────┐
│ const journalSchema = new mongoose.Schema({                                   │ @Document(collection="journal_entries")                               │
│     title:String,                                                             │ public class JournalEntry {                                            │
│     content:String,                                                           │     @Id private String id;                                             │
│     date:Date                                                                 │     private String title;                                              │
│ });                                                                           │     private String content;                                            │
│ module.exports = mongoose.model("JournalEntry",journalSchema);                │     private Date date;                                                 │
│                                                                                │     // getters & setters                                               │
│                                                                                │ }                                                                      │
└────────────────────────────────────────────────────────────────────────────────┴───────────────────────────────────────────────────────────────────────┘
```

## 4. Routes

``` text
┌──────────────────────────── MERN : routes/journalRoutes.js ────────────────────┬────────────────── Spring : JournalEntryController.java ─────────────────┐
│ router.post("/",controller.createEntry);                                      │ @RestController                                                       │
│ router.get("/",controller.getAll);                                            │ @RequestMapping("/journal")                                           │
│ module.exports = router;                                                      │ @PostMapping                                                          │
│                                                                               │ @GetMapping                                                           │
└───────────────────────────────────────────────────────────────────────────────┴───────────────────────────────────────────────────────────────────────┘
```

## 5. Controller

``` text
┌──────────────────────── MERN : controllers/journalController.js ───────────────┬────────────────── Spring : JournalEntryController.java ─────────────────┐
│ exports.createEntry = async(req,res)=>{                                       │ @Autowired                                                            │
│     await service.saveEntry(req.body);                                        │ private JournalEntryService service;                                  │
│     res.json(true);                                                           │ @PostMapping                                                          │
│ };                                                                            │ public boolean createEntry(@RequestBody JournalEntry entry){           │
│                                                                               │     service.saveEntry(entry);                                         │
│                                                                               │     return true;                                                      │
│                                                                               │ }                                                                     │
└───────────────────────────────────────────────────────────────────────────────┴───────────────────────────────────────────────────────────────────────┘
```

## 6. Service

``` text
┌────────────────────────── MERN : services/journalService.js ───────────────────┬──────────────────── Spring : JournalEntryService.java ──────────────────┐
│ exports.saveEntry = async(entry)=>{                                           │ @Component                                                            │
│     await Journal.create(entry);                                              │ @Autowired                                                            │
│ };                                                                            │ private JournalEntryRepository repository;                            │
│                                                                               │ public void saveEntry(JournalEntry entry){                            │
│                                                                               │     repository.save(entry);                                           │
│                                                                               │ }                                                                     │
└───────────────────────────────────────────────────────────────────────────────┴───────────────────────────────────────────────────────────────────────┘
```

## 7. Database Operations

``` text
┌────────────────────────────── MERN : Mongoose ────────────────────────────────┬──────────────────── Spring : Repository ───────────────────────────────┐
│ Journal.create(data);                                                         │ repository.save(entry);                                               │
│ Journal.find();                                                               │ repository.findAll();                                                 │
│ Journal.findById(id);                                                         │ repository.findById(id);                                              │
│ Journal.findByIdAndUpdate(id,data);                                           │ repository.save(updatedEntry);                                        │
│ Journal.findByIdAndDelete(id);                                                │ repository.deleteById(id);                                            │
└───────────────────────────────────────────────────────────────────────────────┴───────────────────────────────────────────────────────────────────────┘
```
