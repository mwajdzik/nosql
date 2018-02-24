
// db.foo.createIndex(keys, options)
//  - options: name, build now, unique, sparse, TTL, language, ...

db.foo.explain().find({});          // search for WINNING_PLAN (COLLSCAN, IXSCAN)
db.foo.explain(true).find({});      // run the query, search for executionStages.docsExamined

// ---------------------------------------------------------------------------------------------------------------------

db.students.createIndex({student_id: 1});           // add an index, 1 for ascending
db.students.createIndex({student_id: 1, clazz: -1});// add a compound index
db.students.createIndex({'scores.score': 1});       // we are not limited to the top level of keys
db.students.createIndex({'scores.score': 1});

db.students.dropIndex({student_id: 1});             // drop the existing index

db.students.getIndexes();                           // list all existing indexes in the collection
db.system.indexes.find();                           // list all existing indexes in the db (not for WiredTiger!)

db.system.indexes.find({ns: 'test.animals'});       // check which fields the indexes exist (not for WiredTiger!)


// ---------------------------------------------------------------------------------------------------------------------


db.students.createIndex({student_id: 1});
db.students.createIndex({student_id: 1}, {unique: 1});                 // keys must be unique, duplication is not allowed
db.students.createIndex({student_id: 1}, {unique: 1, dropDups: true}); // the duplicates will be removed (DANGER)

// we may want to add an index even if some of the values are undefined (null)
// the index will include only the valid entries, if we sort we will see only documents with student_id defined
// we will see the same behavior when we query with {key:null}

db.students.createIndex({student_id: 1}, {unique: 1, sparse: true});


// ---------------------------------------------------------------------------------------------------------------------


// explain()

db.students.find({c: 1}).explain();
db.students.explain().find({c: 1});         // MongoDB 3.0, preferable, can be used with update, find, ... (not insert)

/*
    executionsStats
        nReturned                   - number of returned documents
        executionTimeMillis         - time to execute the query
        totalKeysExamined
        totalDocsExamined
 */

db.foo.find({
    $or: [{c: {$gt: 500}}, {c: {$lt: 750}}]         // an index on a, b, c - so it cannot be used when querying for 'c'
}).sort({a: 1, b: 1});                              // sort, however, can use the index


// ---------------------------------------------------------------------------------------------------------------------


db.foo.stats();                                     // shows for example storageSize
db.foo.totalIndexSize();                            // helps to find out how much memory we need to keep indexes


// ---------------------------------------------------------------------------------------------------------------------


// Giving MongoDB a hint on which index should be used:

// sometimes after analysis we may know that using a particular index will result in better performance
db.foo.find().hint({c: 1});                         // give the DB a hint to use the 'c' index
db.foo.find().hint({$natural: 1});                  // don't use any index


// ---------------------------------------------------------------------------------------------------------------------


// Geospacial index:

db.foo.insert({name: "ACE Hardware", location: [48.232, -74.343]});
db.foo.createIndex({location: "2d", type: 1});      // geo-spacial index (x, y)
db.foo.find({location: {$near: [50, 50]}}).limit(3);

db.foo.insert({name: "ACE Hardware", location: [48.232, -74.343]});
db.foo.createIndex({location: "2d", type: 1});      // geo-spacial spherical index (longitude, latitude)
db.runCommand({geaNear: "foo", near: [50, 50], spherical: true, maxDistance: 1});

db.foo.find({location: {$near: {$geometry: {type: "Point", coordinates: [50, 50]}, $maxDistance: 2000}}}).limit(3);

var point = {
    "_id": {"$oid": "535471aaf28b4d8ee1e1c86f"},
    "store_id": 8,
    "loc": {"type": "Point", "coordinates": [-37.47891236119904, 4.488667018711567]}
};

db.stores.find({loc: {$near: {$geometry: {type: "Point", coordinates: [-130, 39]}, $maxDistance: 1000000}}});


// ---------------------------------------------------------------------------------------------------------------------


// Full text search index:

var s = {"words": "dog moss ruby"};

db.setences.createIndex({"words": "text"});
db.setences.find({$text: {$search: "moss"}});

// we look for: dog || moss || ruby - score helps to find the best one matching
db.setences.find({$text: {$search: "dog moss ruby"}}, {score: {$meta: "textScore"}})
    .sort({score: {$meta: "textScore"}});


// ---------------------------------------------------------------------------------------------------------------------


// Profiling:

db.system.profile.find();           // show profile information
db.system.profile.find().pretty();
db.system.profile.find({ns: /school.students/}).sort({millis: 1}).pretty();
db.system.profile.find({millis: {$gt: 1000}}).sort({ts: -1});


// 0 - off, 1 - log slow queries, 2 - log all queries (debugging)
db.getProfilingLevel();             // 1
db.getProfilingStatus();            // { "was" : 1, "slowms" : 2 }

db.setProfilingLevel(1, 10);        // or when starting the daemon


db.system.profile
    .find({ns: {$regex: "school2.students"}}, {millis: 1, _id: 0})
    .sort({millis: -1})
    .limit(10)
    .pretty();


// ---------------------------------------------------------------------------------------------------------------------