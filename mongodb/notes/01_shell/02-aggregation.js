
// ---------------------------------------------------------------------------------------------------------------------

/*
 An aggregation pipeline concept uses different stages/operators:

 - $project - reshape documents (1:1 - 1 document in, 1 out)
 - $match - filter (n:1), (for example only a specific manufacturer)
 - $group - aggregation (n:1)
 - $sort - sorts (1:1)
 - $skip - skipping (n:1)
 - $limit - limit (n:1)
 - $unwind - for each element in an array produces a new document (1:n)
 - $out - store result in a collection

 Expression that can be used in $group:

 - $sum - using $sum to count: {"$sum": 1}, using to sum prices: {"$sum": "$price"}
 - $avg
 - $min
 - $max
 - $push - for building arrays
 - $addToSet - for building arrays (uniquely)
 - $first - must be used with $sort
 - $last - must be used with $sort
 */

// ---------------------------------------------------------------------------------------------------------------------

db.products.aggregate([
    {
        $group: {
            "_id": "$manufacturer",
            "num_products": {"$sum": 1}
        }
    }
]);


// compound grouping is done using compound keys
db.products.aggregate([
    {
        $group: {
            "_id": {"manufacturer": "$manufacturer", "category": "$category"},
            "num_products": {"$sum": 1}
        }
    }
]);

// ---------------------------------------------------------------------------------------------------------------------

var zips = {
    "city": "CLANTON",
    "loc": [
        -86.642472,
        32.835532
    ],
    "pop": 13990,
    "state": "AL",
    "_id": "35045"
};

// sum up the population (pop) by state and put the result in a field called population
db.zips.aggregate([
    {
        "$group": {
            "_id": "$state",
            "population": {"$sum": "$pop"}
        }
    }
]);

// calculate the average population of a zip code (postal code) by state
db.zips.aggregate([
    {
        "$group": {
            "_id": "$state",
            "average_pop": {"$avg": "$pop"}
        }
    }
]);

// $addToSet - create a new key which value is an array
// $push - similar to $addToSet, but does not support uniqueness
// return the postal codes that cover each city
db.zips.aggregate([
    {
        "$group": {
            "_id": "$city",
            "postal_codes": {"$addToSet": "$_id"}
        }
    }
]);

// return the population of the postal code in each state with the highest population
db.zips.aggregate([
    {
        "$group": {
            "_id": "$state",
            "pop": {"$max": "$pop"}
        }
    }
]);

// double grouping - eg. average score in each class (first average students grade in each class, an then do the average)
db.grades.aggregate([
    {
        "$group": {
            "_id": {class_id: "$class_id", student_id: "$student_id"},
            "average": {"$avg": "$score"}
        }
    },
    {
        "$group": {
            "_id": "$_id.class_id",
            "average": {"$avg": "$average"}
        }
    }
]);


// projection - $project ( http://docs.mongodb.org/manual/reference/operator/aggregation-string/ )
db.products.aggregate([
    {
        "$project": {
            "_id": 0,                                   // remove _id
            "maker": {$toLower: "$manufacturer"},       // $toUpper
            "details": {
                "category": "$category",
                "price": {"$multiply": ["$price", 10]}  // $add
            },
            "item": "$name"
        }
    }
]);

db.zips.aggregate([
    {
        "$project": {
            "_id": 0,
            "loc": 0,
            "city": {$toLower: "$city"},
            "pop": 1,
            "state": 1,
            "zip": "$_id"
        }
    }
]);


// filtering - $match (may pass a subset of documents)
db.zips.aggregate([
    {
        "$match": {
            "state": "NY"
        }
    }
]);

db.zips.aggregate([
    {
        "$match": {
            "pop": {$gt: 100000}
        }
    }
]);


// sorting - $sort - can use a lot of memory (limit of 100MB, in-disk sorting otherwise)

// $skip

// $limit


db.zips.aggregate([
    {
        "$match": {
            "state": "NY",
            "pop": {$gt: 100000}
        }
    },
    {
        "$group": {
            "_id": "$city",
            "population": {"$sum": "$pop"},
            "zip_codes": {"$addToSet": "$_id"}
        }
    },
    {
        "$project": {
            "_id": 0,
            "city": "$_id",
            "population": 1,
            "zip_codes": 1
        }
    },
    {
        "$sort": {
            "population": -1
        }
    },
    {
        $skip: 10
    },
    {
        $limit: 5
    }
]);


// $first, $last
// the largest city in a state
db.zips.aggregate([
    {
        "$group": {
            _id: {state: "$state", city: "$city"},
            population: {$sum: "$pop"}
        }
    },
    {
        "$sort": {
            "_id.state": 1, "population": -1
        }
    },
    {
        "$group": {
            _id: "$_id.state",
            city: {$first: "$_id.city"},
            population: {$first: "$population"}
        }
    },
    {
        "$sort": {
            "_id": 1
        }
    }
]);


// $unwind - find the post popular tags
db.post.aggregate([
    {
        "$unwind": "$tags"
    },
    {
        "$group": {
            "_id": "$tags",
            "count": {$sum: 1}
        }
    },
    {
        "$sort": { "count": 1}
    },
    {
        "$limit": 10
    },
    {
        "$project": {
            _id: 0,
            "tag": "$_id",
            "count": 1
        }
    }
]);


// undo double $unwind
db.post.aggregate([
    {
        "$unwind": "$sizes"
    },
    {
        "$unwind": "$colors"
    },
    {
        "$group": {
            "_id": {name: "$name", size: "$sizes"},
            "colors": {$push: "$colors"}
        }
    },
    {
        "$group": {
            "_id": {name: "$_id.name", colors: "$colors"},
            "sizes": {$push: "$_id.size"}
        }
    },
    {
        "$project": {
            _id: 0,
            name: "$_id.name",
            sizes: 1,
            colors: "$_id.colors"
        }
    }
]);

// ---------------------------------------------------------------------------------------------------------------------

// tricks

// SELECT COUNT(*) AS count FROM orders
db.orders.aggregate([
    {
        $group: {
            _id: null,              // !!!
            count: {$sum: 1}
        }
    }
]);

// SELECT SUM(price) AS total FROM orders
db.orders.aggregate([
    {
        $group: {
            _id: null,              // !!!
            total: {$sum: '$price'}
        }
    }
]);

// ---------------------------------------------------------------------------------------------------------------------

var post = {
    "_id": ObjectId("513d396da0ee6e58987bae74"),
    "author": "andrew",
    "body": "Representatives from the planet Mars...",
    "comments": [
        {
            "author": "Larry Ellison",
            "body": "While I am deeply disappointed that Mars...",
            "email": "larry@oracle.com"
        },
        {
            "author": "Salvatore Sanfilippo",
            "body": "This make no sense to me. Redis would have worked fine."
        }
    ],
    "date": ISODate("2013-03-11T01:54:53.692Z"),
    "permalink": "martians_to_use_mongodb",
    "tags": [
        "martians",
        "seti",
        "nosql",
        "worlddomination"
    ],
    "title": "Martians to use MongoDB"
};


// calculate the author with the greatest number of comments
db.posts.aggregate(
    {
        "$project": {
            _id: 0,
            "comments.author": 1
        }
    },
    {
        "$unwind": "$comments"
    },
    {
        "$project": {
            author: "$comments.author"
        }
    },
    {
        "$group": {
            "_id": "$author",
            "count": {$sum: 1}
        }
    },
    {
        "$sort": { "count": -1}
    },
    {
        "$limit": 1
    }
);

// ---------------------------------------------------------------------------------------------------------------------

// calculate the average population of cities in California (abbreviation CA)
// and New York (NY) (taken together) with populations over 25,000
db.zips.aggregate([
    {
        "$match": {
            "state": { $in: ["CA", "NY"] }
        }
    },
    {
        "$group": {
            "_id": {state: "$state", city: "$city"},
            "population": {"$sum": "$pop"}
        }
    },
    {
        "$match": {
            "population": {$gt: 25000}
        }
    },
    {
        "$group": {
            "_id": null,
            "average_pop": {"$avg": "$population"}
        }
    }
]);

// ---------------------------------------------------------------------------------------------------------------------

// calculate the class with the best average student performance
db.grades.aggregate([
    {
        "$unwind": "$scores"
    },
    {
        "$match": {
            "scores.type": {$ne: "quiz"}
        }
    },
    {
        "$group": {
            "_id": {class_id: "$class_id", student_id: "$student_id"},
            "average_score": {"$avg": "$scores.score"}
        }
    },
    {
        "$group": {
            "_id": "$_id.class_id",
            "average_score_for_class": {"$avg": "$average_score"}
        }
    },
    {
        "$sort": { "average_score_for_class": 1 }
    }
]);

// ---------------------------------------------------------------------------------------------------------------------

// calculate the number of people who live in a zip code in the US where the city starts with a digit
db.zips.aggregate([
    {
        $project: {
            first_char: {$substr: ["$city", 0, 1]},
            city: 1,
            pop: 1
        }
    },
    {
        $match: {
            "first_char": {$gte: "0", $lte: "9"}
        }
    },
    {
        "$group": {
            "_id": null,
            "population": {"$sum": "$pop"}
        }
    }
]);

