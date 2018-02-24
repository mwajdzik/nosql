// ---------------------------------------------------------------------------------------------------------------------
// http://docs.mongodb.org/manual/
// ---------------------------------------------------------------------------------------------------------------------
//
// help
// show dbs
// show collections
// db                                               // db is an object representing the currently loaded database
// use test							                // select db that will be in use (create a new db if it didn't exists)
//
// ---------------------------------------------------------------------------------------------------------------------


// INSERT: db.foo.insert(document)
// SAVE: db.foo.insert(document)

db.things.insert({a: 1, b: 2, c: 3});               // 'things' is a collection inside the current db, 'insert' is a command
db.things.save({a: 1, b: 2, c: 3});                 // if _id provided, save will update, if not, it will insert

// FIND: db.foo.find(query, projection)
// FIND_ONE: db.foo.findOne(query, projection)

db.things.find();
db.things.find().pretty();

db.things.findOne();                                // taken at random from a collection
db.things.findOne({a: 1});                          // the argument can be a document used for searching
db.things.findOne({a: 1}, {a: true, b: false});     // the second argument can specify columns (_id true by default)
db.things.findOne({a: 1}, {a: true, b: false, _id: false});

// COUNT: db.foo.count(query)

db.things.count({a: 1});


// ---------------------------------------------------------------------------------------------------------------------


ObjectId();                                         // returns a new ObjectId()
ObjectId().getTimestamp();                          // there is a timestamp associated with an ObjectId


// ---------------------------------------------------------------------------------------------------------------------


var thing = db.things.findOne({a: 1});
thing.b = 2;
db.things.save(thing);


// ---------------------------------------------------------------------------------------------------------------------


// prepare some sample data
for (var i = 0; i < 1000; i++) {
    var names = ["exam", "essay", "quiz"];
    for (var j = 0; j < names.length; j++) {
        db.scores.insert({
            "student": i,
            "type": names[j],
            "score": Math.round(Math.random() * 100)
        });
    }
}


// ---------------------------------------------------------------------------------------------------------------------


// *conditionals* are an inner document key: { $gt: 95 }
// *modifiers* are always a key in the outer document: {$inc: {age: 1}}


db.scores.find({type: "essay", score: 50}, {student: true, _id: false});

// $gt, $gte, $lt, $lte, $ne - not equal - (query operators) - work for numbers and strings

db.scores.find({type: "essay", score: {$gt: 95}});
db.scores.find({type: "essay", score: {$gt: 95, $lte: 98}});    // range

db.messages.find({'headers.Date': {'$gt': new Date(2001, 3, 1)}}, {'headers.From': 1, _id: 0})
    .sort({'headers.From': 1})
    .explain();

// $exists - check if a property is defined
db.people.find({profession: {$exists: true}});

// $type
db.people.find({name: {$type: 2}});						// 2 - string (BSON specification)

// $regex
db.people.find({name: {$regex: "a"}});					// find style (not match)
db.people.find({name: {$regex: "^A"}});
db.people.find({name: {$regex: "e$"}});

db.users.find({"name": /^joey?/i});

// and (without $and)
db.users.find({name: {$regex: "q"}, email: {$exists: true}});

// $or (prefix operator)
db.people.find({
    $or: [
        {name: {$regex: "e$"}},
        {age: {$exists: true}}
    ]
});

db.scores.find({
    $or: [
        {score: {$lt: 50}},
        {score: {$gt: 90}}
    ]
});

// $and
db.people.find({
    $and: [
        {name: {$gt: "C"}},
        {name: {$regex: "a"}}
    ]
});

// the same as the previous example
db.people.find({
    name: {$gt: "C", $regex: "a"}
});

// !!! watch out (the first condition will be overridden) !!!
db.scores.find({score: {$gt: 50}, score: {$lt: 90}});


// ---------------------------------------------------------------------------------------------------------------------


// searching in an array - if favorites is a string, it will match if its value is 'beer'
// if it is an array, one of its elements must be 'beer'
db.things.find({favorites: "beer"});

// all of them must be present
db.things.find({favorites: {$all: ["pretzels", "beer"]}});

// any of them may be present
db.things.find({favorites: {$in: ["pretzels", "beer"]}});

// neither of them may be present
db.things.find({favorites: {$nin: ["pretzels", "beer"]}});

// find all docs that have peach as a third element in the array fruit
db.food.find({"fruit.2": "peach"});

// find all docs which fruit array has three elements
db.food.find({"fruit": {$size: 3}});

// find docs with a sub-array
db.blog.posts.findOne({}, {"comments": {$slice: 10}});        // first ten
db.blog.posts.findOne({}, {"comments": {$slice: -10}});       // last ten
db.blog.posts.findOne({}, {"comments": {$slice: [23, 10]}});  // 24 -> 34

// dot notation
db.users.insert({name: "Richard", email: {work: "richard@10gem.com", personal: "kreuter@example.org"}});
db.users.find({"email.work": "richard@10gem.com"});


// ---------------------------------------------------------------------------------------------------------------------

// $elemMatch !!!

// Embedded document matches have to match the whole document, and this doesn't match the "comment" key.
db.blog.find({"comments": {"author": "joe", "score": {"$gte": 5}}});

// The author criteria could match a different comment than the score criteria.
db.blog.find({"comments.author": "joe", "comments.score": {"$gte": 5}});

// To correctly group criteria without needing to specify every key, use "$elemMatch".
// It allows you to partially specify criteria to match a single embedded document in an array.
db.blog.find({"comments": {"$elemMatch": {"author": "joe", "score": {"$gte": 5}}}});


// ---------------------------------------------------------------------------------------------------------------------


// not and modulo
db.users.find({"id_num": {"$mod": [5, 1]}});
db.users.find({"id_num": {"$not": {"$mod": [5, 1]}}});


// ---------------------------------------------------------------------------------------------------------------------

// $where

// For queries that cannot be done any other way, there are "$where" clauses,
// which allow you to execute arbitrary JavaScript as part of your query.
// This allows to do (almost) anything within a query.

// The most common case for this is wanting to compare the values for two keys in a document,
// for instance, if we had a list of items and wanted to return documents where any two of the values are equal.
db.foo.insert({"apple": 1, "banana": 6, "peach": 3});
db.foo.insert({"apple": 8, "spinach": 4, "watermelon": 4});

// "spinach" and "watermelon" have the same value, so we’d like that document returned.
db.foo.find({
    "$where": function () {
        for (var current in this) {
            for (var other in this) {
                if (current != other && this[current] == this[other]) {
                    return true;
                }
            }
        }

        return false;
    }
});


// We used a function earlier, but we can also use strings to specify a "$where" query;
// the following two "$where" queries are equivalent:
db.foo.find({"$where": "this.x + this.y == 10"});
db.foo.find({"$where": "function() { return this.x + this.y == 10; }"});


// ---------------------------------------------------------------------------------------------------------------------


// cursors
cur = db.users.find();

cur.size();

while (cur.hasNext()) {
    printjson(cur.next());
}


// these methods are executed on server by the engine so
// they cannot be invoked after hasNext() or next()

cur.limit(5);                                     // limit to 5 elements
cur.sort({name: -1});                             // reverse order by name
cur.sort({name: -1}).limit(5);                    // both
cur.sort({name: -1}).limit(5).skip(2);            // skip first 2 elements


// write a query that retrieves exam documents, sorted by score in descending order,
// skipping the first 50 and showing only the next 20:

db.scores.find({type: "exam"}).sort({score: -1}).skip(50).limit(20);


// ---------------------------------------------------------------------------------------------------------------------


// UPDATE is atomic within a document (no two clients can update
// the same document at the same time)!!!

// db.foo.update(query, update, options);
//  - options: one, many, upsert

// updating - first argument is for search, the second for substitution

// all properties except _id are lost !!!
db.users.update({name: "Dwight"}, {name: "Dwight", city: "Krakow"});

// only add a new property age
db.users.update({name: "Dwight"}, {$set: {age: 32}});

// increment by one
db.users.update({name: "Dwight"}, {$inc: {age: 1}});

// remove property
db.users.update({name: "Andrew"}, {$unset: {city: 1}});

// rename property
db.users.update({naem: "Andrew"}, {$rename: {naem: 'name'}});

// we can modify a value and call save() for updating as well


// ---------------------------------------------------------------------------------------------------------------------


// array operations:

db.arrays.insert({_id: 0, a: [1, 2, 3, 4, 5]});

db.arrays.update({_id: 0}, {$set: {"a.2": 0}});                         // 1, 2, 0, 4, 5
db.arrays.update({_id: 0}, {$push: {a: 6}});                            // 1, 2, 0, 4, 5, 6
db.arrays.update({_id: 0}, {$pop: {a: 1}});                             // 1, 2, 0, 4, 5
db.arrays.update({_id: 0}, {$pop: {a: -1}});                            // 2, 0, 4, 5
db.arrays.update({_id: 0}, {$pull: {a: 4}});                            // 2, 0, 5, 6, 7, 8 - pull removes all matching
db.arrays.update({_id: 0}, {$pullAll: {a: [5, 6, 7]}});                 // 2, 0, 8
db.arrays.update({_id: 0}, {$addToSet: {a: 0}});                        // 2, 0, 8
db.arrays.update({_id: 0}, {$addToSet: {a: 4}});                        // 2, 0, 8, 4
db.arrays.update({_id: 0}, {$addToSet: {a: {$each: [16, 32]}}});        // 2, 0, 8, 4, 16, 32

// only update documents that contain 5 in 'a'
db.arrays.update({a: 5}, {$addToSet: {a: 6}});

// ---------------------------------------------------------------------------------------------------------------------


db.blog.update(
    {"post": post_id},
    {"$inc": {"comments.0.votes": 1}}
);

// positional operator - $
db.blog.update(
    {"comments.author": "John"},
    {"$set": {"comments.$.author": "Jim"}}
);


// upsert - if no document is found that matches the update criteria, a new document will be created
//          by combining the criteria and update documents (faster and atomic)
db.analytics.update(
    {"url": "/blog"},
    {"$inc": {"visits": 1}},
    {upsert: true}
);

db.scores.update(
    {score: {$lt: 70}},
    {$inc: {score: 20}},
    {multi: true}
);


// ---------------------------------------------------------------------------------------------------------------------


// findAndModify - can return the item and update it in a single operation (can also find and remove)
// http://docs.mongodb.org/manual/reference/method/db.collection.findAndModify/

/*
 db.foo.findAndModify({
    query: <document>,
    update: <document>,
    upsert: <boolean>,          -- create a new if it doesn't exist
    remove: <boolean>,
    new: <boolean>,             -- by def. a document before making a change gets returned
    sort: <document>,
    fields: <document>
 })
 */


// ---------------------------------------------------------------------------------------------------------------------


// {} - matches every document, but MongoDB will update only the first one
db.users.update({}, {title: "Dr"});

// will update all documents (however it is possible that some docs will get updated,
// the operation will yield to allow other operations on the collection, and then the update will proceed)
db.users.update({}, {title: "Dr"}, {multi: true});

// give every record whose score was less than 70 an extra 20 points
db.scores.update(
    {score: {$lt: 70}},
    {$inc: {score: 20}},
    {multi: true}
);


// ---------------------------------------------------------------------------------------------------------------------


db.scores.remove({score: {$lt: 20}});
db.scores.remove({score: {$lt: 20}}, {justOne: true});
db.scores.remove();                              // clean the collection one by one document
db.scores.drop();                                // drop the whole collection (faster but removes indexes)


// ---------------------------------------------------------------------------------------------------------------------


db.runCommand({getLastError: 1});


// ---------------------------------------------------------------------------------------------------------------------


// shows the query plan - for example if indexes are used
db.people.find().explain();


// ---------------------------------------------------------------------------------------------------------------------